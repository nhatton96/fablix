DELIMITER //
CREATE PROCEDURE add_movie_with_genre
(IN movId VARCHAR(10), IN title VARCHAR(100), in year integer, in director VARCHAR(100), in genre VARCHAR(100))
BEGIN
  declare gid varchar(10);
  
  if (select id from genres where name = genre) is null then
  insert into genres (name) values (genre);
  end if;
  
  if (select m.id from movies m where m.id = movId) is null then
  insert into movies (id, title, year, director) values (movId,title, year, director);
  end if;
  
  set gid = (select id from genres where name = genre);
  
  if (select genreId from genres_in_movies where movieId = movId and genreId = gid) is null then
  insert into genres_in_movies(genreId, movieId) values (gid, movId);
  end if;
END //
DELIMITER ;