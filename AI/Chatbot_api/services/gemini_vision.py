import os
import google.generativeai as genai
from dotenv import load_dotenv
from google.cloud import translate_v2 as translate
from services.prompts import build_prompt
from services.vector_search import retrieve_relevant_chunks

load_dotenv()

genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))
translator = translate.Client()

# Translate output text to user language
def translate_text(text, target_lang="en"):
    if target_lang == "en":
        return text
    return translator.translate(text, target_language=target_lang)["translatedText"]

# Generate vision-based response from Gemini using image and text
async def generate_vision_response(image_file, user_text: str, user_lang: str) -> str:
    context = retrieve_relevant_chunks(user_text)
    full_prompt = f"{build_prompt(user_lang)}\n\n--- Retrieved Context ---\n{context}\n\n--- User Question ---\n{user_text}"
    model = genai.GenerativeModel("gemini-1.5-flash")
    image_bytes = image_file.read()
    image_data = {
        "mime_type": "image/jpeg",
        "data": image_bytes
    }
    response = model.generate_content([full_prompt, image_data])
    english_answer = response.text
    return translate_text(english_answer, target_lang=user_lang).strip()