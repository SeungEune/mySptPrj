package egovframework.com.cmm.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 권한 정보 VO
 * @author Spatialt 개발팀
 * @since 2025.11.24
 * @version 1.0
 */
@Getter
@Setter
public class RoleVO {
    private String roleCd;      // 권한코드 (PK)
    private String roleNm;      // 권한명
    private String roleDc;      // 권한설명
    private String useYn;       // 사용여부
    private String registerId;  // 등록자ID
    private String registDt;    // 등록일시
    private String updusrId;    // 수정자ID
    private String updtDt;      // 수정일시
}

