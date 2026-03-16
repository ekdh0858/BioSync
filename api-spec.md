# API Specification

이 문서는 BioSync MVP 백엔드 구현을 위한 API 계약을 정의한다. 모든 예시는 `2026-03-16` 기준 문서 버전이며, 시간 값은 ISO 8601 UTC 형식을 기본으로 사용한다.

## 1. 공통 규칙

### Base URL

```text
/api
```

### 공통 응답 형식

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

### 공통 에러 형식

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_INPUT",
    "message": "email must be a valid format"
  }
}
```

### 공통 헤더

| 헤더 | 필수 | 설명 |
| --- | --- | --- |
| `Authorization: Bearer {token}` | 인증 API 제외 | JWT Access Token |
| `X-Request-Id` | 권장 | 추적용 요청 식별자 |
| `Content-Type: application/json` | JSON 요청 시 | 요청 본문 타입 |

### 공통 에러 코드

| 코드 | HTTP 상태 | 설명 |
| --- | --- | --- |
| `INVALID_INPUT` | 400 | 필수 필드 누락, 형식 오류 |
| `UNAUTHORIZED` | 401 | 인증 실패 |
| `FORBIDDEN` | 403 | 권한 부족 |
| `NOT_FOUND` | 404 | 대상 리소스 없음 |
| `CONFLICT` | 409 | 중복 등록, 상태 충돌 |
| `UNPROCESSABLE_SIGNAL` | 422 | 생체신호 검증 실패 |
| `INTERNAL_ERROR` | 500 | 서버 내부 오류 |

## 2. 인증 API

### 2.1 회원가입

```text
POST /api/auth/signup
```

Request:

```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "name": "Taehun",
  "birthDate": "1995-10-10"
}
```

Validation:

- `email`: 필수, 이메일 형식, 255자 이하
- `password`: 필수, 8자 이상 20자 이하, 영문 대소문자, 숫자, 특수문자 포함
- `name`: 필수, 2자 이상 50자 이하
- `birthDate`: 선택, `yyyy-MM-dd`

Success `201 Created`:

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "Taehun",
    "role": "USER"
  },
  "error": null
}
```

### 2.2 로그인

```text
POST /api/auth/login
```

Request:

```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "accessToken": "jwt-access-token",
    "refreshToken": "jwt-refresh-token",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 1,
      "email": "user@example.com",
      "name": "Taehun",
      "role": "USER"
    }
  },
  "error": null
}
```

### 2.3 토큰 재발급

```text
POST /api/auth/refresh
```

Request:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "accessToken": "new-access-token",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "error": null
}
```

### 2.4 로그아웃

```text
POST /api/auth/logout
```

Request:

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "message": "logged out"
  },
  "error": null
}
```

## 3. 사용자 API

### 3.1 내 정보 조회

```text
GET /api/users/me
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "name": "Taehun",
    "birthDate": "1995-10-10",
    "role": "USER",
    "createdAt": "2026-03-16T00:00:00Z"
  },
  "error": null
}
```

## 4. 디바이스 API

### 4.1 디바이스 등록

```text
POST /api/devices
```

Request:

```json
{
  "deviceCode": "GALAXY-WATCH-6-001",
  "deviceName": "Galaxy Watch 6",
  "manufacturer": "Samsung",
  "model": "SM-R940",
  "pairedAt": "2026-03-16T07:00:00Z"
}
```

Validation:

- `deviceCode`: 필수, 고유값, 100자 이하
- `deviceName`: 필수, 100자 이하
- `manufacturer`: 선택, 100자 이하
- `model`: 선택, 100자 이하
- `pairedAt`: 선택, ISO 8601 UTC

Success `201 Created`:

```json
{
  "success": true,
  "data": {
    "deviceId": 10,
    "deviceCode": "GALAXY-WATCH-6-001",
    "deviceName": "Galaxy Watch 6",
    "manufacturer": "Samsung",
    "model": "SM-R940",
    "status": "ACTIVE",
    "pairedAt": "2026-03-16T07:00:00Z"
  },
  "error": null
}
```

### 4.2 내 디바이스 목록 조회

```text
GET /api/devices
```

Success `200 OK`:

```json
{
  "success": true,
  "data": [
    {
      "deviceId": 10,
      "deviceCode": "GALAXY-WATCH-6-001",
      "deviceName": "Galaxy Watch 6",
      "manufacturer": "Samsung",
      "model": "SM-R940",
      "status": "ACTIVE",
      "pairedAt": "2026-03-16T07:00:00Z"
    }
  ],
  "error": null
}
```

## 5. 생체신호 API

### 5.1 생체신호 배치 업로드

```text
POST /api/biosignals/batch
```

