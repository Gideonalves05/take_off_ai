# Use a imagem base do OpenJDK 21
FROM eclipse-temurin:21-jdk AS build

# Defina o diretório de trabalho dentro do container
WORKDIR /app

# Copie o arquivo .jar da sua aplicação para o diretório de trabalho
COPY target/corretor_redacoes.jar /app/corretor_redacoes.jar

# Expõe a porta que o Google Cloud Run usará
EXPOSE 8080

# Define o comando de inicialização da sua aplicação
ENTRYPOINT ["java", "-jar", "/app/corretor-redacoes.jar"]
