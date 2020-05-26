FROM openjdk:8
WORKDIR /
COPY /build/libs/*.war voting-app-service.war
ENTRYPOINT ["java", "-jar", "voting-app-service.war"]