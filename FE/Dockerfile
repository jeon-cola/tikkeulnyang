# 1단계: Node.js 환경에서 프론트엔드 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package.json package-lock.json ./

# 기존 package-lock.json 제거 후, npm install 및 (추가로 필요한 npm update 등) 실행
RUN rm -f package-lock.json && npm install && npm update react-calendar

# 나머지 소스코드 복사 후 빌드 실행
COPY . .
RUN npm run build

# 2단계: Nginx를 사용하여 정적 파일 제공
FROM nginx:alpine
# 빌드된 파일(dist)을 Nginx 기본 HTML 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

# (옵션) 커스텀 Nginx 설정 파일이 있다면 아래처럼 교체
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
