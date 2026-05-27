# LangChain4j 도입 계획

## 1. 목적
`mySptPrj`의 기존 LLM 채팅 기능은 `AiChatServiceImpl` 에서 로컬 LLM HTTP API를 직접 호출하는 방식으로 구성되어 있다.
현재 일부 경로는 이미 `LangChainChatServiceImpl` 기반으로 전환되었으나, 서비스 계층은 legacy 코드와 신규 코드가 혼재된 과도기 상태다.

이 문서의 목적은 다음과 같다.

- LangChain4j 도입 범위를 현재 코드 기준으로 명확히 정리한다.
- 기존 DB 세션/이력 구조를 유지한 채 안전하게 전환한다.
- 단순 라이브러리 교체가 아니라 운영 가능한 구조로 정착시키기 위한 단계별 계획을 수립한다.

---

## 2. 현재 구조 요약

## 2.1 현재 확인된 구성
- 전송 API 기본 경로: `/llm/sendMessage.do`
- 기존 LangChain 전송 API: `/llm/sendMessageLangChain.do` (중복 경로, 정리 대상이었음)
- 기본 화면 진입: `/llm/chatForm.do`

현재 코드상 실제 동작은 다음과 같다.

### Controller
- `AiChatApiController.sendMessage()` -> `LangChainChatService.sendMessage()` 호출
- 기존 `AiChatApiController.sendMessageLangChain()` 는 `LangChainChatService.sendMessage()` 와 중복 연결 상태였음
- `getModelStatus()`, `runPerfTest()` -> `AiChatService` 호출
- `getPromptRoleList()`, `createSession()`, `getSessionList()`, `getSessionDetail()` -> `LlmChatSessionService` 호출

### View
- 현재 공식 화면 진입 경로는 `/llm/chatForm.do` 단일 경로로 정리됨

즉, 화면과 메시지 전송은 LangChain 기준으로 정리되었고, 세션 관리 API도 `LlmChatSessionService` 로 분리되었다. 현재 `AiChatService` 에 남아 있는 것은 legacy 직접 호출 및 운영성 기능 중심이다.

---

## 2.2 현재 서비스 역할

### `AiChatServiceImpl`
역할이 혼합되어 있다.

#### legacy 직접 호출 역할
- 로컬 LLM REST API 직접 호출
- 응답 파싱
- 성능 테스트
- 모델 상태 조회

#### 공통/잔존 역할
- prompt role 목록 조회
- 채팅 세션 생성
- 세션 목록 조회
- 세션 상세 조회
- DB history 저장/조회 일부

### `LangChainChatServiceImpl`
현재는 메시지 전송 중심이다.

- 입력 검증
- 세션 보장
- system prompt 조합
- 대화 이력 문자열화
- LangChain assistant 호출
- 사용자/assistant history 저장

즉, 현재 구조는 **메시지 전송만 LangChain화된 1차 전환 상태**로 보는 것이 맞다.

---

## 3. 도입 원칙

### 3.1 DB 구조 유지
기존 테이블은 유지한다.
- `tb_llm_chat_session`
- `tb_llm_chat_history`

LangChain4j 도입은 대화 실행 계층의 변경이지, 세션/이력 저장 구조를 폐기하는 작업이 아니다.

### 3.2 점진적 전환
한 번에 legacy 삭제보다 아래 순서를 따른다.
1. 메시지 전송 전환
2. 세션/role 공통 서비스 분리
3. 운영성 기능 재배치
4. legacy 직접 호출 제거

### 3.3 운영 친화적 구조 우선
공공/SI 환경 특성상 아래를 우선한다.
- 단순한 bean 구조
- 명확한 책임 분리
- 장애 추적 가능성
- DB 기반 이력 보존
- 기존 화면/URL 충격 최소화

---

## 4. 현재 문제점

