# 1단계: 빌드 단계 (Node.js 컨테이너 사용)
FROM node:18-alpine AS build
WORKDIR /app

# 의존성 설치를 위한 파일 복사
COPY package.json package-lock.json ./

# 깨끗한 설치: npm ci는 package-lock.json 기반으로 설치하며, 캐시 활용에 좋습니다.
RUN npm ci

# 전체 소스 복사 및 빌드 실행
COPY . .
RUN npm run build

# 2단계: 실행 단계 (Nginx를 사용하여 정적 파일 제공)
FROM nginx:alpine
# 빌드 단계에서 생성한 'dist' 폴더를 Nginx의 기본 정적 파일 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
