# Reference Cursor 비교표 및 반영 전략

이 문서는 `reference-source/reference-cursor role`에 있는 참고 자료를 현재 `mySptPrj`에 어떻게 흡수할지 정리한 문서다.

목표는 **기존 자료를 그대로 복붙하는 것**이 아니라,
현재 프로젝트 구조와 충돌하지 않는 범위에서 **가져올 수 있는 규칙과 디자인 레퍼런스를 선별**하는 것이다.

---

## 1. 비교 대상

### 현재 프로젝트 (`mySptPrj`)
- Spring Boot + Thymeleaf + eGovFrame 기반 인트라넷
- 공통 스타일: `common.css`, `components.css`
- 현재 사용 클래스 예시:
  - `primary-button`
  - `outline-button`
  - `table-type01`
  - `txt-head-*`, `txt-body-*`
- 템플릿 구조:
  - `templates/thymeleaf/cmm`
  - `templates/thymeleaf/main`
  - `templates/thymeleaf/system`
  - `templates/thymeleaf/error`

### 참고 소스 (`reference-cursor role`)
- `디자인 시스템 스킬.md`
- `컴포넌트 상세 마크업 레퍼런스.md`
- `cursor룰`

---

## 2. 핵심 결론

reference-cursor 자료는 유용하지만, 현재 `mySptPrj`의 **현행 규칙을 대체하는 기준**으로 바로 사용할 수는 없다.

이유:
- 현재 프로젝트의 CSS 클래스 체계와 reference의 클래스 체계가 다르다.
- 현재 템플릿 구조와 reference의 권장 뷰 구조가 완전히 일치하지 않는다.
- 일부 reference 문구는 현재 프로젝트에서 사용 중인 패턴을 "구버전"으로 간주하므로 충돌 가능성이 있다.

따라서 반영 전략은 아래 3가지로 구분한다.

1. **즉시 반영 가능**
2. **변환 후 반영 가능**
3. **현재는 보류**

---

## 3. 비교표

| 항목 | reference-cursor 내용 | 현재 mySptPrj 상태 | 판단 | 반영 위치 |
|------|------------------------|--------------------|------|-----------|
| Java/JS 명명 규칙 | PascalCase, camelCase, boolean 접두사, UPPER_SNAKE_CASE | 현재 구조와 대체로 충돌 없음 | 즉시 반영 가능 | `docs/backend-rules.md` |
| 계층 구조 규칙 | Controller / Service / DAO / VO / Mapper 분리 | 현재도 유사 구조 사용 중 | 즉시 반영 가능 | `docs/backend-rules.md` |
| 작업 태도 규칙 | 업무 흐름 중심, 명확한 화면 흐름, 주석 규칙 | 현재 프로젝트 성격과 잘 맞음 | 즉시 반영 가능 | `AGENTS.md`, `docs/backend-rules.md` |
| 들여쓰기/기본 스타일 | HTML/JS/CSS 2칸, 한 줄 최대 길이 등 | 일부는 적용 가능, 일부는 운영 합의 필요 | 변환 후 반영 | `docs/backend-rules.md` |
| ViewController / ApiController 분리 | 화면 이동과 API 통신 분리 권장 | 현재 레포 전체에 강제되진 않음 | 변환 후 반영 | `docs/backend-rules.md` |
| 뷰 경로 규칙 | `form/`, `popup/` 등 세분화 | 현재 구조와 1:1 일치하지 않음 | 변환 후 반영 | `docs/backend-rules.md` |
| 디자인 토큰 체계 | `--color-*`, `--font-size-*` 중심 | 현재는 `--spt-*` 토큰 사용 중 | 변환 후 반영 | 별도 비교/개편 문서 |
| 버튼 클래스 체계 | `.btn medium primary` 등 | 현재는 `primary-button`, `outline-button` 중심 | 현재는 보류 | 추후 디자인 시스템 v2 검토 |
| 폼/테이블 클래스 체계 | `.form-input`, `.table row`, `.btn-group` 등 | 현재 클래스 체계와 다름 | 현재는 보류 | 추후 디자인 시스템 v2 검토 |
| "구버전 클래스 사용 금지" | `primary-button`, `table-type01` 금지 | 현재 레포 현행 코드와 충돌 | 현재는 반영 금지 | 없음 |
| 컴포넌트 DOM 구조 | 버튼 span, sr-only, table caption, modal/tab 구조 | 현재 프로젝트에 참고 가치 높음 | 변환 후 반영 | `docs/ui-rules.md` |
| UI 문구/역할 구분 | semantic role, 상태/행동 중심 | 현재 UI 규칙 보강에 유효 | 즉시 반영 가능 | `docs/ui-rules.md` |

