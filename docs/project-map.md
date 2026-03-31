# Project Map

`mySptPrj`의 주요 구조를 빠르게 파악하기 위한 지도 문서.

## 1. 애플리케이션 진입점

- Spring Boot 시작 클래스: `src/main/java/egovframework/EgovBootApplication.java`
- 공통 설정: `src/main/java/egovframework/com/config/`
- 에러 컨트롤러: `src/main/java/egovframework/com/error/CustomErrorController.java`

## 2. Java 코드 구조

### 2.1 업무 도메인

`src/main/java/biz/` 아래에 도메인별 패키지가 위치한다.

대표 예시:
- `biz/login`
- `biz/main`
- `biz/file`
- `biz/system/codemngr`
- `biz/system/dept`
- `biz/system/menu`
- `biz/system/user`

### 2.2 도메인 내부 공통 패턴

각 도메인은 가능하면 아래 구조를 따른다.

- `web/` → Controller
- `service/` → Service 인터페이스
- `service/impl/` → Service 구현체
- `dao/` → DAO 또는 Mapper 연계 계층
- `vo/` → 요청/응답/검색/전달 객체

예:
- `src/main/java/biz/system/menu/web/MenuController.java`
- `src/main/java/biz/system/menu/service/MenuService.java`
- `src/main/java/biz/system/menu/service/impl/MenuServiceImpl.java`
- `src/main/java/biz/system/menu/vo/MenuVO.java`

## 3. 템플릿 구조

Thymeleaf 템플릿은 `src/main/resources/templates/thymeleaf/` 아래에 위치한다.

주요 폴더:
- `cmm/` → 공통 fragment, header/footer/meta/script
- `cmm/layout/` → 레이아웃 템플릿
- `login/` → 로그인 화면
- `main/` → 메인/대시보드 화면
- `system/` → 시스템 관리 화면
- `error/` → 오류 페이지
- `pub/` → 퍼블리싱/임시 화면

중요 파일:
- `cmm/layout/mainLayout.html` → 메인 레이아웃
- `cmm/header.html` / `cmm/footer.html` → 공통 UI 조각
- `main/mainForm.html` → 메인 화면

## 4. 정적 리소스 구조

정적 리소스는 `src/main/resources/static/` 아래에 위치한다.

### CSS
- `static/css/reset.css` → reset
- `static/css/common.css` → 토큰, 유틸리티, 공통 레이아웃
- `static/css/components.css` → 버튼, 배지, 재사용 UI 컴포넌트
- `static/css/cmm/` → 공통 화면/헤더/푸터/로그인 관련 CSS
- `static/css/system/` → 시스템 관리 화면별 CSS
- `static/css/main/` → 메인 대시보드 CSS

### JS
- `static/js/cmm/` → 공통 라이브러리/기능
- `static/js/module/` → 공통 모듈성 JS
- `static/js/main/` → 메인 화면 JS
- `static/js/system/` → 시스템 관리 화면 JS

### Images
- `static/images/logo.png` → 현재 로고
- `static/images/icon/` → 아이콘 리소스
- `static/images/login/` → 로그인 관련 이미지

## 5. Mapper / 설정 구조

### MyBatis XML
- 위치: `src/main/resources/egovframework/mapper/`
- 예:
  - `biz/login/EgovLoginUsr_SQL.xml`
  - `biz/system/menu/Menu_SQL.xml`
  - `biz/system/user/User_SQL.xml`

### 설정 파일
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`
- `src/main/resources/logback-spring.xml`

## 6. 수정 시 빠른 탐색 규칙

### 화면(UI) 수정
1. 템플릿 위치 확인
2. 연결된 CSS/JS 확인
3. 공통 레이아웃/fragment 사용 여부 확인
4. 유사 화면과 패턴 비교

### 백엔드 수정
1. 관련 Controller 확인
2. 연계 Service / ServiceImpl 확인
3. DAO / Mapper / XML 확인
4. VO 및 공통 응답 구조 확인

## 7. 수정 대상에서 제외할 것

- `target/` 아래 빌드 산출물
- IDE 설정 파일 변경(명시적 목적 없는 경우)
- 현재 작업과 무관한 공통 구조 대규모 리팩터링

## 8. 앞으로 보강할 예정인 문서

- `docs/ui-rules.md`
- `docs/task-checklists/ui-fix.md`
- `docs/backend-rules.md` (예정)
- `docs/task-checklists/bugfix.md` (예정)
