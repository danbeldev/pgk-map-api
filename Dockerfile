# Этап сборки приложения
FROM bellsoft/liberica-openjdk-alpine:21.0.4 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.gradle chmod +x gradlew && ./gradlew clean build -x test

# Этап извлечения слоев приложения
FROM bellsoft/liberica-openjre-alpine:21.0.4 AS layers
WORKDIR /application
COPY --from=builder /application/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Финальный образ
FROM bellsoft/liberica-openjre-alpine:21.0.4

# Настройка переменных окружения
ENV FIELD_DIR=/app/resources \
    TZ=Europe/Moscow

# Создаем директорию для файлов и назначаем права
RUN adduser -S spring-user && \
    mkdir -p ${FIELD_DIR} && \
    chown -R spring-user:spring-user ${FIELD_DIR} && \
    chmod -R 755 ${FIELD_DIR} && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME ${FIELD_DIR}
USER spring-user

# Копируем слои приложения
COPY --from=layers /application/dependencies/ ./
COPY --from=layers /application/spring-boot-loader/ ./
COPY --from=layers /application/snapshot-dependencies/ ./
COPY --from=layers /application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]