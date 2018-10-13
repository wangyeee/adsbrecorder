FROM openjdk:9

VOLUME /tmp
EXPOSE 8081
ADD /target/adsb-recorder-1.0.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
