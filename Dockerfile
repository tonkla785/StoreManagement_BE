# ใช้ Java 21 แบบชัดเจนที่ Render pull ได้
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
