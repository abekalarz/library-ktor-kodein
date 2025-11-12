FROM eclipse-temurin:21-jre
COPY build/libs/library-ktor-kodein-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
