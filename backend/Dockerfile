# Estágio 1: build da aplicação
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Define o diretório de trabalho no container
WORKDIR /app

# Copia os arquivos de build para dentro da imagem
COPY pom.xml .
COPY src ./src

# Realiza o build da aplicação sem rodar os testes
RUN mvn clean package -DskipTests


# Estágio 2: imagem final de produção
FROM eclipse-temurin:21-jre

# Define o diretório de trabalho no container final
WORKDIR /app

# Copia o JAR gerado do estágio de build para a imagem final
COPY --from=build /app/target/*.jar app.jar

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
