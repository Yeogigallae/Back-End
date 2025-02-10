-- V1__initial_schema.sql

CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              created_at DATETIME(6) NULL,
                              deleted_at DATETIME(6) NULL,
                              updated_at DATETIME(6) NULL,
                              content VARCHAR(255) NULL,
                              is_read BIT NOT NULL,
                              room_name VARCHAR(255) NULL,
                              type ENUM ('BUDGET_COMPLETE', 'BUDGET_START', 'COURSE_COMPLETE', 'COURSE_START', 'VOTE_COMPLETE', 'VOTE_START') NULL,
                              user_email VARCHAR(255) NULL,
                              user_name VARCHAR(255) NULL
);

CREATE TABLE room (
                      room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL,
                      master_id BIGINT NOT NULL,
                      CONSTRAINT FK_room_master FOREIGN KEY (master_id) REFERENCES users (user_id)
);

CREATE TABLE place (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       address VARCHAR(255) NULL,
                       latitude DOUBLE NOT NULL,
                       longitude DOUBLE NOT NULL,
                       place_name VARCHAR(255) NOT NULL,
                       room_id BIGINT NOT NULL,
                       CONSTRAINT FK_place_room FOREIGN KEY (room_id) REFERENCES room (room_id)
);

CREATE TABLE trip_plans (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            created_at DATETIME(6) NULL,
                            deleted_at DATETIME(6) NULL,
                            updated_at DATETIME(6) NULL,
                            description VARCHAR(255) NULL,
                            end_date DATE NOT NULL,
                            image_url VARCHAR(255) NULL,
                            location VARCHAR(50) NOT NULL,
                            max_days INT NULL,
                            min_days INT NULL,
                            price VARCHAR(255) NULL,
                            group_name VARCHAR(255) NULL,
                            start_date DATE NOT NULL,
                            status ENUM ('COMPLETED', 'ONGOING', 'PLANNED') NOT NULL,
                            trip_plan_type ENUM ('COURSE', 'SCHEDULE') NOT NULL,
                            trip_type ENUM ('DOMESTIC', 'OVERSEAS') NOT NULL,
                            vote_limit_time ENUM ('FOUR_HOURS', 'SIXTY_MINUTES', 'SIX_HOURS', 'THIRTY_MINUTES') NULL,
                            room_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            vote_room_id BIGINT NULL UNIQUE,
                            CONSTRAINT FK_trip_plans_room FOREIGN KEY (room_id) REFERENCES room (room_id),
                            CONSTRAINT FK_trip_plans_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE vote_room (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           created_at DATETIME(6) NULL,
                           deleted_at DATETIME(6) NULL,
                           updated_at DATETIME(6) NULL,
                           trip_plan_id BIGINT NOT NULL UNIQUE,
                           CONSTRAINT FK_vote_room_trip_plan FOREIGN KEY (trip_plan_id) REFERENCES trip_plans (id)
);

ALTER TABLE trip_plans
    ADD CONSTRAINT FK_trip_plans_vote_room FOREIGN KEY (vote_room_id) REFERENCES vote_room (id);

CREATE TABLE vote (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      created_at DATETIME(6) NULL,
                      deleted_at DATETIME(6) NULL,
                      updated_at DATETIME(6) NULL,
                      type ENUM ('BAD', 'GOOD') NOT NULL,
                      trip_plan_id BIGINT NOT NULL,
                      vote_room_id BIGINT NOT NULL,
                      CONSTRAINT FK_vote_trip_plan FOREIGN KEY (trip_plan_id) REFERENCES trip_plans (id),
                      CONSTRAINT FK_vote_vote_room FOREIGN KEY (vote_room_id) REFERENCES vote_room (id)
);

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       created_at DATETIME(6) NULL,
                       deleted_at DATETIME(6) NULL,
                       updated_at DATETIME(6) NULL,
                       access_token VARCHAR(255) NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       profile_image VARCHAR(255) NULL,
                       refresh_token VARCHAR(255) NULL,
                       username VARCHAR(255) NULL,
                       vote_id BIGINT NULL,
                       CONSTRAINT FK_users_vote FOREIGN KEY (vote_id) REFERENCES vote (id)
);

CREATE TABLE friendship (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            status TINYINT NULL,
                            invitee_id BIGINT NULL,
                            inviter_id BIGINT NULL,
                            CONSTRAINT FK_friendship_invitee FOREIGN KEY (invitee_id) REFERENCES users (user_id),
                            CONSTRAINT FK_friendship_inviter FOREIGN KEY (inviter_id) REFERENCES users (user_id)
);

CREATE TABLE friendship_invite (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   token VARCHAR(255) NULL,
                                   invitee_id BIGINT NULL,
                                   inviter_id BIGINT NULL,
                                   CONSTRAINT FK_friendship_invite_invitee FOREIGN KEY (invitee_id) REFERENCES users (user_id),
                                   CONSTRAINT FK_friendship_invite_inviter FOREIGN KEY (inviter_id) REFERENCES users (user_id)
);

CREATE TABLE room_member (
                             room_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             PRIMARY KEY (room_id, user_id),
                             CONSTRAINT FK_room_member_user FOREIGN KEY (user_id) REFERENCES users (user_id),
                             CONSTRAINT FK_room_member_room FOREIGN KEY (room_id) REFERENCES room (room_id)
);
