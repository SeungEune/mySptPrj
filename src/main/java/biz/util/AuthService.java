package biz.util;

import org.springframework.stereotype.Component;

/**
 * 현재 로그인 사용자 권한/식별 헬퍼
 */
@Component
public class AuthService {

    public String getCurrentUserId() {
        return SessionUtil.getUserId();
    }

    public String getCurrentRoleCd() {
        return SessionUtil.getUserRoleCd();
    }

    public boolean canManageNotice() {
        String roleCd = getCurrentRoleCd();
        return "ADMIN".equals(roleCd) || "MANAGER".equals(roleCd);
    }
}
