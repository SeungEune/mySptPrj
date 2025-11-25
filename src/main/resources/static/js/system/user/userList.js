/**
 * 사용자 목록 화면 JavaScript
 */

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 신규 등록 버튼 이벤트
    document.getElementById('registerBtn').addEventListener('click', fn_goRegisterForm);
    
    // 검색 버튼 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => {
        fn_searchData(1);
    });
    
    // 검색어 입력란 엔터키 이벤트
    document.getElementById('searchKeyword').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            fn_searchData(1);
        }
    });
    
    // 초기화 버튼 이벤트
    document.getElementById('resetBtn').addEventListener('click', fn_resetSearch);
});

/**
 * 초기화
 */
function fn_init() {
    fn_searchData(1);
}

/**
 * 검색 초기화
 */
function fn_resetSearch() {
    document.getElementById('searchType').value = 'userNm';
    document.getElementById('searchKeyword').value = '';
    document.getElementById('status').value = '';
    fn_searchData(1);
}

/**
 * 목록 조회
 */
function fn_searchData(pageNo) {
    const searchType = document.getElementById('searchType').value;
    const searchKeyword = document.getElementById('searchKeyword').value;
    const status = document.getElementById('status').value;
    
    const data = {
        status: Util.isEmpty(status) ? null : status,
        searchVO: {
            searchCondition: Util.isEmpty(searchType) ? 'userNm' : searchType,
            searchKeyword: Util.isEmpty(searchKeyword) ? null : searchKeyword,
            pageNo: Util.isEmpty(pageNo) ? 1 : pageNo
        }
    };
    
    const callback = function(result) {
        $('#userListBody').empty();
        
        if (result.list && result.list.length > 0) {
            $('#listTemplate').tmpl(result.list).appendTo('#userListBody');
            setPagination(result.pagingVO, document.getElementById('paginationArea'), 'fn_searchData');
            
            // 전체 건수 표시
            document.getElementById('totalCount').textContent = result.pagingVO.totalRecordCount;
        } else {
            $('#emptyTemplate').tmpl().appendTo('#userListBody');
            document.getElementById('totalCount').textContent = '0';
        }
    };
    
    callModule.call(Util.getRequestUrl('/system/user/getList.do'), data, callback, true, 'POST');
}

/**
 * 사용자 등록 화면으로 이동
 */
function fn_goRegisterForm() {
    location.href = '/system/user/userRegisterForm.do';
}

/**
 * 사용자 상세조회 화면으로 이동
 */
function fn_goViewForm(userId) {
    if (Util.isEmpty(userId)) {
        MessageUtil.error('사용자 정보가 없습니다.');
        return;
    }
    
    const data = { userId: userId };
    callModule.post(Util.getRequestUrl('/system/user/userViewForm.do'), data);
}

