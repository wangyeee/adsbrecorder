FROM openjdk:12

VOLUME /tmp
ARG SERVER_PORT
EXPOSE ${SERVER_PORT}
ARG JAR_FILE
ADD /target/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]
