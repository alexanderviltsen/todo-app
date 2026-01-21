FROM openjdk:21-ea-21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки (Maven или Gradle)
COPY pom.xml ./
COPY mvnw ./
COPY .mvn ./.mvn

# Загружаем зависимости (кешируется если pom.xml не изменился)
RUN ./mvnw dependency:go-offline -B || true

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Находим и копируем собранный JAR файл
RUN mkdir -p target && \
    find target -name '*.jar' -not -name '*-sources.jar' -exec cp {} app.jar \; || \
    echo "app.jar" > app.jar

# Открываем порт приложения
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]