-- PostgreSQL initialization script for Aegis

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Run migrations handled by SQLAlchemy/Alembic
-- This file can stay minimal as SQLAlchemy will create tables
