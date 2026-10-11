# ==========================================
# Build Stage (Compilação)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache de dependências do Maven
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests -Ddependency-check.skip=true || true

# Copia o código fonte e compila o .jar (pulando testes e NVD no build para deploy rápido)
COPY src ./src
RUN mvn clean package -DskipTests -Ddependency-check.skip=true

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
