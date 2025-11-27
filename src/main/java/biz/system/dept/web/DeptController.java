package biz.system.dept.web;

import biz.system.dept.service.DeptService;
import biz.system.dept.vo.DeptVO;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.PagingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 부서 관리를 처리하는 컨트롤러 클래스
 * @author Spatialt 개발팀
 * @since 2025.11.23
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/system/dept")
public class DeptController {

    @Resource(name = "deptService")
    private DeptService deptService;

    /**
     * 부서 목록 화면
     * @param model Model 객체
     * @return 부서 목록 페이지
     */
    @GetMapping("/deptListForm.do")
    public String deptListForm(Model model) {
        return "system/dept/deptListForm";
    }

    /**
     * 부서 목록 조회 (POST + JSON) - 트리 구조
     * @param deptVO 검색 조건
     * @return 부서 목록 (트리 구조)
     */
    @ResponseBody
    @PostMapping("/getDeptList.do")
    public ResponseEntity getDeptList(@RequestBody DeptVO deptVO) {
        try {
            // 페이징 없이 전체 트리 조회
            List<DeptVO> list = deptService.getDeptList(deptVO);
            
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
            
        } catch (Exception e) {
            log.error("부서 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "부서 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 부서 상세 정보 조회 (POST + JSON)
     * @param params 부서코드를 포함한 파라미터
     * @return 부서 상세 정보
     */
    @ResponseBody
    @PostMapping("/getDeptDetail.do")
    public ResponseEntity getDeptDetail(@RequestBody Map<String, String> params) {
        try {
            String deptCd = params.get("deptCd");
            
            // deptCd 유효성 검증
            if (StringUtil.isEmpty(deptCd)) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "부서코드는 필수입니다.");
            }
            
            // 부서 상세 정보 조회
            DeptVO deptVO = deptService.getDeptDetail(deptCd);
            
            if (deptVO == null) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 부서입니다.");
            }
            
            return ApiResponseVO.apiResponse(deptVO, HttpStatus.OK.value(), "조회되었습니다.");
            
        } catch (Exception e) {
            log.error("부서 상세 정보 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "부서 정보 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 부서코드 중복 확인
     * @param params 확인할 부서코드
     * @return 중복 여부 결과 (duplicate: true/false)
     */
    @ResponseBody
    @PostMapping("/checkDeptCodeDuplicate.do")
    public ResponseEntity checkDeptCodeDuplicate(@RequestBody Map<String, String> params) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String deptCd = params.get("deptCd");

            // 입력값 검증
            if (StringUtil.isEmpty(deptCd)) {
                resultMap.put("duplicate", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.BAD_REQUEST.value(), "부서코드를 입력해주세요.");
            }

            // 중복 체크
            boolean isDuplicate = deptService.checkDeptCodeDuplicate(deptCd);
            resultMap.put("duplicate", isDuplicate);

            String message = isDuplicate ? "이미 사용 중인 부서코드입니다." : "사용 가능한 부서코드입니다.";
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), message);

        } catch (Exception e) {
            log.error("부서코드 중복 확인 중 오류 발생", e);
            resultMap.put("duplicate", true);
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.INTERNAL_SERVER_ERROR.value(), "중복 확인 중 오류가 발생했습니다.");
        }
    }

    /**
     * 부서 목록 조회 (모달용 트리 구조)
     * @param deptVO 검색 조건
     * @return 부서 트리 전체 목록
     */
    @ResponseBody
    @PostMapping("/getDeptModalList.do")
    public ResponseEntity getDeptModalList(@RequestBody DeptVO deptVO) {
        try {
            // 페이징 없이 전체 트리 조회
            List<DeptVO> list = deptService.getDeptList(deptVO);
            
            // 결과 맵 생성
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", list);
            resultMap.put("totalCount", list.size());
            
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), "조회 성공");
            
        } catch (Exception e) {
            log.error("부서 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "조회 중 오류가 발생했습니다.");
        }
    }
}

