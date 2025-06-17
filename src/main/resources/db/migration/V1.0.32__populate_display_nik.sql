-- For existing records, set display_nik to a sample NIK
-- Since we can't decode the hashed NIK, we'll use a standard format for existing records
UPDATE freelancer SET display_nik = '1234567890123456' WHERE display_nik IS NULL; 