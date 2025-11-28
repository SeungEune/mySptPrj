package biz.system.menu.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 메뉴-권한 매핑 정보를 담는 VO 클래스
 */
@Getter
@Setter
public class MenuAuthVO {
    
    /** 권한-메뉴 매핑 일련번호 (PK) */
    private Integer roleMenuAuthorSn;
    
    /** 권한코드 */
    private String roleCd;
    
    /** 권한명 (조인) */
    private String roleNm;
    
    /** 메뉴ID */
    private String menuId;
    
    /** 조회 권한 여부 */
    private String readAuthorYn;
    
    /** 생성 권한 여부 */
    private String creatAuthorYn;
    
    /** 수정 권한 여부 */
    private String updtAuthorYn;
    
    /** 삭제 권한 여부 */
    private String deleteAuthorYn;
    
    /** 등록자ID */
    private String registerId;
    
    /** 등록일시 */
    private String registDt;
}

