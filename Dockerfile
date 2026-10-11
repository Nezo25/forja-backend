# ==========================================
# Build Stage (Compilação)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Otimização de cache do Maven (baixa dependências primeiro)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila o .jar (pulando testes no build para ser mais rápido na nuvem)
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Runtime Stage (Execução leve)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que o Render vai escutar
EXPOSE 8080

# Inicia o servidor forçando o profile 'prod'
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
