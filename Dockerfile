# 베이스 이미지: OpenJDK 17 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 실행 명령어
ENTRYPOINT ["java", "-jar", "post-service.jar", "--spring.profiles.active=prod"]