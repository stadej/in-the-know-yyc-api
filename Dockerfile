FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

ARG PORT DB_URL DB_USER

ENV PORT=${PORT}
ENV DB_URL=${DB_URL}
ENV DB_USER=${DB_USER}

COPY target/in-the-know-yyc-api.jar in-the-know-yyc-api.jar
CMD ["java","-jar","in-the-know-yyc-api.jar"]
