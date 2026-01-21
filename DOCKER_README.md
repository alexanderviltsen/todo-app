# Docker Deployment Guide

## 📦 Описание

Данный проект настроен для запуска в Docker контейнерах. Используется:
- **OpenJDK 21** (образ `openjdk:21-ea-21-jdk-slim`)
- **PostgreSQL 15** для базы данных
- **Maven** для сборки приложения
- **Spring Boot 4.0.1** как основной фреймворк

## 🚀 Быстрый старт

### Предварительные требования:

- Docker установлен и запущен
- Docker Compose установлен
- Порты **8081** и **5433** свободны

### Запуск всех сервисов одной командой:

```bash
docker-compose up --build
```

Приложение будет доступно по адресу: **http://localhost:8081**

### Запуск в фоновом режиме:

```bash
docker-compose up -d --build
```

### Остановка контейнеров:

```bash
docker-compose down
```

### Остановка с удалением volumes (БД будет очищена):

```bash
docker-compose down -v
```

## 📋 Структура Docker файлов

### Dockerfile

Многоступенчатый Dockerfile для сборки Spring Boot приложения:

1. **Базовый образ**: `openjdk:21-ea-21-jdk-slim`
2. **Копирование зависимостей**: Maven wrapper и pom.xml
3. **Загрузка зависимостей**: Кэширование слоя с зависимостями
4. **Сборка приложения**: `mvnw clean package -DskipTests`
5. **Создание JAR файла**: Поиск и копирование собранного JAR
6. **Запуск**: Java приложение на порту 8080 (внутри контейнера)

### docker-compose.yml

Оркестрация двух сервисов:

#### Сервис `app` (Spring Boot приложение):
- **Внутренний порт**: 8080
- **Внешний порт**: 8081 (изменен из-за конфликта портов)
- **Spring Profile**: docker
- **Переменные окружения**:
  - `JAVA_OPTS`: Настройки JVM (heap memory: -Xmx512m -Xms256m)
  - `SPRING_DATASOURCE_URL`: jdbc:postgresql://db:5432/tododb
  - `SPRING_DATASOURCE_USERNAME`: todouser
  - `SPRING_DATASOURCE_PASSWORD`: todopass
  - `SPRING_JPA_HIBERNATE_DDL_AUTO`: update

#### Сервис `db` (PostgreSQL):
- **Образ**: postgres:15-alpine
- **Внутренний порт**: 5432
- **Внешний порт**: 5433 (для доступа с хоста)
- **База данных**: tododb
- **Пользователь**: todouser
- **Пароль**: todopass
- **Persistent storage**: Volume `postgres-data`
- **Health check**: Проверка готовности БД перед запуском приложения

### application-docker.yaml

Профиль Spring для Docker окружения:
- Использует переменные окружения для подключения к БД
- Хост БД: `db` (имя сервиса в Docker сети)
- Логирование в файл `/app/logs/application.log`
- Оптимизирован для production (кэширование Thymeleaf)

## 🔧 Команды Docker

### Просмотр запущенных контейнеров:

```bash
docker-compose ps
```

### Просмотр логов:

```bash
# Все сервисы
docker-compose logs -f

# Только приложение
docker-compose logs -f app

# Только база данных
docker-compose logs -f db

# Последние 100 строк
docker-compose logs --tail=100 app
```

### Пересборка образов:

```bash
# С кэшем
docker-compose build

# Без кэша (чистая сборка)
docker-compose build --no-cache
```

### Перезапуск сервиса:

```bash
# Перезапустить приложение
docker-compose restart app

# Перезапустить базу данных
docker-compose restart db

# Перезапустить все
docker-compose restart
```

### Выполнение команд внутри контейнера:

```bash
# Войти в контейнер приложения
docker-compose exec app bash

# Войти в PostgreSQL
docker-compose exec db psql -U todouser -d tododb

# Просмотреть таблицы в БД
docker-compose exec db psql -U todouser -d tododb -c "\dt"
```

### Остановка и запуск без пересборки:

```bash
# Остановить
docker-compose stop

# Запустить снова
docker-compose start
```

## 🔐 Конфигурация

### Изменение портов

Если порты 8081 или 5433 заняты, измените их в `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "9090:8080"  # Изменить на любой свободный порт
  
  db:
    ports:
      - "5434:5432"  # Изменить на любой свободный порт
```

### Переменные окружения

Вы можете создать файл `.env` в корне проекта:

```env
# Database
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=your_secure_password

# Application
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xmx1024m -Xms512m

# Ports
APP_PORT=8081
DB_PORT=5433
```

Затем обновите `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "${APP_PORT:-8081}:8080"
    environment:
      - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
  
  db:
    ports:
      - "${DB_PORT:-5433}:5432"
    environment:
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
```

### Настройка памяти JVM

Измените `JAVA_OPTS` в docker-compose.yml:

```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m  # Увеличить heap
```

## 📊 Volumes и данные

Проект использует следующие volumes:

1. **postgres-data**: Хранилище данных PostgreSQL (Docker volume)
2. **logs**: Директория для логов приложения (монтируется из `./logs`)

### Просмотр volumes:

```bash
docker volume ls
```

### Резервное копирование БД:

```bash
# Создать backup
docker-compose exec db pg_dump -U todouser tododb > backup.sql

# Восстановить из backup
docker-compose exec -T db psql -U todouser tododb < backup.sql
```

