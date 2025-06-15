# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Kopiraj ceo projekat u kontejner
COPY . .

# Omogući izvršavanje mvnw ako postoji
RUN chmod +x mvnw

# Pokreni Maven build, ignoriši testove ako želiš brže
RUN ./mvnw clean package -DskipTests

# ---------- RUN STAGE ----------
FROM openjdk:21-jdk

WORKDIR /app

# Kopiraj izgrađeni JAR iz build stage-a
COPY --from=builder /build/target/warehouse-management-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
