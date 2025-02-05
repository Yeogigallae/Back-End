-- 새로운 테이블 추가
CREATE TABLE notification
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME(6) NULL,
    deleted_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    content    VARCHAR(255) NULL,
    room_name  VARCHAR(255) NULL,
    type       ENUM ('BUDGET_COMPLETE', 'BUDGET_START', 'COURSE_COMPLETE', 'COURSE_START', 'VOTE_COMPLETE', 'VOTE_START') NULL,
    user_name  VARCHAR(255) NULL
);

CREATE TABLE friendship
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    status     TINYINT NULL,
    invitee_id BIGINT NULL,
    inviter_id BIGINT NULL,
    CONSTRAINT FKkde1k41jkpdx8rv1aj8xg21fy FOREIGN KEY (invitee_id) REFERENCES users (user_id),
    CONSTRAINT FKteseubh3ce26ohyiq7y802ei0 FOREIGN KEY (inviter_id) REFERENCES users (user_id)
);

CREATE TABLE friendship_invite
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(255) NULL,
    invitee_id BIGINT NULL,
    inviter_id BIGINT NULL,
    CONSTRAINT FK55s98qnr7mohg9b61nrdg84i6 FOREIGN KEY (invitee_id) REFERENCES users (user_id),
    CONSTRAINT FKkab2ps3rhghh7q6q907q7tplx FOREIGN KEY (inviter_id) REFERENCES users (user_id)
);
