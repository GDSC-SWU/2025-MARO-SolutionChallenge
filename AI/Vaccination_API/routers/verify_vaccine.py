from fastapi import APIRouter, UploadFile, File, Form
from services.vaccine_checker import verify_vaccine_image

router = APIRouter()

@router.post("/verify_vaccine")
async def verify_vaccine(
    vaccine_ko: str = Form(...),
    vaccine_en: str = Form(...),
    period: str = Form(...),  # e.g., "2025.03.25~2025.04.05"
    image: UploadFile = File(...)
):
    # Pass the input to the vaccine image verification logic
    result = await verify_vaccine_image(image.file, vaccine_ko, vaccine_en, period)
    return {"result": str(result)}
