from fastapi import APIRouter, Form
from fastapi.responses import PlainTextResponse
from services.gemini_rag import generate_rag_response

router = APIRouter()

# Text-based chat endpoint
@router.post("/chat", response_class=PlainTextResponse) 
async def chat(user_text: str = Form(...), user_lang: str = Form(...)):
    response = await generate_rag_response(user_text, user_lang)
    return response