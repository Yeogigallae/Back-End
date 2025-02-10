-- 1. notification 테이블 변경
ALTER TABLE notification
    MODIFY COLUMN is_read BIT NOT NULL;

-- 2. room 테이블 변경 (master_id에 외래 키 추가)
ALTER TABLE room
    ADD CONSTRAINT FKhm55owuj9qgvc3hm9ikcd8kw5
        FOREIGN KEY (master_id) REFERENCES users (user_id);

-- 3. trip_plans 테이블 변경 (vote_room_id 외래 키 추가)
ALTER TABLE trip_plans
    ADD CONSTRAINT FKdkhe8451e74bkdthruplc1muu
        FOREIGN KEY (vote_room_id) REFERENCES vote_room (id);

-- 4. trip_plans 테이블에 user_id 외래 키 추가 (기존 유지)
ALTER TABLE trip_plans
    ADD CONSTRAINT FKbmly4beva2ojcevvinyll1ccw
        FOREIGN KEY (user_id) REFERENCES users (user_id);

-- 5. trip_plans 테이블에서 name 컬럼 삭제
ALTER TABLE trip_plans
DROP COLUMN name;
