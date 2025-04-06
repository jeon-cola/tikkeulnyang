# 1단계: Node.js 환경에서 Vite 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json 및 package-lock.json 복사 (이 단계는 캐시로 활용됨)
COPY package.json package-lock.json ./

# 의존성 설치 (package-lock.json이 변경되지 않으면 캐시 사용)
RUN npm ci

# 전체 소스 복사 후 빌드
COPY . .
RUN npm run build

# 2단계: Nginx를 이용해 정적 파일 제공
FROM nginx:alpine

# 빌드된 'dist' 폴더를 Nginx의 html 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
