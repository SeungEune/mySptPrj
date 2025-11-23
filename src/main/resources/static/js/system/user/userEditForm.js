/**
 * 사용자 수정 화면 JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 비밀번호 변경 버튼 이벤트
    document.getElementById('changePasswordBtn').addEventListener('click', fn_showPasswordInput);
    
    // 폼 제출 이벤트
    document.getElementById('userForm').addEventListener('submit', fn_updateUser);
    
    // 삭제 버튼 이벤트
    document.getElementById('deleteBtn').addEventListener('click', fn_deleteUser);
    
    // 부서 검색 버튼 이벤트
    document.getElementById('deptSearchBtn').addEventListener('click', fn_searchDept);
});

/**
 * 초기화
 */
function fn_init() {
    // URL에서 userId 파라미터 추출
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');
    
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자ID가 없습니다.');
        location.href = '/system/user/userListForm.do';
        return;
    }
    
    // 사용자 정보 조회
    fn_loadUserInfo(userId);
}

/**
 * 사용자 정보 조회
 */
function fn_loadUserInfo(userId) {
    const data = { userId: userId };
    const url = Util.getRequestUrl('/system/user/getDetail.do');
    
    callModule.call(url, data, function(result) {
        if (result && result.vo) {
            const vo = result.vo;
            
            // 기본 정보 설정
            document.getElementById('userId').value = vo.userId || '';
            document.getElementById('userIdDisplay').value = vo.userId || '';
            document.getElementById('userNm').value = vo.userNm || '';
            document.getElementById('emailAdres').value = vo.emailAdres || '';
            document.getElementById('moblphonNo').value = vo.moblphonNo || '';
            document.getElementById('deptCd').value = vo.deptCd || '';
            document.getElementById('deptNm').value = vo.deptNm || '';
            
            // 드롭다운 선택
            if (vo.userSttusCd) {
                document.getElementById('userSttusCd').value = vo.userSttusCd;
            }
            if (vo.sexdstnCd) {
                document.getElementById('sexdstnCd').value = vo.sexdstnCd;
            }
            if (vo.jbgdCd) {
                document.getElementById('jbgdCd').value = vo.jbgdCd;
            }
            if (vo.jssfcCd) {
                document.getElementById('jssfcCd').value = vo.jssfcCd;
            }
            
            // 라디오 버튼 선택
            if (vo.useYn) {
                document.querySelector(`input[name="useYn"][value="${vo.useYn}"]`).checked = true;
            }
            if (vo.acntLockYn) {
                document.querySelector(`input[name="acntLockYn"][value="${vo.acntLockYn}"]`).checked = true;
            }
            
            // 날짜 필드 설정
            if (vo.brthdy) {
                document.getElementById('brthdy').value = vo.brthdy;
            }
            if (vo.joinDe) {
                document.getElementById('joinDe').value = vo.joinDe;
            }
            
            // 시스템 정보 설정
            document.getElementById('registerId').textContent = vo.registerId || '-';
            document.getElementById('registDt').textContent = vo.registDt || '-';
            document.getElementById('updusrId').textContent = vo.updusrId || '-';
            document.getElementById('updtDt').textContent = vo.updtDt || '-';
            document.getElementById('lastLoginDt').textContent = vo.lastLoginDt || '-';
            document.getElementById('loginFailCo').textContent = vo.loginFailCo || '0';
            document.getElementById('passwordChangeDe').textContent = vo.passwordChangeDe || '-';
        } else {
            MessageUtil.error('사용자 정보를 조회할 수 없습니다.');
            location.href = '/system/user/userListForm.do';
        }
    }, true, 'POST');
}

/**
 * 비밀번호 변경 입력 필드 표시
 */
function fn_showPasswordInput() {
    const passwordInput = document.getElementById('password');
    const changeBtn = document.getElementById('changePasswordBtn');
    
    if (passwordInput.style.display === 'none') {
        passwordInput.style.display = 'block';
        passwordInput.required = true;
        changeBtn.textContent = '취소';
    } else {
        passwordInput.style.display = 'none';
        passwordInput.value = '';
        passwordInput.required = false;
        changeBtn.textContent = '비밀번호 변경';
    }
}

/**
 * 부서 검색
 */
function fn_searchDept() {
    // 부서 검색 팝업 또는 모달 호출
    // TODO: 부서 검색 기능 구현
    MessageUtil.alert('부서 검색 기능은 추후 구현 예정입니다.');
}

/**
 * 사용자 수정 저장
 */
function fn_updateUser(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value.trim();
    const userNm = document.getElementById('userNm').value.trim();
    const password = document.getElementById('password').value;
    const userSttusCd = document.getElementById('userSttusCd').value;
    const useYn = document.querySelector('input[name="useYn"]:checked').value;
    
    // 필수값 검증
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자ID가 없습니다.');
        return;
    }
    
    if (Util.isEmpty(userNm)) {
        MessageUtil.error('사용자명을 입력해주세요.');
        return;
    }
    
    if (Util.isEmpty(userSttusCd)) {
        MessageUtil.error('사용자상태코드를 선택해주세요.');
        return;
    }
    
    // 폼 데이터 수집
    const formData = {
        userId: userId,
        userNm: userNm,
        emailAdres: document.getElementById('emailAdres').value.trim(),
        moblphonNo: document.getElementById('moblphonNo').value.trim(),
        deptCd: document.getElementById('deptCd').value,
        userSttusCd: userSttusCd,
        sexdstnCd: document.getElementById('sexdstnCd').value,
        brthdy: document.getElementById('brthdy').value,
        jbgdCd: document.getElementById('jbgdCd').value,
        jssfcCd: document.getElementById('jssfcCd').value,
        joinDe: document.getElementById('joinDe').value,
        acntLockYn: document.querySelector('input[name="acntLockYn"]:checked').value,
        useYn: useYn
    };
    
    // 비밀번호 변경이 있는 경우에만 추가
    if (!Util.isEmpty(password)) {
        formData.password = password;
    }
    
    const url = Util.getRequestUrl('/system/user/save.do');
    
    callModule.call(url, formData, function(result) {
        if (result && result.resultValue === true) {
            MessageUtil.success(result.message || '수정이 완료되었습니다.', function() {
                location.href = '/system/user/userListForm.do';
            });
        } else {
            MessageUtil.error(result.message || '수정에 실패하였습니다.');
        }
    }, true, 'POST');
}

/**
 * 사용자 삭제
 */
function fn_deleteUser() {
    const userId = document.getElementById('userId').value.trim();
    
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자ID가 없습니다.');
        return;
    }
    
    MessageUtil.confirm('정말 삭제하시겠습니까?', function() {
        const data = { userId: userId };
        const url = Util.getRequestUrl('/system/user/delete.do');
        
        callModule.call(url, data, function(result) {
            if (result && result.resultValue === true) {
                MessageUtil.success(result.message || '삭제가 완료되었습니다.', function() {
                    location.href = '/system/user/userListForm.do';
                });
            } else {
                MessageUtil.error(result.message || '삭제에 실패하였습니다.');
            }
        }, true, 'POST');
    });
}

