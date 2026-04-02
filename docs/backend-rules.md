# Backend Rules

`mySptPrj`의 Java / Spring Boot / eGovFrame / MyBatis 기반 백엔드 작업 규칙.

이 문서는 현재 레포의 실제 구조를 기준으로 작성한다.
reference-cursor의 좋은 규칙은 반영하되, 현재 코드와 충돌하는 항목은 즉시 강제하지 않는다.

## 1. 기본 원칙

### 구조 존중
- 기존 도메인 구조를 우선 따른다.
- 현재 프로젝트의 기본 흐름은 `Controller -> Service -> DAO/Mapper -> DB` 이다.
- 공통 설정/인프라/예외는 `egovframework/com` 아래를 우선 확인한다.

### 최소 변경
- 단순 수정 작업에서는 필요한 범위만 수정한다.
- 현재 도메인 구조를 무시한 대규모 이동/분리는 별도 합의 없이 수행하지 않는다.

### 명확한 책임 분리
- Controller는 요청 처리, 응답/뷰 진입점 조합에 집중한다.
- Service는 업무 로직을 담당한다.
- DAO/Mapper는 데이터 접근에 집중한다.
- VO/DTO는 계층 간 데이터 전달을 담당한다.

## 2. 패키지 및 파일 구조 규칙

### 도메인 기본 구조
`src/main/java/biz/<domain>/` 아래 구조를 우선 따른다.

- `web/` : Controller
- `service/` : Service 인터페이스
- `service/impl/` : Service 구현체
- `dao/` : DAO 또는 Mapper 연계 계층
- `vo/` : VO / 검색조건 / 전달 객체

### 공통 구조
- `src/main/java/egovframework/com/config/` : 설정
- `src/main/java/egovframework/com/error/` : 에러 처리
- `src/main/java/egovframework/com/cmm/` : 공통 응답, 유틸, 보안, 예외

### XML Mapper
- `src/main/resources/egovframework/mapper/` 아래에 둔다.
- 도메인 구조와 최대한 대응되도록 유지한다.

## 3. 명명 규칙

### Java
- 클래스명: PascalCase
- 메서드명: camelCase
- 변수명: camelCase
- 상수명: UPPER_SNAKE_CASE
- 패키지명: lowercase

### Boolean 명명
- boolean 메서드/변수는 `is*`, `has*`, `can*` 접두사를 우선 사용한다.
- 예: `isValid`, `hasPermission`, `canDelete`

### JavaScript 연계 함수명
백엔드 API와 연결되는 JS 함수나 화면 스크립트 함수는 의미가 드러나는 이름을 사용한다.
- 조회: `get*`, `fetch*`
- 등록/전송: `create*`, `post*`
- 수정: `update*`, `put*`
- 삭제: `delete*`

### 표준 용어
- 같은 의미의 용어는 프로젝트 전반에서 하나로 통일한다.
- 임의 축약보다는 기존 코드에서 이미 사용 중인 표준 표현을 우선 따른다.

## 4. Controller 규칙

### 기본 역할
- View 진입점 또는 API 진입점을 제공한다.
- 요청 파라미터 수집과 Service 호출에 집중한다.
- 복잡한 업무 판단은 Controller에 쌓지 않는다.

### 현재 프로젝트 기준
- 기존 레거시는 단일 Controller 안에 화면 이동과 데이터 처리가 혼재할 수 있다.
- 단순 수정 작업에서는 기존 구조를 존중한다.

### 신규 기능 권장 패턴
- 신규 기능 추가 시에는 ViewController와 ApiController 분리를 우선 고려한다.
- 다만 기존 도메인 전체를 강제로 분리하지는 않는다.
- 레거시 수정에서는 현재 구조를 깨지 않는 것이 우선이며, 신규 기능/리팩터링 시에만 점진적으로 적용한다.

권장 예시:
- 화면 이동: `{Domain}ViewController`
- API 처리: `{Domain}ApiController`

