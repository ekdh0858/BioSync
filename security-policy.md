# Security Policy

이 문서는 BioSync MVP의 인증, 권한, 토큰, 비밀번호 정책 기준을 정의한다.

## 1. 인증 방식

- 기본 인증은 JWT Access Token + Refresh Token 조합을 사용한다.
- Access Token은 API 인증에 사용한다.
- Refresh Token은 재발급 API에서만 사용한다.

## 2. 토큰 정책

| 항목 | 값 |
| --- | --- |
| Access Token 만료 | 60분 |
| Refresh Token 만료 | 14일 |
| Access Token 저장 위치 | 모바일 앱 메모리 우선 |
| Refresh Token 저장 위치 | 보안 저장소 |
| 서명 알고리즘 | HS256 |

세부 규칙:

- 로그아웃 시 해당 Refresh Token은 즉시 폐기한다.
- Refresh Token 재발급 시 기존 토큰은 폐기하고 새 토큰을 발급한다.
- 만료되거나 폐기된 Refresh Token은 재사용할 수 없다.

## 3. 비밀번호 정책

- 길이 8자 이상 20자 이하
- 영문 대문자 1개 이상 포함
- 영문 소문자 1개 이상 포함
- 숫자 1개 이상 포함
- 특수문자 1개 이상 포함
- BCrypt로 해시 저장

## 4. 권한 모델

| 역할 | 설명 |
| --- | --- |
| `USER` | 본인 데이터 조회와 업로드 수행 |
| `ADMIN` | 관리자 조회와 운영 기능 수행 |

권한 규칙:

- `/api/admin/**` 는 `ADMIN`만 접근 가능
- `/api/auth/**` 는 인증 없이 접근 가능
- `/api/users/me`, `/api/devices/**`, `/api/biosignals/**`, `/api/summaries/**`, `/api/alerts/**` 는 인증된 사용자만 접근 가능
- 일반 사용자는 본인 데이터만 접근 가능

## 5. 관리자 계정 정책

- MVP에서는 관리자 계정 생성 API를 공개하지 않는다.
- 최초 관리자 계정은 시드 데이터 또는 운영자 수동 등록으로 생성한다.
- 관리자 계정 이메일은 일반 사용자 이메일과 중복될 수 없다.

## 6. 계정 보호 정책

- 로그인 실패 5회 연속 시 계정을 `LOCKED` 처리한다.
- 계정 잠금 해제는 관리자 수동 조치로 처리한다.
- 비활성 사용자(`INACTIVE`)는 로그인할 수 없다.

## 7. 입력 보안 기준

- 모든 요청 DTO는 Bean Validation으로 검증한다.
- 문자열 입력은 길이 제한을 둔다.
- 동적 쿼리는 금지하고 JPA 또는 파라미터 바인딩을 사용한다.
- 민감 정보는 로그에 평문으로 남기지 않는다.

## 8. 로깅 보안 기준

- 비밀번호, Access Token, Refresh Token 원문은 로그 금지
- 이메일은 운영 로그에서 필요 시 마스킹 가능
- 보안 이벤트는 `requestId`, `userId`, `action`, `result`를 포함한다

## 9. 추후 보강 항목

- Refresh Token 탈취 탐지
- IP 기반 rate limiting
- 관리자 2단계 인증
