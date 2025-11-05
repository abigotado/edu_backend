# --- Stage 1: сборка артефакта ---
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
COPY docs docs

RUN ./gradlew bootJar --no-daemon

# --- Stage 2: минимальный рантайм ---
FROM eclipse-temurin:17-jre

RUN useradd --create-home --shell /bin/bash appuser
WORKDIR /home/appuser

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

