FROM maven:3.8.6-amazoncorretto-17 as build
WORKDIR /app

COPY ./pom.xml ./pom.xml

RUN mvn dependency:go-offline

COPY . .

RUN mvn -Dmaven.test.skip -Dquarkus.profile=docker clean package

FROM amazoncorretto:17.0.10-alpine3.19
WORKDIR /deployments
COPY --from=build /app/target/quarkus-app/lib/ lib/
COPY --from=build /app/target/quarkus-app/*.jar .
COPY --from=build /app/target/quarkus-app/app/ app/
COPY --from=build /app/target/quarkus-app/quarkus/ quarkus/

CMD java -jar /deployments/quarkus-run.jar  