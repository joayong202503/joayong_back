
# base image로 jdk 17버전 지정
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 모든 소스코드 복사
COPY . /app

# 애플리케이션 빌드
RUN ./gradlew clean build -x test

# JAR_FILE 변수 생성
ARG JAR_FILE=build/libs/skillswap-0.0.1-SNAPSHOT.jar

# 빌드된 JAR파일을 복사해서 app.jar로 이름 변경
COPY ${JAR_FILE} app.jar

# 커맨드 실행
ENTRYPOINT ["java","-jar","app.jar"]