## 4.1 서비스 책임이 불명확함
`AiChatServiceImpl` 안에 legacy 호출과 공통 세션 기능이 같이 들어 있다.
그 결과 legacy를 제거하려 해도 세션/role API가 함께 묶여 있어 제거가 어렵다.

## 4.2 API 중복
- `/llm/sendMessage.do`
- `/llm/sendMessageLangChain.do`

두 API가 동일한 LangChain 서비스로 연결되어 있었고, Phase 2에서 `sendMessageLangChain.do` 중복 경로를 제거했다.

## 4.3 화면 URL도 사실상 중복
- 과거 `/llm/chatForm.do`
- 과거 `/llm/langchainChatForm.do`

화면 URL은 현재 `/llm/chatForm.do` 단일 경로로 정리되었다.

## 4.4 LangChain 서비스 범위가 너무 좁음
현재 `LangChainChatServiceImpl` 는 sendMessage 에 집중되어 있어, 전체 채팅 도메인의 공식 서비스가 되기엔 범위가 부족하다.

---

## 5. 목표 구조

권장 목표 구조는 아래와 같다.

### 5.1 채팅 실행 서비스
#### `LangChainChatService`
- 메시지 전송
- LangChain assistant 호출
- 대화 문맥 구성
- 응답 저장

### 5.2 세션 관리 서비스
#### `LlmChatSessionService`
- 세션 생성
- 세션 목록 조회
- 세션 상세 조회
- prompt role 목록 조회
- 세션 소유자/유효성 검증

### 5.3 프롬프트/템플릿 서비스
#### `PromptTemplateService`
- 역할별 system prompt 생성
- 역할 정책 관리

### 5.4 이력 관리 서비스
#### `ChatHistoryService`
- history 저장
- history 조회
- AI 입력용 message 변환

### 5.5 운영/진단 서비스
#### 예시: `LlmModelAdminService`
- 모델 상태 조회
- 성능 테스트
- 운영 점검성 API

### 5.6 legacy 직접 호출 서비스
#### 예시: `LegacyLlmService`
- RestTemplate 직접 호출
- raw chat API 호출
- 필요 시 비교/테스트 전용으로만 한시 유지

이 구조로 정리하면, legacy 제거는 `LegacyLlmService` 와 그 참조 제거로 국소화된다.

---

## 6. 단계별 도입 계획

## Phase 1. LangChain 기본 경로 정리
### 목표
현재 이미 LangChain으로 연결된 기본 전송/API/화면 경로를 정리한다.

### 작업
- `/llm/sendMessage.do` 를 공식 기본 전송 API로 유지
- `/llm/sendMessageLangChain.do` 중복 경로 제거 완료
- `/llm/chatForm.do` 를 공식 진입점으로 유지
- `/llm/langchainChatForm.do` 화면 경로 제거 완료

### 완료 기준
- 사용자 관점 기본 URL/API가 1개로 정리됨

---

## Phase 2. 세션/공통 기능 분리
### 목표
`AiChatServiceImpl` 에 남은 공통 기능을 별도 서비스로 분리한다.

### 우선 분리 대상
- `getPromptRoleList()`
- `createChatSession()`
- `getChatSessionList()`
- `getChatSessionDetail()`

### 권장 결과
- `LlmChatSessionService`

### 완료 기준
- `AiChatApiController` 가 세션/role 조회를 `AiChatService` 가 아니라 `LlmChatSessionService` 에서 받음

---

## Phase 3. 운영 기능 분리
### 목표
legacy 직접 호출과 운영성 API를 분리한다.

### 분리 대상
- `getModelStatus()`
- `runPerformanceTest()`

### 결정 포인트
1. LangChain 기준으로 재구현할지
2. 운영/admin 진단 API로 별도 유지할지
3. 제거할지

### 완료 기준
- 운영 기능 정책이 정해지고, `AiChatService` 에서 분리됨

---

