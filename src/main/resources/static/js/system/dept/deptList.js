/**
 * 부서 목록 화면 JavaScript
 */

// 전역 변수
let deptData = [];           // 전체 부서 데이터
let expandedNodes = [];      // 확장된 노드 목록
let selectedDeptCd = null;   // 선택된 부서코드
let currentMode = 'view';    // 현재 모드: 'view' | 'register' | 'edit'

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 신규 등록 버튼 이벤트
    document.getElementById('registerBtn').addEventListener('click', fn_switchToRegisterMode);
    
    // 검색 버튼 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => {
        fn_searchDeptList();
    });
    
    // 검색어 입력란 엔터키 이벤트
    document.getElementById('searchKeyword').addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            fn_searchDeptList();
        }
    });
});

/**
 * 초기화
 */
function fn_init() {
    fn_searchDeptList();
}

/**
 * 검색 초기화
 */
function fn_resetSearch() {
    document.getElementById('searchKeyword').value = '';
    fn_searchDeptList();
}

/**
 * 부서 목록 조회
 */
function fn_searchDeptList() {
    const searchKeyword = document.getElementById('searchKeyword').value.trim();
    
    const data = {
        searchVO: {
            searchKeyword: Util.isEmpty(searchKeyword) ? null : searchKeyword
        }
    };
    
    const url = Util.getRequestUrl('/system/dept/getDeptList.do');
    
    callModule.call(url, data, (result) => {
        if (result && result.result) {
            deptData = result.result;
            fn_renderTree(deptData);
            document.getElementById('treeTotalCount').textContent = `(${deptData.length}개)`;
        } else {
            document.getElementById('deptTreeList').innerHTML = '<div class="spt-tree-empty">조회 결과가 없습니다.</div>';
            document.getElementById('treeTotalCount').textContent = '(0개)';
            deptData = [];
        }
    }, true, 'POST');
}

/**
 * 트리 구조로 부서 목록 렌더링
 * @param deptList 부서 목록
 */
function fn_renderTree(deptList) {
    const treeContainer = document.getElementById('deptTreeList');
    treeContainer.innerHTML = '';
    
    if (!deptList || deptList.length === 0) {
        treeContainer.innerHTML = '<div class="spt-tree-empty">조회 결과가 없습니다.</div>';
        return;
    }
    
    // 최상위 부서만 필터링 (treeLevel === 0)
    const rootDepts = deptList.filter(d => (d.treeLevel === 0 || d.treeLevel === null));
    
    rootDepts.forEach(dept => {
        const itemElement = fn_createTreeItem(dept, deptList);
        treeContainer.appendChild(itemElement);
    });
}

/**
 * 트리 아이템 생성
 * @param dept 부서 정보
 * @param allDepts 전체 부서 목록
 * @return 트리 아이템 DOM 요소
 */
