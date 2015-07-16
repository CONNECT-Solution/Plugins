-- workaround for non-supported DROP USER IF EXISTS. see MySQL Bug #15287
GRANT USAGE ON *.* TO interopguiuser identified by 'interopguipass';
DROP USER 'interopguiuser';
DELETE FROM mysql.user WHERE User = 'interopguiuser';

DROP DATABASE IF EXISTS interopgui;
FLUSH PRIVILEGES;


