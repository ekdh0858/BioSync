# Operations Guide

이 문서는 BioSync MVP 운영 기준과 개발 환경 준비 규칙을 정의한다.

## 1. 환경 구분

| 환경 | 목적 |
| --- | --- |
| `local` | 개발자 로컬 개발 |
| `dev` | 통합 개발 테스트 |
| `prod` | 운영 |

규칙:

- 환경별 DB와 Redis는 분리한다.
- 운영 환경 비밀값은 코드 저장소에 커밋하지 않는다.
- 설정 값은 Spring Profile로 구분한다.

## 2. 필수 환경 변수

| 변수 | 설명 |
| --- | --- |
| `DB_URL` | PostgreSQL 연결 문자열 |
| `DB_USERNAME` | DB 사용자 |
| `DB_PASSWORD` | DB 비밀번호 |
| `REDIS_HOST` | Redis 호스트 |
| `REDIS_PORT` | Redis 포트 |
| `JWT_SECRET` | JWT 서명 키 |
| `JWT_ACCESS_EXPIRATION` | Access Token 만료 초 |
| `JWT_REFRESH_EXPIRATION` | Refresh Token 만료 초 |

## 3. 로깅 기준

반드시 로그를 남길 이벤트:

- 사용자 로그인 성공 및 실패
- 생체신호 업로드 요청
- 관리자 조회 액션
- 서버 예외

로그 필드:

- `timestamp`
- `level`
- `requestId`
- `userId`
- `action`
- `result`

## 4. 업로드 운영 규칙

- 배치 업로드 최대 요청 건수는 1,000건이다.
- 요청 크기가 한도를 넘으면 `400 INVALID_INPUT`을 반환한다.
- 개별 signal 실패는 응답의 `failedCount`에 반영한다.
- 동일 요청 재시도는 `upload_request_id`를 기준으로 중복 저장을 방지한다.

## 5. 데이터 보존 기준

| 데이터 | 보존 기준 |
| --- | --- |
| `biosignals` | MVP 기준 2년 |
| `daily_summaries` | 장기 보존 |
| `alerts` | 장기 보존 |
| `refresh_tokens` | 만료 후 30일 내 정리 |
| 애플리케이션 로그 | 90일 |

## 6. 장애 대응 기준

- DB 연결 실패 시 애플리케이션은 readiness 실패 상태를 노출한다.
- Redis 장애가 발생해도 인증 핵심 경로가 완전히 막히지 않도록 설계한다.
- 예외 응답은 내부 스택 트레이스를 외부로 노출하지 않는다.

## 7. 개발 시작 체크리스트

- PostgreSQL 실행 확인
- Redis 실행 확인
- 환경 변수 설정 확인
- Swagger 노출 확인
- 기본 관리자 계정 준비
- 마이그레이션 도구 적용 여부 확인

## 8. 권장 추가 작업

- Flyway 또는 Liquibase 도입
- Actuator 헬스체크 추가
- 구조화 로그 포맷 적용
- Alert 운영 대시보드 지표 정의
