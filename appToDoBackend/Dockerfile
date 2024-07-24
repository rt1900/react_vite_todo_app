##FROM openjdk:21
##WORKDIR /app
##COPY . /app
##EXPOSE 8080
##CMD ["sh", "-c", "sleep 30 && ./mvnw spring-boot:run"]


FROM openjdk:21
WORKDIR /app
COPY . .
RUN ./mvnw clean package
EXPOSE 8080
CMD ["sh", "-c", "sleep 30 && ./mvnw spring-boot:run -Dspring-boot.run.profiles=docker"]
