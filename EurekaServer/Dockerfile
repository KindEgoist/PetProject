FROM maven:3.9.9-eclipse-temurin-24 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests

FROM eclipse-temurin:24-jre

WORKDIR /app

RUN addgroup --system app && adduser --system --ingroup app app
USER app

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8761

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=native

HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
