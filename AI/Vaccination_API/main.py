from fastapi import FastAPI
from routers import verify_vaccine

app = FastAPI(
    title="Vaccine Verification API",
    description="Verify vaccination image using Gemini-1.5-Flash",
    version="1.0"
)

# Include vaccine verification endpoint
app.include_router(verify_vaccine.router)
