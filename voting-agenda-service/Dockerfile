FROM openjdk:8
WORKDIR /
COPY /build/libs/*.war voting-agenda-service.war
ENTRYPOINT ["java", "-jar", "voting-agenda-service.war"]