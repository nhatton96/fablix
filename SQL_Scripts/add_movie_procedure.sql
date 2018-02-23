DELIMITER //
CREATE PROCEDURE add_movie
(IN title VARCHAR(100), in year integer, in director VARCHAR(100), in star VARCHAR(100), in genre VARCHAR(100))
BEGIN
  declare sid varchar(10);
  declare gid varchar(10);
  declare mid varchar(10);
  declare maxid varchar(10);
  declare siid integer;
  declare ssid varchar(2);
  declare miid integer;
  declare msid varchar(2);
  declare maxmid varchar(10);
  
  if (select count(*) from stars where name = star) = 0 then
  set siid = (select ((select substring(max(id), 3, 10)  from stars) + 1));
  set ssid = (select substring(max(id), 1, 2)  from stars);
  set maxid = (select concat(ssid,siid));
  insert into stars (id, name) values (maxid, star);
  set sid = maxid;
  else
  set sid = (select id from stars where name = star limit 1);
  end if;
  
  if (select count(*) from genres where name = genre) = 0 then
  insert into genres (name) values (genre);
  end if;
  set gid = (select id from genres where name = genre limit 1);
  
  if (select count(*) from movies m where m.title = title and m.director = director and m.year = year) = 0 then
  set miid = (select ((select substring(max(id), 3, 10)  from movies) + 1));
  set msid = (select substring(max(id), 1, 2)  from movies);
  set maxmid = (select concat(msid,miid));
  insert into movies (id, title, year, director) values (maxmid,title, year, director);
  set mid = maxmid;
  else 
  set mid = (select m.id from movies m where m.title = title and m.year = year and m.director = director limit 1);
  end if;
  
  
  if (select count(*) from stars_in_movies where movieId = mid and starsId = sid) = 0 then
  insert into stars_in_movies(starsId, movieId) values (sid, mid);
  end if;
  
  if (select count(*) from genres_in_movies where movieId = mid and genreId = gid) = 0 then
  insert into genres_in_movies(genreId, movieId) values (gid, mid);
  end if;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE add_star
(in year integer, in star VARCHAR(100))
BEGIN
  declare maxid varchar(10);
  declare siid integer;
  declare ssid varchar(2);
  
  if (select count(*) from stars where name = star and birthYear = year) = 0 then
  set siid = (select ((select substring(max(id), 3, 10)  from stars) + 1));
  set ssid = (select substring(max(id), 1, 2)  from stars);
  set maxid = (select concat(ssid,siid));
  insert into stars (id, name, birthYear) values (maxid, star, year);
  end if;
  
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE add_star_id
(in star VARCHAR(100), in movie_id VARCHAR(100), in title varchar(100), in dir varchar(100))
BEGIN
  declare maxid varchar(10);
  declare siid integer;
  declare ssid varchar(2);
  declare sid varchar(10);
  
  if (select count(*) from movies where id = movie_id) = 0 then
  insert into movies(id,title,year,director) values (movie_id,title,2018,dir);
  end if;
  
  if (select count(*) from stars where name = star) = 0 then
  set siid = (select ((select substring(max(id), 3, 10)  from stars) + 1));
  set ssid = (select substring(max(id), 1, 2)  from stars);
  set maxid = (select concat(ssid,siid));
  insert into stars (id, name) values (maxid, star);
  set sid = maxid;
  else
  set sid = (select id from stars where name = star limit 1);
  end if;
  
  replace into stars_in_movies(starsId, movieId) values (sid, movie_id);
  
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE add_star_id2
(in star VARCHAR(100), in movie_id VARCHAR(100), in title varchar(100), in dir varchar(100))
BEGIN
  declare maxid varchar(10);
  declare siid integer;
  declare ssid varchar(2);
  declare sid varchar(10);
  
  if (select count(*) from stars where name = star) = 0 then
  set siid = (select ((select substring(max(id), 3, 10)  from stars) + 1));
  set ssid = (select substring(max(id), 1, 2)  from stars);
  set maxid = (select concat(ssid,siid));
  insert into stars (id, name) values (maxid, star);
  set sid = maxid;
  else
  set sid = (select id from stars where name = star limit 1);
  end if;
  
  replace into stars_in_movies(starsId, movieId) values (sid, movie_id);
  
END //
DELIMITER ;