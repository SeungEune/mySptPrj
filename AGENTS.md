# AGENTS.md

## 프로젝트 개요

`mySptPrj`는 Spring Boot + Thymeleaf + eGovFrame 기반의 인트라넷/업무 시스템이다.
화면은 서버 렌더링 템플릿과 정적 CSS/JS로 구성되며, 도메인별로 `web/service/dao/vo` 구조를 따른다.

이 문서는 AI 에이전트와 사람이 프로젝트를 수정할 때 따라야 할 기본 진입점이다.
작업 전 이 문서를 먼저 읽고, 작업 유형에 따라 관련 문서를 추가로 확인한다.

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 2.7.12 |
| 기반 프레임워크 | eGovFrame 4.2.0 |
| 뷰 | Thymeleaf |
| 데이터 접근 | MyBatis XML Mapper |
| DB | PostgreSQL |
| 패키징 | WAR |
| 빌드 도구 | Maven (`pom.xml`) |

## 먼저 읽을 문서

- 이 문서: `AGENTS.md`
- 프로젝트 지도: `docs/project-map.md`
- UI/템플릿 수정 시: `docs/ui-rules.md`
- UI 마크업/컴포넌트 예시: `docs/ui-component-patterns.md`
- 백엔드 수정 시: `docs/backend-rules.md`
- 검증 루틴: `docs/verification.md`
- 화면 수정 작업 절차: `docs/task-checklists/ui-fix.md`
- 버그 수정 작업 절차: `docs/task-checklists/bugfix.md`

## 빌드 및 실행

> Maven Wrapper(`mvnw`)를 사용한다. 로컬에 Maven이 없어도 프로젝트 기준으로 동일한 빌드/테스트 진입점을 사용할 수 있다.

```bash
# 의존성 포함 빌드
./mvnw package

# 테스트 포함 기본 검증
./mvnw test

# 테스트 생략 빌드
./mvnw -DskipTests package
```

## 디렉토리 구조

```text
src/main/java/
├── biz/                         # 업무 도메인 코드
│   ├── <domain>/web            # Controller
│   ├── <domain>/service        # Service
│   ├── <domain>/service/impl   # Service 구현체
│   ├── <domain>/dao            # DAO / Mapper 연계
│   └── <domain>/vo             # VO / DTO
└── egovframework/com/          # 공통 설정, 예외, 보안, 인프라

src/main/resources/
├── templates/thymeleaf/        # Thymeleaf 화면 템플릿
├── static/css/                 # 정적 CSS
├── static/js/                  # 정적 JS
├── static/images/              # 정적 이미지
└── egovframework/mapper/       # MyBatis XML Mapper
```

세부 설명은 `docs/project-map.md`를 본다.

## 핵심 작업 원칙

### 1) 공통 우선
- 새 UI를 만들기 전에 기존 공통 스타일과 유사 화면을 먼저 확인한다.
- `common.css`, `components.css`, 공통 fragment를 우선 재사용한다.
- 같은 역할의 버튼/타이포/카드/간격 스타일을 새로 만들지 않는다.

### 2) 최소 변경
- 문제 해결에 필요한 범위만 수정한다.
- 관련 없는 리팩터링을 한 번에 섞지 않는다.
- 파일 이동/이름 변경은 영향 범위가 명확할 때만 수행한다.

### 3) 구조 존중
- Java 코드는 기존 도메인 구조(`web/service/dao/vo`)를 따른다.
- 템플릿은 `templates/thymeleaf/...`의 현재 분류 체계를 따른다.
- CSS/JS는 공통과 화면 전용을 구분한다.

### 4) 레포 근거 기반 판단
- README가 부족하더라도 실제 파일 구조와 기존 패턴을 우선 근거로 삼는다.
- 새 규칙을 만들면 관련 문서를 함께 갱신한다.

## UI 수정 규칙 요약

- 공통 스타일: `src/main/resources/static/css/common.css`
- 공통 컴포넌트: `src/main/resources/static/css/components.css`
- 화면 전용 CSS: `src/main/resources/static/css/<module>/...`
- 공통 레이아웃/fragment: `src/main/resources/templates/thymeleaf/cmm/...`

상세 규칙은 반드시 `docs/ui-rules.md`를 확인한다.

## 금지 사항

- 존재하지 않는 정적 리소스 경로를 참조하지 않는다.
- `target/` 아래 산출물을 수정하지 않는다.
- 기존 공통 스타일을 무시한 단독 UI를 임의로 만들지 않는다.
- 확인되지 않은 URL/매핑/리다이렉트 경로를 임의로 바꾸지 않는다.
- 관련 문서 업데이트 없이 새로운 운영 규칙을 암묵적으로 추가하지 않는다.

## 화면 수정 완료 체크리스트

- [ ] 관련 템플릿/JS/CSS 위치를 확인했다.
- [ ] 공통 스타일 또는 유사 화면 재사용 여부를 확인했다.
- [ ] 존재하지 않는 리소스 참조가 없는지 확인했다.
- [ ] 관련 유사 화면(예: 403/404/500, 목록/상세/모달)을 함께 점검했다.
- [ ] 변경 파일 / 변경 이유 / 검증 포인트를 요약할 수 있다.

## 백엔드 수정 기본 원칙

- Controller는 요청/응답 조합과 뷰 진입점에 집중한다.
- Service는 업무 로직을 담당한다.
- DAO/Mapper는 데이터 접근에 집중한다.
- 도메인 경계를 넘는 공통 로직은 `egovframework/com` 또는 공통 유틸로 신중히 분리한다.

## 현재 프로젝트의 알려진 갭

- 에이전트용 문서 체계는 초기 구축 중이다.
- 테스트 자동화가 약하다.
- CI / lint / formatter / wrapper 체계가 아직 없다.
- `target/` 등 산출물 관리와 stale 자산 정리가 필요하다.

작업 시 위 갭을 고려하고, 불확실한 경우 문서 먼저 보강하는 방향을 우선한다.
