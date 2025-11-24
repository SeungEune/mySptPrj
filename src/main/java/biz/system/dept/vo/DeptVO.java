package biz.system.dept.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 부서 정보 VO
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Getter
@Setter
public class DeptVO {
    
    // 기본 정보
    private String deptCd;           // 부서코드 (PK)
    private String deptNm;           // 부서명
    private String upperDeptCd;      // 상위부서코드
    private String upperDeptNm;      // 상위부서명 (조인)
    private Integer deptLevel;       // 부서레벨
    private Integer deptOrder;       // 부서순서
    private String deptMngrId;       // 부서담당자ID
    private String useYn;            // 사용여부
    
    // 시스템 정보
    private String registerId;       // 등록자ID
    private String registDt;         // 등록일시
    private String updusrId;         // 수정자ID
    private String updtDt;           // 수정일시
    
    // 조회용
    private ComDefaultVO searchVO = new ComDefaultVO();
    private Integer memberCnt;       // 소속 인원수 (통계)
    
    // 트리 구조용
    private Integer treeLevel;       // 트리 깊이 (0부터 시작)
    private String path;             // 계층 경로 (예: "ROOT>TECH>DEV")
    private Integer hasChildren;     // 하위 부서 존재 여부 (0 또는 1)
}

