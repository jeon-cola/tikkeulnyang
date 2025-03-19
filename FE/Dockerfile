# 1단계: Node.js 환경에서 프론트엔드 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json과 package-lock.json만 먼저 복사하여 캐시 활용
COPY package.json package-lock.json ./
RUN npm install && npm update react-calendar

# 나머지 소스코드 복사 후 빌드 실행
COPY . .
RUN npm run build

# 2단계: Nginx를 사용하여 정적 파일 제공
FROM nginx:alpine
# 빌드된 파일을 Nginx 기본 HTML 디렉토리로 복사
COPY --from=build /app/build /usr/share/nginx/html

# 커스텀 Nginx 설정이 있다면 복사 (옵션)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Nginx 기본 포트 80 사용
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