---

## 4. 항목별 상세 판단

## 4.1 즉시 반영 가능

### A. 명명 규칙
가져올 수 있는 내용:
- 클래스명: PascalCase
- 메서드/변수명: camelCase
- boolean: `is*`, `has*`, `can*`
- 상수: UPPER_SNAKE_CASE
- JS 함수명: `get*`, `fetch*`, `post*` 계열 권장

반영 이유:
- 현재 코드 구조와 충돌이 거의 없다.
- 백엔드 규칙 문서의 기본 컨벤션으로 쓰기 좋다.

### B. 계층 구조 설명
가져올 수 있는 내용:
- Controller → Service → DAO/Mapper → DB 흐름
- VO/DTO로 데이터 전달
- XML Mapper 사용 위치 설명

반영 이유:
- 현재 프로젝트 구조와 실제로 잘 맞는다.
- 신규 기능 추가 시 일관성을 높일 수 있다.

### C. UI 문구 및 역할 정의
가져올 수 있는 내용:
- 주요 액션 / 보조 액션 / 위험 액션 구분
- 사용자 메시지는 짧고 명확하게
- 상태와 다음 행동을 중심으로 문구 작성

반영 이유:
- 지금 `ui-rules.md`를 더 실무적으로 보강하는 데 도움이 된다.

---

## 4.2 변환 후 반영 가능

### A. Controller 분리 패턴
reference는 ViewController와 ApiController를 명확히 분리한다.

현재 판단:
- 좋은 방향이지만 레거시 전체에 즉시 강제하면 충돌 가능성이 있다.
- 따라서 **신규 기능 우선 권장 규칙**으로 반영한다.

권장 반영 문구 예시:
- 신규 기능 추가 시 화면 이동 Controller와 API Controller 분리를 우선 고려한다.
- 기존 구조가 단일 Controller인 경우, 단순 수정 작업에서는 무리한 분리를 강제하지 않는다.

### B. 뷰 경로 세분화 규칙
reference는 `form/`, `popup/` 구조를 명시한다.

현재 판단:
- 현재 템플릿 구조와 완전히 일치하지는 않는다.
- 따라서 **신규 화면 또는 리팩터링 시 권장 구조**로만 반영한다.

### C. 컴포넌트 마크업 구조
가져올 수 있는 내용:
- 버튼 내부 `<span>` 구조
- `caption`, `sr-only` 같은 접근성 패턴
- 탭/모달/폼 테이블의 권장 DOM 패턴

현재 판단:
- 클래스명은 그대로 쓰면 안 되지만, DOM 구조와 접근성 패턴은 참고 가치가 높다.
- 따라서 현재 클래스 체계에 맞춰 변환해서 문서화할 수 있다.

### D. 디자인 토큰 철학
reference는 atomic + semantic token 구조가 잘 정리되어 있다.

현재 판단:
- 현재 프로젝트는 이미 `--spt-*` 체계를 사용하고 있다.
- reference 토큰을 바로 도입하기보다, 향후 토큰 정비 시 비교 기준으로 사용한다.

---

## 4.3 현재는 보류

### A. `.btn`, `.form-input`, `.table.row` 클래스 체계 전면 도입
보류 이유:
- 현재 프로젝트 현행 클래스와 충돌한다.
- 신규/기존 화면이 서로 다른 디자인 시스템으로 분열될 수 있다.
- 마이그레이션 계획 없이 도입하면 유지보수 비용만 늘어난다.

