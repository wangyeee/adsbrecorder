FROM openjdk:12

VOLUME /tmp
EXPOSE 8082
ADD /target/user-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
