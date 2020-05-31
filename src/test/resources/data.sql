-- user high numbers because postgres doesn't keep track of sequences and tests will create users and objects from 1 so you will have conflicts

INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(91, current_date , 'sgaamuwa@gmail.com', true, 'Samuel', 'Gaamuwa', current_date, 'samuelgaamuwa' );
INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(92, current_date, 'jbawaya@gmail.com', true, 'Joy', 'Bawaya', current_date, 'jbawaya' );
INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(93, current_date , 'snazziwa@gmail.com', true, 'Sandra', 'Nazziwa', current_date, 'snazziwa' );
INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(94, current_date , 'mgaamuwa@gmail.com', true, 'Merab', 'Gaamuwa', current_date, 'mgaamuwa' );
INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(95, current_date , 'egaamuwa@gmail.com', true, 'Edward', 'Gaamuwa', current_date, 'egaamuwa' );
INSERT INTO users (user_id, created_at, email, enabled, first_name, last_name, updated_at, username)
    VALUES(96, current_date, 'pnakiyemba@gmail.com', true, 'Peace', 'Nakiyemba', current_date, 'pnakiyemba' );

INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(91, current_date, current_date + 2, 0, 'Zanzibar', 'Snockling', current_date, 91);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(92, current_date, current_date + 3, 0, 'Jinja', 'Bungee Jumping', current_date, 91);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(93, current_date, current_date + 1, 0, 'Mukono', 'Night Dancing', current_date, 92);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(94, current_date, current_date + 4, 0, 'Diani', 'Sun Bathing', current_date, 93);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(95, current_date, current_date + 4, 0, 'Bali', 'Jungle Walk', current_date, 95);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(96, current_date, current_date + 5, 0, 'Santorini', 'Surfing', current_date, 94);
INSERT INTO events (event_id, created_at, date, event_status, location, title, updated_at, user_id)
    VALUES(97, current_date, current_date +6, 0, 'Capetown', 'Concert', current_date, 96);

INSERT INTO user_event (user_id, event_id)
    VALUES(91,91);
INSERT INTO user_event (user_id, event_id)
    VALUES(91,92);
INSERT INTO user_event (user_id, event_id)
    VALUES(92,93);
INSERT INTO user_event (user_id, event_id)
    VALUES(93,94);
INSERT INTO user_event (user_id, event_id)
    VALUES(95,95);
INSERT INTO user_event (user_id, event_id)
    VALUES(94,96);
INSERT INTO user_event (user_id, event_id)
    VALUES(96,97);

INSERT INTO friends (created_at, is_active, updated_at, owner_user_id, friend_user_id)
    VALUES(current_date, true, current_date, 91, 92);
INSERT INTO friends (created_at, is_active, updated_at, owner_user_id, friend_user_id)
    VALUES(current_date, true, current_date, 92, 91);
INSERT INTO friends (created_at, is_active, updated_at, owner_user_id, friend_user_id)
    VALUES(current_date, true, current_date, 93, 91);