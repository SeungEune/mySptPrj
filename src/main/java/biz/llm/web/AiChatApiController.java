package biz.llm.web;

import biz.llm.service.AiChatService;
import biz.llm.vo.*;
import egovframework.com.cmm.response.ApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 채팅 API Controller
 *
 * 동작 흐름
 * 1. 화면 JS에서 /llm/sendMessage.do 로 사용자 질문과 이전 대화 이력을 전달한다.
 * 2. Controller는 전달받은 JSON을 AiChatRequestVO로 바인딩한다.
 * 3. 실제 LLM 호출과 메시지 조합은 Service에서 처리한다.
 * 4. 처리 결과는 프로젝트 공통 응답 형식(ApiResponseVO)으로 감싸서 반환한다.
 * 5. 프론트는 result.answer 값을 꺼내 채팅창에 출력한다.
 */
@Slf4j
@RestController
@RequestMapping("/llm")
public class AiChatApiController {

    /**
     * 로컬 LLM 호출과 응답 파싱을 담당하는 서비스
     */
    @Resource(name = "aiChatService")
    private AiChatService aiChatService;

    /**
     * 채팅 메시지 전송 API
     */
    @PostMapping("/sendMessage.do")
    public ResponseEntity<?> sendMessage(@RequestBody AiChatRequestVO requestVO) {
        try {
            AiChatResponseVO responseVO = aiChatService.sendMessage(requestVO);
            return ApiResponseVO.apiResponse(responseVO, HttpStatus.OK.value(), "응답 성공");
        } catch (IllegalArgumentException e) {
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("AI 메시지 처리 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "AI 응답 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 현재 연결 대상으로 설정된 모델 정보를 조회하는 API
     */
    @GetMapping("/getModelStatus.do")
    public ResponseEntity<?> getModelStatus() {
        try {
            AiChatResponseVO responseVO = aiChatService.getModelStatus();
            return ApiResponseVO.apiResponse(responseVO, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("AI 모델 상태 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "AI 모델 상태 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 동시 요청 기반 성능 테스트 실행 API
     */
    @PostMapping("/runPerfTest.do")
    public ResponseEntity<?> runPerfTest(@RequestBody LlmPerfTestRequestVO requestVO) {
        try {
            LlmPerfTestResponseVO responseVO = aiChatService.runPerformanceTest(requestVO);
            return ApiResponseVO.apiResponse(responseVO, HttpStatus.OK.value(), "성능 테스트가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("LLM 성능 테스트 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "성능 테스트 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * 역할 목록 조회 API
     */
    @GetMapping("/getPromptRoleList.do")
    public ResponseEntity<?> getPromptRoleList() {
        try {
            List<LlmPromptRoleVO> roleList = aiChatService.getPromptRoleList();
            return ApiResponseVO.apiResponse(roleList, null, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("프롬프트 역할 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "프롬프트 역할 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 채팅 세션 생성 API
     */
    @PostMapping("/createSession.do")
    public ResponseEntity<?> createSession(@RequestBody LlmChatSessionCreateVO createVO) {
        try {
            LlmChatSessionVO sessionVO = aiChatService.createChatSession(createVO);
            return ApiResponseVO.apiResponse(sessionVO, HttpStatus.OK.value(), "채팅 세션이 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("채팅 세션 생성 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "채팅 세션 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 채팅 세션 목록 조회 API
     */
    @PostMapping("/getSessionList.do")
    public ResponseEntity<?> getSessionList() {
        try {
            List<LlmChatSessionVO> sessionList = aiChatService.getChatSessionList();
            return ApiResponseVO.apiResponse(sessionList, null, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("채팅 세션 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "채팅 세션 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 채팅 세션 상세 조회 API
     */
    @PostMapping("/getSessionDetail.do")
    public ResponseEntity<?> getSessionDetail(@RequestBody Map<String, Object> params) {
        try {
            Object chatSessionIdObj = params.get("chatSessionId");
            if (chatSessionIdObj == null) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "채팅 세션 ID는 필수입니다.");
            }

            Long chatSessionId = Long.valueOf(String.valueOf(chatSessionIdObj));
            LlmChatSessionDetailVO detailVO = aiChatService.getChatSessionDetail(chatSessionId);
            return ApiResponseVO.apiResponse(detailVO, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (IllegalArgumentException e) {
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("채팅 세션 상세 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "채팅 세션 상세 조회 중 오류가 발생했습니다.");
        }
    }
}