function fn_createTreeItem(dept, allDepts) {
    const item = document.createElement('div');
    item.className = 'spt-tree-item';
    item.dataset.deptCd = dept.deptCd;
    item.dataset.level = dept.treeLevel || 0;
    item.dataset.hasChildren = dept.hasChildren || 0;
    
    const indentWidth = (dept.treeLevel || 0) * 20;
    const hasChildren = (dept.hasChildren || 0) > 0;
    const isExpanded = expandedNodes.includes(dept.deptCd);
    
    // 트리 행 생성
    const row = document.createElement('div');
    row.className = 'spt-tree-row';
    if (selectedDeptCd === dept.deptCd) {
        row.classList.add('selected');
    }
    
    // 들여쓰기
    const indent = document.createElement('span');
    indent.className = 'spt-tree-indent';
    indent.style.width = `${indentWidth}px`;
    row.appendChild(indent);
    
    // 토글 버튼
    const toggle = document.createElement('button');
    toggle.className = 'spt-tree-toggle';
    toggle.type = 'button';
    if (!hasChildren) {
        toggle.classList.add('no-children');
    } else {
        toggle.dataset.expanded = isExpanded;
        toggle.onclick = (e) => {
            e.stopPropagation();
            fn_toggleNode(dept.deptCd);
        };
        
        const icon = document.createElement('i');
        icon.className = isExpanded ? 'icon-minus' : 'icon-plus';
        toggle.appendChild(icon);
    }
    row.appendChild(toggle);
    
    // 라벨
    const label = document.createElement('span');
    label.className = 'spt-tree-label';
    label.onclick = () => fn_selectDept(dept.deptCd);
    
    const name = document.createElement('span');
    name.className = 'dep-name';
    name.textContent = dept.deptNm;
    label.appendChild(name);
    
    const code = document.createElement('span');
    code.className = 'dep-code';
    code.textContent = `(${dept.deptCd})`;
    label.appendChild(code);
    
    // 소속 인원수 표시
    if (dept.memberCnt && dept.memberCnt > 0) {
        const count = document.createElement('span');
        count.className = 'dep-count';
        count.textContent = `${dept.memberCnt}명`;
        label.appendChild(count);
    }
    
    row.appendChild(label);
    item.appendChild(row);
    
    // 하위 부서 컨테이너
    if (hasChildren) {
        const childrenContainer = document.createElement('div');
        childrenContainer.className = 'spt-tree-children';
        if (isExpanded) {
            childrenContainer.classList.add('expanded');
        }
        
        // 하위 부서 필터링 및 추가
        const children = allDepts.filter(d => d.upperDeptCd === dept.deptCd);
        children.forEach(child => {
            const childItem = fn_createTreeItem(child, allDepts);
            childrenContainer.appendChild(childItem);
        });
        
        item.appendChild(childrenContainer);
    }
    
    return item;
}

/**
 * 노드 확장/축소 토글
 * @param deptCd 부서코드
 */
function fn_toggleNode(deptCd) {
    const item = document.querySelector(`[data-dept-cd="${deptCd}"]`);
    if (!item) return;
    
    const childrenContainer = item.querySelector('.spt-tree-children');
    if (!childrenContainer) return;
    
    const toggleButton = item.querySelector('.spt-tree-toggle');
    const icon = toggleButton.querySelector('i');
    
    if (childrenContainer.classList.contains('expanded')) {
        // 축소
        childrenContainer.classList.remove('expanded');
        toggleButton.dataset.expanded = 'false';
        icon.className = 'icon-plus';
        
        const index = expandedNodes.indexOf(deptCd);
        if (index > -1) {
            expandedNodes.splice(index, 1);
        }
    } else {
        // 확장
        childrenContainer.classList.add('expanded');
        toggleButton.dataset.expanded = 'true';
        icon.className = 'icon-minus';
        
        if (!expandedNodes.includes(deptCd)) {
            expandedNodes.push(deptCd);
        }
    }
}

/**
 * 부서 선택
 * @param deptCd 부서코드
 */
function fn_selectDept(deptCd) {
    // 이전 선택 해제
    if (selectedDeptCd) {
        const prevItem = document.querySelector(`[data-dept-cd="${selectedDeptCd}"] .spt-tree-row`);
        if (prevItem) {
            prevItem.classList.remove('selected');
        }
    }
    
    // 새 선택
    selectedDeptCd = deptCd;
    const currentItem = document.querySelector(`[data-dept-cd="${deptCd}"] .spt-tree-row`);
    if (currentItem) {
        currentItem.classList.add('selected');
    }
    
    // 조회 모드로 전환
    fn_switchToViewMode(deptCd);
}

/**
 * 조회 모드로 전환
 * @param deptCd 부서코드
 */
function fn_switchToViewMode(deptCd) {
    currentMode = 'view';
    currentDeptCd = deptCd;
    
    if (Util.isEmpty(deptCd)) {
        document.getElementById('deptDetailArea').innerHTML = 
            '<div class="spt-detail-empty"><p class="txt-body">부서를 선택하세요</p></div>';
        return;
    }
    
    // 부서 상세 정보 조회
    const data = { deptCd: deptCd };
    const url = Util.getRequestUrl('/system/dept/getDeptDetail.do');
    
    callModule.call(url, data, (result) => {
        if (result && result.result) {
            const dept = result.result;
            fn_renderViewMode(dept);
        } else {
            document.getElementById('deptDetailArea').innerHTML = 
                '<div class="spt-detail-empty"><p class="txt-body">부서 정보를 조회할 수 없습니다.</p></div>';
        }
    }, true, 'POST');
}

