package biz.system.dept.dao;

import biz.system.dept.vo.DeptVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 부서 관리를 처리하는 DAO 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Repository("deptDAO")
public class DeptDAO extends EgovAbstractMapper {

    /**
     * 부서 목록 카운트 조회
     * @param deptVO 검색 조건
     * @return 부서 목록 카운트
     */
    public int selectDeptListCnt(DeptVO deptVO) {
        return selectOne("deptDAO.selectDeptListCnt", deptVO);
    }

    /**
     * 부서 목록 조회
     * @param deptVO 검색 조건
     * @return 부서 목록
     */
    public List<DeptVO> selectDeptList(DeptVO deptVO) {
        return selectList("deptDAO.selectDeptList", deptVO);
    }

    /**
     * 부서 상세 조회
     * @param deptCd 부서코드
     * @return 부서 정보
     */
    public DeptVO selectDeptDetail(String deptCd) {
        return selectOne("deptDAO.selectDeptDetail", deptCd);
    }

    // 추후 부서 관리 기능 구현 시 사용할 메서드들
    
    /**
     * 부서 등록
     * @param deptVO 부서 정보
     * @return 등록 결과
     */
    public int insertDept(DeptVO deptVO) {
        return insert("deptDAO.insertDept", deptVO);
    }

    /**
     * 부서 수정
     * @param deptVO 부서 정보
     * @return 수정 결과
     */
    public int updateDept(DeptVO deptVO) {
        return update("deptDAO.updateDept", deptVO);
    }

    /**
     * 부서 삭제
     * @param deptCd 부서코드
     * @return 삭제 결과
     */
    public int deleteDept(String deptCd) {
        return delete("deptDAO.deleteDept", deptCd);
    }

    /**
     * 부서 코드 중복 확인
     * @param deptCd 부서코드
     * @return 중복 개수
     */
    public int checkDeptCodeDuplicate(String deptCd) {
        return selectOne("deptDAO.checkDeptCodeDuplicate", deptCd);
    }
}

