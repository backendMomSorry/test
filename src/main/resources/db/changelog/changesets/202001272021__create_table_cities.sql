CREATE TABLE CITIES
(
  ID       BIGSERIAL NOT NULL PRIMARY KEY,
NAME     TEXT      NOT NULL UNIQUE,
TRACKING BOOLEAN   NOT NULL,
INTERVAL BIGINT    NOT NULL
);

CREATE INDEX CITIES_NAME_IND ON CITIES (NAME);
