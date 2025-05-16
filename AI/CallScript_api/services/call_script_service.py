import os
import wave
import contextlib
import base64
import json
import asyncio
import re
from google import genai
from google.cloud import translate_v2 as translate
from dotenv import load_dotenv

load_dotenv()

client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))
translator = translate.Client()

@contextlib.contextmanager
def wave_file(filename, channels=1, rate=24000, sample_width=2):
    """
    Context manager to write WAV audio files.
    Deletes existing file if present to avoid permission errors.
    """
    if os.path.exists(filename):
        os.remove(filename)

    with wave.open(filename, "wb") as wf:
        wf.setnchannels(channels)
        wf.setsampwidth(sample_width)
        wf.setframerate(rate)
        yield wf

async def generate_audio(text: str, filename: str, lang: str = "ko") -> str:
    """
    Generate speech audio from text using Gemini TTS.
    Saves audio file in the current directory with the given filename.
    Returns base64-encoded audio bytes.
    """
    lang_prompts = {
        "ko": (
            "다음 문장을 추가 멘트 없이, 한국어로 꼭꼭 말하며, "
            "또박또박 알아들을 수 있게 정확하게 읽어주세요:\n{text}"
        ),
        "en": (
            "Please read the following text clearly in English, "
            "without adding any extra phrases, "
            "and speak slowly and distinctly so it's easy to understand:\n{text}"
        ),
        "zh": (
            "请清晰地朗读以下内容，"
            "不添加任何额外的话语，"
            "用中文慢速且清楚地朗读，使人容易听懂：\n{text}"
        ),
        "ja": (
            "以下の内容を追加の言葉なしで、はっきりとした日本語で、"
            "ゆっくりと明瞭に読み上げてください：\n{text}"
        ),
        "vi": (
            "Vui lòng đọc đoạn văn sau bằng tiếng Việt, "
            "không thêm lời nào khác, "
            "và đọc chậm, rõ ràng để dễ hiểu:\n{text}"
        ),
        "th": (
            "โปรดอ่านข้อความต่อไปนี้เป็นภาษาไทยชัดเจน "
            "โดยไม่ต้องเพิ่มคำพูดอื่นใด "
            "และพูดช้าๆ ชัดเจนเพื่อให้เข้าใจง่าย:\n{text}"
        ),
    }
    prompt_text = lang_prompts.get(lang, "{text}").format(text=text)

    config = {"response_modalities": ["AUDIO"]}
    async with client.aio.live.connect(model="gemini-2.0-flash-live-001", config=config) as session:
        with wave_file(filename) as wav:
            await session.send_client_content(
                turns={"role": "user", "parts": [{"text": prompt_text}]},
                turn_complete=True,
            )
            async for response in session.receive():
                if response.data:
                    wav.writeframes(response.data)

    with open(filename, "rb") as f:
        audio_bytes = f.read()

    return base64.b64encode(audio_bytes).decode("utf-8")

def translate_text(text: str, target_lang: str) -> str:
    """
    Translate text into the target language.
    Detects the input language first.
    If input is already in the target language, returns original text.
    Otherwise, translates text.
    """
    try:
        if target_lang == "ko":
            detected = translator.detect_language(text)
            if detected["language"] == "ko":
                return text
            else:
                result = translator.translate(text, target_language="ko")
                return result["translatedText"]
        else:
            result = translator.translate(text, target_language=target_lang)
            return result["translatedText"]
    except Exception as e:
        print(f"Translation error: {e}")
        return text

def generate_answer_and_keywords(question_ko: str):
    """
    Generate 3 expected answers with 3 keywords in Korean.
    Includes prompt instructions to forbid trailing commas in JSON.
    Post-processes JSON string to remove trailing commas before parsing.
    """
    prompt = (
        f"You are a hospital reception staff answering phone calls. "
        "Regardless of the input, your answers MUST be written in Korean. "
        "If you use any other language besides Korean, it will be considered a failure. "
        "Do NOT repeat the prompt or instructions in your response. "
        "Answer concisely in 1-2 sentences based only on the user's question.\n"
        "If the caller's question is unclear or nonsensical (e.g., '.', '1231', '...'), "
        "respond with a typical receptionist question like '무엇을 도와드릴까요?'.\n"
        "Generate exactly 3 expected answers with 3 keywords each.\n"
        f"User question (in Korean): {question_ko}\n"
        "Respond ONLY with a valid JSON object in this exact format:\n"
        "{\n"
        "  \"answers\": [\n"
        "    {\"answer\": \"...\", \"keywords\": [\"...\", \"...\", \"...\"]},\n"
        "    {\"answer\": \"...\", \"keywords\": [\"...\", \"...\", \"...\"]},\n"
        "    {\"answer\": \"...\", \"keywords\": [\"...\", \"...\", \"...\"]}\n"
        "  ]\n"
        "}\n"
        "Do NOT include any other text, explanation, or instructions.\n"
        "**Do NOT include trailing commas in JSON.**"
    )
    response = client.models.generate_content(
        model="gemini-1.5-flash",
        contents=[prompt],
    )
    print("Raw response:", response.text)

    raw_text = response.text
    json_match = re.search(r"\{.*\}", raw_text, re.DOTALL)
    if json_match:
        json_str = json_match.group()
        # Remove trailing commas before closing brackets/braces
        json_str = re.sub(r",(\s*[}\]])", r"\1", json_str)
        try:
            data = json.loads(json_str)
            return data["answers"]
        except Exception as e:
            print("JSON parsing error:", e)
            return None
    else:
        print("No JSON found.")
        return None

async def create_response(user_question: str, user_lang: str):
    """
    Generate the final response including the user's question, translated answers, and keywords.
    """
    # Translate user question into Korean
    question_ko = translate_text(user_question, "ko")
    audio_question_ko = await generate_audio(question_ko, "question_ko.wav", lang="ko")

    # Generate answers and keywords based on the Korean question
    answers = generate_answer_and_keywords(question_ko)
    if not answers:
        return {"error": "Failed to generate answers"}

    result = {
        "user_question": user_question,
        "question_ko": question_ko,
        "question_ko_audio": audio_question_ko,
        "answers": []
    }

    # For each answer, generate translated text and Korean audio for answer and keywords
    for idx, ans in enumerate(answers):
        # Translate answer to user's preferred language
        answer_native = translate_text(ans["answer"], user_lang)
        audio_answer_ko = await generate_audio(ans["answer"], f"answer_{idx}_ko.wav", lang="ko")

        audio_keywords_ko = []
        for jdx, kw in enumerate(ans["keywords"]):
            audio_kw_ko = await generate_audio(kw, f"answer_{idx}_keyword_{jdx}_ko.wav", lang="ko")
            audio_keywords_ko.append(audio_kw_ko)

        result["answers"].append({
            "answer_native": answer_native,
            "answer_ko": ans["answer"],
            "answer_ko_audio": audio_answer_ko,
            "keywords_ko": ans["keywords"],
            "keywords_ko_audio": audio_keywords_ko,
        })

    return result
