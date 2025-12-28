package biz.system.codemngr.service;

import biz.system.codemngr.vo.CodeDetailVO;
import biz.system.codemngr.vo.CodeGroupVO;
import biz.system.codemngr.vo.CodeSearchVO;
import egovframework.com.cmm.response.ResultVO;

import java.util.List;
import java.util.Map;

/**
 * 코드 관리를 위한 Service 인터페이스
 * @author Spatialt 개발팀
 * @since 2025.01.XX
 * @version 1.0
 */
public interface CodeMngrService {

    /**
     * 코드 그룹 목록 조회
     * @param searchVO 검색 조건
     * @return 코드 그룹 목록
     */
    List<CodeGroupVO> getCodeGroupList(CodeSearchVO searchVO);

    /**
     * 코드 그룹 상세 조회
     * @param codeId 코드 그룹 ID
     * @return 코드 그룹 정보
     */
    CodeGroupVO getCodeGroupDetail(String codeId);

    /**
     * 코드 그룹 저장 (등록/수정)
     * @param codeGroupVO 코드 그룹 정보
     * @return 처리 결과
     */
    ResultVO saveCodeGroup(CodeGroupVO codeGroupVO);

    /**
     * 코드 그룹 use_yn 업데이트 (일괄 처리)
     * @param updateList 코드 그룹 ID와 useYn의 리스트 (각 Map은 codeId, useYn 키 포함)
     * @return 처리 결과
     */
    ResultVO updateCodeGroupUseYn(List<Map<String, String>> updateList);

    /**
     * 코드 그룹 ID 중복 확인
     * @param codeId 코드 그룹 ID
     * @return 중복 여부
     */
    boolean checkCodeIdDuplicate(String codeId);

    /**
     * 코드 상세값 목록 조회
     * @param codeId 코드 그룹 ID
     * @return 코드 상세값 목록
     */
    List<CodeDetailVO> getCodeDetailList(String codeId);

    /**
     * 코드 상세값 상세 조회
     * @param codeId 코드 그룹 ID
     * @param code 상세 코드값
     * @return 코드 상세값 정보
     */
    CodeDetailVO getCodeDetail(String codeId, String code);

    /**
     * 코드 상세값 저장 (등록/수정)
     * @param codeDetailVO 코드 상세값 정보
     * @return 처리 결과
     */
    ResultVO saveCodeDetail(CodeDetailVO codeDetailVO);

    /**
     * 코드 상세값 중복 확인 (codeId + code 조합)
     * @param codeId 코드 그룹 ID
     * @param code 상세 코드값
     * @return 중복 여부
     */
    boolean checkCodeDetailDuplicate(String codeId, String code);

    /**
     * 코드 상세값 use_yn 업데이트 (일괄 처리)
     * @param updateList 코드 상세값 ID와 useYn의 리스트 (각 Map은 codeId, code, useYn 키 포함)
     * @return 처리 결과
     */
    ResultVO updateCodeDetailUseYn(List<Map<String, String>> updateList);
}
