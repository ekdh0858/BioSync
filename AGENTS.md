# CODEX

이 문서는 BioSync 프로젝트에서 AI 코딩 에이전트가 따라야 하는 작업 규칙을 정의한다. 목표는 코드 스타일 일관성, 안정적인 백엔드 구조 유지, 불필요한 코드 생성 방지, 명확한 작업 기준 제공이다.

## 1. 기본 개발 원칙

AI는 항상 아래 원칙을 우선한다.

1. 단순한 코드 우선
2. 가독성 우선
3. 기존 구조 유지
4. 불필요한 코드 생성 금지
5. 작은 변경 단위 유지

코드를 작성하기 전에 반드시 다음을 먼저 점검한다.

- 기존 코드 구조와 일치하는가
- 동일한 역할의 코드가 이미 존재하는가
- 더 단순한 구현 방법이 있는가

## 2. 아키텍처 규칙

Layered Architecture를 사용한다.

```text
Controller
  -> Service
    -> Repository
      -> Domain(Entity)
```

계층별 책임은 다음과 같다.

| 계층 | 책임 |
| --- | --- |
| Controller | HTTP 요청 처리, Request DTO 변환, Response 반환 |
| Service | 비즈니스 로직, 유스케이스 처리, 트랜잭션 관리 |
| Repository | 데이터 접근 |
| Domain(Entity) | 데이터 모델 정의 |

금지 사항은 다음과 같다.

- Controller에 비즈니스 로직 작성
- Repository에 비즈니스 로직 작성

## 3. 코드 생성 규칙

### Entity

- JPA Entity를 사용한다.
- Lombok 사용은 허용한다.
- API 응답으로 Entity를 직접 반환하지 않는다.

### DTO

- Request DTO와 Response DTO를 분리한다.
- Controller는 반드시 DTO를 사용한다.

## 4. API 응답 규칙

모든 API는 아래 공통 응답 구조를 사용한다.

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

적용 규칙은 다음과 같다.

- 성공 시 `success = true`
- 실패 시 `success = false`
- 에러 메시지는 `error` 필드를 사용

## 5. 패키지 구조 규칙

패키지는 기능 기준으로 분리한다. 루트 패키지는 프로젝트 설정에 맞추되 예시는 `com.biosync`를 기준으로 한다.

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

각 기능 패키지는 아래 구조를 기본으로 사용한다.

```text
controller
service
repository
domain
dto
```

## 6. 보안 규칙

- 인증 방식은 JWT Access Token을 사용한다.
- 권한은 `USER`, `ADMIN`으로 구분한다.
- 관리자 API는 `/api/admin` 경로를 사용한다.
- 인증이 필요한 API는 Security 필터를 통해 보호한다.

## 7. 코드 스타일

- 의미 있는 변수 이름을 사용한다.
- 메서드는 하나의 책임만 가진다.
- 클래스는 하나의 역할만 가진다.
- 불필요한 주석은 작성하지 않는다.
- 메서드 이름은 명확하게 작성한다.

메서드 이름 예시는 다음과 같다.

```java
createUser()
registerDevice()
getUserSignals()
calculateDailySummary()
```

## 8. 작업 방식

AI는 아래 순서로 작업한다.

1. 기존 코드 구조 확인
2. 변경 범위 확인
3. 필요한 코드 설계
4. 코드 작성
5. 테스트 코드 작성
6. 코드 정리

## 9. 작업 체크리스트

### 코드 작성 전

- 기존 구조와 일치하는가
- DTO 분리가 되어 있는가
- Entity와 API 모델이 분리되어 있는가

### 코드 작성 후

- 불필요한 코드가 제거되었는가
- 메서드 책임이 명확한가
- 예외 처리가 존재하는가
- API 응답 형식이 일관적인가

## 10. 완료 기준

아래 조건을 모두 만족하면 작업 완료로 본다.

- 코드 컴파일 가능
- 아키텍처 규칙 준수
- DTO 사용
- API 응답 구조 준수
- 코드 가독성 유지

## 11. Commit Rules

커밋 메시지는 다음 형식을 따른다.

```text
type: description
```

예시는 다음과 같다.

```text
feat: add device registration API
fix: resolve null pointer in user service
refactor: simplify biosignal processing logic
test: add unit test for alert service
```

타입 정의는 다음과 같다.

| 타입 | 의미 |
| --- | --- |
| feat | 새로운 기능 |
| fix | 버그 수정 |
| refactor | 코드 구조 개선 |
| test | 테스트 추가 |
| docs | 문서 수정 |
| chore | 기타 설정 변경 |

## 12. Branch Rules

브랜치 네이밍 규칙은 다음과 같다.

```text
feature/{기능명}
fix/{버그명}
refactor/{리팩토링}
```

예시는 다음과 같다.

```text
feature/device-api
fix/login-null-error
refactor/service-structure
```

`main` 브랜치는 항상 배포 가능한 상태를 유지한다.

## 13. Testing Rules

Service 레이어 테스트를 기본으로 작성한다.

테스트 대상은 다음과 같다.

- 인증 로직
- 생체신호 업로드
- Summary 계산
- Alert 탐지
- 관리자 조회 API

테스트 원칙은 다음과 같다.

- 비즈니스 로직 중심 테스트
- 외부 의존성 최소화
- 명확한 테스트 이름 사용

## 14. Logging Rules

아래 상황에서는 로그를 반드시 기록한다.

- 사용자 로그인
- 생체신호 업로드
- 관리자 액션
- 시스템 에러

로그 레벨 기준은 다음과 같다.

| 레벨 | 용도 |
| --- | --- |
| INFO | 주요 사용자 액션 |
| WARN | 비정상 데이터 |
| ERROR | 시스템 예외 |

로그 메시지에는 아래 정보를 포함한다.

- `userId`
- `requestId`
- `action`
