Для запуска необходим Postges. Либо в application.properties изменить настроки подклбчения на существующие. Либо создать файл docker-compose.yml со следующими содержимым:
postgresdb:
  image: postgres:10.0
  restart: always
  ports:
    - "5432:5432"
  environment:
    - POSTGRES_DB=test
    - POSTGRES_PASSWORD=root
    - POSTGRES_USER=db_admin
    
После чего запустить команду в терминале docker-compose up

Далее можно запускать сервер. Коллекция запросов для postman https://www.getpostman.com/collections/0d23049f7556e8d86336
