# Analysis Rules

이 문서는 Daily Summary와 Alert 생성 규칙을 구현 가능한 수준으로 고정한다. 별도 합의가 생기기 전까지 서버는 이 문서 기준으로 동작한다.

## 1. 시간 기준

- 모든 원본 `recorded_at`은 UTC로 저장한다.
- 일별 집계 날짜는 `Asia/Seoul` 기준으로 계산한다.
- 일별 집계 범위는 해당 날짜 `00:00:00` 부터 `23:59:59.999` 까지다.
- 스케줄 집계는 매일 `00:10 Asia/Seoul`에 전날 데이터를 대상으로 수행한다.
- 사용자가 당일 Summary를 조회하면 필요 시 실시간 재계산을 허용한다.

## 2. Summary 규칙

집계 대상 테이블은 `biosignals`다.

### 2.1 평균 심박수

- `heart_rate` 값이 있는 데이터만 사용한다.
- 소수점 이하는 반올림한다.

### 2.2 최대 심박수

- `heart_rate` 값 중 최대값을 사용한다.

### 2.3 최소 심박수

- `heart_rate` 값 중 최소값을 사용한다.

### 2.4 총 걸음 수

- 하루 동안의 `step_count` 합계를 사용한다.
- 음수 값은 허용하지 않는다.

### 2.5 총 수면 시간

- 하루 동안의 `sleep_minutes` 합계를 사용한다.
- 합계는 최대 1,440분으로 제한한다.

### 2.6 평균 스트레스 지수

- `stress_level` 값이 있는 데이터만 평균 계산에 사용한다.
- 소수점 이하는 반올림한다.

### 2.7 signalCount

- 집계 대상이 된 `biosignals` row 수를 저장한다.

## 3. Alert 생성 규칙

Alert는 배치 업로드 처리 직후와 일별 집계 시점에 생성할 수 있다.

### 3.1 HIGH_HEART_RATE

조건:

- 단일 `biosignal.heart_rate > 120`

심각도:

- `CRITICAL`

메시지:

- `Heart rate exceeded threshold`

중복 처리:

- 동일 사용자에게 10분 이내 같은 타입 Alert가 이미 열려 있으면 신규 생성하지 않는다.

### 3.2 LOW_ACTIVITY

조건:

- 당일 `total_steps`가 직전 3일 평균 걸음 수 대비 50% 이하
- 직전 3일 Summary가 모두 존재해야 한다

심각도:

- `WARNING`

메시지:

- `Daily activity dropped significantly`

생성 시점:

- 일별 Summary 생성 후

중복 처리:

- 사용자당 하루 1건만 생성한다.

### 3.3 UPLOAD_MISSING

조건:

- 활성 디바이스가 있는 사용자가 `Asia/Seoul` 기준 하루 동안 업로드를 한 번도 하지 않음

심각도:

- `WARNING`

메시지:

- `No biosignal uploaded for the day`

생성 시점:

- 매일 `23:30 Asia/Seoul`

중복 처리:

- 사용자당 하루 1건만 생성한다.

## 4. Alert 해제 규칙

- MVP에서는 자동 해제를 지원하지 않는다.
- 관리자가 확인 후 수동으로 `resolved = true`로 변경할 수 있다.
- 사용자는 Alert를 읽기 전용으로 조회한다.

## 5. 재처리 규칙

- 업로드 중 실패한 개별 signal은 전체 요청 실패 대신 건별 실패로 집계한다.
- Summary 재집계 시 기존 Summary는 같은 날짜 기준으로 upsert 한다.
- 재집계로 인해 조건이 사라져도 기존 Alert는 삭제하지 않는다.

## 6. 향후 확장 후보

- 안정 시 심박수 기반 개인화 임계값
- 장기 추세 기반 수면 품질 Alert
- 여러 지표를 조합한 종합 위험 점수
