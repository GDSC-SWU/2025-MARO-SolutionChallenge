import os
import re
from datetime import datetime
from dotenv import load_dotenv
import google.generativeai as genai

load_dotenv()
genai.configure(api_key=os.getenv("GOOGLE_API_KEY"))

# Check if the extracted date is within the valid vaccination period
def is_date_in_range(datestr, period):
    try:
        date = datetime.strptime(datestr, "%Y.%m.%d")
        start_str, end_str = period.split("~")
        start = datetime.strptime(start_str.strip(), "%Y.%m.%d")
        end = datetime.strptime(end_str.strip(), "%Y.%m.%d")
        return start <= date <= end
    except:
        return False

# Extract first date found in string (e.g., "2025.03.25")
def extract_date(text):
    match = re.search(r"\d{4}\.\d{2}\.\d{2}", text)
    return match.group() if match else None

# Main verification function using Gemini Vision API
async def verify_vaccine_image(image_file, vaccine_ko, vaccine_en, period):
    model = genai.GenerativeModel("gemini-1.5-flash")
    image_bytes = image_file.read()
    image_data = {
        "mime_type": "image/png",  
        "data": image_bytes
    }

    # English prompt for Gemini
    prompt = f"""
    Analyze the following image of a vaccination certificate or sticker.
    Extract the name of the vaccine and the vaccination date from the image.

    The vaccine name must match either '{vaccine_ko}' (Korean) or '{vaccine_en}' (English).
    The date must fall within the given period: '{period}'.

    If both the vaccine name and the date match the conditions, respond only with: True
    Otherwise, respond only with: False
    """

    response = model.generate_content([prompt, image_data])
    text = response.text.strip()
    return text == "True"
