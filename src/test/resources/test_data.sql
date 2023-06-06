MERGE INTO films (film_id, name, release_date, description, duration, mpa_rating_id)
KEY (film_id)
VALUES
  (1, 'name 1', '1999-04-30', 'description 1', 120, 1),
  (2, 'name 2', '1999-04-30', 'description 2', 120, 1);

MERGE INTO genres (genre_id, name)
KEY (genre_id)
VALUES
  (1, 'Комедия'),
  (2, 'Драма'),
  (3, 'Мультфильм'),
  (4, 'Триллер'),
  (5, 'Документальный'),
  (6, 'Боевик');

MERGE INTO mpa_ratings (mpa_rating_id, name)
KEY (mpa_rating_id)
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG-13'),
  (4, 'R'),
  (5, 'NC-17');

MERGE INTO app_users (user_id, email, login, name, birthday)
KEY (user_id)
VALUES
  (1, 'email1@email.ru', 'login1', 'name1', '1999-04-30'),
  (2, 'email2@email.ru', 'login2', 'name2', '1999-04-30');

MERGE INTO friends (user_id, friend_id)
KEY (user_id, friend_id)
VALUES
  (2, 1)
