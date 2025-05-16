import os
from uuid import uuid4

def save_upload_file(upload_file, save_dir="temp_images"):
    os.makedirs(save_dir, exist_ok=True)

    filename = f"{uuid4().hex}_{upload_file.filename}"
    file_path = os.path.join(save_dir, filename)

    with open(file_path, "wb") as buffer:
        buffer.write(upload_file.file.read())

    return file_path