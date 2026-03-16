# Branch Strategy

이 문서는 BioSync 저장소의 기본 브랜치 전략을 정의한다. 현재 저장소는 `main` 과 `develop` 을 기준 브랜치로 사용하며, 실제 작업은 목적별 작업 브랜치에서 진행한다.

## 1. 브랜치 역할

### `main`

- 항상 배포 가능한 안정 브랜치
- 운영 기준 최종 코드만 반영
- 직접 작업 금지

### `develop`

- 다음 개발 작업이 통합되는 기본 개발 브랜치
- 기능 개발 완료 후 우선 머지되는 기준 브랜치
- 로컬 개발 시작 기준 브랜치

### 작업 브랜치

- 실제 기능 구현, 수정, 리팩토링, 문서 작업은 모두 작업 브랜치에서 수행
- 생성 기준 브랜치는 기본적으로 `develop`

## 2. 브랜치 네이밍 규칙

```text
feature/{기능명}
fix/{버그명}
refactor/{리팩토링명}
docs/{문서명}
test/{테스트명}
chore/{작업명}
```

예시:

```text
feature/device-registration
fix/login-null-error
refactor/summary-service
docs/api-spec-update
test/alert-service
chore/dev-config
```

## 3. 기본 작업 흐름

### 새 기능 개발

1. `develop` 최신화
2. `feature/*` 브랜치 생성
3. 작업 및 커밋
4. `develop` 으로 Pull Request 또는 merge
5. 통합 검증 후 `main` 반영

예시:

```powershell
git checkout develop
git pull origin develop
git checkout -b feature/device-registration
```

### 버그 수정

- 일반 개발 중 발견한 버그는 `fix/*` 브랜치로 생성한다.
- 긴급 운영 버그가 아니라면 기본 병합 대상은 `develop` 이다.

### 문서 작업

- 문서 보완은 `docs/*` 브랜치를 사용한다.
- 문서도 코드와 동일하게 `develop` 기준으로 머지한다.

## 4. 머지 규칙

- 작업 브랜치는 직접 `main` 에 머지하지 않는다.
- 기본 흐름은 `task branch -> develop -> main`
- `main` 반영 전에는 최소한 기능 검토와 테스트 확인이 필요하다.

## 5. 커밋 규칙

커밋 메시지는 아래 형식을 사용한다.

```text
type: description
```

예시:

```text
feat: add device registration API
fix: resolve login token validation issue
docs: update branch strategy
```

## 6. 운영 권장 사항

- 한 브랜치에는 하나의 목적만 담는다.
- 너무 큰 작업은 하위 작업 브랜치로 쪼갠다.
- `develop` 이 오래 밀리지 않도록 자주 동기화한다.
- 머지 전에는 충돌을 먼저 정리하고 푸시한다.

## 7. 현재 적용 상태

- 기본 안정 브랜치: `main`
- 기본 개발 브랜치: `develop`
- 새 작업은 `develop` 에서 분기
