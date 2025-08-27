# 🧾 Abstract
**BankilyChatbot** is an intelligent AI-powered chatbot built using **Spring Boot**, **Spring AI**, and a **Retrieval-Augmented Generation (RAG)** pipeline to assist users with inquiries related to **Bankily**, a digital banking service in Mauritania.  

This system integrates **natural language processing (NLP)**, **vector embedding**, and **context-aware retrieval** to deliver precise, helpful, and polite answers based on official documents such as user guides and FAQs.  

The chatbot utilizes both simple **in-memory vector stores** and a **pgvector-backed PostgreSQL storage system**, allowing scalable and persistent document retrieval.  

This article presents a full breakdown of the system’s **architecture, methodology, and performance**.  

---

# 📘 Introduction
Digital banking adoption in Mauritania is growing rapidly, with services like **Bankily** at the forefront. However, users often struggle to get quick and relevant information about features, procedures, and troubleshooting.  

To address this gap, we introduce **BankilyChatbot**, a context-aware chatbot built on modern AI tools and capable of **semantic search** and **LLM-based response generation**.  

### 🎯 Goals:
- Support natural language questions in **Arabic and French**.  
- Retrieve relevant documentation snippets using **semantic embeddings**.  
- Provide accurate, concise responses grounded in official **Bankily documentation**.  
- Ensure **data privacy** by keeping all data securely stored.  

---

# 🔬 Methodology
BankilyChatbot follows the **Retrieval-Augmented Generation (RAG)** architecture:

### 1. Ingestion Phase:
- Collect **PDF documents** (e.g., user guides, help docs, FAQs) related to Bankily.  
- Load and split the documents into chunks (e.g., *500 tokens with 50-token overlap*).  
- Generate **embeddings** for each chunk using a supported embedding model.  
- Store the resulting vectors in a vector store: either a simple **in-memory store** or **pgvector-backed PostgreSQL** database.  

### 2. Query Phase:
- Accept a **natural language question** from the user.  
- Generate an **embedding** for the query.  
- Use **cosine similarity** to find the *top-k* most relevant document chunks.  
- Pass the retrieved chunks as **context** to the LLM.  
- The **LLM generates a response** strictly based on the provided context.  

---

# ⚙️ Implementation Details
### 🛠️ Technologies Used
- **Spring Boot**  
- **Spring AI**  
- **pgvector** (PostgreSQL extension for vector search)  
- **Apache PDFBox** for PDF parsing  
- **OpenAI Embedding API**  
- **OpenAI Chat Completions API**  
