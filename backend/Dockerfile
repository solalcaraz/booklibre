# ==========================================
# ETAPA 1: CONSTRUCCIÓN (El Empaquetado)
# ==========================================
# Usamos una imagen que ya tiene Gradle y Java 21 instalados
FROM gradle:8.8-jdk21 AS build

# Copiamos todo el código a una carpeta dentro del contenedor
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Ejecutamos el empaquetado "Fat JAR"
# Agregamos "-x test" para saltear los tests durante el deploy y hacerlo más rápido
RUN gradle bootJar -x test --no-daemon

# ==========================================
# ETAPA 2: EJECUCIÓN (El Runtime)
# ==========================================
# Usamos una imagen mucho más liviana que solo tiene Java 21 (sin Gradle)
FROM eclipse-temurin:21-jre-jammy

# Exponemos el puerto donde corre Spring Boot
EXPOSE 8080

# Copiamos el "Fat JAR" de la Etapa 1 a esta nueva imagen limpia
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar /app.jar

# Le decimos al contenedor cómo arrancar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]