/**
 * 등록 모드로 전환
 */
function fn_switchToRegisterMode() {
    currentMode = 'register';
    currentDeptCd = null;
    fn_renderRegisterMode();
}

/**
 * 수정 모드로 전환
 * @param deptCd 부서코드
 */
function fn_switchToEditMode(deptCd) {
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드가 없습니다.');
        return;
    }
    
    currentMode = 'edit';
    currentDeptCd = deptCd;
    
    // 부서 상세 정보 조회 후 수정 폼 렌더링
    const data = { deptCd: deptCd };
    const url = Util.getRequestUrl('/system/dept/getDeptDetail.do');
    
    callModule.call(url, data, (result) => {
        if (result && result.result) {
            const dept = result.result;
            fn_renderEditMode(dept);
        } else {
            MessageUtil.error('부서 정보를 조회할 수 없습니다.');
        }
    }, true, 'POST');
}

/**
 * 부서 상세 정보 렌더링 (조회 모드)
 * @param dept 부서 정보
 */
function fn_renderViewMode(dept) {
    const detailArea = document.getElementById('deptDetailArea');
    const template = document.getElementById('deptViewTemplate');
    
    if (!template) {
        console.error('조회 모드 템플릿을 찾을 수 없습니다.');
        return;
    }
    
    // 템플릿 복제
    const clone = template.content.cloneNode(true);
    
    // 데이터 바인딩
    const bindValue = (selector, value, formatter) => {
        const element = clone.querySelector(`[data-bind="${selector}"]`);
        if (element) {
            element.textContent = formatter ? formatter(value) : (value || '-');
        }
    };
    
    bindValue('deptCd', dept.deptCd);
    bindValue('deptNm', dept.deptNm);
    bindValue('upperDeptNm', dept.upperDeptNm);
    bindValue('deptLevel', dept.deptLevel);
    bindValue('deptOrder', dept.deptOrder);
    bindValue('deptMngrId', dept.deptMngrId);
    bindValue('useYn', dept.useYn, (val) => {
        if (val === 'Y') return '사용';
        if (val === 'N') return '미사용';
        return '-';
    });
    bindValue('memberCnt', dept.memberCnt, (val) => `${val || 0}명`);
    bindValue('hasChildren', dept.hasChildren, (val) => `${(val || 0) > 0 ? val : 0}개`);
    bindValue('registerId', dept.registerId);
    bindValue('registDt', dept.registDt);
    bindValue('updusrId', dept.updusrId);
    bindValue('updtDt', dept.updtDt);
    
    // 액션 버튼 이벤트 바인딩
    const editBtn = clone.querySelector('[data-action="edit"]');
    if (editBtn) {
        editBtn.onclick = () => fn_switchToEditMode(dept.deptCd);
    }
    
    const deleteBtn = clone.querySelector('[data-action="delete"]');
    if (deleteBtn) {
        deleteBtn.onclick = () => fn_deleteDept(dept.deptCd);
    }
    
    // 기존 내용 제거 후 추가
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

/**
 * 등록 모드 렌더링
 */
function fn_renderRegisterMode() {
    const detailArea = document.getElementById('deptDetailArea');
    const template = document.getElementById('deptRegisterTemplate');
    
    if (!template) {
        console.error('등록 모드 템플릿을 찾을 수 없습니다.');
        return;
    }
    
    // 템플릿 복제
    const clone = template.content.cloneNode(true);
    
    // 부서코드 입력 필드에 숫자만 입력 가능하도록 이벤트 리스너 추가
    const deptCdNumberInput = clone.querySelector('#deptCdNumber');
    const deptCdHidden = clone.querySelector('#deptCd');
    
    if (deptCdNumberInput && deptCdHidden) {
        // 숫자만 입력 가능하도록 처리
        deptCdNumberInput.addEventListener('input', function(e) {
            // 숫자가 아닌 문자 제거
            let value = e.target.value.replace(/[^0-9]/g, '');
            e.target.value = value;
            
            // DEPT_ + 숫자 조합하여 hidden 필드에 저장
            if (value) {
                deptCdHidden.value = 'DEPT_' + value;
            } else {
                deptCdHidden.value = '';
            }
        });
        
        // 키보드 이벤트로 숫자만 입력 가능하도록 제한
        deptCdNumberInput.addEventListener('keydown', function(e) {
            // 숫자 키, 백스페이스, 삭제, 탭, 화살표 키만 허용
            const allowedKeys = [
                'Backspace', 'Delete', 'Tab', 'ArrowLeft', 'ArrowRight', 
                'ArrowUp', 'ArrowDown', 'Home', 'End'
            ];
            
            if (allowedKeys.includes(e.key)) {
                return;
            }
            
            // Ctrl/Cmd + A, C, V, X 허용
            if ((e.ctrlKey || e.metaKey) && ['a', 'c', 'v', 'x'].includes(e.key.toLowerCase())) {
                return;
            }
            
            // 숫자 키만 허용
            if (!/[0-9]/.test(e.key)) {
                e.preventDefault();
            }
        });
        
        // 붙여넣기 이벤트 처리
        deptCdNumberInput.addEventListener('paste', function(e) {
            e.preventDefault();
            const pastedText = (e.clipboardData || window.clipboardData).getData('text');
            const numbersOnly = pastedText.replace(/[^0-9]/g, '');
            e.target.value = numbersOnly;
            
            // DEPT_ + 숫자 조합하여 hidden 필드에 저장
            if (numbersOnly) {
                deptCdHidden.value = 'DEPT_' + numbersOnly;
            } else {
                deptCdHidden.value = '';
            }
        });
    }
    
    // 기존 내용 제거 후 추가
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

/**
 * 수정 모드 렌더링
 * @param dept 부서 정보
 */
function fn_renderEditMode(dept) {
    const detailArea = document.getElementById('deptDetailArea');
    const template = document.getElementById('deptEditTemplate');
    
    if (!template) {
        console.error('수정 모드 템플릿을 찾을 수 없습니다.');
        return;
    }
    
    // 템플릿 복제
    const clone = template.content.cloneNode(true);
    
    // 데이터 바인딩
    const bindValue = (selector, value) => {
        const elements = clone.querySelectorAll(`[data-bind="${selector}"]`);
        elements.forEach(element => {
            if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
                element.value = value || '';
            } else {
                element.textContent = value || '-';
            }
        });
    };
    
    bindValue('deptCd', dept.deptCd);
    bindValue('deptNm', dept.deptNm);
    bindValue('upperDeptNm', dept.upperDeptNm);
    bindValue('upperDeptCd', dept.upperDeptCd);
    bindValue('deptOrder', dept.deptOrder);
    
    // 라디오 버튼 처리
    const useYnRadioY = clone.querySelector('[data-bind-radio="useYn-Y"]');
    const useYnRadioN = clone.querySelector('[data-bind-radio="useYn-N"]');
    if (useYnRadioY && useYnRadioN) {
        if (dept.useYn === 'Y') {
            useYnRadioY.checked = true;
            useYnRadioN.checked = false;
        } else if (dept.useYn === 'N') {
            useYnRadioY.checked = false;
            useYnRadioN.checked = true;
        }
    }
    
    // 기존 내용 제거 후 추가
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

/**
 * 부서 저장 (등록/수정)
 */
function fn_saveDept() {
    const form = document.getElementById('deptForm');
    if (!form) {
        MessageUtil.error('폼을 찾을 수 없습니다.');
        return;
    }
    
    // 필수값 검증
    let deptCd = '';
    const deptCdHidden = document.getElementById('deptCd');
    const deptCdNumber = document.getElementById('deptCdNumber');
    
    // 등록 모드인 경우 hidden 필드 또는 숫자 필드에서 부서코드 조합
    if (currentMode === 'register') {
        if (deptCdHidden && deptCdHidden.value) {
            deptCd = deptCdHidden.value.trim();
        } else if (deptCdNumber && deptCdNumber.value) {
            deptCd = 'DEPT_' + deptCdNumber.value.trim();
        }
    } else {
        // 수정 모드인 경우 기존 방식
        deptCd = document.getElementById('deptCd')?.value?.trim();
    }
    
    const deptNm = document.getElementById('deptNm')?.value?.trim();
    
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드는 필수입니다.');
        return;
    }
    if (Util.isEmpty(deptNm)) {
        MessageUtil.error('부서명은 필수입니다.');
        return;
    }
    
    // 등록 모드인 경우 중복 체크 필요 (이미 체크했는지 확인)
    if (currentMode === 'register') {
        const checkMsg = document.getElementById('deptCdCheckMsg');
        if (!checkMsg || !checkMsg.textContent.includes('사용 가능')) {
            MessageUtil.error('부서코드 중복 확인을 해주세요.');
            return;
        }
    }
    
    // 폼 데이터 수집
    const upperDeptCdHidden = document.getElementById('upperDeptCdHidden');
    let upperDeptCd = null;
    
    // 상위부서 코드는 hidden 필드에서만 가져옴 (텍스트 필드에는 부서명이 있음)
    if (upperDeptCdHidden && upperDeptCdHidden.value) {
        const value = upperDeptCdHidden.value.trim();
        upperDeptCd = Util.isEmpty(value) ? null : value;
    }
    
    // 부서순서 검증
    const deptOrderInput = document.getElementById('deptOrder');
    const deptOrder = parseInt(deptOrderInput?.value || '1');
    if (deptOrder < 1) {
        MessageUtil.error('부서순서는 1 이상이어야 합니다.');
        if (deptOrderInput) {
            deptOrderInput.focus();
        }
        return;
    }
    
    const formData = {
        deptCd: deptCd,
        deptNm: deptNm,
        upperDeptCd: upperDeptCd,
        deptOrder: deptOrder,
        useYn: document.querySelector('input[name="useYn"]:checked')?.value || 'Y'
    };
    
    // 빈 문자열을 null로 변환
    if (Util.isEmpty(formData.upperDeptCd)) {
        formData.upperDeptCd = null;
    }
    
    const url = Util.getRequestUrl('/system/dept/saveDept.do');
    
    callModule.call(url, formData, (result) => {
        if (result && result.result && result.result.success) {
            MessageUtil.success(result.status.message || '저장이 완료되었습니다.', function() {
                // 트리 새로고침
                fn_searchDeptList();
                // 저장된 부서ㅣ 선택 상태 유지
                if (currentMode === 'register') {
                    // 등록 모드인 경우 새로 등록된 부서 선택
                    setTimeout(() => {
                        fn_switchToViewMode(formData.deptCd);
                    }, 100);
                } else {
                    // 수정 모드인 경우 기존 부서 선택 상태 유지
                    fn_switchToViewMode(formData.deptCd);
                }
            });
        } else {
            MessageUtil.error((result && result.status && result.status.message) || '저장에 실패하였습니다.');
        }
    }, true, 'POST');
}

/**
 * 폼 취소
 */
function fn_cancelForm() {
    if (currentMode === 'register') {
        // 등록 모드 취소 시 빈 화면
        document.getElementById('deptDetailArea').innerHTML = 
            '<div class="spt-detail-empty"><p class="txt-body">부서를 선택하세요</p></div>';
        currentMode = 'view';
        currentDeptCd = null;
    } else if (currentMode === 'edit' && currentDeptCd) {
        // 수정 모드 취소 시 조회 모드로 복귀
        fn_switchToViewMode(currentDeptCd);
    }
}

/**
 * 부서코드 중복 확인
 */
function fn_checkDeptCodeDuplicate() {
    // 등록 모드인 경우 hidden 필드의 값을 사용
    const deptCdHidden = document.getElementById('deptCd');
    const deptCdNumber = document.getElementById('deptCdNumber');
    const checkMsg = document.getElementById('deptCdCheckMsg');
    
    let deptCd = '';
    if (deptCdHidden && deptCdHidden.value) {
        // 등록 모드: hidden 필드에서 전체 부서코드 가져오기
        deptCd = deptCdHidden.value.trim();
    } else if (deptCdNumber && deptCdNumber.value) {
        // 폴백: 숫자만 입력된 경우 DEPT_ 추가
        deptCd = 'DEPT_' + deptCdNumber.value.trim();
    }
    
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드를 입력해주세요.');
        return;
    }
    
    const data = { deptCd: deptCd };
    const url = Util.getRequestUrl('/system/dept/checkDeptCodeDuplicate.do');
    
    callModule.call(url, data, (result) => {
        if (result && result.result) {
            const isDuplicate = result.result.duplicate;
            if (checkMsg) {
                if (isDuplicate) {
                    checkMsg.textContent = '이미 사용 중인 부서코드입니다.';
                    checkMsg.className = 'form-msg error';
                } else {
                    checkMsg.textContent = '사용 가능한 부서코드입니다.';
                    checkMsg.className = 'form-msg success';
                }
            }
        }
    }, true, 'POST');
}

