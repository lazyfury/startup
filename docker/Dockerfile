# Multi-stage build: compile with Maven and run on JRE 17

FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy only pom files first for better layer caching
COPY pom.xml ./
COPY common/pom.xml common/pom.xml
COPY modules/pom.xml modules/pom.xml
COPY run/pom.xml run/pom.xml

# Pre-fetch dependencies
RUN mvn -B -DskipTests dependency:go-offline

# Copy source code and build all modules
COPY . .
RUN mvn -B -DskipTests package


FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Copy the Spring Boot executable jar from run module
COPY --from=builder /workspace/run/target/run-*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]