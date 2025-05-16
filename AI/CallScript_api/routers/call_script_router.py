from fastapi import APIRouter
from pydantic import BaseModel
from services import call_script_service

router = APIRouter()

class RequestModel(BaseModel):
    """
    Request body schema for the call script help API.

    Attributes:
        user_question (str): The user's question or inquiry in their native language.
        user_lang (str): The language code of the user's native language, e.g. "en", "vi", "zh".
    """
    user_question: str
    user_lang: str

@router.post("/api/callscript/help")
async def callscript_help(request: RequestModel):
    """
    API endpoint to process user's inquiry for call script assistance.

    Steps:
    1. Accept the user's question and native language code.
    2. Pass these inputs to the service layer to generate answers, translations, and audio.
    3. Return the structured JSON response including original and translated texts,
       audio data (base64), and keywords.

    Args:
        request (RequestModel): Incoming POST request body.

    Returns:
        dict: The generated answers with translations and audio.
    """
    result = await call_script_service.create_response(request.user_question, request.user_lang)
    return result
