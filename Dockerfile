# ---- STAGE 1: build ----
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Копируем всё, что нужно для Maven Wrapper
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Делаем скрипт исполняемым (иначе Alpine выдаст "not found")
RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# ---- STAGE 2: runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/tickets_shop-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
