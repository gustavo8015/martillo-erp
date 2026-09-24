# Imagen del servicio de Martillo ERP en dos etapas.
# Etapa 1: compila con Maven. Etapa 2: solo el runtime de Java y el jar.
FROM maven:3.9-eclipse-temurin-17 AS construccion
WORKDIR /origen
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests=false

FROM eclipse-temurin:17-jre
WORKDIR /aplicacion
COPY --from=construccion /origen/target/*.jar aplicacion.jar

# El contenedor no corre como root.
RUN useradd --system --create-home martillo
USER martillo

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/aplicacion/aplicacion.jar"]
