/**
 * 공통코드 관리 화면 JavaScript
 */

// 전역 변수
let selectedCodeId = null; // 선택된 코드 그룹 ID
let selectedCodeDetail = null; // 선택된 코드 상세값 (codeId, code)

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

    // 추가/수정/저장 버튼 이벤트
    document.getElementById('addCodeGroupBtn').addEventListener('click', fn_addCodeGroup);
    document.getElementById('editCodeGroupBtn').addEventListener('click', fn_editCodeGroupClick);
    document.getElementById('deleteCodeGroupBtn').addEventListener('click', fn_saveCodeGroupUseYn);
    document.getElementById('addCodeDetailBtn').addEventListener('click', fn_addCodeDetail);
    document.getElementById('editCodeDetailBtn').addEventListener('click', fn_editCodeDetailClick);
    document.getElementById('saveCodeDetailBtn').addEventListener('click', fn_saveCodeDetailUseYn);
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
    selectedCodeDetail = null; // 소분류 선택 해제

    // 선택 행 하이라이트
    $('#codeGroupTable tbody tr').removeClass('selected');
    $('#codeGroupTable tbody tr[data-code-id="' + codeId + '"]').addClass('selected');

    // 우측 소분류 목록 변경
    fn_renderCodeDetailList(codeId);
}

/**
 * 코드 상세값 선택
 */
function fn_selectCodeDetail(codeId, code) {
    selectedCodeDetail = { codeId: codeId, code: code };

    // 선택 행 하이라이트
    $('#codeDetailTable tbody tr').removeClass('selected');
    $('#codeDetailTable tbody tr[data-code-id="' + codeId + '"][data-code="' + code + '"]').addClass('selected');
}

/**
 * 코드 상세값 목록 렌더링
 */
function fn_renderCodeDetailList(codeId) {
    $('#codeDetailTbody').empty();
    selectedCodeDetail = null; // 소분류 선택 해제

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

        // 템플릿으로 목록 렌더링 (원본 데이터 그대로 사용)
        $('#codeDetailListTemplate').tmpl(detailList).appendTo('#codeDetailTbody');
    };

    callModule.call(Util.getRequestUrl('/system/code/getCodeDetailList.do'), data, callback, true, 'POST');
}

/**
 * 코드 그룹 검색 및 목록 렌더링
 */
