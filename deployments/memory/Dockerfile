FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY ../../../zxf-java-memory/target/zxf-java-memory-1.0-SNAPSHOT.jar my-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xms256M", "-Xmx1024M", "-XshowSettings", "-XX:+PrintFlagsFinal", "-XX:NativeMemoryTracking=detail","-jar", "/app/my-app.jar"]