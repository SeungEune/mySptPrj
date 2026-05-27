# Legacy LLM Chat 제거 계획

## 1. 목적
현재 `mySptPrj` 에는 아래 두 개의 채팅 경로가 공존하고 있다.

- legacy 경로: `AiChatServiceImpl` 기반
- 신규 경로: `LangChainChatServiceImpl` 기반

이 문서의 목적은 기존 legacy LLM chat 소스를 바로 삭제하는 것이 아니라, **안전하게 제거하기 위한 단계별 계획**을 수립하는 것이다.

핵심 원칙은 다음과 같다.

- 사용자 영향 없이 점진적으로 전환한다.
- 먼저 LangChain 경로를 운영 가능한 수준으로 안정화한다.
- 안정화 검증 후 legacy 경로를 제거한다.
- 세션/이력 DB 구조는 유지한다.

---

## 2. 제거 대상 범위

## 2.1 직접 제거 대상 후보
아래 항목은 legacy 경로에 직접 연결된 제거 후보이다.

다만 현재 코드 기준으로는 일부 항목이 이미 LangChain 경로 또는 공통 경로로 재배치되어 있으므로, 삭제 전 역할 분리가 선행되어야 한다.

### Java
- `src/main/java/biz/llm/service/impl/AiChatServiceImpl.java` 내 legacy 직접 호출 메서드
  - `sendMessage()`
  - `callChatApi()`
  - `parseChatResponse()`
  - `buildMessages()`
  - 성능 테스트 관련 내부 호출 로직
- `src/main/java/biz/llm/service/AiChatService.java` 내 legacy 전용 메서드
  - 단, 세션/조회성 메서드는 즉시 삭제 대상이 아니라 분리 대상임

### Controller/API
- `src/main/java/biz/llm/web/AiChatApiController.java` 내 중복 또는 legacy 성격 API
  - `/llm/sendMessageLangChain.do` (현재는 제거 완료, 과거 `/llm/sendMessage.do` 와 실질적으로 중복이었음)
  - `/llm/runPerfTest.do` (정책에 따라 제거 또는 별도 유지)
- `/llm/sendMessage.do` 는 현재 LangChain 기본 경로이므로 제거 대상이 아니라 공식 경로 후보임

### View
- 과거 legacy 화면 전용 템플릿/JS
- 현재 공식 진입 URL은 `/llm/chatForm.do` 단일 경로로 유지

### Frontend
- `src/main/resources/templates/thymeleaf/llm/chatForm.html` (삭제 완료)
- `src/main/resources/static/js/llm/chatForm.js` (삭제 완료)

---

## 2.2 유지 대상
legacy 제거 이후에도 아래는 유지한다.

- `PromptTemplateService`
- `ChatHistoryService`
- `LangChainChatService`
- `LangChainAiAssistant`
- `LangChainAssistantFactory`
- `LangChainChatServiceImpl`
- `tb_llm_chat_session`
- `tb_llm_chat_history`
- LangChain 전용 화면 및 API
- 분리 완료된 공통 세션 관리 서비스 `LlmChatSessionService`

---

## 3. 제거 전 선행 조건
legacy 소스를 바로 제거하면 위험하다.
반드시 아래 선행 조건을 만족한 뒤 제거해야 한다.

## 3.1 기능 선행 조건
- LangChain 전용 화면이 실사용 가능한 상태여야 한다.
- 세션 생성/목록/상세/메시지 전송이 모두 정상 동작해야 한다.
- prompt role 기능이 legacy와 동등 수준으로 동작해야 한다.
- DB 저장이 안정적으로 이뤄져야 한다.
- 현재 `AiChatService` 에 남아 있는 세션/조회성 기능 분리가 완료되어야 한다. (현재 완료)

## 3.2 운영 선행 조건
- 사용자 또는 운영자가 LangChain 전용 화면으로 전환 가능해야 한다.
- 기존 메뉴/링크/사용 흐름이 LangChain 기준으로 재정비되어야 한다.
- 오류 시 fallback 계획이 있어야 한다.

