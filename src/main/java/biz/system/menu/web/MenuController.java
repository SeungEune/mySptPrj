package biz.system.menu.web;

import biz.system.menu.service.MenuService;
import biz.system.menu.vo.MenuAuthVO;
import biz.system.menu.vo.MenuVO;
import biz.system.menu.vo.UserMenuAuthVO;
import biz.system.menu.vo.UserRoleVO;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 관리를 처리하는 컨트롤러 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.28
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/system/menu")
public class MenuController {

    @Resource(name = "menuService")
    private MenuService menuService;

    /**
     * 메뉴 목록 화면
     * @param model Model 객체
     * @return 메뉴 목록 페이지
     */
    @GetMapping("/menuListForm.do")
    public String menuListForm(Model model) {
        return "system/menu/menuListForm";
    }
    
    /**
     * 메뉴 목록 조회 (POST + JSON)
     * @param menuVO 검색 조건
     * @return 메뉴 목록 (트리 구조)
     */
    @ResponseBody
    @PostMapping("/getMenuList.do")
    public ResponseEntity<?> getMenuList(@RequestBody MenuVO menuVO) {
        try {
            List<MenuVO> list = menuService.getMenuList(menuVO);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("메뉴 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "메뉴 목록 조회 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 메뉴 상세 조회
     * @param params 파라미터 (menuId)
     * @return 메뉴 상세 정보
     */
    @ResponseBody
    @PostMapping("/getMenuDetail.do")
    public ResponseEntity<?> getMenuDetail(@RequestBody Map<String, String> params) {
        try {
            String menuId = params.get("menuId");
            MenuVO vo = menuService.getMenuDetail(menuId);
            return ApiResponseVO.apiResponse(vo, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("메뉴 상세 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "메뉴 상세 조회 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 메뉴 저장 (등록/수정)
     * @param menuVO 메뉴 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/saveMenu.do")
    public ResponseEntity<?> saveMenu(@RequestBody MenuVO menuVO) {
        try {
            ResultVO result = menuService.saveMenu(menuVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("메뉴 저장 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "메뉴 저장 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 메뉴 삭제
     * @param params 파라미터 (menuId)
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/deleteMenu.do")
    public ResponseEntity<?> deleteMenu(@RequestBody Map<String, String> params) {
        try {
            String menuId = params.get("menuId");
            ResultVO result = menuService.deleteMenu(menuId);
            if (result.isResultValue()) {
                 return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                 return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "메뉴 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 메뉴에 대한 모든 권한 그룹 목록 조회
     * @param params 파라미터 (menuId)
     * @return 권한 그룹 목록
     */
    @ResponseBody
    @PostMapping("/getRoleListByMenu.do")
    public ResponseEntity<?> getRoleListByMenu(@RequestBody Map<String, String> params) {
        try {
            String menuId = params.get("menuId");
            List<MenuAuthVO> list = menuService.getRoleListByMenu(menuId);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("권한 그룹 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "권한 그룹 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 권한 그룹에 속한 사용자 목록 조회
     * @param params 파라미터 (roleCd)
     * @return 사용자 목록
     */
    @ResponseBody
    @PostMapping("/getUserListByRole.do")
    public ResponseEntity<?> getUserListByRole(@RequestBody Map<String, String> params) {
        try {
            String roleCd = params.get("roleCd");
            List<UserRoleVO> list = menuService.getUserListByRole(roleCd);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 메뉴-권한 매핑 저장
     * @param menuAuthVO 권한 매핑 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/saveMenuAuth.do")
    public ResponseEntity<?> saveMenuAuth(@RequestBody MenuAuthVO menuAuthVO) {
        try {
            ResultVO result = menuService.saveMenuAuth(menuAuthVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("권한 저장 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "권한 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 특정 메뉴에 개별 권한이 부여된 사용자 목록 조회
     * @param params 파라미터 (menuId)
     * @return 사용자 목록
     */
    @ResponseBody
    @PostMapping("/getUserListByMenu.do")
    public ResponseEntity<?> getUserListByMenu(@RequestBody Map<String, String> params) {
        try {
            String menuId = params.get("menuId");
            List<UserMenuAuthVO> list = menuService.getUserListByMenu(menuId);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 전체 사용자 목록 조회 (메뉴 권한 여부 포함)
     * @param params 파라미터 (menuId)
     * @return 사용자 목록
     */
    @ResponseBody
    @PostMapping("/getAllUsersForMenu.do")
    public ResponseEntity<?> getAllUsersForMenu(@RequestBody Map<String, String> params) {
        try {
            String menuId = params.get("menuId");
            List<UserMenuAuthVO> list = menuService.getAllUsersForMenu(menuId);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("전체 사용자 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "전체 사용자 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자-메뉴 권한 저장
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/saveUserMenuAuth.do")
    public ResponseEntity<?> saveUserMenuAuth(@RequestBody UserMenuAuthVO userMenuAuthVO) {
        try {
            ResultVO result = menuService.saveUserMenuAuth(userMenuAuthVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("사용자 권한 저장 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 권한 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자-메뉴 권한 삭제
     * @param userMenuAuthVO 사용자-메뉴 권한 매핑 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/deleteUserMenuAuth.do")
    public ResponseEntity<?> deleteUserMenuAuth(@RequestBody UserMenuAuthVO userMenuAuthVO) {
        try {
            ResultVO result = menuService.deleteUserMenuAuth(userMenuAuthVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("사용자 권한 삭제 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 권한 삭제 중 오류가 발생했습니다.");
        }
    }
}
