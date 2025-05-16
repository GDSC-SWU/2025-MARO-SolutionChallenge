from fastapi import FastAPI
from routers import chat, vision

app = FastAPI(
    title="AI Chat API",
    description="Text + Image based chatbot using Gemini + RAG",
    version="0.2"
)

app.include_router(chat.router)
app.include_router(vision.router)