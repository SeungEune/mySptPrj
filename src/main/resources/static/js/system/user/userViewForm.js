/**
 * 사용자 상세조회 화면 JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 목록 버튼 이벤트
    document.getElementById('listBtn').addEventListener('click', fn_goList);
    
    // 수정 버튼 이벤트
    document.getElementById('editBtn').addEventListener('click', fn_goEditForm);
});

/**
 * 초기화
 */
function fn_init() {
    const userId = document.getElementById('userId').value;
    
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자 ID가 존재하지 않습니다.');
        return;
    }
    
    fn_loadUserInfo(userId);
}

/**
 * 사용자 상세 정보 조회
 */
function fn_loadUserInfo(userId) {
    const data = {
        userId: userId
    };
    const callback = function(resultData) {
        if(resultData.status.code !== 200) {
            MessageUtil.error('조회중 오류발생');
        } else {
            if (resultData.result) {
                const user = resultData.result;
                // 기본 정보
                document.getElementById('userIdDisplay').textContent = user.userId || '-';
                document.getElementById('userNm').textContent = user.userNm || '-';
                document.getElementById('emailAdres').textContent = user.emailAdres || '-';
                document.getElementById('moblphonNo').textContent = user.moblphonNo || '-';
                document.getElementById('deptNm').textContent = user.deptNm || '-';
                document.getElementById('userSttusCdNm').textContent = user.userSttusCdNm || '-';
                document.getElementById('useYn').textContent = user.useYn === 'Y' ? '사용' : user.useYn === 'N' ? '미사용' : '-';
                document.getElementById('roleNm').textContent = user.roleNm || '-';

                // 개인 정보
                document.getElementById('sexdstnCdNm').textContent = user.sexdstnCdNm || '-';
                document.getElementById('brthdy').textContent = user.brthdy || '-';
                document.getElementById('jbgdCdNm').textContent = user.jbgdCdNm || '-';
                document.getElementById('jssfcCdNm').textContent = user.jssfcCdNm || '-';
                document.getElementById('joinDe').textContent = user.joinDe || '-';
                document.getElementById('acntLockYn').textContent = user.acntLockYn === 'Y' ? '잠금' : user.acntLockYn === 'N' ? '정상' : '-';

                // 시스템 정보
                document.getElementById('registerId').textContent = user.registerId || '-';
                document.getElementById('registDt').textContent = user.registDt || '-';
                document.getElementById('updusrId').textContent = user.updusrId || '-';
                document.getElementById('updtDt').textContent = user.updtDt || '-';
                document.getElementById('lastLoginDt').textContent = user.lastLoginDt || '-';
                document.getElementById('loginFailCo').textContent = user.loginFailCo !== null && user.loginFailCo !== undefined ? user.loginFailCo : '-';
                document.getElementById('passwordChangeDe').textContent = user.passwordChangeDe || '-';
            } else {
                MessageUtil.error('사용자 정보를 불러올 수 없습니다.');
            }
        }
    };
    
    callModule.call(Util.getRequestUrl('/system/user/getUserDetail.do'), data, callback, true, 'POST');
}

/**
 * 목록 화면으로 이동
 */
function fn_goList() {
    location.href = '/system/user/userListForm.do';
}

/**
 * 수정 화면으로 이동
 */
function fn_goEditForm() {
    const userId = document.getElementById('userId').value;
    
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자 정보가 없습니다.');
        return;
    }

    const data = { userId: userId };
    callModule.post(Util.getRequestUrl('/system/user/userEditForm.do'), data, 'post');
}

