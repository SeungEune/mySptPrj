package biz.menu.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 메뉴 정보를 담는 VO 클래스
 */
@Getter
@Setter
public class MenuVO {
    
    /** 메뉴ID */
    private String menuId;
    
    /** 메뉴명 */
    private String menuNm;
    
    /** 상위메뉴ID */
    private String upperMenuId;
    
    /** 메뉴레벨 */
    private Integer menuLevel;
    
    /** 메뉴정렬순서 */
    private Integer menuOrder;
    
    /** 메뉴URL */
    private String menuUrl;
    
    /** 메뉴아이콘 */
    private String menuIcon;
    
    /** 사용여부 */
    private String useYn;
    
    /** 하위 메뉴 목록 */
    private List<MenuVO> subMenuList;
    
    /**
     * 하위 메뉴 목록 초기화
     */
    public List<MenuVO> getSubMenuList() {
        if (subMenuList == null) {
            subMenuList = new ArrayList<>();
        }
        return subMenuList;
    }
}

