FROM chromadb/chroma:latest

ENV IS_PERSISTENT=TRUE
ENV PERSIST_DIRECTORY=/chroma/chroma

EXPOSE 8000

VOLUME ["/chroma/chroma"]

CMD ["chroma", "run", "--host", "0.0.0.0", "--port", "8000"]