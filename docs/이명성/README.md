AWS EC2 + Nginx(HTTPS) + Docker + Jenkins 연동
1. 아키텍처 개요

사용자  →  [HTTPS] Nginx  →  [Docker 컨테이너] Jenkins  →  어플리케이션 배포

AWS EC2: 클라우드에서 가상 머신을 호스팅하여 서비스를 실행

Nginx (HTTPS 적용): Reverse Proxy 및 HTTPS 인증서 적용

Docker: 컨테이너 환경을 구성하여 Jenkins 및 애플리케이션 실행

Jenkins: CI/CD 자동화 도구로서 코드 변경 시 자동 빌드 및 배포 수행

2. 구축 방법

2.1 AWS EC2 인스턴스 생성

AWS 콘솔에서 EC2 인스턴스를 생성

보안 그룹에서 80, 443 (HTTP, HTTPS) 및 8080 (Jenkins) 포트 개방

SSH로 EC2에 접속

ssh -i your-key.pem ec2-user@your-ec2-ip

2.2 Docker & Docker Compose 설치

EC2에서 Docker 및 Docker Compose를 설치합니다.

# 패키지 업데이트
sudo yum update -y

# Docker 설치
sudo yum install docker -y

# Docker 서비스 활성화 및 실행
sudo systemctl enable docker
sudo systemctl start docker

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

2.3 Nginx 설치 및 HTTPS 설정

Nginx를 설치하고, Let's Encrypt 인증서를 사용하여 HTTPS를 적용합니다.

sudo amazon-linux-extras enable nginx1
sudo yum install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx

🔹 Let’s Encrypt (SSL 인증서) 적용

sudo yum install -y certbot python-certbot-nginx
sudo certbot --nginx -d your-domain.com

SSL 인증이 완료되면, /etc/nginx/nginx.conf에 Jenkins 리버스 프록시 설정을 추가합니다.

server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# Nginx 설정 테스트 및 적용
sudo nginx -t
sudo systemctl restart nginx

2.4 Jenkins 컨테이너 실행

sudo docker run -d \
  --name jenkins \
  --user root \
  -p 8080:8080 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

초기 비밀번호 확인 후 Jenkins 설정을 진행합니다.

sudo cat /var/jenkins_home/secrets/initialAdminPassword

2.5 CI/CD 파이프라인 구축
GitHub 연동: GitHub Webhook을 활용하여 자동 빌드 트리거

Docker Build & Run: Jenkins에서 GitHub 코드 변경 감지 후 Docker 이미지를 빌드하고 실행

배포 자동화: Jenkins Pipeline을 통해 배포 자동화 구성

🔹 Jenkins Pipeline 예제

pipeline {
    agent any
    stages {
        stage('Clone Repository') {
            steps {
                git 'https://github.com/your-repository.git'
            }
        }
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t my-app .'
            }
        }
        stage('Run Container') {
            steps {
                sh 'docker run -d -p 80:80 my-app'
            }
        }
    }
}


