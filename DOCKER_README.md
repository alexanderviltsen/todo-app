# Docker Deployment Guide

## 📦 Описание

Данный проект настроен для запуска в Docker контейнерах. Используется:
- **OpenJDK 21** (образ `openjdk:21-ea-21-jdk-slim`)
- **PostgreSQL 15** для базы данных
- **Maven** для сборки приложения
- **Spring Boot 4.0.1** как основной фреймворк

## 🚀 Быстрый старт

### Запуск всех сервисов одной командой:

```bash
docker-compose up --build
```

Приложение будет доступно по адресу: **http://localhost:8080**

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
6. **Запуск**: Java приложение на порту 8080

### docker-compose.yml

Оркестрация двух сервисов:

#### Сервис `app` (Spring Boot приложение):
- **Порт**: 8080
- **Переменные окружения**:
  - `JAVA_OPTS`: Настройки JVM (heap memory)
  - `SPRING_DATASOURCE_URL`: Подключение к PostgreSQL
  - `SPRING_DATASOURCE_USERNAME`: todouser
  - `SPRING_DATASOURCE_PASSWORD`: todopass
  - `SPRING_JPA_HIBERNATE_DDL_AUTO`: update

#### Сервис `db` (PostgreSQL):
- **Образ**: postgres:15-alpine
- **Порт**: 5432
- **База данных**: tododb
- **Persistent storage**: Volume для сохранения данных

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
```

### Пересборка образов:

```bash
docker-compose build --no-cache
```

### Запуск в фоновом режиме:

```bash
docker-compose up -d
```

### Выполнение команд внутри контейнера:

```bash
# Войти в контейнер приложения
docker-compose exec app bash

# Войти в PostgreSQL
docker-compose exec db psql -U todouser -d tododb
```

### Перезапуск сервиса:

```bash
# Перезапустить приложение
docker-compose restart app

# Перезапустить базу данных
docker-compose restart db
```

## 🔐 Переменные окружения

Вы можете создать файл `.env` в корне проекта для переопределения переменных:

```env
# Database
POSTGRES_DB=tododb
POSTGRES_USER=todouser
POSTGRES_PASSWORD=todopass

# Application
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xmx1024m -Xms512m
```

Затем обновите `docker-compose.yml`:

```yaml
services:
  app:
    environment:
      - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
```

## 📊 Volumes

Проект использует следующие volumes:

1. **postgres-data**: Хранилище данных PostgreSQL
2. **logs**: Директория для логов приложения (монтируется из `./logs`)

### Просмотр volumes:

```bash
docker volume ls
```

### Очистка неиспользуемых volumes:

```bash
docker volume prune
```

## 🌐 Сетевая конфигурация

Все сервисы находятся в одной сети `app-network` (bridge driver).

Внутри Docker сети:
- Приложение доступно как `app`
- База данных доступна как `db`

## 🐛 Отладка

### Проверка состояния контейнеров:

```bash
docker-compose ps
```

### Проверка использования ресурсов:

```bash
docker stats
```

### Проверка сетевых подключений:

```bash
docker network inspect <network_name>
```

### Доступ к базе данных:

```bash
# Из хоста
psql -h localhost -p 5432 -U todouser -d tododb

# Из контейнера приложения
docker-compose exec app bash
apk add postgresql-client
psql -h db -p 5432 -U todouser -d tododb
```

## 🔄 Обновление приложения

1. Внесите изменения в код
2. Пересоберите образ:
   ```bash
   docker-compose build app
   ```
3. Перезапустите контейнер:
   ```bash
   docker-compose up -d app
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
```

### 2. Настройте health checks:

```yaml
services:
  app:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

### 3. Ограничьте ресурсы:

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

### 4. Используйте multi-stage build:

Для уменьшения размера образа можно создать multi-stage Dockerfile:

```dockerfile
# Build stage
FROM openjdk:21-ea-21-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:21-ea-21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 🧪 Тестирование

### Проверка работоспособности:

```bash
# Проверка API
curl http://localhost:8080/

# Проверка health endpoint (если настроен Spring Actuator)
curl http://localhost:8080/actuator/health
```

## 📚 Полезные ссылки

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)

## ⚠️ Troubleshooting

### Проблема: Контейнер app не может подключиться к БД

**Решение**: Убедитесь, что:
1. Контейнер db запущен: `docker-compose ps`
2. Правильные credentials в переменных окружения
3. Используется правильный host: `db` (не `localhost`)

### Проблема: Порт 8080 уже занят

**Решение**: Измените порт в docker-compose.yml:
```yaml
ports:
  - "8081:8080"  # Изменить внешний порт
```

### Проблема: Недостаточно памяти для JVM

**Решение**: Увеличьте heap size:
```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m
```

### Проблема: База данных не сохраняет данные после перезапуска

**Решение**: Убедитесь, что volume создан:
```bash
docker volume ls | grep postgres-data
```

---

**Версия**: 1.0  
**Последнее обновление**: 2024
