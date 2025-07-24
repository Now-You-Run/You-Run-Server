# --- STAGE 1: The "Builder" ---
# Gradle과 JDK가 설치된 이미지를 빌드 환경으로 사용합니다.
FROM gradle:jdk17 as builder

# 소스 코드를 컨테이너 안으로 복사합니다.
WORKDIR /home/gradle/src
COPY . .
# 여기서 Spring Boot 애플리케이션을 빌드합니다! (.jar 파일 생성)
# 이 명령은 GitHub Actions 러너가 아닌, 이 'builder' 컨테이너 내부에서 실행됩니다.
RUN gradle build -x test --no-daemon


# --- STAGE 2: The "Runner" ---
# 실제 운영 환경에서 사용할 가벼운 이미지를 기반으로 시작합니다.
FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# builder 스테이지에서 생성된 .jar 파일만 최종 이미지로 복사합니다.
# 소스 코드, Gradle 등 빌드에만 필요했던 것들은 모두 버려집니다.
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java","-jar","/app.jar"]
