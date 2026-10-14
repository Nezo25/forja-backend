# ==========================================
# Build Stage (Compilação)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache de dependências do Maven
COPY pom.xml .
RUN mvn dependency:go-offline -DskipTests -Dmaven.test.skip=true -Ddependency-check.skip=true || true

# Copia o código fonte e compila o .jar (pulando testes e NVD no build para deploy rápido)
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true -Ddependency-check.skip=true

# ==========================================
# Runtime Stage (Execução leve e segura)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Atualiza pacotes do SO para eliminar vulnerabilidades da imagem base (CVEs Alpine)
RUN apk update && apk upgrade --no-cache

# Copia apenas o .jar gerado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Configura usuário não-root para atender às boas práticas de segurança de contêineres
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Expõe a porta que o Render vai escutar
EXPOSE 8080

# Inicia o servidor forçando o profile 'prod' com controle de memória para container de 512MB
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Xmx384m", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