/**
 * 부서 검색 모달 열기
 */
function fn_openDeptSearchModal() {
    if (typeof DeptSearchModal !== 'undefined') {
        DeptSearchModal.open(function(deptCd, deptNm) {
            // 선택한 부서 정보를 폼에 반영
            const upperDeptCdInput = document.getElementById('upperDeptCd');
            const upperDeptCdHidden = document.getElementById('upperDeptCdHidden');
            const upperDeptNmInput = document.getElementById('upperDeptNm');
            
            if (upperDeptCdInput) {
                upperDeptCdInput.value = deptNm || '';
            }
            if (upperDeptCdHidden) {
                upperDeptCdHidden.value = deptCd || '';
            } else if (upperDeptCdInput && upperDeptCdInput.type === 'hidden') {
                upperDeptCdInput.value = deptCd || '';
            }
            if (upperDeptNmInput) {
                upperDeptNmInput.value = deptNm || '';
            }
        });
    } else {
        MessageUtil.error('부서 검색 모달을 사용할 수 없습니다.');
    }
}

/**
 * 상위부서 초기화
 */
function fn_clearUpperDept() {
    const upperDeptCdInput = document.getElementById('upperDeptCd');
    const upperDeptCdHidden = document.getElementById('upperDeptCdHidden');
    const upperDeptNmInput = document.getElementById('upperDeptNm');
    
    if (upperDeptCdInput) {
        upperDeptCdInput.value = '';
    }
    if (upperDeptCdHidden) {
        upperDeptCdHidden.value = '';
    }
    if (upperDeptNmInput) {
        upperDeptNmInput.value = '';
    }
}

