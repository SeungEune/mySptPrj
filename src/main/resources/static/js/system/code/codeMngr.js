/**
 * 공통코드 관리 화면 JavaScript
 */

// 전역 변수
let selectedCodeId = null; // 선택된 코드 그룹 ID

document.addEventListener('DOMContentLoaded', () => {
    fn_init();

    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => {
        fn_searchCodeGroup();
    });

    // 검색어 입력 후 Enter 키 이벤트
    document.getElementById('searchKeyword').addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            fn_searchCodeGroup();
        }
    });

    // 초기화 버튼 이벤트
    document.getElementById('resetBtn').addEventListener('click', fn_resetSearch);

    // 추가/삭제 버튼 이벤트
    document.getElementById('addCodeGroupBtn').addEventListener('click', fn_addCodeGroup);
    document.getElementById('deleteCodeGroupBtn').addEventListener('click', fn_deleteCodeGroup);
    document.getElementById('addCodeDetailBtn').addEventListener('click', fn_addCodeDetail);
    document.getElementById('deleteCodeDetailBtn').addEventListener('click', fn_deleteCodeDetail);
});

/**
 * 초기화
 */
function fn_init() {
    fn_searchCodeGroup();
}

/**
 * 코드 그룹 선택
 */
function fn_selectCodeGroup(codeId) {
    selectedCodeId = codeId;

    // 선택 행 하이라이트
    $('#codeGroupTable tbody tr').removeClass('selected');
    $('#codeGroupTable tbody tr[data-code-id="' + codeId + '"]').addClass('selected');

    // 우측 소분류 목록 변경
    fn_renderCodeDetailList(codeId);
}

/**
 * 코드 상세값 목록 렌더링
 */
function fn_renderCodeDetailList(codeId) {
    $('#codeDetailTbody').empty();

    if (!codeId) {
        $('#codeDetailEmptyTemplate').tmpl().appendTo('#codeDetailTbody');
        return;
    }

    const data = {
        codeId: codeId
    };

    const callback = function(result) {
        $('#codeDetailTbody').empty();

        let detailList = [];
        if (result && result.result) {
            detailList = result.result;
        }

        if (!detailList || detailList.length === 0) {
            $('#codeDetailEmptyTemplate').tmpl().appendTo('#codeDetailTbody');
            return;
        }

        // useYn 체크박스 HTML 추가 (템플릿용)
        const processedDetailList = detailList.map(item => {
            const checkboxHtml = item.useYn === 'Y' 
                ? '<input type="checkbox" checked disabled>'
                : '<input type="checkbox" disabled>';
            return {
                ...item,
                useYnCheckbox: checkboxHtml
            };
        });

        // 템플릿으로 목록 렌더링
        $('#codeDetailListTemplate').tmpl(processedDetailList).appendTo('#codeDetailTbody');
    };

    callModule.call(Util.getRequestUrl('/system/code/getCodeDetailList.do'), data, callback, true, 'POST');
}

/**
 * 코드 그룹 검색 및 목록 렌더링
 */
function fn_searchCodeGroup() {
    const searchType = document.getElementById('searchType').value;
    const searchKeyword = document.getElementById('searchKeyword').value;

    const data = {
        searchType: Util.isEmpty(searchType) ? null : searchType,
        searchKeyword: Util.isEmpty(searchKeyword) ? null : searchKeyword
    };

    const callback = function(result) {
        // 목록 렌더링
        $('#codeGroupTbody').empty();

        let list = [];
        if (result && result.result) {
            list = result.result;
        }

        if (!list || list.length === 0) {
            $('#codeGroupEmptyTemplate').tmpl().appendTo('#codeGroupTbody');
        } else {
            // 템플릿으로 목록 렌더링
            $('#codeGroupListTemplate').tmpl(list).appendTo('#codeGroupTbody');

            // 선택된 행 하이라이트 적용
            if (selectedCodeId) {
                $('#codeGroupTable tbody tr[data-code-id="' + selectedCodeId + '"]').addClass('selected');
            } else if (list.length > 0) {
                // 선택된 항목이 없으면 첫 번째 항목 자동 선택
                fn_selectCodeGroup(list[0].codeId);
            }
        }

        // 선택된 코드 그룹이 필터링된 목록에 없으면 선택 해제
        if (selectedCodeId && list && !list.find(item => item.codeId === selectedCodeId)) {
            selectedCodeId = null;
            fn_renderCodeDetailList(null);
        }
    };

    callModule.call(Util.getRequestUrl('/system/code/getCodeGroupList.do'), data, callback, true, 'POST');
}

/**
 * 검색 초기화
 */
function fn_resetSearch() {
    document.getElementById('searchType').value = '';
    document.getElementById('searchKeyword').value = '';
    selectedCodeId = null;
    fn_renderCodeDetailList(null);
    fn_searchCodeGroup();
}

/**
 * 코드 그룹 추가
 */
function fn_addCodeGroup() {
    if (typeof CodeGroupRegisterModal !== 'undefined') {
        CodeGroupRegisterModal.open();
    } else {
        MessageUtil.error('코드 그룹 등록 모달을 사용할 수 없습니다.');
    }
}

/**
 * 코드 그룹 삭제
 */
function fn_deleteCodeGroup() {
    // TODO: 구현 예정
    if (!selectedCodeId) {
        MessageUtil.warning('삭제할 코드 그룹을 선택하세요.');
        return;
    }
    MessageUtil.alert('코드 그룹 삭제 기능은 구현 예정입니다.');
}

/**
 * 코드 상세값 추가
 */
function fn_addCodeDetail() {
    // TODO: 구현 예정
    if (!selectedCodeId) {
        MessageUtil.warning('대분류를 먼저 선택하세요.');
        return;
    }
    MessageUtil.alert('코드 상세값 추가 기능은 구현 예정입니다.');
}

/**
 * 코드 상세값 삭제
 */
function fn_deleteCodeDetail() {
    // TODO: 구현 예정
    if (!selectedCodeId) {
        MessageUtil.warning('대분류를 먼저 선택하세요.');
        return;
    }
    MessageUtil.alert('코드 상세값 삭제 기능은 구현 예정입니다.');
}