## Phase 4. legacy 직접 호출 제거
### 목표
`AiChatServiceImpl.sendMessage()` 와 raw REST 호출 로직을 제거한다.

### 제거 대상
- `AiChatService.sendMessage()`
- `AiChatServiceImpl.sendMessage()`
- `callChatApi()`
- `parseChatResponse()`
- `buildMessages()`
- legacy direct prompt 조합 경로

### 완료 기준
- 메시지 전송은 LangChain 경로만 사용
- direct REST 호출이 운영 코드에서 제거됨

---

## Phase 5. LangChain 고도화
### 목표
도입 후 품질을 운영 수준으로 높인다.

### 후보 항목
- prompt/history 조합 구조 개선
- role별 assistant 전략 분리
- 응답 메타데이터 기록 확장
- timeout/retry 정책 정교화
- 장애 로그 표준화

---

## 7. 설계 포인트

## 7.1 history 처리
현재는 DB history를 문자열 블록으로 조합해서 assistant 입력에 넣고 있다.
이 방식은 단순하고 운영 친화적이지만, 추후 다음을 고려할 수 있다.
- message 단위 변환 정교화
- 최근 history window 조절
- role별 context 정책 차등화

초기에는 현 구조를 유지하되, `ChatHistoryService` 를 중심으로 확장 가능하게 두는 것이 현실적이다.

## 7.2 prompt 정책
기존 `AiChatServiceImpl` 내부 프롬프트 빌더는 이미 실무형 역할 구분이 잘 되어 있다.
따라서 prompt 자산은 버리지 말고 `PromptTemplateService` 기준으로 수렴시키는 방향이 적절하다.

## 7.3 세션 제목 생성 정책
현재 legacy와 LangChain 구현의 제목 생성 규칙이 다르다.
- legacy: role명 + "대화"
- LangChain: `[역할명] 질문 일부`

운영 일관성을 위해 세션 제목 정책은 하나로 통일하는 것이 좋다.

---

## 8. 리스크

## 리스크 1. `AiChatService` 제거 시 공통 기능까지 함께 손상될 수 있음
### 대응
삭제보다 먼저 역할 분리

## 리스크 2. 운영자가 중복 API/URL을 동시에 사용 중일 수 있음
### 대응
호출 로그 또는 프론트 참조 기준으로 실제 사용 경로 확인 후 정리

## 리스크 3. perf/model status 기능 공백
### 대응
운영 기능을 별도 admin service 로 분리하거나 정책적으로 제거 여부 확정

## 리스크 4. LangChain 경로만 남긴 뒤 장애 시 비교 수단이 사라짐
### 대응
legacy 제거 전까지는 브랜치/태그 보관 또는 테스트 전용 서비스로 한시 유지

---

## 9. 권장 즉시 액션
현재 코드 기준으로 가장 먼저 할 일은 아래다.

1. `AiChatService` 의 공통 기능을 별도 서비스로 분리하는 리팩토링 설계 확정
2. API/URL 중복 제거 정책 결정
3. perf/model status 기능의 존치 여부 결정
4. 이후 legacy 직접 호출 코드 제거

즉, 지금 단계의 핵심은 **LangChain 도입 자체보다 서비스 책임 분해**다.

---

## 10. 향후 고도화 메모
추후 이 프로젝트를 더 키우게 되면, 외부 SaaS형 LangSmith 대신 내부 DB 기반 trace/observability 구조를 재검토하는 것이 적절하다.

이 프로젝트는 이미 DB 기반 세션/이력 저장 구조가 있으므로, 향후 아래 같은 내부 추적 체계를 붙이기 좋다.
- 요청별 실행 trace id
- prompt/version 기록
- 응답 시간/토큰 유사 메트릭 저장
- 실패 원인 분류
- 세션별 품질 모니터링

즉, LangChain4j 도입 1차 목표는 채팅 실행 구조 정리이고, trace 체계는 이후 단계에서 별도로 확장하는 것이 좋다.
