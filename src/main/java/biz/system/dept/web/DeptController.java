package biz.system.dept.web;

import biz.system.dept.service.DeptService;
import biz.system.dept.vo.DeptVO;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.PagingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    // 추후 부서 관리 기능 구현 시 추가될 메서드들
    
    // @GetMapping("/deptListForm.do")
    // public String deptListForm(Model model) { ... }
    
    // @GetMapping("/deptRegisterForm.do")
    // public String deptRegisterForm(Model model) { ... }
    
    // @GetMapping("/deptForm.do")
    // public String deptForm(@RequestParam String deptCd, Model model) { ... }
    
    // @ResponseBody
    // @PostMapping("/save.do")
    // public ResponseEntity saveDept(@RequestBody DeptVO deptVO) { ... }
    
    // @ResponseBody
    // @PostMapping("/delete.do")
    // public ResponseEntity deleteDept(@RequestBody Map<String, String> params) { ... }
}

