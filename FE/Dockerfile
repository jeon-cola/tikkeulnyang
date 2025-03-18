# 1단계: Node.js 환경에서 프론트엔드 빌드
FROM node:18-alpine AS build
WORKDIR /app

# package.json과 package-lock.json 복사 후 의존성 설치
COPY package.json package-lock.json ./
RUN npm install

# 모든 소스 코드 복사 후 빌드 실행
COPY . .
RUN npm run build

# 2단계: Nginx를 사용하여 정적 파일 제공
FROM nginx:alpine

# 빌드된 파일을 Nginx 기본 HTML 디렉토리로 복사
COPY --from=build /app/build /usr/share/nginx/html

# 컨테이너가 실행될 때 열릴 포트 설정
EXPOSE 3000

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
