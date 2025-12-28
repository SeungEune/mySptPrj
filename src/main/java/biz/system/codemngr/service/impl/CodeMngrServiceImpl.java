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
import java.util.Map;

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
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateCodeGroupUseYn(List<Map<String, String>> updateList) {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 리스트 유효성 검증
            if (updateList == null || updateList.isEmpty()) {
                resultVO.setResultValue(false);
                resultVO.setMessage("수정할 코드 그룹이 없습니다.");
                return resultVO;
            }
            
            // 각 항목에 대해 유효성 검증 및 업데이트 처리
            for (Map<String, String> item : updateList) {
                String codeId = item.get("codeId");
                String useYn = item.get("useYn");
                
                // 필수값 검증
                if (StringUtil.isEmpty(codeId)) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("코드 ID는 필수입니다.");
                    return resultVO;
                }
                if (StringUtil.isEmpty(useYn) || (!useYn.equals("Y") && !useYn.equals("N"))) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("사용여부는 'Y' 또는 'N'이어야 합니다. (코드: " + codeId + ")");
                    return resultVO;
                }
                
                // 대분류 use_yn 업데이트
                int updateResult = codeMngrDAO.updateCodeGroupUseYn(codeId, useYn);
                if (updateResult <= 0) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("코드 그룹 사용여부 수정에 실패했습니다. (코드: " + codeId + ")");
                    return resultVO;
                }
                
                // use_yn이 'N'이 되면 하위 테이블도 일괄 업데이트
                if ("N".equals(useYn)) {
                    codeMngrDAO.updateCodeDetailUseYnByCodeId(codeId);
                }
            }
            
            resultVO.setResultValue(true);
            resultVO.setMessage("코드 그룹 사용여부가 수정되었습니다.");
            
        } catch (Exception e) {
            log.error("코드 그룹 사용여부 수정 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("코드 그룹 사용여부 수정 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }

    @Override
    public boolean checkCodeIdDuplicate(String codeId) {
        int count = codeMngrDAO.checkCodeIdDuplicate(codeId);
        return count > 0;
    }

    @Override
    public boolean checkCodeDetailDuplicate(String codeId, String code) {
        int count = codeMngrDAO.checkCodeDetailDuplicate(codeId, code);
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
    @Transactional(rollbackFor = Exception.class)
    public ResultVO saveCodeDetail(CodeDetailVO codeDetailVO) {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 로그인 사용자 정보
            String loginUserId = SessionUtil.getUserId();
            
            // 필수값 검증
            if (StringUtil.isEmpty(codeDetailVO.getCodeId())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드 ID는 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(codeDetailVO.getCode())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드는 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(codeDetailVO.getCodeNm())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드명은 필수입니다.");
                return resultVO;
            }
            
            // 중복 확인
            int duplicateCount = codeMngrDAO.checkCodeDetailDuplicate(
                codeDetailVO.getCodeId(), 
                codeDetailVO.getCode()
            );
            if (duplicateCount > 0) {
                resultVO.setResultValue(false);
                resultVO.setMessage("이미 사용 중인 코드입니다.");
                return resultVO;
            }
            
            // 등록자 정보 설정
            codeDetailVO.setRegisterId(loginUserId);
            
            // 기본값 설정
            if (StringUtil.isEmpty(codeDetailVO.getUseYn())) {
                codeDetailVO.setUseYn("Y");
            }
            
            // codeOrder가 없으면 기존 최대값 + 1로 설정
            if (codeDetailVO.getCodeOrder() == null || codeDetailVO.getCodeOrder() < 1) {
                List<CodeDetailVO> existingList = codeMngrDAO.selectCodeDetailList(codeDetailVO.getCodeId());
                int maxOrder = 0;
                if (existingList != null && !existingList.isEmpty()) {
                    maxOrder = existingList.stream()
                        .filter(vo -> vo.getCodeOrder() != null)
                        .mapToInt(CodeDetailVO::getCodeOrder)
                        .max()
                        .orElse(0);
                }
                codeDetailVO.setCodeOrder(maxOrder + 1);
            }
            
            // 코드 상세값 등록
            int insertResult = codeMngrDAO.insertCodeDetail(codeDetailVO);
            if (insertResult > 0) {
                resultVO.setResultValue(true);
                resultVO.setMessage("코드 상세값이 등록되었습니다.");
            } else {
                resultVO.setResultValue(false);
                resultVO.setMessage("코드 상세값 등록에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("코드 상세값 등록 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("코드 상세값 등록 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updateCodeDetailUseYn(List<Map<String, String>> updateList) {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 리스트 유효성 검증
            if (updateList == null || updateList.isEmpty()) {
                resultVO.setResultValue(false);
                resultVO.setMessage("수정할 코드 상세값이 없습니다.");
                return resultVO;
            }
            
            // 각 항목에 대해 유효성 검증 및 업데이트 처리
            for (Map<String, String> item : updateList) {
                String codeId = item.get("codeId");
                String code = item.get("code");
                String useYn = item.get("useYn");
                
                // 필수값 검증
                if (StringUtil.isEmpty(codeId)) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("코드 ID는 필수입니다.");
                    return resultVO;
                }
                if (StringUtil.isEmpty(code)) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("코드는 필수입니다.");
                    return resultVO;
                }
                if (StringUtil.isEmpty(useYn) || (!useYn.equals("Y") && !useYn.equals("N"))) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("사용여부는 'Y' 또는 'N'이어야 합니다. (코드: " + codeId + "-" + code + ")");
                    return resultVO;
                }
                
                // 소분류 use_yn 업데이트
                int updateResult = codeMngrDAO.updateCodeDetailUseYn(codeId, code, useYn);
                if (updateResult <= 0) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("코드 상세값 사용여부 수정에 실패했습니다. (코드: " + codeId + "-" + code + ")");
                    return resultVO;
                }
            }
            
            resultVO.setResultValue(true);
            resultVO.setMessage("코드 상세값 사용여부가 수정되었습니다.");
            
        } catch (Exception e) {
            log.error("코드 상세값 사용여부 수정 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("코드 상세값 사용여부 수정 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }
}
