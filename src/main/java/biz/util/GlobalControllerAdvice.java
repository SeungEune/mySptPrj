package biz.util;

import biz.login.vo.LoginVO;
import biz.system.menu.service.MenuService;
import biz.system.menu.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 모든 컨트롤러에 공통적으로 적용되는 설정
 * 메뉴 목록을 전역으로 제공하여 권한별 동적 메뉴 구성
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Resource(name = "menuService")
    private MenuService menuService;
    
    /**
     * 모든 뷰에 메뉴 목록을 자동으로 추가
     * 로그인한 사용자의 권한에 따라 메뉴 목록을 동적으로 제공
     * 권한 그룹 권한과 개별 사용자 권한을 모두 고려
     * 
     * @return 권한별 메뉴 목록
     */
    @ModelAttribute("menuList")
    public List<MenuVO> populateMenu() {
        LoginVO loginVO = SessionUtil.getLoginUser();
        
        // 로그인하지 않은 경우 빈 리스트 반환
        if (loginVO == null) {
            return new ArrayList<>();
        }
        
        // 사용자 ID 조회
        String userId = loginVO.getUserId();
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 사용자 권한 코드 조회
        String roleCd = loginVO.getRoleCd();
        
        // 권한 그룹 권한과 개별 사용자 권한을 모두 고려하여 메뉴 목록 조회
        List<MenuVO> menuList = menuService.getMenuListByRoleAndUser(roleCd, userId);
        return menuList;
    }
}

