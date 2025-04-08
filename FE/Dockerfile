# 1단계: 빌드 단계 (Node.js 20)
FROM node:20-alpine AS build
WORKDIR /app

# package.json + package-lock.json 복사
COPY package.json package-lock.json ./
# npm ci로 의존성 설치
RUN npm ci

# 전체 소스 복사 후 빌드
COPY . .
RUN npm run build

# 2단계: Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
