CREATE
user 'admin'@'%' identified by 'pass123';
grant all
on main_database.* to 'admin'@'%';