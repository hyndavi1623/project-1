CREATE DATABASE instagram_clone;

USE instagram_clone;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    image_url TEXT,
    caption TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

INSERT INTO users(username,email,password)
VALUES
('john','john@gmail.com','12345');

INSERT INTO posts(user_id,image_url,caption)
VALUES
(
1,
'https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d',
'My first post!'
);
