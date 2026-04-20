-- Tabla para Google Translate
CREATE TABLE IF NOT EXISTS google_translate (
    id SERIAL PRIMARY KEY,
    text_original TEXT NOT NULL,
    language_from VARCHAR(10) NOT NULL,
    language_to VARCHAR(10) NOT NULL,
    text_translated TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_translate_from ON google_translate(language_from);
CREATE INDEX IF NOT EXISTS idx_translate_to ON google_translate(language_to);
CREATE INDEX IF NOT EXISTS idx_translate_created ON google_translate(created_at);

-- Tabla para YouTube MP3 Downloads (simplificada)
CREATE TABLE IF NOT EXISTS youtube_mp3 (
    id SERIAL PRIMARY KEY,
    video_url TEXT NOT NULL,
    download_url TEXT,
    status VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_youtube_status ON youtube_mp3(status);
CREATE INDEX IF NOT EXISTS idx_youtube_created ON youtube_mp3(created_at);
