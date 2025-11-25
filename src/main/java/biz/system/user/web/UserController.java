package biz.system.user.web;

import biz.system.user.service.UserService;
import biz.system.user.vo.UserVO;
import biz.util.CmmCodeUtil;
import biz.util.SessionUtil;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.PagingVO;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관리를 처리하는 컨트롤러 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/system/user")
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    /**
     * 사용자 목록 화면
     * @param model Model 객체
     * @return 사용자 목록 페이지
     */
    @GetMapping("/userListForm.do")
    public String userListForm(Model model) {
        return "system/user/userListForm";
    }

    /**
     * 사용자 목록 조회 (POST + JSON)
     * @param userVO 검색 조건
     * @return 사용자 목록 및 페이징 정보
     */
    @ResponseBody
    @PostMapping("/getList.do")
    public ResponseEntity<Map<String, Object>> getList(@RequestBody UserVO userVO) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 전체 건수 조회
            int totalCnt = userService.getUserListCnt(userVO);
            
            // offset 계산
            userVO.getSearchVO().calculateOffset();
            
            // 목록 조회
            List<UserVO> list = userService.getUserList(userVO);
            
            // 페이징 정보 생성
            PagingVO pagingVO = new PagingVO(
                totalCnt,
                userVO.getSearchVO().getPageNo(),
                userVO.getSearchVO().getPageSize()
            );
            
            resultMap.put("list", list);
            resultMap.put("pagingVO", pagingVO);
            resultMap.put("searchVO", userVO.getSearchVO());
            
            return ResponseEntity.ok(resultMap);
            
        } catch (Exception e) {
            log.error("사용자 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
    }

    /**
     * 사용자 등록 화면
     * @param model Model 객체
     * @return 사용자 등록 페이지
     */
    @GetMapping("/userRegisterForm.do")
    public String userRegisterForm(Model model) {
        // 공통 코드 목록 전달
        model.addAttribute("userSttusCodeList", CmmCodeUtil.getCmmnCodeList("USER_STTUS"));
        model.addAttribute("sexdstnCodeList", CmmCodeUtil.getCmmnCodeList("SEXDSTN"));
        model.addAttribute("jbgdCodeList", CmmCodeUtil.getCmmnCodeList("JGBD"));
        model.addAttribute("jssfcCodeList", CmmCodeUtil.getCmmnCodeList("JSSFC"));
        model.addAttribute("roleCodeList", CmmCodeUtil.getRoleList());
        
        return "system/user/userRegisterForm";
    }

    /**
     * 사용자 수정 화면
     * @param userId 사용자 ID
     * @param model Model 객체
     * @return 사용자 수정 페이지
     */
    @PostMapping("/userForm.do")
    public String userForm(@RequestParam String userId, Model model) {
        // userId 필수 검증
        if (userId == null || userId.isEmpty()) {
            return "redirect:/system/user/userListForm.do";
        }

        // 공통 코드 목록 전달
        model.addAttribute("userSttusCodeList", CmmCodeUtil.getCmmnCodeList("USER_STTUS"));
        model.addAttribute("sexdstnCodeList", CmmCodeUtil.getCmmnCodeList("SEXDSTN"));
        model.addAttribute("jbgdCodeList", CmmCodeUtil.getCmmnCodeList("JGBD"));
        model.addAttribute("jssfcCodeList", CmmCodeUtil.getCmmnCodeList("JSSFC"));

        model.addAttribute("userId", userId);

        return "system/user/userEditForm";
    }

    /**
     * 사용자 상세조회 화면
     * @param userId 사용자 ID
     * @param model Model 객체
     * @return 사용자 상세조회 페이지
     */
    @PostMapping("/userViewForm.do")
    public String userViewForm(@RequestParam String userId, Model model) {
        // userId 필수 검증
        if (userId == null || userId.isEmpty()) {
            return "redirect:/system/user/userListForm.do";
        }
        model.addAttribute("userId", userId);
        
        return "system/user/userViewForm";
    }
    
    /**
     * 사용자 상세 정보 조회 (POST + JSON)
     * @param params 사용자 ID를 포함한 파라미터
     * @return 사용자 상세 정보
     */
    @ResponseBody
    @PostMapping("/getUserDetail.do")
    public ResponseEntity getUserDetail(@RequestBody Map<String, String> params) {
        try {
            String userId = params.get("userId");
            
            // userId 유효성 검증
            if (StringUtil.isEmpty(userId)) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "사용자 ID는 필수입니다.");
            }
            
            // 사용자 상세 정보 조회
            UserVO userVO = userService.getUserDetail(userId);
            
            return ApiResponseVO.apiResponse(userVO, HttpStatus.OK.value(), "조회되었습니다.");
            
        } catch (IllegalArgumentException e) {
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (Exception e) {
            log.error("사용자 상세 정보 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 정보 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자 정보 등록
     * @param userVO 사용자 정보
     * @return 등록 결과
     */
    @ResponseBody
    @PostMapping("/saveUser.do")
    public ResponseEntity saveUser(@RequestBody UserVO userVO) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 필수 입력값 검증
            if (StringUtil.isEmpty(userVO.getUserId())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "사용자ID는 필수입니다.");
            }
            if (StringUtil.isEmpty(userVO.getUserNm())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "사용자명은 필수입니다.");
            }
            if (StringUtil.isEmpty(userVO.getUserSttusCd())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "사용자상태는 필수입니다.");
            }
            if (StringUtil.isEmpty(userVO.getUserPassword())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "비밀번호는 필수입니다.");
            }
            if (StringUtil.isEmpty(userVO.getPasswordConfirm())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "비밀번호 확인은 필수입니다.");
            }
            if (!userVO.getUserPassword().equals(userVO.getPasswordConfirm())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            }
            if(StringUtil.isEmpty(userVO.getRoleCd())) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "사용자 권한이 설정되지 않았습니다.");
            }
            
            // 사용자 정보 등록
            ResultVO resultVO = userService.userSave(userVO);
            
            if (resultVO.isResultValue()) {
                resultMap.put("success", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), resultVO.getMessage());
            } else {
                // 비즈니스 로직 실패는 BAD_REQUEST가 적절
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), resultVO.getMessage());
            }
            
        } catch (Exception e) {
            log.error("사용자 등록 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "사용자 등록 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자 ID 중복 확인
     * @param params 확인할 사용자 ID
     * @return 중복 여부 결과 (duplicate: true/false)
     */
    @ResponseBody
    @PostMapping("/checkUserId.do")
    public ResponseEntity checkUserId(@RequestBody Map<String, String> params) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String userId = params.get("userId");

            // 입력값 검증
            if (StringUtil.isEmpty(userId)) {
                resultMap.put("duplicate", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.BAD_REQUEST.value(), "사용자ID를 입력해주세요.");
            }

            // 중복 체크
            boolean isDuplicate = userService.checkUserIdDuplicate(userId);
            resultMap.put("duplicate", isDuplicate);

            String message = isDuplicate ? "이미 사용 중인 사용자ID입니다." : "사용 가능한 사용자ID입니다.";
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), message);

        } catch (Exception e) {
            log.error("사용자 ID 중복 확인 중 오류 발생", e);
            resultMap.put("duplicate", true);
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.INTERNAL_SERVER_ERROR.value(), "중복 확인 중 오류가 발생했습니다.");
        }
    }
}

