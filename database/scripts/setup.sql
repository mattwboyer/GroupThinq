-- USERS
DROP TABLE IF EXISTS USERS
CREATE TABLE USERS (
    ID SERIAL PRIMARY KEY,
    USERNAME VARCHAR(32) UNIQUE NOT NULL,
    PW VARCHAR(256) UNIQUE NOT NULL,
    LAST_NAME VARCHAR(32) NOT NULL,
    FIRST_NAME VARCHAR(32) NOT NULL,
    EMAIL VARCHAR(32) NOT NULL,
    BIRTH_DATE TIMESTAMP NOT NULL,
    CREATED_DATE TIMESTAMP,
    UPDATED_DATE TIMESTAMP,
    LAST_LOGGED_IN TIMESTAMP
);

-- DECISION
DROP TABLE IF EXISTS DECISION 
CREATE TABLE DECISION (
    ID INTEGER UNIQUE PRIMARY KEY NOT NULL,
    NAME VARCHAR(32) NOT NULL
);

INSERT INTO DECISION VALUES (1, 'TEST DECISION 1');
INSERT INTO DECISION VALUES (2, 'TEST DECISION 2');
INSERT INTO DECISION VALUES (3, 'TEST DECISION 3');
INSERT INTO DECISION VALUES (4, 'TEST DECISION 4');
INSERT INTO DECISION VALUES (5, 'TEST DECISION 5');

-- ROLE
DROP TABLE IF EXISTS ROLE
CREATE TABLE ROLE (
    ID INTEGER UNIQUE PRIMARY KEY NOT NULL,
    NAME VARCHAR(32) UNIQUE NOT NULL
);

INSERT INTO ROLE VALUES (1, 'User');
INSERT INTO ROLE VALUES (2, 'Admin');

-- USER_ROLES
DROP TABLE IF EXISTS USER_ROLES
CREATE TABLE USER_ROLES (
    USER_ID INTEGER REFERENCES USERS(ID) NOT NULL,
    ROLE_ID INTEGER REFERENCES ROLE(ID) NOT NULL
);

-- DECISION_USERS
DROP TABLE IF EXISTS DECISION_USERS
CREATE TABLE DECISION_USERS (
    DECISION_ID INTEGER REFERENCES DECISION(ID) NOT NULL,
    USER_ID INTEGER REFERENCES USERS(ID) NOT NULL
);