### Очистка volumes:

```bash
# Удалить неиспользуемые volumes
docker volume prune

# Удалить конкретный volume
docker volume rm <volume_name>
```

## 🌐 Сетевая конфигурация

Все сервисы находятся в одной сети `app-network` (bridge driver).

**Внутри Docker сети:**
- Приложение доступно как `app:8080`
- База данных доступна как `db:5432`

**Снаружи (с хоста):**
- Приложение: `localhost:8081`
- База данных: `localhost:5433`

### Просмотр сети:

```bash
docker network inspect <network_name>
```

## 🐛 Отладка

### Проблема: Приложение не может подключиться к БД

**Симптомы:**
```
FATAL: password authentication failed for user "postgres"
Role "postgres" does not exist
```

**Решение:**
1. Убедитесь, что используется профиль `docker`: `SPRING_PROFILES_ACTIVE=docker`
2. Проверьте credentials в docker-compose.yml
3. Убедитесь, что host БД: `db` (не `localhost`)
4. Пересоздайте volume:
   ```bash
   docker-compose down -v
   docker-compose up --build
   ```

### Проблема: Порт уже занят

**Симптомы:**
```
Error: bind: Only one usage of each socket address is normally permitted
```

**Решение:**
```yaml
# Изменить порт в docker-compose.yml
ports:
  - "8082:8080"  # Используйте любой свободный порт
```

### Проблема: Приложение не запускается

**Проверка логов:**
```bash
# Посмотреть логи приложения
docker-compose logs app

# Проверить статус
docker-compose ps
```

**Войти в контейнер для отладки:**
```bash
docker-compose exec app bash
ls -la
cat logs/application.log
```

### Проблема: База данных не готова

**Решение:** Health check уже настроен в docker-compose.yml. Приложение подождет пока БД будет готова.

Если проблема сохраняется:
```bash
# Проверить health check БД
docker-compose exec db pg_isready -U todouser

# Посмотреть логи БД
docker-compose logs db
```

### Проверка состояния:

```bash
# Статус контейнеров
docker-compose ps

# Использование ресурсов
docker stats

# Проверка health checks
docker inspect todo-postgres | grep -A 10 Health
```

## 🧪 Тестирование

### Проверка работоспособности:

```bash
# Проверка главной страницы
curl http://localhost:8081/

# Проверка API (пример)
curl http://localhost:8081/api/tasks

# Проверка БД
docker-compose exec db psql -U todouser -d tododb -c "SELECT version();"
```

### Проверка подключения к БД с хоста:

```bash
# Установить psql клиент (если нет)
# Ubuntu/Debian: sudo apt-get install postgresql-client
# MacOS: brew install postgresql

psql -h localhost -p 5433 -U todouser -d tododb
```

## 🔄 Обновление приложения

### Стандартный workflow:

1. Внесите изменения в код
2. Остановите контейнеры:
   ```bash
   docker-compose down
   ```
3. Пересоберите образ:
   ```bash
   docker-compose build app
   ```
4. Запустите снова:
   ```bash
   docker-compose up -d
   ```

### Быстрое обновление (сохранить БД):

```bash
# Пересобрать только приложение
docker-compose up -d --build app
```

## 📝 Production рекомендации

### 1. Используйте secrets для паролей:

```yaml
secrets:
  db_password:
    file: ./secrets/db_password.txt

services:
  app:
    secrets:
      - db_password
    environment:
      - SPRING_DATASOURCE_PASSWORD=/run/secrets/db_password
```

### 2. Ограничьте ресурсы:

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### 3. Настройте мониторинг:

```yaml
services:
  app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

### 4. Используйте production БД credentials:

```yaml
db:
  environment:
    - POSTGRES_PASSWORD=${DB_PASSWORD}  # Из .env файла
```

### 5. Настройте backup:

```bash
# Создать cron job для backup
0 2 * * * docker-compose exec -T db pg_dump -U todouser tododb > /backups/backup-$(date +\%Y\%m\%d).sql
```

## 🔒 Безопасность

### Рекомендации:

1. **Не коммитьте** `.env` файл с паролями в git
2. **Используйте сложные пароли** для production
3. **Ограничьте доступ** к портам БД (удалите `ports:` для db в production)
4. **Используйте SSL/TLS** для подключения к БД
5. **Регулярно обновляйте** образы Docker

### Проверка безопасности образов:

```bash
# Сканирование на уязвимости
docker scan todo-app
```

## 📚 Полезные ссылки

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)
- [OpenJDK Docker Images](https://hub.docker.com/_/openjdk)

## 📞 Поддержка

Если возникли проблемы:

1. Проверьте логи: `docker-compose logs`
2. Убедитесь что порты свободны: `netstat -an | grep 8081`
3. Проверьте статус: `docker-compose ps`
4. Очистите всё и начните заново:
   ```bash
   docker-compose down -v
   docker system prune -a
   docker-compose up --build
   ```

---

## 🎯 Краткая справка

```bash
# Запуск
docker-compose up -d --build

# Остановка
docker-compose down

# Логи
docker-compose logs -f app

# Перезапуск
docker-compose restart app

# Очистка
docker-compose down -v
docker system prune -a
```

**Приложение**: http://localhost:8081  
**База данных**: localhost:5433

---

**Версия**: 2.0  
**Последнее обновление**: 2024-01-21
