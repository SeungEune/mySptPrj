package biz.system.dept.service.impl;

import biz.system.dept.dao.DeptDAO;
import biz.system.dept.service.DeptService;
import biz.system.dept.vo.DeptVO;
import biz.util.SessionUtil;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ResultVO;
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

    /**
     * 부서 등록/수정 처리
     * @param deptVO 부서 정보
     * @return 처리 결과
     * @throws Exception
     */
    @Override
    public ResultVO saveDept(DeptVO deptVO) throws Exception {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 로그인 사용자 정보
            String loginUserId = SessionUtil.getUserId();
            
            // 필수값 검증
            if (StringUtil.isEmpty(deptVO.getDeptCd())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("부서코드는 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(deptVO.getDeptNm())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("부서명은 필수입니다.");
                return resultVO;
            }
            
            // 부서순서 검증 (1 이상만 허용)
            if (deptVO.getDeptOrder() != null && deptVO.getDeptOrder() < 1) {
                resultVO.setResultValue(false);
                resultVO.setMessage("부서순서는 1 이상이어야 합니다.");
                return resultVO;
            }
            
            // 기존 부서 조회 (등록/수정 구분)
            DeptVO existingDept = deptDAO.selectDeptDetail(deptVO.getDeptCd());
            boolean isUpdate = (existingDept != null);
            
            // 상위부서코드 빈 문자열 처리
            if (deptVO.getUpperDeptCd() != null && deptVO.getUpperDeptCd().trim().isEmpty()) {
                deptVO.setUpperDeptCd(null);
            }
            
            if (isUpdate) {
                // 수정 모드
                // 수정자 정보 설정
                deptVO.setUpdusrId(loginUserId);
                
                // 부서레벨 계산 (상위부서가 변경된 경우)
                if (!StringUtil.isEmpty(deptVO.getUpperDeptCd())) {
                    DeptVO upperDept = deptDAO.selectDeptDetail(deptVO.getUpperDeptCd());
                    if (upperDept != null) {
                        deptVO.setDeptLevel(upperDept.getDeptLevel() + 1);
                    } else {
                        resultVO.setResultValue(false);
                        resultVO.setMessage("상위부서가 존재하지 않습니다.");
                        return resultVO;
                    }
                } else {
                    // 상위부서가 없으면 레벨 1
                    deptVO.setDeptLevel(1);
                }
                
                // 기본값 설정
                if (StringUtil.isEmpty(deptVO.getUseYn())) {
                    deptVO.setUseYn("Y");
                }
                
                // 부서 정보 수정
                int updateResult = deptDAO.updateDept(deptVO);
                if (updateResult > 0) {
                    resultVO.setResultValue(true);
                    resultVO.setMessage("부서 정보가 수정되었습니다.");
                } else {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("부서 수정에 실패했습니다.");
                }
            } else {
                // 등록 모드
                // 부서코드 중복 체크
                boolean isDuplicate = checkDeptCodeDuplicate(deptVO.getDeptCd());
                if (isDuplicate) {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("이미 사용 중인 부서코드입니다.");
                    return resultVO;
                }
                
                // 부서레벨 계산
                if (!StringUtil.isEmpty(deptVO.getUpperDeptCd())) {
                    DeptVO upperDept = deptDAO.selectDeptDetail(deptVO.getUpperDeptCd());
                    if (upperDept != null) {
                        deptVO.setDeptLevel(upperDept.getDeptLevel() + 1);
                    } else {
                        resultVO.setResultValue(false);
                        resultVO.setMessage("상위부서가 존재하지 않습니다.");
                        return resultVO;
                    }
                } else {
                    // 상위부서가 없으면 레벨 1
                    deptVO.setDeptLevel(1);
                }
                
                // 등록자 정보 설정
                deptVO.setRegisterId(loginUserId);
                
                // 기본값 설정
                if (StringUtil.isEmpty(deptVO.getUseYn())) {
                    deptVO.setUseYn("Y");
                }
                if (deptVO.getDeptOrder() == null) {
                    deptVO.setDeptOrder(1);
                }
                
                // 부서 등록
                int insertResult = deptDAO.insertDept(deptVO);
                if (insertResult > 0) {
                    resultVO.setResultValue(true);
                    resultVO.setMessage("부서가 등록되었습니다.");
                } else {
                    resultVO.setResultValue(false);
                    resultVO.setMessage("부서 등록에 실패했습니다.");
                }
            }
            
        } catch (Exception e) {
            log.error("부서 저장 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("부서 저장 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }

    /**
     * 부서 삭제 처리
     * @param deptCd 부서코드
     * @return 처리 결과
     * @throws Exception
     */
    @Override
    public ResultVO deleteDept(String deptCd) throws Exception {
        ResultVO resultVO = new ResultVO();
        
        try {
            // 필수값 검증
            if (StringUtil.isEmpty(deptCd)) {
                resultVO.setResultValue(false);
                resultVO.setMessage("부서코드는 필수입니다.");
                return resultVO;
            }
            
            // 기존 부서 조회
            DeptVO existingDept = deptDAO.selectDeptDetail(deptCd);
            if (existingDept == null) {
                resultVO.setResultValue(false);
                resultVO.setMessage("존재하지 않는 부서입니다.");
                return resultVO;
            }
            
            // 하위 부서 존재 여부 확인
            if (existingDept.getHasChildren() != null && existingDept.getHasChildren() > 0) {
                resultVO.setResultValue(false);
                resultVO.setMessage("하위 부서가 존재하여 삭제할 수 없습니다.");
                return resultVO;
            }
            
            // 소속 인원 존재 여부 확인
            if (existingDept.getMemberCnt() != null && existingDept.getMemberCnt() > 0) {
                resultVO.setResultValue(false);
                resultVO.setMessage("소속 인원이 존재하여 삭제할 수 없습니다.");
                return resultVO;
            }
            
            // 부서 삭제
            int deleteResult = deptDAO.deleteDept(deptCd);
            if (deleteResult > 0) {
                resultVO.setResultValue(true);
                resultVO.setMessage("부서가 삭제되었습니다.");
            } else {
                resultVO.setResultValue(false);
                resultVO.setMessage("부서 삭제에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("부서 삭제 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("부서 삭제 중 오류가 발생했습니다.");
        }
        
        return resultVO;
    }
}

