package biz.system.dept.service.impl;

import biz.system.dept.dao.DeptDAO;
import biz.system.dept.service.DeptService;
import biz.system.dept.vo.DeptVO;
import lombok.extern.slf4j.Slf4j;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 부서 관리를 처리하는 서비스 구현 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Slf4j
@Service("deptService")
public class DeptServiceImpl extends EgovAbstractServiceImpl implements DeptService {

    @Resource(name = "deptDAO")
    private DeptDAO deptDAO;

    /**
     * 부서 목록 카운트 조회
     * @param deptVO 검색 조건
     * @return 부서 목록 카운트
     * @throws Exception
     */
    @Override
    public int getDeptListCnt(DeptVO deptVO) throws Exception {
        return deptDAO.selectDeptListCnt(deptVO);
    }

    /**
     * 부서 목록 조회
     * @param deptVO 검색 조건
     * @return 부서 목록
     * @throws Exception
     */
    @Override
    public List<DeptVO> getDeptList(DeptVO deptVO) throws Exception {
        return deptDAO.selectDeptList(deptVO);
    }

    /**
     * 부서 상세 조회
     * @param deptCd 부서코드
     * @return 부서 정보
     * @throws Exception
     */
    @Override
    public DeptVO getDeptDetail(String deptCd) throws Exception {
        return deptDAO.selectDeptDetail(deptCd);
    }

    /**
     * 부서코드 중복 여부를 확인한다
     * @param deptCd 부서코드
     * @return 중복이면 true, 사용 가능하면 false
     * @throws Exception
     */
    @Override
    public boolean checkDeptCodeDuplicate(String deptCd) throws Exception {
        // DB에서 해당 deptCd가 존재하는지 확인
        int count = deptDAO.checkDeptCodeDuplicate(deptCd);
        // 1개 이상 존재하면 중복, 0개면 사용 가능
        return count > 0;
    }
}