### B. "구버전 클래스 사용 금지" 선언
보류 이유:
- reference 문서에서 구버전으로 보는 클래스가 현재 프로젝트에선 아직 현행이다.
- 이를 문서에 그대로 반영하면 문서와 코드가 즉시 불일치한다.

### C. reference 뷰 구조 전면 강제
보류 이유:
- 현재 레포 템플릿 구조와 완전히 일치하지 않는다.
- 신규 기능 기준 권장 패턴으로는 가능하지만 전체 규칙으로 선언하긴 이르다.

---

## 5. 추천 반영 전략

## 5.1 1차 반영 (지금 바로 가능)

### 목표
현재 하네스 문서를 현실 기반으로 강화한다.

### 반영 대상
- 명명 규칙
- 계층 구조 규칙
- UI 문구/역할 정의
- 접근성/마크업 패턴 일부

### 반영 문서
- `docs/backend-rules.md` 신규 작성
- `docs/ui-rules.md` 보강
- `AGENTS.md` 일부 보강

---

## 5.2 2차 반영 (현재 구조와 맞춘 변환)

### 목표
reference의 좋은 패턴을 현재 구조에 맞게 번역한다.

### 반영 대상
- ViewController / ApiController 분리 권장안
- 템플릿 하위 구조 권장안
- 컴포넌트 DOM 구조 표준안

### 반영 문서
- `docs/backend-rules.md`
- `docs/ui-component-patterns.md` (필요 시 신규)

---

## 5.3 3차 반영 (중장기 디자인 시스템 정비)

### 목표
현재 디자인 시스템과 reference 디자인 시스템 간의 차이를 관리한다.

### 반영 대상
- 토큰 체계 비교
- 버튼/폼/테이블 클래스 마이그레이션 후보
- v2 디자인 시스템 검토

### 반영 문서
- `docs/design-system-migration-notes.md` (추후)

---

## 6. 실무 적용 원칙

### 원칙 1. reference는 기준서가 아니라 참고 자산이다
현재 프로젝트의 실제 코드가 항상 1차 근거다.
reference는 방향성과 개선 아이디어를 주는 자료로 사용한다.

### 원칙 2. 문서가 코드보다 앞서서 거짓말하면 안 된다
현재 레포에서 아직 사용하지 않는 구조를 "기본 규칙"으로 선언하지 않는다.
필요하면 "권장", "신규 기능 우선", "추후 전환 후보"로 명확히 표시한다.

### 원칙 3. 디자인 시스템 변경은 별도 계획으로 다룬다
클래스 체계 전환은 단순 문서 반영이 아니라 마이그레이션 작업이다.
현행 클래스 체계를 유지한 채 문서만 바꾸지 않는다.

---

## 7. 다음 액션 추천

### 바로 진행할 것
1. `docs/backend-rules.md` 작성
2. `docs/ui-rules.md`에 reference 기반 문구/접근성 규칙 보강

### 다음 단계로 검토할 것
3. `docs/ui-component-patterns.md` 작성 여부 판단
4. 디자인 시스템 v2 마이그레이션 필요성 검토

---

## 8. 요약

reference-cursor 자료에서 가져올 수 있는 핵심은 다음과 같다.

### 바로 가져올 수 있는 것
- 명명 규칙
- 계층 구조 설명
- UI 문구 원칙
- 접근성 중심 마크업 아이디어

### 바꿔서 가져와야 하는 것
- Controller 분리 패턴
- 뷰 구조 세분화 규칙
- 컴포넌트 DOM 패턴
- 토큰 설계 철학

### 아직 가져오면 안 되는 것
- `.btn` 기반 새 클래스 체계 전면 도입
- 현재 클래스 금지 선언
- reference 기준 뷰 구조 전면 강제

결론적으로, reference-cursor는 **현재 하네스 문서를 강화하는 재료**로는 매우 유용하지만,
**현재 프로젝트 규칙을 대체하는 기준**으로 바로 쓰면 안 된다.
