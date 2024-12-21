create table reservation(id bigint, res_table integer, res_datetime datetime, primary key(id));


rname table reservation to reservation_old;

alter table reservation rename to reservation_old;

alter table reservation2 rename to reservation;

SELECT * FROM INFORMATION_SCHEMA.COLUMNS

SELECT * FROM INFORMATION_SCHEMA.COLUMNS  where TABLE_NAME = 'RESERVATION'

# Make a column nullable:
ALTER TABLE RESERVATION ALTER COLUMN RES_TABLE INTEGER NULL;

CREATE SEQUENCE KEWEBSIIDS START WITH 1 INCREMENT BY 1;