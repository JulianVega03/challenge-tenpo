FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy dependency descriptors first — Docker caches this layer until build.gradle changes
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle/
RUN ./gradlew dependencies --no-daemon --configuration runtimeClasspath

# Copy source and build
COPY src ./src/
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/build/libs/*.jar app.jar
RUN chown spring:spring app.jar

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
