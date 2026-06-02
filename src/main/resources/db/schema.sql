CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    deleted_at TIMESTAMP NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS checkouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    checkout_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    returned_at TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_checkouts_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
    -- TODO Books deletion in the beginning probably won't be needed:
    -- 1) we are loosing info about hisotrical checkouts
    -- 2) it could be real problem, but really in the next a couple of years
    -- 3) BUT let think about situation that book was stolen, or simmply dissapeared -> thinhk about this -> maybe mark with some Book-STATUS, like "LOST" or "MISSING" etc ???
);
