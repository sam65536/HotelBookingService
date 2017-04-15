CREATE TABLE authority
(
    id SERIAL PRIMARY KEY NOT NULL,
    role VARCHAR(255) DEFAULT NULL::character varying
);
CREATE UNIQUE INDEX authority_role_uindex ON authority (role);

CREATE TABLE "user"
(
    id SERIAL PRIMARY KEY NOT NULL,
    email VARCHAR(255) DEFAULT NULL::character varying,
    name VARCHAR(255) DEFAULT NULL::character varying,
    password VARCHAR(255) DEFAULT NULL::character varying,
    username VARCHAR(255) DEFAULT NULL::character varying,
    authority_id BIGINT,
    CONSTRAINT user_authority__fk FOREIGN KEY (authority_id) REFERENCES authority (id)
);
CREATE UNIQUE INDEX user_username_uindex ON "user" (username);

CREATE TABLE category
(
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) DEFAULT NULL::character varying
);

CREATE TABLE city
(
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) DEFAULT NULL::character varying
);

CREATE TABLE hotel
(
    id SERIAL PRIMARY KEY NOT NULL,
    address VARCHAR(255) DEFAULT NULL::character varying,
    name VARCHAR(255) DEFAULT NULL::character varying,
    rating INTEGER NOT NULL,
    status BOOLEAN NOT NULL,
    category_id BIGINT NOT NULL,
    city_id BIGINT,
    manager_id BIGINT,
  	CONSTRAINT hotel_category__fk FOREIGN KEY (category_id) REFERENCES category (id),
  	CONSTRAINT hotel_city__fk FOREIGN KEY (city_id) REFERENCES city (id),
  	CONSTRAINT hotel_user__fk FOREIGN KEY (manager_id) REFERENCES "user" (id)
);

CREATE TABLE image
(
    id SERIAL PRIMARY KEY NOT NULL,
    insertion_date TIMESTAMP,
    path VARCHAR(255) DEFAULT NULL::character varying,
    hotel_id BIGINT,
    CONSTRAINT image_hotel__fk FOREIGN KEY (hotel_id) REFERENCES hotel (id)
);

