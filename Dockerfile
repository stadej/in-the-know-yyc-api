FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/in-the-know-yyc-api.jar in-the-know-yyc-api.jar
CMD ["java","-jar","in-the-know-yyc-api.jar"]