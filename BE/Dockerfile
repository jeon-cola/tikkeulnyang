FROM openjdk:17-alpine
WORKDIR /app

# 고쳤음 (여러 파일이 아닌, app.jar만 지정)
COPY build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
