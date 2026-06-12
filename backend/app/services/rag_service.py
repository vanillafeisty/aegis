"""Retrieval-Augmented Generation service with ChromaDB."""
import json
import logging
from typing import Optional
from uuid import UUID

import httpx

from app.core.config import settings

logger = logging.getLogger("aegis")


class RAGService:
    def __init__(self):
        self.chroma_url = f"http://{settings.CHROMA_HOST}:{settings.CHROMA_PORT}"
        self.client = httpx.AsyncClient(base_url=self.chroma_url)

    async def create_collection(self, user_id: UUID, collection_name: str) -> dict:
        """Create a user-scoped collection in ChromaDB."""
        coll_name = f"{user_id}_{collection_name}"
        payload = {
            "name": coll_name,
            "metadata": {"user_id": str(user_id), "type": collection_name},
        }
        try:
            response = await self.client.post("/api/v1/collections", json=payload)
            return response.json()
        except Exception as e:
            logger.error(f"Failed to create ChromaDB collection: {e}")
            return {}

    async def add_embedding(
        self, user_id: UUID, collection_name: str, doc_id: str, text: str, metadata: dict
    ) -> str:
        """Add a text embedding to ChromaDB."""
        coll_name = f"{user_id}_{collection_name}"
        payload = {
            "documents": [text],
            "ids": [doc_id],
            "metadatas": [metadata],
        }
        try:
            response = await self.client.post(f"/api/v1/collections/{coll_name}/add", json=payload)
            return doc_id
        except Exception as e:
            logger.error(f"Failed to add embedding: {e}")
            return ""

    async def query_embeddings(self, user_id: UUID, collection_name: str, query_text: str, top_k: int = 5) -> list:
        """Query similar embeddings from ChromaDB."""
        coll_name = f"{user_id}_{collection_name}"
        payload = {
            "query_texts": [query_text],
            "n_results": top_k,
        }
        try:
            response = await self.client.post(f"/api/v1/collections/{coll_name}/query", json=payload)
            data = response.json()
            results = []
            if data.get("documents"):
                for i, doc in enumerate(data["documents"][0]):
                    results.append(
                        {
                            "id": data["ids"][0][i] if data.get("ids") else None,
                            "text": doc,
                            "metadata": data["metadatas"][0][i] if data.get("metadatas") else {},
                            "distance": data["distances"][0][i] if data.get("distances") else 0,
                        }
                    )
            return results
        except Exception as e:
            logger.error(f"Failed to query embeddings: {e}")
            return []

    async def store_resume(self, user_id: UUID, resume_text: str) -> str:
        """Store resume in RAG system."""
        doc_id = f"resume_{user_id}"
        await self.add_embedding(user_id, "resume", doc_id, resume_text, {"type": "resume"})
        return doc_id

    async def store_job_history(self, user_id: UUID, job_title: str, jd_text: str) -> str:
        """Store job description in job history."""
        doc_id = f"job_{job_title}_{user_id}"
        await self.add_embedding(user_id, "job_history", doc_id, jd_text, {"type": "job_description", "title": job_title})
        return doc_id

    async def retrieve_resume_context(self, user_id: UUID) -> str:
        """Retrieve full resume context for planning."""
        results = await self.query_embeddings(user_id, "resume", "resume", top_k=1)
        if results:
            return results[0]["text"]
        return ""

    async def retrieve_similar_jobs(self, user_id: UUID, query: str, top_k: int = 5) -> list:
        """Find similar jobs the user has searched before."""
        return await self.query_embeddings(user_id, "job_history", query, top_k=top_k)

    async def health_check(self) -> bool:
        """Check ChromaDB health."""
        try:
            response = await self.client.get("/api/v1/heartbeat")
            return response.status_code == 200
        except Exception as e:
            logger.error(f"ChromaDB health check failed: {e}")
            return False
