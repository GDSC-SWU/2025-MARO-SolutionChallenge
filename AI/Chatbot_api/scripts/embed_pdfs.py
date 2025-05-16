import os
import fitz  # PyMuPDF
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import FAISS
from langchain.docstore.document import Document
from langchain_community.embeddings import HuggingFaceEmbeddings

PDF_DIR = "data/documents"
VECTOR_STORE_DIR = "vector_store/faiss_index"

def extract_text_from_pdf(pdf_path):
    doc = fitz.open(pdf_path)
    text = ""
    for page in doc:
        page_text = page.get_text()
        print(f"[📄] {os.path.basename(pdf_path)} - Page {page.number + 1} text length: {len(page_text)}")
        text += page_text
    return text

def split_text(text, source_name):
    splitter = RecursiveCharacterTextSplitter(
        chunk_size=500,
        chunk_overlap=50
    )
    docs = splitter.create_documents([text])
    for doc in docs:
        doc.metadata["source"] = source_name
    return docs

def build_vector_db(docs, save_path):
    print("[*] Embedding...")
    embedding_model = HuggingFaceEmbeddings(model_name="BAAI/bge-base-en-v1.5")

    vector_db = FAISS.from_documents(docs, embedding_model)
    vector_db.save_local(save_path)
    print(f"[✓] Success: {save_path}")

def main():
    all_docs = []
    for filename in os.listdir(PDF_DIR):
        if filename.endswith(".pdf"):
            path = os.path.join(PDF_DIR, filename)
            print(f"[*] Extracting: {filename}")
            text = extract_text_from_pdf(path)
            if text.strip() == "":
                print(f"[⚠️] {filename} unable to extract text")
                continue
            docs = split_text(text, filename)
            print(f"[+] {filename} → {len(docs)} page")
            all_docs.extend(docs)

    print(f"[✓] All pages: {len(all_docs)}")
    build_vector_db(all_docs, VECTOR_STORE_DIR)

if __name__ == "__main__":
    main()
