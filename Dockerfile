# base image로 jdk 17버전 지정
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 모든 소스코드 복사
COPY . /app

# 애플리케이션 빌드
RUN ./gradlew clean build -x test