Request:

```json
{
  "deviceId": 10,
  "uploadRequestId": "8ff2e4bd-4f34-4f67-9fd3-3b1b3e6f7d10",
  "signals": [
    {
      "recordedAt": "2026-03-16T08:30:00Z",
      "heartRate": 72,
      "stepCount": 120,
      "sleepMinutes": 0,
      "stressLevel": 32
    },
    {
      "recordedAt": "2026-03-16T08:35:00Z",
      "heartRate": 121,
      "stepCount": 80,
      "sleepMinutes": 0,
      "stressLevel": 48
    }
  ]
}
```

Validation:

- `deviceId`: 필수, 등록된 사용자 디바이스여야 함
- `uploadRequestId`: 필수, UUID 또는 64자 이하 고유 요청 식별자
- `signals`: 필수, 1건 이상 1,000건 이하
- `recordedAt`: 필수, ISO 8601 UTC, 미래 시각 불가
- `heartRate`: 선택, 20 이상 240 이하
- `stepCount`: 선택, 0 이상 100,000 이하
- `sleepMinutes`: 선택, 0 이상 1,440 이하
- `stressLevel`: 선택, 0 이상 100 이하
- 각 signal은 최소 1개의 측정값 필드가 있어야 함

Success `201 Created`:

```json
{
  "success": true,
  "data": {
    "deviceId": 10,
    "uploadRequestId": "8ff2e4bd-4f34-4f67-9fd3-3b1b3e6f7d10",
    "receivedCount": 2,
    "savedCount": 2,
    "failedCount": 0,
    "uploadedAt": "2026-03-16T08:40:00Z"
  },
  "error": null
}
```

### 5.2 내 생체신호 조회

```text
GET /api/biosignals?from=2026-03-15T00:00:00Z&to=2026-03-16T23:59:59Z&deviceId=10&page=0&size=100
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "bioSignalId": 1000,
        "deviceId": 10,
        "recordedAt": "2026-03-16T08:30:00Z",
        "heartRate": 72,
        "stepCount": 120,
        "sleepMinutes": 0,
        "stressLevel": 32
      }
    ],
    "page": 0,
    "size": 100,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```

## 6. Summary API

### 6.1 일별 Summary 조회

```text
GET /api/summaries/daily?date=2026-03-16
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "date": "2026-03-16",
    "averageHeartRate": 72,
    "maxHeartRate": 121,
    "minHeartRate": 58,
    "totalSteps": 8420,
    "totalSleepMinutes": 405,
    "averageStressLevel": 35,
    "signalCount": 288
  },
  "error": null
}
```

## 7. Alert API

### 7.1 내 Alert 목록 조회

```text
GET /api/alerts?severity=CRITICAL&date=2026-03-16&page=0&size=20
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "alertId": 501,
        "type": "HIGH_HEART_RATE",
        "severity": "CRITICAL",
        "message": "Heart rate exceeded threshold",
        "occurredAt": "2026-03-16T08:35:00Z",
        "resolved": false
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```

## 8. 관리자 API

모든 관리자 API는 `ADMIN` 권한이 필요하다.

### 8.1 사용자 목록 조회

```text
GET /api/admin/users?page=0&size=20&keyword=tae
```

### 8.2 특정 사용자 상세 조회

```text
GET /api/admin/users/{userId}
```

Response fields:

- 사용자 기본 정보
- 연결 디바이스 목록
- 최근 업로드 시각
- 최근 Alert 요약

### 8.3 특정 사용자 Summary 조회

```text
GET /api/admin/users/{userId}/summaries/daily?date=2026-03-16
```

### 8.4 Alert 목록 조회

```text
GET /api/admin/alerts?severity=CRITICAL&resolved=false&page=0&size=50
```

### 8.5 수집 현황 조회

```text
GET /api/admin/monitoring/ingestion-status?date=2026-03-16
```

Response fields:

- 총 등록 사용자 수
- 당일 업로드 성공 사용자 수
- 당일 업로드 실패 건수
- 미수집 사용자 수
- 위험 Alert 발생 수

### 8.6 Alert 해제

```text
PATCH /api/admin/alerts/{alertId}/resolve
```

Request:

```json
{
  "resolved": true
}
```

Success `200 OK`:

```json
{
  "success": true,
  "data": {
    "alertId": 501,
    "resolved": true,
    "resolvedAt": "2026-03-16T09:00:00Z"
  },
  "error": null
}
```

## 9. 우선 구현 순서

1. `/api/auth/signup`
2. `/api/auth/login`
3. `/api/devices`
4. `/api/biosignals/batch`
5. `/api/summaries/daily`
6. `/api/alerts`
7. `/api/admin/*`
