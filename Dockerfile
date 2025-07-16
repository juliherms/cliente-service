# syntax=docker/dockerfile:1.4

########################################
# Stage 1: build da aplicação
########################################
FROM maven:3.9.0-openjdk-17-slim AS builder
WORKDIR /app

# Cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build do artefato
COPY src ./src
RUN mvn clean package -DskipTests -B

########################################
# Stage 2: imagem de runtime
########################################
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Criação do usuário não-root e instalação do curl em um único layer
RUN groupadd -r appuser \
 && useradd -r -g appuser appuser \
 && apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# Copia o JAR gerado e ajusta permissões
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser

# Variáveis de ambiente
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Healthcheck do Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Startup da aplicação
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
