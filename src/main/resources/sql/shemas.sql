CREATE TABLE connections(
    user_id BIGINT,
    connected_user_id BIGINT,
    PRIMARY KEY (user_id,connected_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (connected_user_id) REFERENCES users(id)
);


CREATE TABLE accounts(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    account_balance DECIMAL(10, 2),
    iban VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE users(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(200) UNIQUE,
    password VARCHAR(255),
    firstName VARCHAR(100),
    lastName VARCHAR(100),
    balance DECIMAL(10,2)
);

CREATE TABLE transactions(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT,
    receiver_id BIGINT,
    amount DECIMAL(10, 2),
    description VARCHAR(255),
    fee DECIMAL(10,2) NOT NUll,
    date DATETIME,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);