# BioSync

BioSync는 웨어러블 기기에서 수집한 생체신호 데이터를 저장, 분석, 조회하기 위한 백엔드 중심 프로젝트다. 이 저장소는 현재 제품 요구사항, 개발 계획, 디자인 기준, 와이어프레임, 에이전트 작업 규칙을 문서로 정리한 상태다.

## 문서 구성

| 문서 | 목적 |
| --- | --- |
| [prd.md](prd.md) | 제품 목표, 사용자, 핵심 기능, 요구사항 정의 |
| [plan.md](plan.md) | 기술 스택, 개발 단계, 마일스톤, 완료 기준 정리 |
| [api-spec.md](api-spec.md) | 엔드포인트, DTO, 검증 규칙, 에러 코드 정의 |
| [data-model.md](data-model.md) | 엔티티 필드, 관계, 인덱스, 시간 기준 정의 |
| [analysis-rules.md](analysis-rules.md) | Summary 계산식과 Alert 생성 규칙 정의 |
| [security-policy.md](security-policy.md) | JWT, 권한, 비밀번호, 관리자 계정 정책 정의 |
| [operations.md](operations.md) | 환경 변수, 로깅, 보존 정책, 운영 체크리스트 정리 |
| [design-system.md](design-system.md) | UI 톤앤매너, 타이포그래피, 컬러, 컴포넌트 원칙 |
| [wireframe.md](wireframe.md) | 주요 화면의 정보 구조와 텍스트 와이어프레임 |
| [AGENTS.md](AGENTS.md) | AI 코딩 에이전트 작업 규칙과 체크리스트 |

## 제품 요약

- 사용자 앱과 관리자 콘솔을 분리한 구조를 전제로 한다.
- 생체신호 업로드는 배치 업로드를 기본 방식으로 사용한다.
- 분석 결과는 일별 요약과 이상징후 탐지를 중심으로 제공한다.
- 인증은 JWT Access Token과 Refresh Token 기반으로 설계한다.

## 공통 원칙

- 백엔드는 Layered Architecture를 유지한다.
- Controller는 DTO만 다루고 Entity를 직접 반환하지 않는다.
- 모든 API 응답은 공통 포맷을 사용한다.

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

## 권장 문서 확인 순서

1. [prd.md](prd.md)
2. [plan.md](plan.md)
3. [api-spec.md](api-spec.md)
4. [data-model.md](data-model.md)
5. [analysis-rules.md](analysis-rules.md)
6. [security-policy.md](security-policy.md)
7. [operations.md](operations.md)
8. [design-system.md](design-system.md)
9. [wireframe.md](wireframe.md)
10. [AGENTS.md](AGENTS.md)
