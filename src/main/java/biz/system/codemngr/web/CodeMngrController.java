package biz.system.codemngr.web;

import biz.system.codemngr.service.CodeMngrService;
import biz.system.codemngr.vo.CodeDetailVO;
import biz.system.codemngr.vo.CodeGroupVO;
import biz.system.codemngr.vo.CodeSearchVO;
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
 * 코드 관리를 처리하는 컨트롤러 클래스
 * @author Spatialt 개발팀
 * @since 2025.01.XX
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/system/code")
public class CodeMngrController {

    @Resource(name = "codeMngrService")
    private CodeMngrService codeMngrService;

    /**
     * 코드 관리 목록 화면
     * @param model Model 객체
     * @return 코드 관리 목록 페이지
     */
    @GetMapping("/codeMngrForm.do")
    public String codeMngrForm(Model model) {
        return "system/codemngr/codeMngrForm";
    }

    /**
     * 코드 그룹 목록 조회 (POST + JSON)
     * @param searchVO 검색 조건
     * @return 코드 그룹 목록
     */
    @ResponseBody
    @PostMapping("/getCodeGroupList.do")
    public ResponseEntity<?> getCodeGroupList(@RequestBody CodeSearchVO searchVO) {
        try {
            List<CodeGroupVO> list = codeMngrService.getCodeGroupList(searchVO);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("코드 그룹 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 그룹 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 그룹 상세 조회 (POST + JSON)
     * @param params 파라미터 (codeId)
     * @return 코드 그룹 상세 정보
     */
    @ResponseBody
    @PostMapping("/getCodeGroupDetail.do")
    public ResponseEntity<?> getCodeGroupDetail(@RequestBody Map<String, String> params) {
        try {
            String codeId = params.get("codeId");
            // TODO: 구현 예정
            CodeGroupVO vo = codeMngrService.getCodeGroupDetail(codeId);
            return ApiResponseVO.apiResponse(vo, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("코드 그룹 상세 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 그룹 상세 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 그룹 저장 (등록/수정) (POST + JSON)
     * @param codeGroupVO 코드 그룹 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/saveCodeGroup.do")
    public ResponseEntity<?> saveCodeGroup(@RequestBody CodeGroupVO codeGroupVO) {
        try {
            ResultVO result = codeMngrService.saveCodeGroup(codeGroupVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("코드 그룹 저장 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 그룹 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 그룹 use_yn 업데이트 (POST + JSON)
     * @param updateList 코드 그룹 ID와 useYn의 리스트
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/updateCodeGroupUseYn.do")
    public ResponseEntity<?> updateCodeGroupUseYn(@RequestBody List<Map<String, String>> updateList) {
        try {
            ResultVO result = codeMngrService.updateCodeGroupUseYn(updateList);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("코드 그룹 사용여부 수정 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 그룹 사용여부 수정 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 그룹 ID 중복 확인 (POST + JSON)
     * @param params 파라미터 (codeId)
     * @return 중복 여부
     */
    @ResponseBody
    @PostMapping("/checkCodeIdDuplicate.do")
    public ResponseEntity<?> checkCodeIdDuplicate(@RequestBody Map<String, String> params) {
        try {
            String codeId = params.get("codeId");
            boolean isDuplicate = codeMngrService.checkCodeIdDuplicate(codeId);
            Map<String, Object> resultMap = new java.util.HashMap<>();
            resultMap.put("duplicate", isDuplicate);
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), isDuplicate ? "이미 사용 중인 코드 그룹 ID입니다." : "사용 가능한 코드 그룹 ID입니다.");
        } catch (Exception e) {
            log.error("코드 그룹 ID 중복 확인 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 그룹 ID 중복 확인 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 상세값 목록 조회 (POST + JSON)
     * @param params 파라미터 (codeId)
     * @return 코드 상세값 목록
     */
    @ResponseBody
    @PostMapping("/getCodeDetailList.do")
    public ResponseEntity<?> getCodeDetailList(@RequestBody Map<String, String> params) {
        try {
            String codeId = params.get("codeId");
            List<CodeDetailVO> list = codeMngrService.getCodeDetailList(codeId);
            return ApiResponseVO.apiResponse(list, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("코드 상세값 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 상세값 목록 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 상세값 상세 조회 (POST + JSON)
     * @param params 파라미터 (codeId, code)
     * @return 코드 상세값 정보
     */
    @ResponseBody
    @PostMapping("/getCodeDetail.do")
    public ResponseEntity<?> getCodeDetail(@RequestBody Map<String, String> params) {
        try {
            String codeId = params.get("codeId");
            String code = params.get("code");
            // TODO: 구현 예정
            CodeDetailVO vo = codeMngrService.getCodeDetail(codeId, code);
            return ApiResponseVO.apiResponse(vo, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("코드 상세값 상세 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 상세값 상세 조회 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 상세값 저장 (등록/수정) (POST + JSON)
     * @param codeDetailVO 코드 상세값 정보
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/saveCodeDetail.do")
    public ResponseEntity<?> saveCodeDetail(@RequestBody CodeDetailVO codeDetailVO) {
        try {
            ResultVO result = codeMngrService.saveCodeDetail(codeDetailVO);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("코드 상세값 저장 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 상세값 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 상세값 중복 확인 (POST + JSON)
     * @param params 파라미터 (codeId, code)
     * @return 중복 여부
     */
    @ResponseBody
    @PostMapping("/checkCodeDetailDuplicate.do")
    public ResponseEntity<?> checkCodeDetailDuplicate(@RequestBody Map<String, String> params) {
        try {
            String codeId = params.get("codeId");
            String code = params.get("code");
            boolean isDuplicate = codeMngrService.checkCodeDetailDuplicate(codeId, code);
            Map<String, Object> resultMap = new java.util.HashMap<>();
            resultMap.put("duplicate", isDuplicate);
            return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), isDuplicate ? "이미 사용 중인 코드입니다." : "사용 가능한 코드입니다.");
        } catch (Exception e) {
            log.error("코드 상세값 중복 확인 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 상세값 중복 확인 중 오류가 발생했습니다.");
        }
    }

    /**
     * 코드 상세값 use_yn 업데이트 (POST + JSON)
     * @param updateList 코드 상세값 ID와 useYn의 리스트
     * @return 처리 결과
     */
    @ResponseBody
    @PostMapping("/updateCodeDetailUseYn.do")
    public ResponseEntity<?> updateCodeDetailUseYn(@RequestBody List<Map<String, String>> updateList) {
        try {
            ResultVO result = codeMngrService.updateCodeDetailUseYn(updateList);
            if (result.isResultValue()) {
                return ApiResponseVO.apiResponse(result, HttpStatus.OK.value(), result.getMessage());
            } else {
                return ApiResponseVO.apiResponse(result, HttpStatus.BAD_REQUEST.value(), result.getMessage());
            }
        } catch (Exception e) {
            log.error("코드 상세값 사용여부 수정 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "코드 상세값 사용여부 수정 중 오류가 발생했습니다.");
        }
    }
}
