CREATE
    user 'admin'@'%' identified by 'pass123';
grant all
    on main_database.* to 'admin'@'%';

CREATE
    user 'test'@'%' identified by 'pass123';
grant all
    on test_main_database.* to 'test'@'%';