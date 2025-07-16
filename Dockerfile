# syntax=docker/dockerfile:1.4

########################################
# Stage 1: Build da aplicação
########################################
FROM maven:3.8.5-openjdk-17-slim as build

# Definir diretório de trabalho
WORKDIR /app

# Copiar pom.xml e resolver dependências antes de copiar o código-fonte
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar o restante do código-fonte e empacotar
COPY src ./src
RUN mvn clean package -DskipTests -B

########################################
# Stage 2: Runtime
########################################
FROM eclipse-temurin:17-jre-jammy AS runtime

WORKDIR /app

# Utilizar usuário não-root para maior segurança
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copiar o jar gerado
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appuser app.jar

# Mudar para o usuário não-root
USER appuser

# Configurar entrypoint
ENTRYPOINT ["java","-jar","/app/app.jar"]
