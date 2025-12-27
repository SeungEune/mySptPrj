package biz.system.codemngr.service.impl;

import biz.system.codemngr.dao.CodeMngrDAO;
import biz.system.codemngr.service.CodeMngrService;
import biz.system.codemngr.vo.CodeDetailVO;
import biz.system.codemngr.vo.CodeGroupVO;
import biz.system.codemngr.vo.CodeSearchVO;
import biz.util.SessionUtil;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 코드 관리를 위한 Service 구현체
 * @author Spatialt 개발팀
 * @since 2025.01.XX
 * @version 1.0
 */
@Slf4j
@Service("codeMngrService")
public class CodeMngrServiceImpl implements CodeMngrService {

    @Resource(name = "codeMngrDAO")
    private CodeMngrDAO codeMngrDAO;

    @Override
    public List<CodeGroupVO> getCodeGroupList(CodeSearchVO searchVO) {
        return codeMngrDAO.selectCodeGroupList(searchVO);
    }

    @Override
    public CodeGroupVO getCodeGroupDetail(String codeId) {
        // TODO: 구현 예정
        // return codeMngrDAO.selectCodeGroupDetail(codeId);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO saveCodeGroup(CodeGroupVO codeGroupVO) {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 로그인 사용자 정보
            String loginUserId = SessionUtil.getUserId();
            
            // 필수값 검증
            if (StringUtil.isEmpty(codeGroupVO.getCodeId())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드 ID는 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(codeGroupVO.getCodeIdNm())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드 그룹명은 필수입니다.");
                return resultVO;
            }
            
            // 등록자 정보 설정
            codeGroupVO.setRegisterId(loginUserId);
            
            // 기본값 설정
            if (StringUtil.isEmpty(codeGroupVO.getUseYn())) {
                codeGroupVO.setUseYn("Y");
            }
            
            // 코드 그룹 등록
            int insertResult = codeMngrDAO.insertCodeGroup(codeGroupVO);
            if (insertResult > 0) {
                resultVO.setResultValue(true);
                resultVO.setMessage("코드 그룹이 등록되었습니다.");
            } else {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드 그룹 등록에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("코드 그룹 등록 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("코드 그룹 등록 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }

    @Override
    public ResultVO deleteCodeGroup(String codeId) {
        // TODO: 구현 예정
        ResultVO resultVO = new ResultVO();
        resultVO.setResultValue(false);
        resultVO.setMessage("구현 예정입니다.");
        return resultVO;
    }

    @Override
    public boolean checkCodeIdDuplicate(String codeId) {
        int count = codeMngrDAO.checkCodeIdDuplicate(codeId);
        return count > 0;
    }

    @Override
    public List<CodeDetailVO> getCodeDetailList(String codeId) {
        return codeMngrDAO.selectCodeDetailList(codeId);
    }

    @Override
    public CodeDetailVO getCodeDetail(String codeId, String code) {
        // TODO: 구현 예정
        // CodeDetailVO codeDetailVO = new CodeDetailVO();
        // codeDetailVO.setCodeId(codeId);
        // codeDetailVO.setCode(code);
        // return codeMngrDAO.selectCodeDetail(codeDetailVO);
        return null;
    }

    @Override
    public ResultVO saveCodeDetail(CodeDetailVO codeDetailVO) {
        // TODO: 구현 예정
        ResultVO resultVO = new ResultVO();
        resultVO.setResultValue(false);
        resultVO.setMessage("구현 예정입니다.");
        return resultVO;
    }

    @Override
    public ResultVO deleteCodeDetail(String codeId, String code) {
        // TODO: 구현 예정
        ResultVO resultVO = new ResultVO();
        resultVO.setResultValue(false);
        resultVO.setMessage("구현 예정입니다.");
        return resultVO;
    }
}