function fn_searchCodeGroup() {
    const searchCategory = document.getElementById('searchCategory').value;
    const searchKeyword = document.getElementById('searchKeyword').value;

    const data = {
        searchCategory: Util.isEmpty(searchCategory) ? null : searchCategory,
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
    document.getElementById('searchCategory').value = '대분류';
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
 * 코드 그룹 수정 버튼 클릭
 */
function fn_editCodeGroupClick() {
    if (!selectedCodeId) {
        MessageUtil.warning('수정할 대분류를 먼저 선택하세요.');
        return;
    }
    
    if (typeof CodeGroupRegisterModal !== 'undefined') {
        CodeGroupRegisterModal.openEdit(selectedCodeId);
    } else {
        MessageUtil.error('코드 그룹 수정 모달을 사용할 수 없습니다.');
    }
}

/**
 * 코드 그룹 사용여부 저장
 */
function fn_saveCodeGroupUseYn() {
    // 테이블의 모든 행에서 체크박스 상태 수집
    const rows = document.querySelectorAll('#codeGroupTable tbody tr[data-code-id]');
    
    if (!rows || rows.length === 0) {
        MessageUtil.warning('저장할 코드 그룹이 없습니다.');
        return;
    }
    
    // 모든 행의 체크박스 상태를 배열로 수집
    const updateList = [];
    rows.forEach(function(row) {
        const codeId = row.getAttribute('data-code-id');
        if (!codeId) {
            return;
        }
        
        const checkbox = row.querySelector('input[type="checkbox"][data-code-id="' + codeId + '"]');
        if (!checkbox) {
            return;
        }
        
        const useYn = checkbox.checked ? 'Y' : 'N';
        updateList.push({
            codeId: codeId,
            useYn: useYn
        });
    });
    
    if (updateList.length === 0) {
        MessageUtil.warning('저장할 데이터가 없습니다.');
        return;
    }
    
    // API 호출
    const callback = function(result) {
        if (result && result.status && result.status.code === 200) {
            MessageUtil.success('코드 그룹 사용여부가 저장되었습니다.', function() {
                // 목록 새로고침
                fn_searchCodeGroup();
                // 소분류 목록도 새로고침 (대분류가 변경되었을 수 있으므로)
                if (selectedCodeId) {
                    fn_renderCodeDetailList(selectedCodeId);
                }
            });
        } else {
            const message = (result && result.status && result.status.message) || '코드 그룹 사용여부 저장에 실패했습니다.';
            MessageUtil.error(message);
        }
    };
    
    callModule.call(Util.getRequestUrl('/system/code/updateCodeGroupUseYn.do'), updateList, callback, true, 'POST');
}

/**
 * 코드 상세값 추가
 */
function fn_addCodeDetail() {
    if (!selectedCodeId) {
        MessageUtil.warning('대분류를 먼저 선택하세요.');
        return;
    }
    
    if (typeof CodeDetailRegisterModal !== 'undefined') {
        CodeDetailRegisterModal.open(selectedCodeId);
    } else {
        MessageUtil.error('코드 상세값 등록 모달을 사용할 수 없습니다.');
    }
}

/**
 * 코드 상세값 수정 버튼 클릭
 */
function fn_editCodeDetailClick() {
    if (!selectedCodeDetail || !selectedCodeDetail.codeId || !selectedCodeDetail.code) {
        MessageUtil.warning('수정할 소분류를 먼저 선택하세요.');
        return;
    }
    
    if (typeof CodeDetailRegisterModal !== 'undefined') {
        CodeDetailRegisterModal.openEdit(selectedCodeDetail.codeId, selectedCodeDetail.code);
    } else {
        MessageUtil.error('코드 상세값 수정 모달을 사용할 수 없습니다.');
    }
}

/**
 * 코드 상세값 사용여부 저장
 */
function fn_saveCodeDetailUseYn() {
    if (!selectedCodeId) {
        MessageUtil.warning('대분류를 먼저 선택하세요.');
        return;
    }
    
    // 테이블의 모든 행에서 체크박스 상태 수집
    const rows = document.querySelectorAll('#codeDetailTable tbody tr');
    
    if (!rows || rows.length === 0) {
        MessageUtil.warning('저장할 코드 상세값이 없습니다.');
        return;
    }
    
    // 모든 행의 체크박스 상태를 배열로 수집
    const updateList = [];
    let hasUseYnY = false; // 하나라도 Y가 있는지 확인
    
    rows.forEach(function(row) {
        const checkbox = row.querySelector('input[type="checkbox"][data-code-id][data-code]');
        if (!checkbox) {
            return;
        }
        
        const codeId = checkbox.getAttribute('data-code-id');
        const code = checkbox.getAttribute('data-code');
        
        if (!codeId || !code) {
            return;
        }
        
        const useYn = checkbox.checked ? 'Y' : 'N';
        if (useYn === 'Y') {
            hasUseYnY = true;
        }
        
        updateList.push({
            codeId: codeId,
            code: code,
            useYn: useYn
        });
    });
    
    if (updateList.length === 0) {
        MessageUtil.warning('저장할 데이터가 없습니다.');
        return;
    }
    
    // 소분류에 Y가 하나라도 있으면 대분류 사용여부 확인
    if (hasUseYnY) {
        // 대분류 테이블에서 선택된 행의 체크박스 상태 확인
        const codeGroupRow = document.querySelector('#codeGroupTable tbody tr[data-code-id="' + selectedCodeId + '"]');
        if (codeGroupRow) {
            const codeGroupCheckbox = codeGroupRow.querySelector('input[type="checkbox"][data-code-id="' + selectedCodeId + '"]');
            if (codeGroupCheckbox && !codeGroupCheckbox.checked) {
                // 대분류가 N이면 알럿창 표시
                MessageUtil.confirmed('소분류를 사용하려면 대분류도 사용 상태로 변경해야 합니다. 대분류를 사용 상태로 변경하시겠습니까?', function() {
                    // 대분류를 Y로 변경하는 API 호출
                    const codeGroupUpdateList = [{
                        codeId: selectedCodeId,
                        useYn: 'Y'
                    }];
                    
                    const codeGroupCallback = function(result) {
                        if (result && result.status && result.status.code === 200) {
                            // 대분류 체크박스 상태 업데이트
                            if (codeGroupCheckbox) {
                                codeGroupCheckbox.checked = true;
                            }
                            
                            // 대분류 목록 새로고침
                            fn_searchCodeGroup();
                            
                            // 소분류 저장 진행
                            fn_saveCodeDetailUseYnInternal(updateList);
                        } else {
                            const message = (result && result.status && result.status.message) || '대분류 사용여부 변경에 실패했습니다.';
                            MessageUtil.error(message);
                        }
                    };
                    
                    callModule.call(Util.getRequestUrl('/system/code/updateCodeGroupUseYn.do'), codeGroupUpdateList, codeGroupCallback, true, 'POST');
                });
                return; // 확인 대기 중이므로 여기서 종료
            }
        }
    }
    
    // 대분류가 이미 Y이거나 소분류에 Y가 없으면 바로 저장
    fn_saveCodeDetailUseYnInternal(updateList);
}

/**
 * 코드 상세값 사용여부 저장 (내부 함수)
 * @param {Array} updateList 업데이트할 리스트
 */
function fn_saveCodeDetailUseYnInternal(updateList) {
    // API 호출
    const callback = function(result) {
        if (result && result.status && result.status.code === 200) {
            MessageUtil.success('코드 상세값 사용여부가 저장되었습니다.', function() {
                // 소분류 목록 새로고침
                if (selectedCodeId) {
                    fn_renderCodeDetailList(selectedCodeId);
                }
            });
        } else {
            const message = (result && result.status && result.status.message) || '코드 상세값 사용여부 저장에 실패했습니다.';
            MessageUtil.error(message);
        }
    };
    
    callModule.call(Util.getRequestUrl('/system/code/updateCodeDetailUseYn.do'), updateList, callback, true, 'POST');
}