# mySptPrj

Spring Boot + Thymeleaf + eGovFrame 기반의 인트라넷/업무 시스템 프로젝트.

이 레포는 현재 기능 개발과 함께 **harness engineering** 문서 체계를 정비하고 있다.
즉, 코드만 있는 프로젝트가 아니라 **AI와 사람이 같은 기준으로 작업할 수 있도록 운영 문서와 체크리스트를 함께 관리하는 구조**로 발전시키는 중이다.

## 1. 프로젝트 개요

- 프레임워크: Spring Boot 2.7.12
- 기반 프레임워크: eGovFrame 4.2.0
- 언어: Java 17
- 뷰: Thymeleaf
- 데이터 접근: MyBatis XML Mapper
- DB: PostgreSQL
- 패키징: WAR

## 2. 빠른 구조 요약

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

프로젝트 지도 상세는 `docs/project-map.md`를 참고한다.

## 3. 빌드 및 기본 검증

> 현재 Maven Wrapper(`mvnw`)는 없다. 로컬 환경에 Maven이 설치되어 있어야 한다.

```bash
# 기본 빌드
mvn package

# 테스트 실행
mvn test

# 테스트 생략 빌드
mvn -DskipTests package
```

## 4. 문서 허브

### 최상위 진입점
- `AGENTS.md`  
  AI 에이전트와 사람이 작업을 시작할 때 가장 먼저 읽는 운영 문서

### 구조/규칙 문서
- `docs/project-map.md`  
  프로젝트 구조 지도
- `docs/backend-rules.md`  
  백엔드 작업 규칙
- `docs/ui-rules.md`  
  UI/템플릿 작업 규칙
- `docs/ui-component-patterns.md`  
  현재 클래스 체계 기준 UI 마크업/컴포넌트 예시
- `docs/design-reference-adoption.md`  
  reference-cursor 자료를 현재 프로젝트에 어떻게 반영할지 정리한 비교표/전략 문서

### 작업 체크리스트
- `docs/task-checklists/ui-fix.md`  
  UI 수정 작업 절차
- `docs/task-checklists/bugfix.md`  
  일반 버그 수정 작업 절차

## 5. 작업 시작 추천 흐름

### UI 수정
1. `AGENTS.md`
2. `docs/project-map.md`
3. `docs/ui-rules.md`
4. `docs/ui-component-patterns.md`
5. `docs/task-checklists/ui-fix.md`

### 백엔드 수정
1. `AGENTS.md`
2. `docs/project-map.md`
3. `docs/backend-rules.md`
4. `docs/task-checklists/bugfix.md`

### reference-cursor 반영 검토
1. `docs/design-reference-adoption.md`
2. 관련 규칙 문서(`ui-rules.md`, `backend-rules.md`) 확인

## 6. 현재 운영 원칙

- 기존 구조를 무시한 대규모 변경보다 **최소 변경**을 우선한다.
- UI는 현재 프로젝트의 공통 스타일과 fragment를 우선 재사용한다.
- reference 자료는 그대로 복붙하지 않고, 현재 구조에 맞게 변환해서 반영한다.
- 새로운 규칙을 만들면 관련 문서도 함께 갱신한다.
- `target/` 같은 빌드 산출물은 수정 대상이 아니다.

## 7. 현재 상태 메모

이 프로젝트는 하네스 문서 체계를 초기 구축한 상태다.
현재는 아래 문서들을 기준으로 작업 방식을 정리하는 단계이며,
추후 다음 항목들을 순차적으로 보강할 수 있다.

- README 지속 보강
- 테스트/검증 루틴 보강
- Maven Wrapper 도입 검토
- 디자인 시스템 마이그레이션 노트 정리
- CI 도입

## 8. 참고

프로젝트의 상세 작업 규칙은 README보다 `AGENTS.md`와 `docs/` 문서를 우선한다.
README는 입구 역할, `AGENTS.md`는 실제 운영 기준 역할을 맡는다.
