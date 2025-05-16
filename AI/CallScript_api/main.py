from fastapi import FastAPI
from routers.call_script_router import router as call_script_router

app = FastAPI()

app.include_router(call_script_router)
