# Design System

BioSync의 사용자 앱과 관리자 콘솔은 의료 서비스처럼 무겁지 않으면서도 신뢰감을 주는 방향을 유지한다. 전체 톤은 "명확함, 안정감, 가벼운 건강 관리"를 기준으로 설계한다.

## 1. 디자인 원칙

- 데이터 중심 화면에서도 핵심 수치를 빠르게 읽을 수 있어야 한다.
- 경고와 일반 정보는 색상과 아이콘 체계로 명확히 구분한다.
- 모바일 앱은 한 손 사용을 고려하고, 관리자 화면은 밀도 높은 표와 필터를 우선한다.
- 장식보다 가독성과 상태 전달을 우선한다.

## 2. Typography

### Primary Font

- `Gmarket Sans`
- 참고: https://noonnu.cc/font_page/375

### Fallback Font

- `Noto Sans KR`
- 참고: https://fonts.google.com/noto/specimen/Noto+Sans+KR

### Text Scale

| 용도 | 크기 | 굵기 |
| --- | --- | --- |
| Page Title | 28px | 700 |
| Section Title | 20px | 700 |
| Card Title | 18px | 600 |
| Body | 14px to 16px | 400 to 500 |
| Caption | 12px | 400 |

## 3. Color System

데이터 상태를 빠르게 구분할 수 있도록 파스텔 기반 색상을 사용하되, 상태 컬러는 충분한 대비를 확보한다.

| 토큰 | 값 | 용도 |
| --- | --- | --- |
| `primary` | `#6FA8DC` | 주요 액션, 강조 카드 |
| `secondary` | `#A8D8B9` | 보조 강조, 성공 상태 |
| `accent` | `#F6C28B` | 보조 CTA, 배지 |
| `background` | `#F7F9FC` | 기본 배경 |
| `surface` | `#FFFFFF` | 카드, 패널 |
| `text-primary` | `#1F2937` | 본문 |
| `text-secondary` | `#6B7280` | 보조 텍스트 |
| `success` | `#3BA776` | 정상 상태 |
| `warning` | `#F0B429` | 주의 상태 |
| `danger` | `#D64545` | 이상징후, 에러 |

추천 팔레트 참고: https://coolors.co/palettes/trending/pastel

## 4. Iconography

저작권 이슈가 없고 관리가 쉬운 아이콘 세트를 사용한다.

| 라이브러리 | 링크 | 라이선스 | 용도 |
| --- | --- | --- | --- |
| Lucide Icons | https://lucide.dev/ | MIT | 기본 사용 |
| Heroicons | https://heroicons.com/ | MIT | 대체 옵션 |

## 5. Component Guidelines

필수 컴포넌트는 다음과 같다.

- Button
- Card
- Input
- Select
- Table
- Modal
- Badge
- Navbar
- Sidebar
- Chart Container
- Empty State
- Alert Banner

컴포넌트 공통 원칙은 다음과 같다.

- 버튼은 `Primary`, `Secondary`, `Danger`, `Ghost` 4종 변형을 기본으로 둔다.
- 배지는 `normal`, `warning`, `critical` 상태를 표현할 수 있어야 한다.
- 카드 상단에는 제목과 핵심 지표를 먼저 배치한다.
- 테이블은 정렬, 필터, 빈 상태 메시지를 기본 지원한다.

## 6. Layout

### Mobile App

- 최대 폭 420px 기준 설계
- 하단 내비게이션 우선
- 카드 스택 중심 레이아웃

### Admin Console

- 12-column grid 기준
- 좌측 사이드바 + 상단 필터 바 구조
- 표와 차트가 섞인 대시보드 레이아웃

## 7. Accessibility

- 텍스트 대비는 WCAG AA 수준을 목표로 한다.
- 색상만으로 상태를 전달하지 않고 텍스트와 아이콘을 함께 사용한다.
- 주요 버튼과 입력 요소는 키보드 포커스를 명확히 표시한다.

## 8. UI Library

추천 조합은 다음과 같다.

- `shadcn/ui`
- 참고: https://ui.shadcn.com/
- 라이선스: MIT

대안으로 Tailwind 기반 UI 구성도 허용한다. 단, 색상 토큰과 상태 체계는 이 문서 기준을 유지한다.
