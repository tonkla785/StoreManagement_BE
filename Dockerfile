# ใช้ Java 21 เป็น base image
FROM openjdk:21-jdk-slim

# ตั้ง working directory
WORKDIR /app

# คัดลอกไฟล์ .jar จาก target
COPY target/*.jar app.jar

# เปิดพอร์ต 8080
EXPOSE 8080

# คำสั่งเริ่มรันแอป
ENTRYPOINT ["java", "-jar", "app.jar"]
