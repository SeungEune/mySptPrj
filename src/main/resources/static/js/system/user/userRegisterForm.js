/**
 * 사용자 등록 화면 JavaScript
 */

// 중복확인 완료 여부를 추적하는 전역 변수
let isUserIdChecked = false;
let checkedUserId = '';

document.addEventListener('DOMContentLoaded', () => {
    // 중복확인 버튼 이벤트
    document.getElementById('checkIdBtn').addEventListener('click', fn_checkUserId);
    
    // 폼 제출 이벤트
    document.getElementById('userForm').addEventListener('submit', fn_saveUser);
    
    // 부서 검색 버튼 이벤트
    document.getElementById('deptSearchBtn').addEventListener('click', fn_searchDept);
    
    // userId 입력 시 중복확인 플래그 초기화
    document.getElementById('userId').addEventListener('input', function() {
        if (this.value.trim() !== checkedUserId) {
            isUserIdChecked = false;
        }
    });
});

/**
 * 사용자ID 중복확인
 */
function fn_checkUserId() {
    const userId = document.getElementById('userId').value.trim();
    
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자ID를 입력해주세요.');
        return;
    }
    
    const data = { userId: userId };
    const url = Util.getRequestUrl('/system/user/checkUserId.do');
    
    callModule.call(url, data, function(result) {
        if (result && result.result && result.result.duplicate === false) {
            // 중복확인 성공 시 플래그 설정
            isUserIdChecked = true;
            checkedUserId = userId;
            MessageUtil.success(result.status.message);
        } else if (result && result.status) {
            // 중복 시 플래그 초기화
            isUserIdChecked = false;
            checkedUserId = '';
            MessageUtil.error(result.status.message);
        } else {
            // 오류 시 플래그 초기화
            isUserIdChecked = false;
            checkedUserId = '';
            MessageUtil.error('중복 확인 중 오류가 발생했습니다.');
        }
    }, true, 'POST');
}

/**
 * 부서 검색
 */
function fn_searchDept() {
    // 부서 검색 모달 열기
    DeptSearchModal.open(function(deptCd, deptNm) {
        document.getElementById('deptCd').value = deptCd;
        document.getElementById('deptNm').value = deptNm;
    });
}

/**
 * 사용자 등록 저장
 */
function fn_saveUser(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value.trim();
    const userNm = document.getElementById('userNm').value.trim();
    const password = document.getElementById('password').value;
    const passwordConfirm = document.getElementById('passwordConfirm').value;
    const userSttusCd = document.getElementById('userSttusCd').value;
    const roleCd = document.getElementById('roleCd').value;
    const useYn = document.querySelector('input[name="useYn"]:checked').value;
    
    // 중복확인 여부 검증
    if (!isUserIdChecked) {
        MessageUtil.error('사용자ID 중복확인을 해주세요.');
        return;
    }
    
    if (userId !== checkedUserId) {
        MessageUtil.error('사용자ID가 변경되었습니다. 중복확인을 다시 해주세요.');
        return;
    }
    
    // 필수값 검증
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자ID를 입력해주세요.');
        return;
    }
    
    if (Util.isEmpty(userNm)) {
        MessageUtil.error('사용자명을 입력해주세요.');
        return;
    }
    
    if (Util.isEmpty(password)) {
        MessageUtil.error('비밀번호를 입력해주세요.');
        return;
    }
    
    if (Util.isEmpty(passwordConfirm)) {
        MessageUtil.error('비밀번호 확인을 입력해주세요.');
        return;
    }
    
    // 비밀번호 일치 검증
    if (password !== passwordConfirm) {
        MessageUtil.error('비밀번호와 비밀번호 확인이 일치하지 않습니다.');
        return;
    }
    
    if (Util.isEmpty(userSttusCd)) {
        MessageUtil.error('사용자상태코드를 선택해주세요.');
        return;
    }
    
    if (Util.isEmpty(roleCd)) {
        MessageUtil.error('권한을 선택해주세요.');
        return;
    }
    
    // 폼 데이터 수집
    const formData = {
        userId: userId,
        userNm: userNm,
        userPassword: password,
        passwordConfirm: passwordConfirm,
        emailAdres: document.getElementById('emailAdres').value.trim(),
        moblphonNo: document.getElementById('moblphonNo').value.trim(),
        deptCd: document.getElementById('deptCd').value,
        userSttusCd: userSttusCd,
        roleCd: roleCd,
        sexdstnCd: document.querySelector('input[name="sexdstnCd"]:checked') ? document.querySelector('input[name="sexdstnCd"]:checked').value : '',
        brthdy: document.getElementById('brthdy').value,
        jbgdCd: document.getElementById('jbgdCd').value,
        jssfcCd: document.getElementById('jssfcCd').value,
        joinDe: document.getElementById('joinDe').value,
        acntLockYn: document.querySelector('input[name="acntLockYn"]:checked').value,
        useYn: useYn
    };
    
    const url = Util.getRequestUrl('/system/user/saveUser.do');
    
    callModule.call(url, formData, function(result) {
        if (result && result.result && result.result.success) {
            MessageUtil.success(result.status.message || '등록이 완료되었습니다.', function() {
                location.href = '/system/user/userListForm.do';
            });
        } else {
            MessageUtil.error((result && result.status && result.status.message) || '등록에 실패하였습니다.');
        }
    }, true, 'POST');
}

