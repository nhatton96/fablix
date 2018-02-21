USE `moviedb`;
Delimiter $$
CREATE PROCEDURE add_movie (IN id VARCHAR(10), IN title VARCHAR(100), IN year INT, IN director VARCHAR(100), 
							IN sId VARCHAR(10), IN star VARCHAR(100), IN starName VARCHAR(100), IN starBirthYear INT, 
							IN genre VARCHAR(32))
Begin
	DECLARE checkStar int;
    DECLARE starId int;
    DECLARE checkGenre int;
    DECLARE genreId int;
    DECLARE movieId int;
    
    SET checkStar = 0;
    SET checkGenre = 0;
    
    INSERT INTO movies (id, title, year, director)
    VALUES (@id, @title, @year, @director);
    SELECT id INTO movieId FROM movies WHERE title = @title;
    
    SELECT count(*) INTO checkStar from stars WHERE id = @sId; 
    SELECT count(*) INTO checkGenre from genres WHERE name = @genre; 
    
    IF (checkStar > 0) THEN
		SELECT id INTO starId FROM stars WHERE name = @star;
        INSERT INTO stars_in_movies (starId, movieId)
        VALUES (starId, movieId);
	ELSE
		INSERT INTO stars (id, name, birthYear)
        VALUES (@sId, @starName, @starBirthYear);
        INSERT INTO stars_in_movies (starId, movieId)
        VALUES (@sId, @id);
	END IF;
    
    IF (checkGenre > 0) THEN
		SELECT id INTO genreId FROM genre WHERE name = @genre;
        INSERT INTO genres_in_movies (genreId, movieId)
		VALUES (genreId, movieId);
	ELSE
		INSERT INTO genre (name)
        VALUES (@genre);
        SELECT id INTO genreId FROM genre WHERE name = @genre;
        INSERT INTO genres_in_movies (genreId, movieId)
		VALUES (genreId, movieId);
	END IF;
End
$$
Delimiter ;

