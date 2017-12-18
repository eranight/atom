FROM openjdk:8-jre-alpine
ENV JAVA_OPTIONS "-Xmx300m"
COPY build/libs/game_server-1.0-SNAPSHOT.jar /services/game_server.jar
EXPOSE 8080
CMD ["java", "-jar", "/services/game_server.jar"]