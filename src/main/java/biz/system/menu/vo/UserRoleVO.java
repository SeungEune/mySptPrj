package biz.system.menu.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자-권한 매핑 정보를 담는 VO 클래스
 */
@Getter
@Setter
public class UserRoleVO {
    
    /** 사용자-권한 매핑 일련번호 (PK) */
    private Integer userRoleSn;
    
    /** 사용자ID */
    private String userId;
    
    /** 사용자명 (조인) */
    private String userNm;
    
    /** 권한코드 */
    private String roleCd;
    
    /** 권한명 (조인) */
    private String roleNm;
    
    /** 등록자ID */
    private String registerId;
    
    /** 등록일시 */
    private String registDt;
}

