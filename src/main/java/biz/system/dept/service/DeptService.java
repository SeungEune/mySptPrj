package biz.system.dept.service;

import biz.system.dept.vo.DeptVO;

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

    // 추후 부서 관리 기능 구현 시 사용할 메서드들 (인터페이스만 정의)
    // ResultVO saveDept(DeptVO deptVO) throws Exception;
    // ResultVO deleteDept(String deptCd) throws Exception;
    // boolean checkDeptCodeDuplicate(String deptCd) throws Exception;
}

