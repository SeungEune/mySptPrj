package biz.notice.web;

import biz.notice.service.NoticeService;
import biz.notice.vo.NoticeVO;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ApiResponseVO;
import egovframework.com.cmm.response.PagingVO;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공지사항 API Controller
 */
@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeApiController {

    @Resource(name = "noticeService")
    private NoticeService noticeService;

    @PostMapping("/getList.do")
    public ResponseEntity<?> getList(@RequestBody NoticeVO noticeVO) {
        try {
            int totalCnt = noticeService.getNoticeListCnt(noticeVO);
            List<NoticeVO> list = noticeService.getNoticeList(noticeVO);
            PagingVO pagingVO = new PagingVO(totalCnt, noticeVO.getSearchVO().getPageNo(), noticeVO.getSearchVO().getPageSize());
            return ApiResponseVO.apiResponse(list, pagingVO);
        } catch (Exception e) {
            log.error("공지사항 목록 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "공지사항 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/getDetail.do")
    public ResponseEntity<?> getDetail(@RequestBody Map<String, String> params) {
        try {
            String noticeSnStr = params.get("noticeSn");
            boolean increaseViewCnt = !"N".equals(params.get("increaseViewCnt"));
            if (StringUtil.isEmpty(noticeSnStr)) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "공지사항 번호는 필수입니다.");
            }
            NoticeVO noticeVO = noticeService.getNoticeDetail(Long.valueOf(noticeSnStr), increaseViewCnt);
            if (noticeVO == null) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 공지사항입니다.");
            }
            return ApiResponseVO.apiResponse(noticeVO, HttpStatus.OK.value(), "조회되었습니다.");
        } catch (Exception e) {
            log.error("공지사항 상세 조회 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "공지사항 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/saveNotice.do")
    public ResponseEntity<?> saveNotice(@RequestBody NoticeVO noticeVO) {
        try {
            ResultVO resultVO = noticeService.saveNotice(noticeVO);
            if (resultVO.isResultValue()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("success", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), resultVO.getMessage());
            }
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), resultVO.getMessage());
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "공지사항 등록 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/updateNotice.do")
    public ResponseEntity<?> updateNotice(@RequestBody NoticeVO noticeVO) {
        try {
            ResultVO resultVO = noticeService.updateNotice(noticeVO);
            if (resultVO.isResultValue()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("success", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), resultVO.getMessage());
            }
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), resultVO.getMessage());
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "공지사항 수정 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/deleteNotice.do")
    public ResponseEntity<?> deleteNotice(@RequestBody Map<String, String> params) {
        try {
            String noticeSnStr = params.get("noticeSn");
            if (StringUtil.isEmpty(noticeSnStr)) {
                return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), "공지사항 번호는 필수입니다.");
            }
            ResultVO resultVO = noticeService.deleteNotice(Long.valueOf(noticeSnStr));
            if (resultVO.isResultValue()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("success", true);
                return ApiResponseVO.apiResponse(resultMap, HttpStatus.OK.value(), resultVO.getMessage());
            }
            return ApiResponseVO.apiResponse(null, HttpStatus.BAD_REQUEST.value(), resultVO.getMessage());
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생", e);
            return ApiResponseVO.apiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), "공지사항 삭제 중 오류가 발생했습니다.");
        }
    }
}
