############################################################
##                   BUILD MAVEN                          ##
############################################################

FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml ./pom.xml
COPY config-server/pom.xml ./config-server/pom.xml
COPY eureka-server/pom.xml ./eureka-server/pom.xml
COPY gateway-server/pom.xml ./gateway-server/pom.xml

<<<<<<< HEAD

=======
>>>>>>> 4446aeb (second commit)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl config-server -am dependency:go-offline

COPY config-server/src ./config-server/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl config-server -am clean package -DskipTests

    
############################################################
##                   IMAGE DOCKER                         ##
############################################################

FROM eclipse-temurin:25-jre

RUN apt-get update \
&& apt-get install -y --no-install-recommends wget \
&& rm -rf /var/lib/apt/lists/*

RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

COPY --from=build /build/config-server/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=default
EXPOSE 9999
USER appuser

ENTRYPOINT ["java", "-jar", "/app/app.jar"]