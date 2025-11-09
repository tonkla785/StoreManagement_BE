# Stage 1: Build the JAR
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu AS builder

WORKDIR /app

# ติดตั้ง Maven
RUN apt-get update && apt-get install -y maven git && rm -rf /var/lib/apt/lists/*

# คัดลอกไฟล์ source code
COPY . .

# Build JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

# คัดลอก JAR จาก stage builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
