# 1. JDK 17 베이스 이미지 사용
FROM eclipse-temurin:17-jdk-alpine as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트의 Gradle 파일과 소스 코드 복사
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# 4. Gradle Wrapper에 실행 권한 부여
RUN chmod +x ./gradlew

# 5. Gradle 빌드를 실행하여 JAR 파일 생성 (테스트를 건너뜀)
RUN ./gradlew clean build -x test --no-daemon

# 5. 런타임 이미지 설정
FROM eclipse-temurin:17-jre-alpine

# 6. JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 7. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
