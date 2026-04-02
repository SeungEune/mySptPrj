package biz.notice.web;

import biz.util.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeViewControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private NoticeViewController noticeViewController;

    @Test
    void noticeRegisterForm_shouldRedirect_whenNoPermission() {
        when(authService.canManageNotice()).thenReturn(false);
        String viewName = noticeViewController.noticeRegisterForm(new ExtendedModelMap());
        assertEquals("redirect:/notice/noticeListForm.do", viewName);
    }

    @Test
    void noticeRegisterForm_shouldReturnForm_whenAuthorized() {
        when(authService.canManageNotice()).thenReturn(true);
        String viewName = noticeViewController.noticeRegisterForm(new ExtendedModelMap());
        assertEquals("notice/noticeRegisterForm", viewName);
    }
}
