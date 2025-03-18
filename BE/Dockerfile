# OpenJDK 17 기반의 경량 컨테이너 이미지 사용
FROM openjdk:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# Gradle로 빌드된 JAR 파일을 복사 (JAR 파일은 build/libs에 생성되어야 함)
COPY build/libs/*.jar app.jar

# 컨테이너에서 사용할 포트 지정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
