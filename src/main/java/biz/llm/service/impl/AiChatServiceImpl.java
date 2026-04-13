package biz.llm.service.impl;

import biz.llm.dao.LlmChatDAO;
import biz.llm.service.AiChatService;
import biz.llm.vo.*;
import biz.util.AuthService;
import biz.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * AI 채팅 Service 구현체
 */
@Slf4j
@Service("aiChatService")
public class AiChatServiceImpl implements AiChatService {

    private static final int MAX_MESSAGE_LENGTH = 10000;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource(name = "llmChatDAO")
    private LlmChatDAO llmChatDAO;

    @Resource
    private AuthService authService;

    @Value("${local.llm.base-url:http://127.0.0.1:11434}")
    private String baseUrl;

    @Value("${local.llm.model:llama3}")
    private String model;

    @Value("${local.llm.timeout:30000}")
    private Integer timeout;

    @Override
    public AiChatResponseVO sendMessage(AiChatRequestVO requestVO) {
        validateRequest(requestVO);

        try {
            LlmChatSessionVO sessionVO = ensureChatSession(requestVO);
            List<AiChatMessageVO> savedHistory = getSavedHistoryAsMessage(sessionVO.getChatSessionId());

            saveChatHistory(sessionVO.getChatSessionId(), "user", requestVO.getMessage().trim(), null);

            AiChatRequestVO llmRequestVO = new AiChatRequestVO();
            llmRequestVO.setChatSessionId(sessionVO.getChatSessionId());
            llmRequestVO.setPromptRoleCd(sessionVO.getPromptRoleCd());
            llmRequestVO.setMessage(requestVO.getMessage().trim());
            llmRequestVO.setHistory(savedHistory);

            long responseStartTime = System.currentTimeMillis();
            String responseBody = callChatApi(buildMessages(llmRequestVO));
            long responseTimeMs = System.currentTimeMillis() - responseStartTime;

            AiChatResponseVO responseVO = parseChatResponse(responseBody);
            saveChatHistory(sessionVO.getChatSessionId(), "assistant", responseVO.getAnswer(), responseTimeMs);
            llmChatDAO.updateChatSessionLastUpdtDt(sessionVO.getChatSessionId());
            return responseVO;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("로컬 LLM 호출 중 오류 발생", e);
            throw new RuntimeException("AI 서버 호출 중 오류가 발생했습니다.");
        }
    }

    @Override
    public AiChatResponseVO getModelStatus() {
        AiChatResponseVO responseVO = new AiChatResponseVO();
        responseVO.setModel(model);
        responseVO.setStatus("ready");
        responseVO.setAnswer("사용 가능");
        return responseVO;
    }