## 3.3 기술 선행 조건
- `LangChainChatServiceImpl` 구조 안정화
- PromptTemplate + AI Service 구조 정착
- history 주입 정책 정리
- 로그/장애 추적 가능 수준 확보

---

## 4. 제거 전략

## 4.1 원칙
### 한 번에 삭제하지 않는다.
아래 순서로 단계적으로 제거한다.

1. 사용 경로 차단
2. 메뉴/화면 전환
3. API 비활성화
4. 코드 제거

즉, **사용을 먼저 멈추고 코드는 나중에 삭제**한다.

---

## 4.2 단계별 제거 방식

## Phase 1. LangChain 경로를 기본 경로로 승격
목표: 실사용 기준 기본 화면과 기본 API 흐름을 LangChain 쪽으로 옮긴다.

현재 코드상 `sendMessage.do` 와 `chatForm.do` 는 이미 LangChain 기준으로 수렴 중이므로, 이 단계의 핵심은 전환 그 자체보다 중복 경로 정리다.

### 작업 항목
- `/llm/sendMessage.do` 를 공식 기본 전송 API로 유지할지 확정
- `/llm/sendMessageLangChain.do` 중복 경로 제거 완료
- `/llm/chatForm.do` 를 공식 진입 URL로 확정
- 운영 문서/안내 문구를 실제 공식 경로 기준으로 수정

### 완료 기준
- 사용자 관점에서 기본 화면과 기본 API가 각각 1개로 정리됨

---

## Phase 2. 공통 기능 분리 및 화면 정책 정리
목표: `AiChatService` 에 남아 있는 공통 기능과 화면 정책을 분리 정리한다.

### 작업 항목
- 세션 생성/목록/상세 기능을 `LlmChatSessionService` 로 이동
- prompt role 조회 기능을 `LlmChatSessionService` 로 이동
- `/llm/chatForm.do` 를 공식 URL로 확정
- `/llm/langchainChatForm.do` 제거 완료

### 완료 기준
- `AiChatService` 가 세션/조회성 API의 중심이 아니게 됨
- 화면 URL 정책이 정리됨

---

## Phase 3. legacy API 비활성화
목표: legacy 직접 호출 또는 중복 API를 더 이상 사용하지 않도록 한다.

### 대상 API
- `AiChatServiceImpl.sendMessage()` 에 의존하는 경로
- 제거 전까지 중복이었던 `/llm/sendMessageLangChain.do` 경로
- 필요 시 `/llm/runPerfTest.do`

### 작업 항목
- 프론트에서 중복 API 참조 제거
- controller 에서 `AiChatService.sendMessage()` 의존 제거
- 운영상 사용 중단 확인
- 일정 기간 모니터링 후 API 제거

### 완료 기준
- 메시지 전송은 LangChain 단일 경로만 사용
- 중복 API가 제거되거나 비공식화됨

---

## Phase 4. legacy 서비스 제거
목표: 실제 Java 코드에서 legacy 직접 호출 경로를 제거한다.

### 제거 대상
- `AiChatServiceImpl` 내 legacy direct REST 호출 로직
- `AiChatService` 내 legacy 전용 메서드
- 중복 또는 불필요해진 bean wiring
- legacy 성능 테스트 로직(정책에 따라 별도 유지 가능)

### 주의사항
현재 `AiChatService` 는 완전한 legacy 인터페이스가 아니라 공통 기능이 섞인 과도기 인터페이스다.
따라서 interface 전체 삭제보다 아래 순서가 맞다.

1. 세션/조회 기능 분리
2. 운영 기능 분리 여부 결정
3. legacy 호출 메서드 제거
4. 마지막에 빈 인터페이스/구현 정리

---

## Phase 5. legacy 프론트 소스 제거
목표: 템플릿과 JS까지 정리한다.

### 제거 대상
- `templates/thymeleaf/llm/chatForm.html` (삭제 완료)
- `static/js/llm/chatForm.js` (삭제 완료)

### 조건
- LangChain 화면이 완전히 대체 가능해야 함
- redirect 또는 메뉴 정비가 이미 끝나 있어야 함

---

## 5. 사전 정리 필요 항목

