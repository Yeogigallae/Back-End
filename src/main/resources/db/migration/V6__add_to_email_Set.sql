-- 1. notification 테이블에서 잘못 추가된 user_email 컬럼이 있는지 확인 후 삭제
ALTER TABLE notification DROP COLUMN IF EXISTS user_email;

-- 2. notification 테이블에서 user_email 컬럼을 다시 추가
ALTER TABLE notification ADD COLUMN user_email VARCHAR(255) NULL;
