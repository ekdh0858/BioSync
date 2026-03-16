# CODEX

이 파일은 AI 코딩 에이전트(Codex, Cursor, Copilot 등)가
이 프로젝트에서 코드를 작성할 때 반드시 따라야 하는 규칙을 정의한다.

목표
- 일관된 코드 스타일 유지
- 안정적인 백엔드 구조 유지
- 불필요한 코드 생성 방지
- 명확한 작업 체크리스트 제공

--------------------------------------------------
# 1. 기본 개발 원칙

AI는 항상 다음 원칙을 따른다.

1. 단순한 코드 우선
2. 가독성 우선
3. 기존 구조 유지
4. 불필요한 코드 생성 금지
5. 작은 변경 단위 유지

항상 다음 질문을 먼저 확인한다.

- 기존 코드 구조와 일치하는가
- 동일한 역할의 코드가 이미 존재하는가
- 더 단순한 구현 방법이 있는가

--------------------------------------------------
# 2. 아키텍처 규칙

Layered Architecture 사용

Controller
→ Service
→ Repository
→ Domain(Entity)

각 계층 책임

Controller
- HTTP 요청 처리
- Request DTO 변환
- Response 반환

Service
- 비즈니스 로직
- 유스케이스 처리
- 트랜잭션 관리

Repository
- 데이터 접근만 담당

Domain(Entity)
- 데이터 모델 정의

금지
- Controller에 비즈니스 로직 작성
- Repository에 비즈니스 로직 작성

--------------------------------------------------
# 3. 코드 생성 규칙

Entity
- JPA Entity 사용
- Lombok 사용 가능
- API 응답으로 직접 반환하지 않는다

DTO

Request DTO
Response DTO

Controller는 반드시 DTO를 사용한다.

--------------------------------------------------
# 4. API 응답 규칙

모든 API는 동일한 응답 구조 사용

{
  "success": true,
  "data": {},
  "error": null
}

규칙

- 성공 시 success = true
- 실패 시 success = false
- 에러 메시지는 error 필드 사용

--------------------------------------------------
# 5. 패키지 구조 규칙

패키지는 기능 기준으로 분리한다.

com.project

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

각 패키지 내부 구조

controller
service
repository
domain
dto

--------------------------------------------------
# 6. 보안 규칙

인증 방식

JWT Access Token

권한

USER
ADMIN

규칙

- 관리자 API는 /api/admin 경로 사용
- 인증이 필요한 API는 Security 필터 사용

--------------------------------------------------
# 7. 코드 스타일

- 의미 있는 변수 이름 사용
- 메서드는 하나의 책임만 가진다
- 클래스는 하나의 역할만 가진다
- 불필요한 주석 작성 금지
- 명확한 메서드 이름 사용

메서드 예시

createUser()
registerDevice()
getUserSignals()
calculateDailySummary()

--------------------------------------------------
# 8. 작업 방식 (AI Workflow)

AI는 다음 순서로 작업한다.

1. 기존 코드 구조 확인
2. 변경 범위 확인
3. 필요한 코드 설계
4. 코드 작성
5. 테스트 코드 작성
6. 코드 정리

--------------------------------------------------
# 9. 작업 체크리스트

코드 작성 전

- 기존 구조와 일치하는가
- DTO 분리가 되어있는가
- Entity와 API 모델이 분리되어 있는가

코드 작성 후

- 불필요한 코드 제거
- 메서드 책임이 명확한가
- 예외 처리가 존재하는가
- API 응답 형식 일관성 유지

--------------------------------------------------
# 10. 완료 기준 (Definition of Done)

다음 조건을 만족하면 작업 완료

- 코드 컴파일 가능
- 아키텍처 규칙 준수
- DTO 사용
- API 응답 구조 준수
- 코드 가독성 유지

--------------------------------------------------
# 11. Commit Rules

커밋 메시지는 다음 규칙을 따른다.

형식

type: description

예시

feat: add device registration API
fix: resolve null pointer in user service
refactor: simplify biosignal processing logic
test: add unit test for alert service

타입

feat     새로운 기능
fix      버그 수정
refactor 코드 구조 개선
test     테스트 추가
docs     문서 수정
chore    기타 설정 변경

--------------------------------------------------
# 12. Branch Rules

브랜치 네이밍 규칙

feature/{기능명}
fix/{버그명}
refactor/{리팩토링}

예시

feature/device-api
fix/login-null-error
refactor/service-structure

main 브랜치는 항상 배포 가능한 상태 유지

--------------------------------------------------
# 13. Testing Rules

Service 레이어 테스트 작성

테스트 대상

- 인증 로직
- 생체신호 업로드
- summary 계산
- alert 탐지
- 관리자 조회 API

테스트 원칙

- 비즈니스 로직 중심 테스트
- 외부 의존성 최소화
- 명확한 테스트 이름 사용

--------------------------------------------------
# 14. Logging Rules

다음 상황에서는 로그를 반드시 기록한다.

- 사용자 로그인
- 생체신호 업로드
- 관리자 액션
- 시스템 에러

로그 레벨

INFO
- 주요 사용자 액션

WARN
- 비정상 데이터

ERROR
- 시스템 예외

로그 메시지는 다음 정보를 포함한다.

- userId
- requestId
- action