CREATE TABLE comment
(
    id SERIAL PRIMARY KEY NOT NULL,
    date TIMESTAMP,
    is_answer BOOLEAN NOT NULL,
    status BOOLEAN NOT NULL,
    text VARCHAR(500) DEFAULT NULL::character varying,
    hotel_id BIGINT,
    reply_id BIGINT,
    user_id BIGINT,
    CONSTRAINT comment_hotel__fk FOREIGN KEY (hotel_id) REFERENCES hotel (id),
    CONSTRAINT comment_comment__fk FOREIGN KEY (reply_id) REFERENCES comment (id),
    CONSTRAINT comment_user__fk FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE room_type
(
    id SERIAL PRIMARY KEY NOT NULL,
    description VARCHAR(255) DEFAULT NULL::character varying,
    occupancy VARCHAR(255) DEFAULT NULL::character varying
);

CREATE TABLE room
(
  id SERIAL PRIMARY KEY NOT NULL,
  floor INTEGER NOT NULL,
  price INTEGER NOT NULL,
  room_number VARCHAR(255) DEFAULT NULL::character varying NOT NULL,
  hotel_id BIGINT,
  type_id BIGINT,
  CONSTRAINT room_hotel__fk FOREIGN KEY (hotel_id) REFERENCES hotel (id),
  CONSTRAINT room_room_type__fk FOREIGN KEY (type_id) REFERENCES room_type (id)
);

CREATE TABLE public.booking (
  id SERIAL PRIMARY KEY NOT NULL,
  begin_date DATE,
  end_date DATE,
  state BOOLEAN NOT NULL,
  user_id BIGINT,
  room_id BIGINT,
  CONSTRAINT booking_user__fk FOREIGN KEY (user_id) REFERENCES "user" (id),
  CONSTRAINT booking_room__fk FOREIGN KEY (room_id) REFERENCES room (id)
);

INSERT INTO category (id, name)
VALUES
  (1, 'Luxury'),
  (2, 'Apartment Hotel'),
  (3, 'Motel'),
  (4, 'Bed and Breakfast'),
  (5, 'Resort');

INSERT INTO room_type (id, description, occupancy)
VALUES
  (1, 'Single', 1),
  (2, 'Double', 2),
  (3, 'Studio', 2),
  (4, 'Presidential Suite', 6);

INSERT INTO authority (id, role)
VALUES
  (1, 'ROLE_USER'),
  (2, 'ROLE_COMMENT_MODERATOR'),
  (3, 'ROLE_HOTEL_MANAGER'),
  (4, 'ROLE_ADMIN');

INSERT INTO "user" (id, email, name, password, username, authority_id)
VALUES
  (1, 'pedro@email.com','Pedro Carvalho', 'pass', 'pedro', 4),
  (2, 'manuel@email.com','Manuel Bastos', 'pass', 'manuel', 2),
  (3, 'tiago@email.com','Tiago Pereira', 'pass', 'tiago', 1),
  (4, 'rui@email.com','Rui Abreu', 'pass', 'rui', 3),
  (5, 'luis@email.com','Luis Piedade', 'pass', 'luis', 3),
  (6, 'dhill0@bloglines.com', 'Deborah Hill', 'UKtpJgDr', 'dhill0', 1),
  (7, 'sbradley1@microsoft.com', 'Sandra Bradley', 'tSOTgV', 'sbradley1', 3),
  (8, 'jmurray2@gizmodo.com', 'John Murray', 'Rnjon0', 'jmurray2', 1),
  (9, 'ahoward3@ox.ac.uk', 'Aaron Howard', 'ScFVd7p42Yn', 'ahoward3', 1),
  (10, 'kflores4@parallels.com', 'Kathryn Flores', 'ztTx2Oa4', 'kflores4', 3),
  (11, 'dpeters5@ucoz.ru', 'Diana Peters', 'MKySNw', 'dpeters5', 1),
  (12, 'blynch6@tinypic.com', 'Brenda Lynch', 'Kp1ID5o69k', 'blynch6', 1),
  (13, 'mandrews7@dmoz.org', 'Michael Andrews', 'ZXnMzMytUWaV', 'mandrews7', 1),
  (14, 'mfranklin8@cbc.ca', 'Matthew Franklin', 'c72DCVcD', 'mfranklin8', 1),
  (15, 'ajackson9@adobe.com', 'Amy Jackson', 'LxIhudJ', 'ajackson9', 1);

INSERT INTO city (id, name) VALUES (1, 'Lisbon');
INSERT INTO city (id, name) VALUES (2, 'Atlanta');
INSERT INTO city (id, name) VALUES (3, 'New York');
INSERT INTO city (id, name) VALUES (4, 'San Francisco');
INSERT INTO city (id, name) VALUES (5, 'Las Vegas');
INSERT INTO city (id, name) VALUES (6, 'Quebec');
INSERT INTO city (id, name) VALUES (7, 'London');
INSERT INTO city (id, name) VALUES (8, 'Barcelona');
INSERT INTO city (id, name) VALUES (9, 'Tel Aviv');
INSERT INTO city (id, name) VALUES (10, 'Tokyo');

INSERT INTO hotel (id, address, name, rating, city_id, category_id, manager_id, status)
VALUES
  (1, 'Rua Castilho 149, Lisbon','Intercontinental', 5, 1, 1, 4, true),
  (2, 'Av. D. Joao II, Lisbon','Tryp', 4, 1, 2, 4, true),
  (3, 'Rua da Madalena 96, Lisbon','Holiday Inn', 2, 1, 3, 5, true),
  (4, 'Avenida dos Combatentes, Lisbon', 'Marriott', 5, 1, 5, 5, false);

INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (1, 1, '101', 1, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (2, 1, '102', 1, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (3, 1, '103', 1, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (4, 1, '104', 1, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (5, 1, '105', 1, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (6, 2, '201', 1, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (7, 2, '202', 1, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (8, 2, '203', 1, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (9, 2, '204', 1, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (10, 2, '205', 1, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (11, 1, '101', 2, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (12, 1, '102', 2, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (13, 1, '103', 2, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (14, 1, '104', 2, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (15, 1, '105', 2, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (16, 2, '201', 2, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (17, 2, '202', 2, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (18, 2, '203', 2, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (19, 2, '204', 2, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (20, 2, '205', 2, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (21, 1, '101', 3, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (22, 1, '102', 3, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (23, 1, '103', 3, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (24, 1, '104', 3, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (25, 1, '105', 3, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (26, 2, '201', 3, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (27, 2, '202', 3, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (28, 2, '203', 3, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (29, 2, '204', 3, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (30, 2, '205', 3, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (31, 1, '101', 4, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (32, 1, '102', 4, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (33, 1, '103', 4, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (34, 1, '104', 4, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (35, 1, '105', 4, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (36, 2, '201', 4, 2, 75);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (37, 2, '202', 4, 3, 100);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (38, 2, '203', 4, 4, 200);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (39, 2, '204', 4, 1, 50);
INSERT INTO room (id, floor, room_number, hotel_id, type_id, price) VALUES (40, 2, '205', 4, 2, 75);

INSERT INTO comment (id, date, status, text, hotel_id, user_id, is_answer)
VALUES
  (1, '2013-08-30 19:05:00',TRUE,'The best thing about this hotel were the owners. They were lovely friendly people. Giovanni asked us what he could cook for us. In no time we had a delicious pasta all amatriciana and a mixed meat dish. It was very nice.', 1, 3, false),
  (2, '2015-10-18 15:10:00',TRUE,'Really helpful staff, studio room was perfect.', 1, 6, false),
  (3, '2015-10-20 16:43:00',FALSE,'We loved the nice quiet location, the wonderful hospitality of the proprietor and superbly attentive staff. Breakfast always well presented and varied. Beautiful apartment with excellent facilities!', 1, 3, false),
  (4, '2015-09-30 20:12:00',TRUE,'The location of the Hotel is great. Its only a view kms from the Airport, there is the great shopping centre Vasco da Gama in front of the hotel and the Metro station is next door as well.', 2, 3, false),
  (5, '2015-09-30 17:55:00',TRUE,'Shower head moves all over the place. Boiler is noisy even though its in an outside cupboard its still next to the bed. Be good if the wall could be insulated.', 3, 8, false),
  (6, '2015-09-30 17:55:00',TRUE,'Friendly and helpful staff. Great pool. Not in the city centre of Lisbon but close to the undergound station.', 4, 9, false);

INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (7, '2015-04-25 07:51:47', false, 'In congue. Etiam justo. Etiam pretium iaculis justo.', 4, 11, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (8, '2015-07-26 20:32:02', false, 'Praesent blandit. Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.', 2, 12, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (9, '2015-09-15 04:37:56', false, 'Quisque id justo sit amet sapien dignissim vestibulum. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', 2, 13, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (10, '2015-01-13 01:51:10', true, 'Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis. Ut at dolor quis odio consequat varius.', 4, 14, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (11, '2015-06-28 12:12:37', true, 'Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam. Suspendisse potenti.', 1, 15, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES(12, '2015-07-01 06:38:51', true, 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', 1, 6, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (13, '2015-10-19 20:57:42', true, 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', 1, 3, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (14, '2014-12-01 11:35:37', true, 'In congue. Etiam justo. Etiam pretium iaculis justo.', 3, 8, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (15, '2015-05-25 01:04:13', false, 'Sed ante. Vivamus tortor. Duis mattis egestas metus.', 1, 9, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (16, '2015-08-09 12:02:23', true, 'Nulla ut erat id mauris vulputate elementum. Nullam varius. Nulla facilisi.', 2, 3, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (17, '2015-01-02 00:28:37', true, 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.', 2, 14, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (18, '2015-10-21 19:00:14', true, 'Duis bibendum, felis sed interdum venenatis, turpis enim blandit mi, in porttitor pede justo eu massa. Donec dapibus. Duis at velit eu est congue elementum.', 4, 12, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (19, '2015-09-19 21:49:18', false, 'Curabitur at ipsum ac tellus semper interdum. Mauris ullamcorper purus sit amet nulla. Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam.', 3, 11, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (20, '2015-02-06 11:01:13', false, 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat.', 1, 3, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (21, '2015-05-21 14:39:19', true, 'Fusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.', 4, 3, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (22, '2015-09-05 14:28:02', false, 'Duis aliquam convallis nunc. Proin at turpis a pede posuere nonummy. Integer non velit.', 2, 6, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (23, '2015-08-30 18:38:37', true, 'Nullam porttitor lacus at turpis. Donec posuere metus vitae ipsum. Aliquam non mauris.', 1, 13, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (24, '2015-02-05 08:57:59', false, 'Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.', 1, 12, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (25, '2015-02-15 05:51:24', true, 'Nullam porttitor lacus at turpis. Donec posuere metus vitae ipsum. Aliquam non mauris.', 1, 8, false);
INSERT INTO comment (id, "date", status, text, hotel_id, user_id, is_answer) VALUES (26, '2015-01-11 09:44:24', false, 'Donec diam neque, vestibulum eget, vulputate ut, ultrices vel, augue. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi. Integer ac neque.', 2, 9, false);

INSERT INTO image (id, insertion_date, hotel_id, path)
VALUES
  (1, '2015-10-22 15:00:00', 1, 'intercontinental.jpg'),
  (2, '2015-10-22 15:00:00', 1, 'intercontinental2.jpg'),
  (3, '2015-10-22 15:00:00', 1, 'intercontinental3.jpg'),
  (4, '2015-10-22 15:00:00', 2, 'tryp.jpg'),
  (5, '2015-10-22 15:00:00', 2, 'tryp2.jpg'),
  (6, '2015-10-22 15:00:00', 3, 'inn.jpg'),
  (7, '2015-10-22 15:00:00', 3, 'inn2.jpg'),
  (8, '2015-10-22 15:00:00', 4, 'marriott.jpg'),
  (9, '2015-10-22 15:00:00', 4, 'marriott2.jpg');

INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (1, '2017-03-25', '2017-04-25', TRUE, 15, 1);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (2, '2017-03-18', '2017-04-11', TRUE, 9, 2);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (3, '2017-03-29', '2017-04-11', TRUE, 3, 3);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (4, '2017-03-31', '2017-04-13', false, 3, 4);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (5, '2017-04-05', '2017-04-15', false, 6, 5);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (6, '2017-04-15', '2017-04-20', false, 8, 6);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (7, '2017-04-02', '2017-04-12', true, 9, 7);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (8, '2017-04-12', '2017-04-15', true, 11, 8);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (9, '2017-04-23', '2017-04-30', false, 4, 9);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (10, '2017-04-27', '2017-05-07', false, 3, 10);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (11, '2017-04-19', '2017-04-22', true, 12, 11);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (12, '2017-04-16', '2017-04-20', true, 13, 12);
INSERT INTO booking (id, begin_date, end_date, state, user_id, room_id) VALUES (13, '2017-04-28', '2017-05-01', true, 14, 13);
