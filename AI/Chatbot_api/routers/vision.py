from fastapi import APIRouter, UploadFile, File, Form
from fastapi.responses import PlainTextResponse  # 추가
from services.gemini_vision import generate_vision_response

router = APIRouter()

# Multimodal (image + text) chat endpoint
@router.post("/vision", response_class=PlainTextResponse) 
async def vision_chat(
    user_text: str = Form(...),
    user_lang: str = Form(...),
    image: UploadFile = File(...)
):
    response = await generate_vision_response(image.file, user_text, user_lang)
    return response
