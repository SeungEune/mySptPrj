package biz.notice.service;

import biz.notice.dao.NoticeDAO;
import biz.notice.service.impl.NoticeServiceImpl;
import biz.notice.vo.NoticeVO;
import biz.util.AuthService;
import egovframework.com.cmm.response.ResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeDAO noticeDAO;

    @Mock
    private AuthService authService;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    private NoticeVO noticeVO;

    @BeforeEach
    void setUp() {
        noticeVO = new NoticeVO();
        noticeVO.setNoticeSn(1L);
        noticeVO.setNoticeTitle("공지 제목");
        noticeVO.setNoticeCn("공지 내용");
        noticeVO.setImportantYn("Y");
    }

    @Test
    void saveNotice_shouldFail_whenUnauthorized() {
        when(authService.canManageNotice()).thenReturn(false);

        ResultVO result = noticeService.saveNotice(noticeVO);

        assertFalse(result.isResultValue());
        assertEquals("공지사항 등록 권한이 없습니다.", result.getMessage());
        verify(noticeDAO, never()).insertNotice(any());
    }

    @Test
    void saveNotice_shouldSuccess_whenManagerAuthorized() {
        when(authService.canManageNotice()).thenReturn(true);
        when(authService.getCurrentUserId()).thenReturn("manager01");
        when(noticeDAO.insertNotice(any())).thenReturn(1);

        ResultVO result = noticeService.saveNotice(noticeVO);

        assertTrue(result.isResultValue());
        assertEquals("공지사항이 등록되었습니다.", result.getMessage());
        verify(noticeDAO, times(1)).insertNotice(any(NoticeVO.class));
    }

    @Test
    void updateNotice_shouldSuccess_whenAdminAuthorized() {
        when(authService.canManageNotice()).thenReturn(true);
        when(authService.getCurrentUserId()).thenReturn("admin01");
        when(noticeDAO.updateNotice(any())).thenReturn(1);

        ResultVO result = noticeService.updateNotice(noticeVO);

        assertTrue(result.isResultValue());
        assertEquals("공지사항이 수정되었습니다.", result.getMessage());
        verify(noticeDAO, times(1)).updateNotice(any(NoticeVO.class));
    }

    @Test
    void deleteNotice_shouldFail_whenUnauthorized() {
        when(authService.canManageNotice()).thenReturn(false);

        ResultVO result = noticeService.deleteNotice(1L);

        assertFalse(result.isResultValue());
        assertEquals("공지사항 삭제 권한이 없습니다.", result.getMessage());
        verify(noticeDAO, never()).deleteNotice(anyLong());
    }

    @Test
    void deleteNotice_shouldSuccess_whenAuthorized() {
        when(authService.canManageNotice()).thenReturn(true);
        when(noticeDAO.deleteNotice(1L)).thenReturn(1);

        ResultVO result = noticeService.deleteNotice(1L);

        assertTrue(result.isResultValue());
        assertEquals("공지사항이 삭제되었습니다.", result.getMessage());
        verify(noticeDAO, times(1)).deleteNotice(1L);
    }
}
