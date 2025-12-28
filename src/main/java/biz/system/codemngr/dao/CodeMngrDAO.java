package biz.system.codemngr.dao;

import biz.system.codemngr.vo.CodeDetailVO;
import biz.system.codemngr.vo.CodeGroupVO;
import biz.system.codemngr.vo.CodeSearchVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 코드 관리 DAO 클래스
 * @author Spatialt 개발팀
 * @since 2025.01.XX
 * @version 1.0
 */
@Repository("codeMngrDAO")
public class CodeMngrDAO extends EgovAbstractMapper {

    /**
     * 코드 그룹 목록 조회
     * @param searchVO 검색 조건
     * @return 코드 그룹 목록
     */
    public List<CodeGroupVO> selectCodeGroupList(CodeSearchVO searchVO) {
        return selectList("codeMngrMapper.selectCodeGroupList", searchVO);
    }

    /**
     * 코드 그룹 상세 조회
     * @param codeId 코드 그룹 ID
     * @return 코드 그룹 정보
     */
    public CodeGroupVO selectCodeGroupDetail(String codeId) {
        // TODO: 구현 예정
        // return selectOne("codeMngrMapper.selectCodeGroupDetail", codeId);
        return null;
    }

    /**
     * 코드 그룹 등록
     * @param codeGroupVO 코드 그룹 정보
     * @return 등록 결과
     */
    public int insertCodeGroup(CodeGroupVO codeGroupVO) {
        return insert("codeMngrMapper.insertCodeGroup", codeGroupVO);
    }

    /**
     * 코드 그룹 수정
     * @param codeGroupVO 코드 그룹 정보
     * @return 수정 결과
     */
    public int updateCodeGroup(CodeGroupVO codeGroupVO) {
        // TODO: 구현 예정
        // return update("codeMngrMapper.updateCodeGroup", codeGroupVO);
        return 0;
    }

    /**
     * 코드 그룹 use_yn 업데이트
     * @param codeId 코드 그룹 ID
     * @param useYn 사용여부 ('Y' 또는 'N')
     * @return 업데이트 결과
     */
    public int updateCodeGroupUseYn(String codeId, String useYn) {
        Map<String, String> params = new HashMap<>();
        params.put("codeId", codeId);
        params.put("useYn", useYn);
        return update("codeMngrMapper.updateCodeGroupUseYn", params);
    }

    /**
     * 코드 상세값 use_yn 일괄 업데이트 (대분류 비활성화 시)
     * @param codeId 코드 그룹 ID
     * @return 업데이트 결과
     */
    public int updateCodeDetailUseYnByCodeId(String codeId) {
        return update("codeMngrMapper.updateCodeDetailUseYnByCodeId", codeId);
    }

    /**
     * 코드 그룹 ID 중복 확인
     * @param codeId 코드 그룹 ID
     * @return 중복 개수
     */
    public int checkCodeIdDuplicate(String codeId) {
        return selectOne("codeMngrMapper.checkCodeIdDuplicate", codeId);
    }

    /**
     * 코드 상세값 목록 조회
     * @param codeId 코드 그룹 ID
     * @return 코드 상세값 목록
     */
    public List<CodeDetailVO> selectCodeDetailList(String codeId) {
        return selectList("codeMngrMapper.selectCodeDetailList", codeId);
    }

    /**
     * 코드 상세값 상세 조회
     * @param codeDetailVO 코드 그룹 ID와 상세 코드값
     * @return 코드 상세값 정보
     */
    public CodeDetailVO selectCodeDetail(CodeDetailVO codeDetailVO) {
        // TODO: 구현 예정
        // return selectOne("codeMngrMapper.selectCodeDetail", codeDetailVO);
        return null;
    }

    /**
     * 코드 상세값 등록
     * @param codeDetailVO 코드 상세값 정보
     * @return 등록 결과
     */
    public int insertCodeDetail(CodeDetailVO codeDetailVO) {
        return insert("codeMngrMapper.insertCodeDetail", codeDetailVO);
    }

    /**
     * 코드 상세값 중복 확인 (codeId + code 조합)
     * @param codeId 코드 그룹 ID
     * @param code 상세 코드값
     * @return 중복 개수
     */
    public int checkCodeDetailDuplicate(String codeId, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("codeId", codeId);
        params.put("code", code);
        return selectOne("codeMngrMapper.checkCodeDetailDuplicate", params);
    }

    /**
     * 코드 상세값 수정
     * @param codeDetailVO 코드 상세값 정보
     * @return 수정 결과
     */
    public int updateCodeDetail(CodeDetailVO codeDetailVO) {
        // TODO: 구현 예정
        // return update("codeMngrMapper.updateCodeDetail", codeDetailVO);
        return 0;
    }

    /**
     * 코드 상세값 use_yn 업데이트
     * @param codeId 코드 그룹 ID
     * @param code 상세 코드값
     * @param useYn 사용여부 ('Y' 또는 'N')
     * @return 업데이트 결과
     */
    public int updateCodeDetailUseYn(String codeId, String code, String useYn) {
        Map<String, String> params = new HashMap<>();
        params.put("codeId", codeId);
        params.put("code", code);
        params.put("useYn", useYn);
        return update("codeMngrMapper.updateCodeDetailUseYn", params);
    }
}
