# Data Model

이 문서는 BioSync MVP의 핵심 엔티티, 필드, 관계, 인덱스 기준을 정의한다. 데이터베이스는 PostgreSQL 기준으로 설계한다.

## 1. 설계 원칙

- 모든 테이블은 `id`, `created_at`, `updated_at`를 가진다.
- 시각 컬럼은 `timestamp with time zone`을 사용한다.
- 날짜 기준 집계는 사용자 로컬 날짜가 아니라 `Asia/Seoul` 기준 날짜를 사용한다.
- 삭제는 물리 삭제보다 상태값 기반 비활성화를 우선한다.

## 2. 엔티티 목록

- `users`
- `devices`
- `biosignals`
- `daily_summaries`
- `alerts`
- `refresh_tokens`

## 3. 테이블 정의

### 3.1 users

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | 사용자 ID |
| `email` | varchar(255) | unique, not null | 로그인 이메일 |
| `password_hash` | varchar(255) | not null | BCrypt 해시 |
| `name` | varchar(50) | not null | 사용자 이름 |
| `birth_date` | date | null | 생년월일 |
| `role` | varchar(20) | not null | `USER`, `ADMIN` |
| `status` | varchar(20) | not null | `ACTIVE`, `INACTIVE`, `LOCKED` |
| `last_login_at` | timestamptz | null | 마지막 로그인 시각 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Indexes:

- `uk_users_email (email)`
- `idx_users_role_status (role, status)`

### 3.2 devices

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | 디바이스 ID |
| `user_id` | bigint | FK, not null | 소유 사용자 |
| `device_code` | varchar(100) | unique, not null | 기기 식별 코드 |
| `device_name` | varchar(100) | not null | 표시 이름 |
| `manufacturer` | varchar(100) | null | 제조사 |
| `model` | varchar(100) | null | 모델명 |
| `status` | varchar(20) | not null | `ACTIVE`, `INACTIVE`, `DISCONNECTED` |
| `paired_at` | timestamptz | null | 연결 시각 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Indexes:

- `uk_devices_device_code (device_code)`
- `idx_devices_user_status (user_id, status)`

### 3.3 biosignals

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | 생체신호 ID |
| `user_id` | bigint | FK, not null | 사용자 ID |
| `device_id` | bigint | FK, not null | 디바이스 ID |
| `recorded_at` | timestamptz | not null | 측정 시각 |
| `recorded_date_kr` | date | not null | `Asia/Seoul` 기준 날짜 |
| `heart_rate` | integer | null | 심박수 |
| `step_count` | integer | null | 구간 걸음 수 |
| `sleep_minutes` | integer | null | 구간 수면 시간 |
| `stress_level` | integer | null | 스트레스 지수 |
| `upload_request_id` | varchar(64) | null | 업로드 요청 식별자 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Rules:

- `heart_rate`는 20 이상 240 이하
- `step_count`는 0 이상
- `sleep_minutes`는 0 이상 1440 이하
- `stress_level`는 0 이상 100 이하
- 측정값 4개 중 최소 1개는 존재해야 한다

Indexes:

- `idx_biosignals_user_recorded_at (user_id, recorded_at desc)`
- `idx_biosignals_device_recorded_at (device_id, recorded_at desc)`
- `idx_biosignals_recorded_date_kr (recorded_date_kr)`
- `uk_biosignals_device_recorded_at_request (device_id, recorded_at, upload_request_id)`

### 3.4 daily_summaries

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | Summary ID |
| `user_id` | bigint | FK, not null | 사용자 ID |
| `summary_date` | date | not null | `Asia/Seoul` 기준 날짜 |
| `average_heart_rate` | integer | null | 평균 심박수 |
| `max_heart_rate` | integer | null | 최대 심박수 |
| `min_heart_rate` | integer | null | 최소 심박수 |
| `total_steps` | integer | not null | 총 걸음 수 |
| `total_sleep_minutes` | integer | not null | 총 수면 시간 |
| `average_stress_level` | integer | null | 평균 스트레스 |
| `signal_count` | integer | not null | 집계 대상 신호 수 |
| `aggregated_at` | timestamptz | not null | 집계 완료 시각 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Indexes:

- `uk_daily_summaries_user_date (user_id, summary_date)`

### 3.5 alerts

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | Alert ID |
| `user_id` | bigint | FK, not null | 사용자 ID |
| `device_id` | bigint | FK, null | 디바이스 ID |
| `biosignal_id` | bigint | FK, null | 발생 원본 데이터 |
| `type` | varchar(50) | not null | Alert 유형 |
| `severity` | varchar(20) | not null | `INFO`, `WARNING`, `CRITICAL` |
| `message` | varchar(255) | not null | 사용자 표시 문구 |
| `occurred_at` | timestamptz | not null | 발생 시각 |
| `resolved` | boolean | not null | 해결 여부 |
| `resolved_at` | timestamptz | null | 해결 시각 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Indexes:

- `idx_alerts_user_occurred_at (user_id, occurred_at desc)`
- `idx_alerts_severity_resolved (severity, resolved)`

### 3.6 refresh_tokens

| 필드 | 타입 | 제약 | 설명 |
| --- | --- | --- | --- |
| `id` | bigint | PK | 토큰 ID |
| `user_id` | bigint | FK, not null | 사용자 ID |
| `token` | varchar(512) | unique, not null | Refresh Token |
| `expires_at` | timestamptz | not null | 만료 시각 |
| `revoked` | boolean | not null | 폐기 여부 |
| `created_at` | timestamptz | not null | 생성 시각 |
| `updated_at` | timestamptz | not null | 수정 시각 |

Indexes:

- `uk_refresh_tokens_token (token)`
- `idx_refresh_tokens_user_revoked (user_id, revoked)`

## 4. 엔티티 관계

```text
users 1 --- N devices
users 1 --- N biosignals
devices 1 --- N biosignals
users 1 --- N daily_summaries
users 1 --- N alerts
devices 1 --- N alerts
biosignals 1 --- N alerts
users 1 --- N refresh_tokens
```

## 5. JPA 매핑 기준

- `users` -> `User`
- `devices` -> `Device`
- `biosignals` -> `BioSignal`
- `daily_summaries` -> `DailySummary`
- `alerts` -> `Alert`
- `refresh_tokens` -> `RefreshToken`

Enum 기준:

- `Role`: `USER`, `ADMIN`
- `UserStatus`: `ACTIVE`, `INACTIVE`, `LOCKED`
- `DeviceStatus`: `ACTIVE`, `INACTIVE`, `DISCONNECTED`
- `AlertSeverity`: `INFO`, `WARNING`, `CRITICAL`
- `AlertType`: `HIGH_HEART_RATE`, `LOW_ACTIVITY`, `UPLOAD_MISSING`

## 6. 초기 구현 메모

- `biosignals`는 원본 이력 보존이 목적이므로 수정 API를 두지 않는다.
- `daily_summaries`는 재집계 가능성을 고려해 upsert 방식으로 관리한다.
- `alerts`는 MVP에서 soft resolve만 지원한다.
