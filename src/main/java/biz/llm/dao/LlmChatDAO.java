package biz.llm.dao;

import biz.llm.vo.LlmChatHistoryVO;
import biz.llm.vo.LlmChatSessionVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LLM 채팅 DAO
 */
@Repository("llmChatDAO")
public class LlmChatDAO extends EgovAbstractMapper {

    public int insertChatSession(LlmChatSessionVO sessionVO) {
        return insert("llmChatDAO.insertChatSession", sessionVO);
    }

    public List<LlmChatSessionVO> selectChatSessionList(LlmChatSessionVO sessionVO) {
        return selectList("llmChatDAO.selectChatSessionList", sessionVO);
    }

    public LlmChatSessionVO selectChatSessionDetail(Long chatSessionId) {
        return selectOne("llmChatDAO.selectChatSessionDetail", chatSessionId);
    }

    public int updateChatSessionLastUpdtDt(Long chatSessionId) {
        return update("llmChatDAO.updateChatSessionLastUpdtDt", chatSessionId);
    }

    public int insertChatHistory(LlmChatHistoryVO historyVO) {
        return insert("llmChatDAO.insertChatHistory", historyVO);
    }

    public List<LlmChatHistoryVO> selectChatHistoryList(Long chatSessionId) {
        return selectList("llmChatDAO.selectChatHistoryList", chatSessionId);
    }

    public Integer selectNextMessageSeq(Long chatSessionId) {
        return selectOne("llmChatDAO.selectNextMessageSeq", chatSessionId);
    }
}
