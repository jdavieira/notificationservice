CREATE TABLE notification_request (
     notification_request_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
     book_id INTEGER NOT NULL,
     user_email Varchar(200) NOT NULL,
     stock_updated_on TIMESTAMP NULL,
     notified_on TIMESTAMP NULL,
     created_On TIMESTAMP NOT NULL,
     updated_On TIMESTAMP NULL
);