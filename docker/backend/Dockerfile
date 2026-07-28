# ============================================
# Phase 11.2: Spring Boot Backend Dockerfile
# 构建命令：
#   docker build -t inspection-backend -f docker/backend/Dockerfile ./backend
# ============================================

# ---- 阶段 1：Maven 编译 ----
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# 先复制 pom.xml 和 Maven 镜像配置，利用 Docker 缓存依赖下载
COPY pom.xml .
COPY settings-docker.xml /root/.m2/settings.xml
RUN mvn dependency:go-offline -q

# 复制源码并编译
COPY src ./src
RUN mvn package -DskipTests -q

# ---- 阶段 2：JRE 运行 ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 从构建阶段复制 jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]