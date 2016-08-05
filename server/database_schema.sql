DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS PUBLIC.cat_feeder
(
  id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  description TEXT,
  name VARCHAR(255),
  ip_address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS PUBLIC.users
(
  id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255),
  feeder_id INT
);

CREATE TABLE IF NOT EXISTS PUBLIC.schedule
(
  id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  feeder_id INT,
  gram_amount INT,
  recurring BOOL DEFAULT FALSE ,
  first_delivery TIMESTAMP,
  days_of_week OTHER,
  end_date DATE DEFAULT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS "USERS_EMAIL_uindex" ON PUBLIC.USERS (EMAIL);




INSERT INTO cat_feeder (description, name, ip_address) VALUES ('This cat feeder is a test cat feeder', 'Test', '0.0.0.0');

INSERT INTO users VALUES (null,
                          'zl2mjp@gmail.com',
                          '1000:8f1cb1c59d098559fd7e84ff75d7bf84:fd80db905f7960a3fe258b53acfa639ab55f6c3eff94f9c1b63d6efeae6f285baa63d30defd4adba7eec7cc7ff612d427ab60618a1232edbd35345ca45962981',
                          'Michael Pearson',
                          1);

INSERT INTO users VALUES (null,
                          'test@test.com',
                          '1000:e87c47727dc5a3a64552c7b5238c4f57:2a76704823b9a0beadabb453455fb3a34d300b2a10651c3b1f3f1da43a80470486a03ba150cac9edd8aa8efb8217f0b98c135e8590bcbbfe59e79a41db6f9ff6',
                          'Test User',
                          1);

INSERT INTO schedule VALUES (null, 1, 100, false, '2016-08-06 12:00:00', null, null);