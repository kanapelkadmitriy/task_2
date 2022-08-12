ALTER TABLE IF EXISTS document
    ADD COLUMN IF NOT EXISTS start_date date;

ALTER TABLE IF EXISTS document
    ADD COLUMN IF NOT EXISTS end_date date;