권장 URL 성격:
- 화면 이동: `*Form.do`, `*ListForm.do`, `*EditForm.do`, `*Popup.do`
- API: 도메인 기준 JSON/데이터 응답 경로

## 5. Service 규칙

- Service는 업무 규칙과 처리 흐름을 담당한다.
- 트랜잭션 경계는 현재 프로젝트 설정을 존중한다.
- Controller에서 DAO를 직접 호출하지 않는다.
- 여러 DAO/Mapper를 조합한 처리나 검증 로직은 Service에 둔다.

## 6. DAO / Mapper 규칙

- DAO/Mapper는 데이터 접근에 집중한다.
- SQL은 XML Mapper와 연계된 현재 구조를 존중한다.
- 비즈니스 규칙을 SQL이나 DAO 계층에 과도하게 밀어 넣지 않는다.
- 신규 SQL 추가 시 도메인 기준 위치를 우선 따른다.

## 7. VO / DTO 규칙

- 계층 간 전달 데이터는 VO/DTO로 관리한다.
- 검색 조건, 페이징, 화면 입력값은 기존 VO 패턴을 우선 따른다.
- 필드명은 현재 도메인 용어와 일관되게 유지한다.

## 8. 주석 규칙

### 원칙
- 주석은 코드가 드러내지 못하는 맥락과 이유를 설명할 때만 사용한다.
- obvious한 동작 설명 주석은 지양한다.
- 공통 템플릿성 주석보다 실제 업무 의미가 드러나는 주석이 낫다.

### 권장 내용
- 왜 이 분기/검증이 필요한지
- 왜 공통 유틸이 아닌 도메인 로직으로 남겨두는지
- 외부 제약(정책, 업무 규칙, 레거시 호환성 등)

## 9. 신규 기능 추가 시 권장 구조

### 권장 흐름
1. 도메인 위치 결정
2. Controller 진입점 설계
3. Service 인터페이스/구현 분리
4. VO 설계
5. Mapper/XML 연결
6. 템플릿/JS/CSS 연계

### 권장 템플릿 구조
- 신규 화면은 필요 시 `form/`, `popup/` 같은 하위 구조를 고려할 수 있다.
- 단, 현재 레포 전체에 강제하지 않고 신규 기능 또는 리팩터링 시에만 적용을 검토한다.

## 10. 코딩 스타일 기본값

- HTML / JS / CSS는 2칸 들여쓰기를 권장한다.
- Java는 기존 파일 스타일을 우선 따른다.
- 한 줄이 과도하게 길어지면 의미 단위로 줄바꿈한다.
- 파일 끝에는 개행 1줄을 유지한다.

## 11. 금지 사항

- Controller에서 직접 DAO를 호출하지 않는다.
- 공통 구조를 무시하고 임의의 새 계층을 만들지 않는다.
- 현재 도메인 구조와 무관한 위치에 파일을 추가하지 않는다.
- 신규 기능에서 ViewController와 ApiController를 하나의 Controller로 합치지 않는다.
- 현재 코드와 충돌하는 규칙을 문서만으로 강제하지 않는다.
- reference-cursor의 구조를 현재 레포에 그대로 복사하지 않는다.

## 12. 관련 체크리스트

- 일반 버그 수정 절차는 `docs/task-checklists/bugfix.md`를 참고한다.
- 화면/UI 중심 수정은 `docs/task-checklists/ui-fix.md`를 함께 참고한다.

## 13. 작업 후 확인 항목

- [ ] 수정 위치가 현재 도메인 구조에 맞는가?
- [ ] Controller / Service / DAO 책임이 섞이지 않았는가?
- [ ] 명명 규칙이 기존 코드와 일관적인가?
- [ ] XML Mapper / VO / 화면 연계가 끊기지 않았는가?
- [ ] 새로 만든 규칙이 있다면 관련 문서도 함께 갱신했는가?
�� 연계가 끊기지 않았는가?
- [ ] 새로 만든 규칙이 있다면 관련 문서도 함께 갱신했는가?
