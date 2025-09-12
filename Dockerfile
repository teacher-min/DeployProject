# 멀티 스테이지 (2개 이상의 베이스 이미지 사용)

##### 첫 번째 스테이지
FROM maven:3.8.4-openjdk-11 AS build

# 작업 영역 설정 (이제부터 수행하는 작업은 이미지 내부 /myapp 디렉터리에서 수행)
WORKDIR /myapp

# 현재 디렉터리(Dockerfile이 존재하는 디렉터리) 코드 -> 이미지 내부 /myapp 디렉터리
COPY pom.xml .
COPY src ./src

# 이미지 내부 /myapp 코드를 이용해 빌드 수행 (두 번째 빌드)
# 빌드 결과로 /myapp/target/*.war 생성
# 도커 이미지 빌드 시 이 빌드 결과를 사용
RUN mvn clean package -DskipTests

##### 두 번째 스테이지
FROM tomcat:9.0-jdk11

# 첫 번째 스테이지의 war 파일을 현재 스테이지로 복사하는 COPY
COPY --from=build /myapp/target/*.war /usr/local/tomcat/webapps/deploy.war

# 첫 번째 스테이지를 제거하고, Github runner의 war 파일을 복사하는 경우에 사용하는 COPY
# Github runner의 파일을 COPY 하는 경우 Github runner의 작업 디렉터리를 기준으로 실행
# COPY target/*.war /usr/local/tomcat/webapps/deploy.war

# 포트
EXPOSE 8080