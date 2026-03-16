# Development Plan

## 기술 스택

Backend
- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA

Database
- PostgreSQL

Cache
- Redis

Infra
- Docker
- Docker Compose

API 문서
- OpenAPI (Swagger)

---

## 아키텍처

Layered Architecture

Controller
→ Service
→ Repository
→ Domain

패키지 구조

com.biosync
- auth
- user
- device
- biosignal
- summary
- alert
- admin
- common

---

## 개발 단계

1단계
- 인증 시스템
- 회원가입
- 로그인
- JWT

2단계
- 디바이스 등록
- 디바이스 조회

3단계
- 생체신호 업로드
- batch upload API

4단계
- 데이터 분석
- summary 생성
- alert 탐지

5단계
- 관리자 API
- 사용자 조회
- alert 조회

---

## AI 코드 생성 지시사항

AI는 다음 규칙을 따른다.

- Spring Boot REST API 작성
- Controller / Service / Repository 분리
- DTO와 Entity 분리
- 공통 응답 구조 사용

응답 포맷

{
  "success": true,
  "data": {},
  "error": null
}

---

## 주요 용어

BioSignal
생체신호 원본 데이터

Summary
일별 데이터 집계

Alert
이상징후 이벤트

Device
사용자의 웨어러블 장치
