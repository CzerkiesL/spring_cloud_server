############################################################
##                   BUILD MAVEN                          ##
############################################################

FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml ./pom.xml
COPY config-server/pom.xml ./config-server/pom.xml
COPY eureka-server/pom.xml ./eureka-server/pom.xml
COPY gateway-server/pom.xml ./gateway-server/pom.xml


RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl eureka-server -am dependency:go-offline

COPY eureka-server/src ./eureka-server/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl eureka-server -am clean package -DskipTests


############################################################
##                   IMAGE DOCKER                         ##
############################################################

FROM eclipse-temurin:25-jre

RUN apt-get update \
&& apt-get install -y --no-install-recommends wget \
&& rm -rf /var/lib/apt/lists/*

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

COPY --from=build /build/eureka-server/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=default
EXPOSE 8761
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]