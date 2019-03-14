# ADSB recorder
An application saves ADSB data from RTL SDR to local database for further analysis.

## Prerequisites
1. You need to setup a RTL SDR device on a Linux machine, the wiki [here](https://osmocom.org/projects/rtl-sdr/wiki/Rtl-sdr) is a good start point. OR run with remote receiver, which is in this [repo](https://github.com/wangyeee/adsbreceiver). If you choose remote receiver, then the JNI library will no longer be needed on the server side.
2. JDK and Maven. This application is developed in openjdk-11, which is the latest version in Debian Stretch.
3. ~~MySQL~~ MariaDB to store recorded data. Create an empty database and update application.properties with connection strings and credentials.

## Build and Run
1. Clone and build the [JNI library](https://github.com/wangyeee/dump1090/tree/jni) which passes data from RTL SDR device to Java objects. And add the path of the .so file to java.library.path.
2. Run `mvn package` to build jar for this application. "adsb-recorder-<version>.jar" will be generated in targer directory.
3. Simply run `java -jar adsb-recorder-<version>.jar` to start this application. Open your web browser and navigate to [localhost:8081](http://localhost:8081/) to view live data.
4. Optionally you can import the pom.xml to your IDE of choice, and run or debug it in an IDE.
