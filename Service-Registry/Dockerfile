FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/service-registry.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java","-jar","app.jar"]