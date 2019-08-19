FROM java:8
EXPOSE 80
ADD /target/authentication-0.0.1-SNAPSHOT.jar authentication-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "authentication-0.0.1-SNAPSHOT.jar"]


