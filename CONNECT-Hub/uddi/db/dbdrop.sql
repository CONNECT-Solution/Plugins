-- workaround for non-supported DROP USER IF EXISTS. see MySQL Bug #15287
GRANT USAGE ON *.* TO juddiuser identified by 'juddipas';
DROP USER 'juddiuser';
DELETE FROM mysql.user WHERE User = 'juddiuser';

DROP DATABASE IF EXISTS juddiv3;
FLUSH PRIVILEGES;


