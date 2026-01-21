FROM openjdk:21-ea-21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки (Maven или Gradle)
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Даем права на выполнение Maven wrapper
RUN chmod +x mvnw

# Загружаем зависимости (кешируется если pom.xml не изменился)
RUN ./mvnw dependency:go-offline -B || true

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Находим и копируем собранный JAR файл
RUN mkdir -p target && \
    find target -name '*.jar' -not -name '*-sources.jar' -not -name '*-javadoc.jar' -exec cp {} app.jar \;

# Создаем директорию для логов
RUN mkdir -p /app/logs

# Открываем порт приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]