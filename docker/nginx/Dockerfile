# ============================================
# Phase 11.3: Admin 前端 + Nginx Dockerfile
# 构建命令：
#   docker build -t inspection-nginx -f docker/nginx/Dockerfile .
# ============================================

# ---- 阶段 1：Node 构建 Vue3 Admin ----
FROM node:20-alpine AS builder
WORKDIR /app

# 复制依赖文件并安装
COPY admin/package*.json ./
RUN npm ci

# 复制源码并构建
COPY admin/ .
RUN npm run build

# ---- 阶段 2：Nginx 运行 ----
FROM nginx:1.25-alpine

# 复制 Nginx 配置
COPY docker/nginx/nginx.conf /etc/nginx/conf.d/default.conf

# 复制构建产物到 Nginx 静态目录
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]