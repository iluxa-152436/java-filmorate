CREATE TABLE IF NOT EXISTS app_users (
        user_id INTEGER PRIMARY KEY,
        email varchar(40) NOT NULL,
        login varchar(40) NOT NULL,
        name varchar(40),
        birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
mpa_rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(20)
);

CREATE TABLE IF NOT EXISTS genres (
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(20)
);

CREATE TABLE IF NOT EXISTS films (
        film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name varchar(40) NOT NULL,
        release_date date,
        description varchar(200),
        duration integer,
        mpa_rating_id integer REFERENCES mpa_ratings (mpa_rating_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
film_id INTEGER REFERENCES films (film_id),
genre_id INTEGER REFERENCES genres (genre_id),
CONSTRAINT pk_film_genre PRIMARY KEY (
film_id,
genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
user_id INTEGER REFERENCES app_users (user_id),
film_id INTEGER REFERENCES films (film_id),
CONSTRAINT pk_likes PRIMARY KEY (
user_id,
film_id)
);

CREATE TABLE IF NOT EXISTS friends (
user_id INTEGER REFERENCES app_users (user_id),
friend_id INTEGER REFERENCES app_users (user_id),
CONSTRAINT pk_friends PRIMARY KEY (
user_id,
friend_id)
);
