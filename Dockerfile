FROM openjdk:17-oracle
EXPOSE 8080
ADD target/electronic-store-0.0.1-SNAPSHOT.jar electronic-store.jar
ENTRYPOINT ["java","-jar","/electronic-store.jar"]