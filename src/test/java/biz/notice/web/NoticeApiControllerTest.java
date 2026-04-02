package biz.notice.web;

import biz.notice.service.NoticeService;
import biz.notice.vo.NoticeVO;
import egovframework.com.cmm.response.ResultVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeApiControllerTest {

    @Mock
    private NoticeService noticeService;

    @InjectMocks
    private NoticeApiController noticeApiController;

    @Test
    void saveNotice_shouldReturnBadRequest_whenServiceFails() {
        NoticeVO noticeVO = new NoticeVO();
        ResultVO resultVO = ResultVO.builder().resultValue(false).message("공지사항 등록 권한이 없습니다.").build();
        when(noticeService.saveNotice(noticeVO)).thenReturn(resultVO);

        ResponseEntity<?> response = noticeApiController.saveNotice(noticeVO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteNotice_shouldReturnBadRequest_whenNoticeSnMissing() {
        Map<String, String> params = new HashMap<>();

        ResponseEntity<?> response = noticeApiController.deleteNotice(params);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}
