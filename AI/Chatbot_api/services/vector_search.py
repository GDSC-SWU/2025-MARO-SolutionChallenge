import os
from fastembed import TextEmbedding
from langchain_community.vectorstores import FAISS
from langchain_community.embeddings import HuggingFaceEmbeddings

# Load vector DB using pretrained embedding model
VECTOR_STORE_DIR = "vector_store/faiss_index"

embedding_model = HuggingFaceEmbeddings(model_name="BAAI/bge-base-en-v1.5")
vector_db = FAISS.load_local(
    VECTOR_STORE_DIR,
    embedding_model,
    allow_dangerous_deserialization=True
)

# Retrieve relevant document chunks from vector DB
def retrieve_relevant_chunks(query: str, top_k: int = 3) -> str:
    docs = vector_db.similarity_search(query, k=top_k)
    context = "\n\n".join([f"[source={doc.metadata.get('source')}]\n{doc.page_content}" for doc in docs])
    return context