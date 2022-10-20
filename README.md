# TextAnalyzerREST
Классификатор https://github.com/SBelov13/TextAnalyzer переделан под REST-сервис.

Инструкция по использованию:
1) скачать клиент h2 и создать БД: jdbc:h2:tcp://localhost/~/warn_test
2) развернуть в БД из п.1 дамб БД: resources/dumpH2.db
3) скачать директорию с текстами и моделью классификатора: https://disk.yandex.ru/d/yWVnE0nqRsHvfw
4) скачать и установить сервер приложений apache tomcat
5) скорректировать в application.yml параметр к директории с текстами и моделью config.rootdir (см. пункт 3)
6) артефакт TextAnalizerREST-0.4.war скопировать в ./tomcat/webapps
7) запустить сервер приложений
