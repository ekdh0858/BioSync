# Development Plan

## 1. 목표

BioSync의 초기 개발 목표는 생체신호 수집부터 일별 요약과 이상징후 탐지, 관리자 조회 기능까지 이어지는 최소 기능 제품을 안정적으로 구축하는 것이다.

## 2. 기술 스택

| 영역 | 선택 |
| --- | --- |
| Backend | Java 17, Spring Boot 3.x |
| Security | Spring Security, JWT |
| Data Access | Spring Data JPA |
| Database | PostgreSQL |
| Cache | Redis |
| Infra | Docker, Docker Compose |
| API 문서 | OpenAPI (Swagger) |
| Test | JUnit 5, Mockito |

## 3. 아키텍처 기준

Layered Architecture를 사용한다.

```text
Controller
  -> Service
    -> Repository
      -> Domain
```

루트 패키지 예시는 다음과 같다.

```text
com.biosync
  auth
  user
  device
  biosignal
  summary
  alert
  admin
  common
  config
  security
```

## 4. 개발 단계

### Phase 1. 인증 기반 구축

목표: 사용자 인증과 공통 응답 구조를 먼저 안정화한다.

- 회원가입 API
- 로그인 API
- JWT 발급 및 검증
- 기본 예외 처리
- 공통 API 응답 래퍼

### Phase 2. 디바이스 관리

목표: 사용자와 웨어러블 기기 연결 관계를 관리한다.

- 디바이스 등록 API
- 사용자별 디바이스 조회 API
- 중복 등록 방지 로직

### Phase 3. 생체신호 수집

목표: 원본 데이터 적재 경로를 정의한다.

- 배치 업로드 API
- 업로드 유효성 검증
- 저장 로그 기록
- 데이터 수집 실패 처리

### Phase 4. 데이터 분석

목표: 사용자에게 의미 있는 일별 건강 요약을 제공한다.

- 일별 Summary 생성
- 평균, 최대, 최소 심박수 계산
- 총 걸음 수 계산
- 총 수면 시간 계산
- 스케줄 기반 집계 작업 정리

### Phase 5. 이상징후 탐지

목표: 기준치를 넘는 데이터를 이벤트로 전환한다.

- Alert 규칙 정의
- Alert 저장 및 조회 API
- 사용자별 이상징후 목록 제공

### Phase 6. 관리자 기능

목표: 운영 관점의 조회와 모니터링 기능을 제공한다.

- 관리자 인증 및 권한 분리
- 사용자 목록 조회
- 특정 사용자 데이터 조회
- Alert 조회
- 수집 현황 대시보드용 API

## 5. 마일스톤 산출물

| 단계 | 핵심 산출물 |
| --- | --- |
| Phase 1 | 인증 모듈, 공통 응답, 예외 처리 |
| Phase 2 | 디바이스 관리 모듈 |
| Phase 3 | 생체신호 업로드 모듈 |
| Phase 4 | Summary 집계 모듈 |
| Phase 5 | Alert 탐지 모듈 |
| Phase 6 | 관리자 조회 모듈 |

## 6. 테스트 전략

- Service 레이어 테스트를 우선 작성한다.
- 인증, 업로드, Summary 계산, Alert 탐지를 핵심 테스트 대상으로 둔다.
- 외부 의존성은 가능한 한 Mock으로 대체한다.
- 비즈니스 규칙이 변경되면 테스트 케이스를 먼저 갱신한다.

## 7. 완료 기준

- 핵심 API가 공통 응답 구조를 사용한다.
- DTO와 Entity가 분리되어 있다.
- 인증과 권한 검증이 적용되어 있다.
- 주요 서비스 로직에 대한 테스트가 존재한다.
- Swagger 또는 OpenAPI 문서가 최신 상태다.

## 8. 주요 용어

| 용어 | 정의 |
| --- | --- |
| BioSignal | 웨어러블 장치에서 수집한 원본 생체신호 데이터 |
| Summary | 일별 기준으로 집계한 사용자 건강 지표 |
| Alert | 이상징후 규칙에 의해 생성된 이벤트 |
| Device | 사용자의 웨어러블 장치 |