## 5.1 공통 기능 분리 여부 점검
legacy 제거 전에 아래가 공통 서비스로 정리되어야 한다.

- 프롬프트 역할 조회
- 세션 생성
- 세션 목록 조회
- 세션 상세 조회
- history 저장/조회

이미 일부는 분리되었지만, 완전 제거 전엔 legacy 의존 흔적이 남아있는지 다시 점검해야 한다.

---

## 5.2 성능 테스트 기능 정책 결정
현재 perf test 는 legacy 쪽에 더 가깝다.

선택지:
1. perf 기능도 LangChain 쪽으로 재구현 후 legacy 제거
2. perf 기능은 별도 admin/test 도구로 분리
3. perf 기능 자체 제거

이 항목은 legacy 제거 전에 반드시 결정해야 한다.

---

## 5.3 URL 정책 정리
최종적으로 아래 중 하나를 결정해야 한다.

### 안 A
- `/llm/chatForm.do` 를 LangChain 화면으로 재사용
- 기존 URL 유지, 내부 구현만 변경

### 결정 결과
- `/llm/chatForm.do` 를 공식 URL로 유지
- `/llm/langchainChatForm.do` 는 제거

### 비고
초기 사용자 혼란을 줄이기 위해 기존 URL을 공식 경로로 유지하는 방향을 채택했다.

---

## 6. 권장 실행 순서

## Step 1. LangChain 경로 안정화
- 화면/JS/API 기능 검증
- prompt/history/session 동작 점검
- 운영 테스트

## Step 2. 기본 진입점을 LangChain으로 변경
- 메뉴 연결 수정
- 기존 화면은 숨김 또는 redirect

## Step 3. legacy API 사용 중단 확인
- 프론트 참조 제거
- 호출 로그 점검

## Step 4. legacy 코드 제거
- `AiChatServiceImpl` 제거
- 관련 interface/bean/unused code 제거

## Step 5. legacy 프론트 파일 제거
- 구 템플릿/JS 제거
- 문서 정리

---

## 7. 리스크 및 대응

## 리스크 1. 일부 기능이 legacy 쪽에만 남아 있을 수 있음
### 대응
- 제거 전 기능 목록 비교표 작성
- role/session/history/perf 포함 여부 점검

## 리스크 2. 운영자가 기존 URL을 계속 사용할 수 있음
### 대응
- redirect 처리
- 메뉴/북마크 안내

## 리스크 3. 성능 테스트 기능 공백
### 대응
- 정책 먼저 결정
- 필요 시 LangChain 전용 perf 기능 별도 구현

## 리스크 4. 비교 실험 경로 상실
### 대응
- 일정 기간은 legacy 브랜치 또는 태그 보관
- 제거 전 결과 비교 문서 남기기

---

## 8. 완료 기준
legacy 제거 완료는 아래 조건을 만족해야 한다.

- 사용자 진입 경로가 LangChain 화면 기준으로 정리됨
- legacy API 호출이 더 이상 발생하지 않음
- legacy 서비스/프론트 소스가 제거됨
- 공통 기능은 LangChain 기준 구조로 유지됨
- 운영 및 개발 문서가 최신 상태로 정리됨

---

## 9. 최종 권장안
지금 시점에서 바로 `AiChatService` 전체를 삭제하는 것은 비추천이다.
현재 구조상 가장 현실적인 제거 방식은 아래 순서다.

1. LangChain 메시지 전송 경로를 공식 단일 경로로 확정
2. 세션/role 조회 기능을 `AiChatService` 에서 분리 완료
3. perf/model status 기능의 존치 여부 결정
4. legacy direct 호출 메서드 제거
5. 남은 legacy 인터페이스와 구현 정리

즉, 원칙은 여전히 **전환 완료 후 제거**지만, 현재 단계의 핵심은 삭제보다 먼저 **책임 분리**다.

---

## 10. 후속 문서화 필요 항목
legacy 제거 시 아래 문서도 함께 업데이트한다.

- LangChain 도입 문서
- 프론트 개발 계획 문서
- 운영/테스트 체크리스트
- 메뉴/화면 진입 문서
- 향후 trace/memory/RAG 고도화 문서
