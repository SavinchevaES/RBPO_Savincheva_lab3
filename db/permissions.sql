-- Права пользователя приложения. Выполнить в pgAdmin под postgres в базе volunteer_db
-- ПОСЛЕ первого запуска приложения (когда таблицы уже созданы) и после constraints.sql.
-- Таблицами владеет администратор, приложению остаётся только работа с данными.

REASSIGN OWNED BY volunteer_app TO postgres;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO volunteer_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO volunteer_app;

REVOKE CREATE ON SCHEMA public FROM volunteer_app;

-- После этого в application.yml поменять ddl-auto: update на ddl-auto: validate
