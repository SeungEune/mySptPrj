package biz.system.dept.service;

import biz.system.dept.vo.DeptVO;
import egovframework.com.cmm.response.ResultVO;

import java.util.List;

/**
 * 부서 관리를 처리하는 서비스 인터페이스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
public interface DeptService {

    /**
     * 부서 목록 카운트 조회
     * @param deptVO 검색 조건
     * @return 부서 목록 카운트
     * @throws Exception
     */
    int getDeptListCnt(DeptVO deptVO) throws Exception;

    /**
     * 부서 목록 조회
     * @param deptVO 검색 조건
     * @return 부서 목록
     * @throws Exception
     */
    List<DeptVO> getDeptList(DeptVO deptVO) throws Exception;

    /**
     * 부서 상세 조회
     * @param deptCd 부서코드
     * @return 부서 정보
     * @throws Exception
     */
    DeptVO getDeptDetail(String deptCd) throws Exception;

    /**
     * 부서코드 중복 여부를 확인한다
     * @param deptCd 부서코드
     * @return 중복이면 true, 사용 가능하면 false
     * @throws Exception
     */
    boolean checkDeptCodeDuplicate(String deptCd) throws Exception;

    /**
     * 부서 등록/수정 처리
     * @param deptVO 부서 정보
     * @return 처리 결과
     * @throws Exception
     */
    ResultVO saveDept(DeptVO deptVO) throws Exception;

    /**
     * 부서 삭제 처리
     * @param deptCd 부서코드
     * @return 처리 결과
     * @throws Exception
     */
    ResultVO deleteDept(String deptCd) throws Exception;
}

