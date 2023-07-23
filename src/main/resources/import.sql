-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-1');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-2');
-- insert into myentity (id, field) values(nextval('hibernate_sequence'), 'field-3');
-- insert into userdata user_id, name, event
-- Füge den Benutzer hinzu
INSERT INTO userdata (id, username) VALUES (999, 'user');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (10, 134, true, 'INTERESTED');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (11, 95, true, 'NOT_INTERESTED');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (12, 149, false, 'UNSURE');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (13, 25, true, 'MISS_OUT');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (14, 46, false, 'SEEN');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (15, 41, true, 'NONE');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (16, 43, false, 'INTERESTED');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (17, 70, true, 'UNSURE');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (18, 66, true, 'MISS_OUT');
INSERT INTO userevent (id, eventid, isfavorite, eventstate) VALUES (19, 122, false, 'SEEN');
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 10);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 11);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 12);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 13);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 14);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 15);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 16);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 17);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 18);
INSERT INTO userdata_userevent (userdata_id, userevents_id) VALUES (999, 19);
-- add 10 userperformances
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (20, 134, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (21, 95, 'IS_BOOKED');

INSERT INTO userperformance (id, performanceid, performancestate) VALUES (22, 149, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (23, 25, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (24, 46, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (25, 41, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (26, 43, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (27, 70, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (28, 66, 'IS_BOOKED');
INSERT INTO userperformance (id, performanceid, performancestate) VALUES (29, 122, 'IS_BOOKED');