# 1단계: Node.js 환경에서 Vite 빌드 (Debian 기반 이미지 사용)
FROM node:18-bullseye-slim AS build
WORKDIR /app

# package.json 및 package-lock.json 복사 (캐시 활용)
COPY package.json package-lock.json ./

# 의존성 설치
RUN npm ci

# 누락된 optional dependency 설치 (Rollup의 GNU용 native 모듈)
RUN npm install @rollup/rollup-linux-x64-gnu

# 전체 소스 복사 후 빌드 실행
COPY . .
RUN npm run build

# 2단계: Nginx를 이용해 정적 파일 제공
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
