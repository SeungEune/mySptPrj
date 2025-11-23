/**
 * 사용자 등록 화면 JavaScript
 */
document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 중복확인 버튼 이벤트
    document.getElementById('checkIdBtn').addEventListener('click', fn_checkUserId);
    
    // 폼 제출 이벤트
    document.getElementById('userForm').addEventListener('submit', fn_saveUser);
    
    // 부서 검색 버튼 이벤트
    document.getElementById('deptSearchBtn').addEventListener('click', fn_searchDept);
});

/**
 * 초기화
 */
function fn_init() {
    // 초기화 로직
}

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
        if (result && result.duplicate === false) {
            MessageUtil.success('사용 가능한 사용자ID입니다.');
        } else {
            MessageUtil.error('이미 사용 중인 사용자ID입니다.');
        }
    }, true, 'POST');
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
 * 사용자 등록 저장
 */
function fn_saveUser(event) {
    event.preventDefault();
    
    const userId = document.getElementById('userId').value.trim();
    const userNm = document.getElementById('userNm').value.trim();
    const password = document.getElementById('password').value;
    const userSttusCd = document.getElementById('userSttusCd').value;
    const useYn = document.querySelector('input[name="useYn"]:checked').value;
    
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
    
    if (Util.isEmpty(userSttusCd)) {
        MessageUtil.error('사용자상태코드를 선택해주세요.');
        return;
    }
    
    // 폼 데이터 수집
    const formData = {
        userId: userId,
        userNm: userNm,
        password: password,
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
    
    const url = Util.getRequestUrl('/system/user/save.do');
    
    callModule.call(url, formData, function(result) {
        if (result && result.resultValue === true) {
            MessageUtil.success(result.message || '등록이 완료되었습니다.', function() {
                location.href = '/system/user/userListForm.do';
            });
        } else {
            MessageUtil.error(result.message || '등록에 실패하였습니다.');
        }
    }, true, 'POST');
}

