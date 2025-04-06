# 1단계: Node.js 환경에서 Vite 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json 및 package-lock.json 복사 (캐시 활용을 위해)
COPY package.json package-lock.json ./

# 의존성 설치 시 optional dependencies 건너뛰기
RUN npm ci --no-optional

# 전체 소스 복사 후 빌드
COPY . .
RUN npm run build

# 2단계: Nginx를 이용해 정적 파일 제공
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
