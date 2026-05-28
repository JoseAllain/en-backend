FROM gradle:9.4.1-jdk17 as builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . /home/gradle/project
RUN gradle clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE=build/libs/backend-0.0.1-SNAPSHOT.jar
COPY --from=builder /home/gradle/project/${JAR_FILE} /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar /app/app.jar"]