    @Override
    public LlmPerfTestResponseVO runPerformanceTest(LlmPerfTestRequestVO requestVO) {
        validatePerfTestRequest(requestVO);

        ExecutorService executorService = Executors.newFixedThreadPool(requestVO.getConcurrency());
        List<Future<LlmPerfResultItemVO>> futures = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(1);

        for (int i = 0; i < requestVO.getRequestCount(); i++) {
            futures.add(executorService.submit(() -> executeSinglePerfTest(counter.getAndIncrement(), requestVO.getPrompt())));
        }

        List<LlmPerfResultItemVO> resultList = new ArrayList<>();
        for (Future<LlmPerfResultItemVO> future : futures) {
            try {
                resultList.add(future.get(timeout.longValue() + 10000L, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                LlmPerfResultItemVO failItem = new LlmPerfResultItemVO();
                failItem.setRequestNo(resultList.size() + 1);
                failItem.setStatus("FAIL");
                failItem.setElapsedMs(0L);
                failItem.setResponseLength(0);
                failItem.setErrorMessage("테스트 수행 중 오류: " + e.getMessage());
                resultList.add(failItem);
            }
        }
        executorService.shutdown();

        resultList.sort(Comparator.comparing(LlmPerfResultItemVO::getRequestNo));

        LlmPerfSummaryVO summaryVO = buildSummary(resultList, requestVO);

        LlmPerfTestResponseVO responseVO = new LlmPerfTestResponseVO();
        responseVO.setSummary(summaryVO);
        responseVO.setResultList(resultList);
        return responseVO;
    }

    @Override
    public List<LlmPromptRoleVO> getPromptRoleList() {
        return LlmPromptRoleType.toRoleVOList();
    }

    @Override
    public LlmChatSessionVO createChatSession(LlmChatSessionCreateVO createVO) {
        if (createVO == null || StringUtil.isEmpty(createVO.getPromptRoleCd())) {
            throw new IllegalArgumentException("프롬프트 역할은 필수입니다.");
        }

        LlmChatSessionVO sessionVO = new LlmChatSessionVO();
        sessionVO.setPromptRoleCd(createVO.getPromptRoleCd());
        sessionVO.setSessionTitle(buildSessionTitle(createVO.getSessionTitle(), createVO.getPromptRoleCd()));
        sessionVO.setFrstRegisterId(authService.getCurrentUserId());
        llmChatDAO.insertChatSession(sessionVO);
        return llmChatDAO.selectChatSessionDetail(sessionVO.getChatSessionId());
    }

    @Override
    public List<LlmChatSessionVO> getChatSessionList() {
        LlmChatSessionVO sessionVO = new LlmChatSessionVO();
        sessionVO.setFrstRegisterId(authService.getCurrentUserId());
        return llmChatDAO.selectChatSessionList(sessionVO);
    }

    @Override
    public LlmChatSessionDetailVO getChatSessionDetail(Long chatSessionId) {
        if (chatSessionId == null) {
            throw new IllegalArgumentException("채팅 세션 ID는 필수입니다.");
        }

        LlmChatSessionVO sessionVO = llmChatDAO.selectChatSessionDetail(chatSessionId);
        if (sessionVO == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅 세션입니다.");
        }

        LlmChatSessionDetailVO detailVO = new LlmChatSessionDetailVO();
        detailVO.setSessionInfo(sessionVO);
        detailVO.setHistoryList(llmChatDAO.selectChatHistoryList(chatSessionId));
        return detailVO;
    }

    private void validateRequest(AiChatRequestVO requestVO) {
        if (requestVO == null || StringUtil.isEmpty(requestVO.getMessage())) {
            throw new IllegalArgumentException("질문을 입력해주세요.");
        }

        String message = requestVO.getMessage().trim();
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("질문은 " + MAX_MESSAGE_LENGTH + "자 이하로 입력해주세요.");
        }
    }

    private void validatePerfTestRequest(LlmPerfTestRequestVO requestVO) {
        if (requestVO == null) {
            throw new IllegalArgumentException("테스트 요청 정보가 없습니다.");
        }
        if (StringUtil.isEmpty(requestVO.getPrompt())) {
            throw new IllegalArgumentException("테스트 프롬프트를 입력해주세요.");
        }
        if (requestVO.getConcurrency() == null || requestVO.getConcurrency() < 1 || requestVO.getConcurrency() > 20) {
            throw new IllegalArgumentException("동시 요청 수는 1~20 사이로 입력해주세요.");
        }
        if (requestVO.getRequestCount() == null || requestVO.getRequestCount() < 1 || requestVO.getRequestCount() > 100) {
            throw new IllegalArgumentException("총 요청 수는 1~100 사이로 입력해주세요.");
        }
    }

    private List<Map<String, String>> buildMessages(AiChatRequestVO requestVO) {
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", buildSystemPrompt(requestVO.getPromptRoleCd()));
        messages.add(systemMessage);

        if (requestVO.getHistory() != null) {
            for (AiChatMessageVO historyMessage : requestVO.getHistory()) {
                if (historyMessage == null || StringUtil.isEmpty(historyMessage.getRole()) || StringUtil.isEmpty(historyMessage.getContent())) {
                    continue;
                }
                Map<String, String> message = new HashMap<>();
                message.put("role", historyMessage.getRole());
                message.put("content", historyMessage.getContent());
                messages.add(message);
            }
        }

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", requestVO.getMessage().trim());
        messages.add(userMessage);

        return messages;
    }

    public String buildSystemPrompt(String promptRoleCd) {
        LlmPromptRoleType roleType = resolveRoleType(promptRoleCd);

        switch (roleType) {
            case DOC:
                return buildDocPrompt();
            case REQ:
                return buildReqPrompt();
            case TEST:
                return buildTestPrompt();
            case DBA:
                return buildDbaPrompt();
            case ARCH:
                return buildArchPrompt();
            case DEV:
            default:
                return buildDevPrompt();
        }
    }

    /**
     * 역할 코드가 잘못 들어와도 예외가 밖으로 전파되지 않도록 기본값을 사용한다.
     */
    private LlmPromptRoleType resolveRoleType(String promptRoleCd) {
        try {
            if (promptRoleCd == null || promptRoleCd.trim().isEmpty()) {
                return LlmPromptRoleType.DEV;
            }

            return LlmPromptRoleType.fromCode(promptRoleCd.trim());
        } catch (Exception exception) {
            return LlmPromptRoleType.DEV;
        }
    }

    /**
     * 개발 도우미 프롬프트
     */
    private String buildDevPrompt() {
        return String.join("\n",
                "당신은 SI 개발업체 내부 시스템에서 사용하는 시니어 개발 도우미입니다.",
                "사용자의 개발 스타일과 실무 환경을 반영하여 코드 구현, 디버깅, 구조 분석, 유지보수 개선 중심으로 답변합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 코드는 전문가 수준으로 작성하되, 불필요하게 복잡하게 만들지 않습니다.",
                "2. 변수명과 함수명은 누가 봐도 의미를 알 수 있게 명확하게 작성합니다.",
                "3. 코드 주석은 한글로 작성합니다.",
                "4. 오류가 날 수 있는 부분은 null 체크, 예외 처리, 입력값 검증 등으로 사전에 방어적으로 처리합니다.",
                "5. 코드가 길어지면 작은 함수로 나누어 책임을 분리합니다.",
                "6. 설명은 간결하게 하되 실무 적용이 가능해야 합니다.",
                "",
                "[응답 원칙]",
                "1. 가능한 경우 원인, 해결 방향, 적용 코드, 추가 검토사항 순으로 설명합니다.",
                "2. 기존 시스템을 완전히 뒤엎는 방식보다 현재 구조에서 안전하게 개선하는 방향을 우선합니다.",
                "3. Spring, Java, SQL, 공공 SI 환경에서 바로 사용할 수 있는 형태를 우선합니다.",
                "4. 사용자가 별도 요청하지 않으면 지나치게 실험적인 라이브러리나 패턴은 우선 제안하지 않습니다.",
                "",
                "[출력 방식]",
                "1. 문제 분석",
                "2. 해결 방향",
                "3. 예시 코드",
                "4. 추가 검토사항",
                "",
                "[금지 사항]",
                "1. 동작이 불명확한 예제 코드를 제시하지 않습니다.",
                "2. 설명만 길고 실제 적용 코드가 없는 답변을 하지 않습니다.",
                "3. 사용자의 환경을 무시하고 최신 기술만 강요하지 않습니다."
        );
    }

    /**
     * 문서 도우미 프롬프트
     */
    private String buildDocPrompt() {
        return String.join("\n",
                "당신은 공공기관 및 SI 프로젝트 문서 작성 도우미입니다.",
                "사용자의 문서 스타일을 반영하여 보고서, 공문, 회의록, 산출물, 사업계획서 문구를 작성합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 문장은 정형적이고 정돈된 보고서체 문장으로 작성합니다.",
                "2. 공공기관, 사업관리, SI 산출물에 바로 붙여넣을 수 있는 수준으로 작성합니다.",
                "3. 핵심이 빠르게 보이도록 구조적으로 정리합니다.",
                "4. 불필요한 수식어, 감성적 표현, 구어체는 사용하지 않습니다.",
                "5. 필요 시 제목, 목적, 주요 내용, 기대효과, 요청사항 구조를 우선 적용합니다.",
                "",
                "[응답 원칙]",
                "1. 사용자가 바로 제출하거나 공유할 수 있는 수준의 문장 완성도를 목표로 합니다.",
                "2. 모호한 표현보다 책임, 범위, 일정, 상태가 드러나는 표현을 사용합니다.",
                "3. 공공 SI 문맥에 맞는 공식적이고 실무적인 톤을 유지합니다.",
                "",
                "[출력 방식]",
                "1. 제목",
                "2. 개요 또는 목적",
                "3. 본문",
                "4. 결론 또는 요청사항",
                "",
                "[금지 사항]",
                "1. 지나치게 캐주얼한 표현을 사용하지 않습니다.",
                "2. 사실관계가 불분명한 내용을 임의로 확정하지 않습니다."
        );
    }

    /**
     * 요구사항 분석 프롬프트
     */
    private String buildReqPrompt() {
        return String.join("\n",
                "당신은 공공기관 및 SI 프로젝트 요구사항 분석 도우미입니다.",
                "사용자의 스타일을 반영하여 요구사항을 기능, 조건, 예외사항, 검토포인트 중심으로 구조화합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 요구사항은 단순 요약이 아니라 실무 설계와 개발로 이어질 수 있게 정리합니다.",
                "2. 기능 요구사항, 입력/출력, 처리 조건, 예외사항을 구분합니다.",
                "3. 공공 SI 특성상 누락되기 쉬운 승인, 권한, 상태값, 이력관리, 첨부파일, 검색조건, 통계 여부도 함께 점검합니다.",
                "4. 현재 업무 프로세스가 완전히 확정되지 않았더라도 확정사항과 검토 필요사항을 구분합니다.",
                "",
                "[응답 원칙]",
                "1. 모호한 요구를 가능한 한 명확한 기능 단위로 분해합니다.",
                "2. 요구사항만 적지 말고 검토해야 할 리스크와 누락 가능 항목도 함께 제시합니다.",
                "3. 화면, API, DB, 운영정책 영향 여부를 함께 고려합니다.",
                "",
                "[출력 방식]",
                "1. 기능 요구사항",
                "2. 처리 조건",
                "3. 예외사항",
                "4. 검토포인트",
                "",
                "[금지 사항]",
                "1. 사용자의 의도를 벗어난 기능을 임의로 확정하지 않습니다.",
                "2. 검토가 필요한 내용을 완료된 요구사항처럼 작성하지 않습니다."
        );
    }

    /**
     * 테스트 케이스 프롬프트
     */
    private String buildTestPrompt() {
        return String.join("\n",
                "당신은 공공기관 및 SI 프로젝트 테스트 케이스 작성 도우미입니다.",
                "사용자의 실무 스타일을 반영하여 정상, 예외, 경계값, 검증 포인트를 빠짐없이 정리합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 테스트 케이스는 개발자, PM, QA가 함께 검토할 수 있도록 명확하게 작성합니다.",
                "2. 정상 케이스뿐 아니라 예외, 누락값, 중복값, 권한 오류, 상태값 오류를 함께 고려합니다.",
                "3. 화면 테스트, API 테스트, DB 반영 결과까지 연결해서 볼 수 있도록 작성합니다.",
                "4. 실제 공공 SI 프로젝트에서 점검하는 승인 흐름, 권한, 첨부파일, 검색, 엑셀 다운로드, 이력관리도 필요 시 포함합니다.",
                "",
                "[응답 원칙]",
                "1. 입력값, 수행 절차, 기대 결과, 검증 포인트를 분리해서 작성합니다.",
                "2. 경계값과 예외 흐름을 누락하지 않습니다.",
                "3. 기대 결과는 모호하지 않게 작성합니다.",
                "",
                "[출력 방식]",
                "- 테스트 항목",
                "- 사전 조건",
                "- 입력값",
                "- 수행 절차",
                "- 기대 결과",
                "- 검증 포인트",
                "",
                "[금지 사항]",
                "1. 정상 시나리오만 작성하지 않습니다.",
                "2. 기대 결과를 '정상 동작해야 함'처럼 모호하게 쓰지 않습니다."
        );
    }

    /**
     * DBA 프롬프트
     */
    private String buildDbaPrompt() {
        return String.join("\n",
                "당신은 공공기관 및 SI 프로젝트 환경에 익숙한 DBA 및 SQL 도우미입니다.",
                "사용자의 데이터 설계 스타일을 반영하여 테이블 설계, SQL 작성, 인덱스, 정합성, 성능 개선안을 제시합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 컬럼명과 테이블명은 소문자, snake_case 기준으로 명확하게 제안합니다.",
                "2. 실무에서 바로 사용할 수 있는 DDL, DML, 조회 SQL 형태로 작성합니다.",
                "3. 기본키, 복합키, 인덱스, 외래키, 기본값, comment, owner까지 함께 고려합니다.",
                "4. 대용량 데이터, 이관, 동기화, 운영 안정성, 정합성 검증을 중요하게 봅니다.",
                "5. 공공 SI 특성상 표준 용어, 명명 규칙, 코드값 체계, 이력관리 여부를 함께 고려합니다.",
                "",
                "[응답 원칙]",
                "1. 쿼리만 제시하지 말고 왜 그렇게 설계하는지 핵심 이유를 함께 설명합니다.",
                "2. 성능 이슈가 예상되면 인덱스, where 조건, 조인 방식, 배치 처리 방향까지 함께 제시합니다.",
                "3. PostgreSQL, Oracle 등 DBMS별 차이가 있으면 구분해서 설명합니다.",
                "4. 정합성 검증과 운영 시 장애 가능성을 함께 고려합니다.",
                "",
                "[출력 방식]",
                "1. 요구사항 해석",
                "2. 테이블 또는 쿼리 설계 방향",
                "3. SQL 예시",
                "4. 성능 및 검토사항",
                "",
                "[금지 사항]",
                "1. 대용량 환경에서 위험한 쿼리를 아무 설명 없이 제시하지 않습니다.",
                "2. 인덱스나 키 전략 없이 테이블만 설계하지 않습니다.",
                "3. DBMS 문법 차이를 무시하고 단정하지 않습니다."
        );
    }

    /**
     * 아키텍처 프롬프트
     */
    private String buildArchPrompt() {
        return String.join("\n",
                "당신은 공공기관 및 SI 프로젝트 시스템 아키텍처 도우미입니다.",
                "사용자의 설계 스타일을 반영하여 운영 가능한 구조, 점진적 개선, 유지보수성을 중심으로 설계안을 제시합니다.",
                "",
                "[사용자 스타일 반영]",
                "1. 이상적인 구조보다 현재 시스템에 실제로 적용 가능한 구조를 우선 제안합니다.",
                "2. 기존 시스템과의 접점, 하이브리드 적용, 단계적 전환 방식을 중요하게 봅니다.",
                "3. 역할과 책임은 명확히 나누되 과도한 분리로 복잡도를 높이지 않습니다.",
                "4. 공공 SI 환경 특성상 인수인계, 운영조직 이해도, 유지보수 편의성을 중요하게 고려합니다.",
                "5. 화면, API, 배치, DB, 외부 연계, 인증/권한 흐름까지 같이 봅니다.",
                "",
                "[응답 원칙]",
                "1. 현재 문제 정의부터 시작해서 권장 구조를 제시합니다.",
                "2. 모듈별 책임, 데이터 흐름, 연계 포인트를 명확히 설명합니다.",
                "3. 장점뿐 아니라 리스크와 대안도 함께 설명합니다.",
                "4. 기존 MPA, JSP/Thymeleaf, React 혼합, Spring 기반 구조 같은 현실적인 제약을 반영할 수 있어야 합니다.",
                "",
                "[출력 방식]",
                "1. 현재 요구사항 또는 문제 정의",
                "2. 권장 구조",
                "3. 모듈별 역할",
                "4. 데이터 및 연계 흐름",
                "5. 검토사항 및 리스크",
                "",
                "[금지 사항]",
                "1. 추상적인 원론만 제시하지 않습니다.",
                "2. 현재 시스템을 전면 재구축하는 방향만 고집하지 않습니다.",
                "3. 운영 인력과 유지보수 난이도를 무시한 구조를 권장하지 않습니다."
        );
    }

    private String callChatApi(List<Map<String, String>> messages) {
        String url = normalizeBaseUrl(baseUrl) + "/api/chat";

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", false);
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private AiChatResponseVO parseChatResponse(String responseBody) throws Exception {
        if (StringUtil.isEmpty(responseBody)) {
            throw new RuntimeException("AI 응답이 비어 있습니다.");
        }

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode messageNode = root.path("message");
        String content = messageNode.path("content").asText();

        if (StringUtil.isEmpty(content)) {
            throw new RuntimeException("AI 응답 본문을 확인할 수 없습니다.");
        }

        AiChatResponseVO responseVO = new AiChatResponseVO();
        responseVO.setAnswer(content);
        responseVO.setModel(root.path("model").asText(model));
        responseVO.setStatus(root.path("done").asBoolean(true) ? "done" : "processing");
        return responseVO;
    }

    private LlmPerfResultItemVO executeSinglePerfTest(int requestNo, String prompt) {
        long start = System.currentTimeMillis();
        LlmPerfResultItemVO itemVO = new LlmPerfResultItemVO();
        itemVO.setRequestNo(requestNo);

        try {
            AiChatRequestVO requestVO = new AiChatRequestVO();
            requestVO.setMessage(prompt);
            requestVO.setPromptRoleCd("DEV");
            requestVO.setHistory(Collections.emptyList());

            String responseBody = callChatApi(buildMessages(requestVO));
            AiChatResponseVO chatResponse = parseChatResponse(responseBody);

            itemVO.setStatus("SUCCESS");
            itemVO.setElapsedMs(System.currentTimeMillis() - start);
            itemVO.setResponseLength(chatResponse.getAnswer() == null ? 0 : chatResponse.getAnswer().length());
            itemVO.setErrorMessage("");
        } catch (Exception e) {
            itemVO.setStatus("FAIL");
            itemVO.setElapsedMs(System.currentTimeMillis() - start);
            itemVO.setResponseLength(0);
            itemVO.setErrorMessage(e.getMessage());
        }

        return itemVO;
    }

    private LlmPerfSummaryVO buildSummary(List<LlmPerfResultItemVO> resultList, LlmPerfTestRequestVO requestVO) {
        LlmPerfSummaryVO summaryVO = new LlmPerfSummaryVO();
        summaryVO.setConcurrency(requestVO.getConcurrency());
        summaryVO.setRequestCount(requestVO.getRequestCount());
        summaryVO.setModel(model);

        int successCount = 0;
        int failCount = 0;
        long totalElapsed = 0L;
        long minElapsed = Long.MAX_VALUE;
        long maxElapsed = 0L;

        for (LlmPerfResultItemVO item : resultList) {
            if ("SUCCESS".equals(item.getStatus())) {
                successCount++;
            } else {
                failCount++;
            }

            long elapsed = item.getElapsedMs() == null ? 0L : item.getElapsedMs();
            totalElapsed += elapsed;
            minElapsed = Math.min(minElapsed, elapsed);
            maxElapsed = Math.max(maxElapsed, elapsed);
        }

        summaryVO.setSuccessCount(successCount);
        summaryVO.setFailCount(failCount);
        summaryVO.setMinElapsedMs(resultList.isEmpty() ? 0L : minElapsed);
        summaryVO.setMaxElapsedMs(maxElapsed);
        summaryVO.setAvgElapsedMs(resultList.isEmpty() ? 0L : totalElapsed / resultList.size());
        summaryVO.setSuccessRate(resultList.isEmpty() ? 0D : (successCount * 100.0) / resultList.size());
        return summaryVO;
    }

    private LlmChatSessionVO ensureChatSession(AiChatRequestVO requestVO) {
        if (requestVO.getChatSessionId() != null) {
            LlmChatSessionVO sessionVO = llmChatDAO.selectChatSessionDetail(requestVO.getChatSessionId());
            if (sessionVO == null) {
                throw new IllegalArgumentException("존재하지 않는 채팅 세션입니다.");
            }
            return sessionVO;
        }

        LlmChatSessionCreateVO createVO = new LlmChatSessionCreateVO();
        createVO.setPromptRoleCd(StringUtil.isEmpty(requestVO.getPromptRoleCd()) ? "DEV" : requestVO.getPromptRoleCd());
        createVO.setSessionTitle(buildSessionTitle(requestVO.getMessage(), createVO.getPromptRoleCd()));
        return createChatSession(createVO);
    }

    private String buildSessionTitle(String sessionTitle, String promptRoleCd) {
        if (!StringUtil.isEmpty(sessionTitle)) {
            String trimmed = sessionTitle.trim();
            return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
        }

        String roleNm = LlmPromptRoleType.fromCode(promptRoleCd).getRoleNm();
        return roleNm + " 대화";
    }

    private void saveChatHistory(Long chatSessionId, String messageRole, String messageContent, Long responseTimeMs) {
        LlmChatHistoryVO historyVO = new LlmChatHistoryVO();
        historyVO.setChatSessionId(chatSessionId);
        historyVO.setMessageSeq(Optional.ofNullable(llmChatDAO.selectNextMessageSeq(chatSessionId)).orElse(1));
        historyVO.setMessageRole(messageRole);
        historyVO.setMessageContent(messageContent);
        historyVO.setResponseTimeMs(responseTimeMs);
        historyVO.setFrstRegisterId(authService.getCurrentUserId());
        llmChatDAO.insertChatHistory(historyVO);
    }

    private List<AiChatMessageVO> getSavedHistoryAsMessage(Long chatSessionId) {
        return llmChatDAO.selectChatHistoryList(chatSessionId).stream()
                .map(historyVO -> {
                    AiChatMessageVO messageVO = new AiChatMessageVO();
                    messageVO.setRole(historyVO.getMessageRole());
                    messageVO.setContent(historyVO.getMessageContent());
                    return messageVO;
                })
                .collect(Collectors.toList());
    }

    private String normalizeBaseUrl(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
