package biz.llm.service.impl;

import biz.llm.dao.LlmChatDAO;
import biz.llm.service.ChatHistoryService;
import biz.llm.service.LlmChatSessionService;
import biz.llm.vo.LlmChatSessionCreateVO;
import biz.llm.vo.LlmChatSessionDetailVO;
import biz.llm.vo.LlmChatSessionVO;
import biz.llm.vo.LlmPromptRoleType;
import biz.llm.vo.LlmPromptRoleVO;
import biz.util.AuthService;
import biz.util.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * LLM 채팅 세션 및 역할 조회 서비스 구현체
 */
@Service("llmChatSessionService")
public class LlmChatSessionServiceImpl implements LlmChatSessionService {

    @Resource(name = "llmChatDAO")
    private LlmChatDAO llmChatDAO;

    @Resource
    private AuthService authService;

    @Resource(name = "chatHistoryService")
    private ChatHistoryService chatHistoryService;

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
        detailVO.setHistoryList(chatHistoryService.getChatHistoryList(chatSessionId));
        return detailVO;
    }

    private String buildSessionTitle(String sessionTitle, String promptRoleCd) {
        if (!StringUtil.isEmpty(sessionTitle)) {
            String trimmed = sessionTitle.trim();
            return trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
        }

        String roleNm = LlmPromptRoleType.fromCode(promptRoleCd).getRoleNm();
        return roleNm + " 대화";
    }
}
