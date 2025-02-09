-- 1. notification 테이블에서 user_email 컬럼이 없는 경우에만 추가
ALTER TABLE notification
    ADD COLUMN IF NOT EXISTS user_email VARCHAR(255) NULL;
