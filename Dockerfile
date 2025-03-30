# Base image로 Amazon Corretto 17 사용
FROM amazoncorretto:17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 의존성 파일만 먼저 복사 (빌드 캐시 최적화)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 실행 권한 추가
RUN chmod +x gradlew

# Gradle 의존성 미리 다운로드 (캐시 활용)
RUN ./gradlew dependencies

# 나머지 소스 코드 복사
COPY . .

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew clean build -x test

# ------------------------ #
# 최종 실행 이미지 생성 #
# ------------------------ #
FROM amazoncorretto:17

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 실행 포트 설정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