/**
 * 부서 삭제
 * @param deptCd 부서코드
 */
function fn_deleteDept(deptCd) {
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드가 없습니다.');
        return;
    }
    
    // 하위 부서 존재 여부 확인
    const dept = deptData.find(d => d.deptCd === deptCd);
    if (dept && (dept.hasChildren || 0) > 0) {
        MessageUtil.error('하위 부서가 존재하여 삭제할 수 없습니다.');
        return;
    }
    
    if (dept && (dept.memberCnt || 0) > 0) {
        MessageUtil.error('소속 인원이 존재하여 삭제할 수 없습니다.');
        return;
    }
    
    MessageUtil.confirmed('정말 삭제하시겠습니까?', function() {
        const data = { deptCd: deptCd };
        const url = Util.getRequestUrl('/system/dept/deleteDept.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result && result.result.success) {
                MessageUtil.success(result.status.message || '삭제가 완료되었습니다.', function() {
                    // 트리 새로고침
                    fn_searchDeptList();
                    // 상세 영역 초기화
                    document.getElementById('deptDetailArea').innerHTML = 
                        '<div class="spt-detail-empty"><p class="txt-body">부서를 선택하세요</p></div>';
                    selectedDeptCd = null;
                    currentMode = 'view';
                    currentDeptCd = null;
                });
            } else {
                MessageUtil.error((result && result.status && result.status.message) || '삭제에 실패하였습니다.');
            }
        }, true, 'POST');
    });
}


