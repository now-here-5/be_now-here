# 1. JDK 17 베이스 이미지 사용 (빌드 단계)
FROM eclipse-temurin:17-jdk-alpine AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 프로젝트의 Gradle 파일과 소스 코드 복사 (종속성 캐싱을 위해 먼저 복사)
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Gradlew 파일에 실행 권한을 추가
RUN chmod +x gradlew

# Gradle 캐시를 활성화하는 설정 파일 추가
COPY gradle.properties .

# 4. 종속성 캐싱을 위해 Gradle의 의존성만 먼저 다운로드
RUN ./gradlew --no-daemon build --refresh-dependencies || return 0

# 5. 소스 코드 복사
COPY src ./src

# 6. Gradle 빌드를 실행하여 JAR 파일 생성 (테스트를 건너뜀)
RUN ./gradlew clean build -x test --no-daemon --stacktrace

# 7. 런타임 이미지 설정 (최종 단계)
FROM eclipse-temurin:17-jre-alpine

# 8. JAR 파일 복사 (정확한 JAR 이름으로)
COPY --from=build /app/build/libs/*.jar app.jar

# 9. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]