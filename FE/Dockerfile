# 1단계: Node.js 환경에서 Vite 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json + package-lock.json 복사
COPY package.json package-lock.json ./

# (선택) package-lock.json 제거 후 재설치
RUN rm -f package-lock.json && npm install && npm update react-calendar

# 나머지 FE 소스 복사 후 빌드
COPY . .
RUN npm run build

# 2단계: Nginx로 정적 파일 제공
FROM nginx:alpine

# 빌드된 'dist' 폴더를 Nginx html 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

# (옵션) 커스텀 Nginx 설정이 있으면 추가
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

