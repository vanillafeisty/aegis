"""Security utilities: JWT tokens, password hashing, AES encryption."""
import base64
import hashlib
import os
import secrets
from datetime import datetime, timedelta, timezone
from typing import Optional

from cryptography.fernet import Fernet
from jose import JWTError, jwt
from passlib.context import CryptContext

from app.core.config import settings

# ── Password Hashing ──────────────────────────────────────────────────────────
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def hash_password(password: str) -> str:
    return pwd_context.hash(password)


def verify_password(plain: str, hashed: str) -> bool:
    return pwd_context.verify(plain, hashed)


# ── JWT Tokens ────────────────────────────────────────────────────────────────
def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + (
        expires_delta or timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    )
    to_encode.update({"exp": expire, "type": "access"})
    return jwt.encode(to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM)


def create_refresh_token(data: dict) -> str:
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(days=settings.REFRESH_TOKEN_EXPIRE_DAYS)
    to_encode.update({"exp": expire, "type": "refresh"})
    return jwt.encode(to_encode, settings.SECRET_KEY, algorithm=settings.ALGORITHM)


def decode_token(token: str) -> dict:
    return jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])


def generate_reset_token() -> str:
    return secrets.token_urlsafe(32)


# ── AES-256 Encryption (for LinkedIn credentials) ─────────────────────────────
def _derive_fernet_key(secret_key: str) -> bytes:
    """Derive a 32-byte Fernet key from the app secret key."""
    key_bytes = hashlib.sha256(secret_key.encode()).digest()
    return base64.urlsafe_b64encode(key_bytes)


_fernet = Fernet(_derive_fernet_key(settings.SECRET_KEY))


def encrypt_credential(plaintext: str) -> str:
    """AES-256 encrypt a credential string. Returns base64 ciphertext."""
    return _fernet.encrypt(plaintext.encode()).decode()


def decrypt_credential(ciphertext: str) -> str:
    """Decrypt an AES-256 encrypted credential."""
    return _fernet.decrypt(ciphertext.encode()).decode()
