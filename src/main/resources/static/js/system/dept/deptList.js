/**
 * 부서 목록 화면 JavaScript
 */

// 전역 변수
let deptData = [];           // 전체 부서 데이터
let expandedNodes = [];      // 확장된 노드 목록
let selectedDeptCd = null;   // 선택된 부서코드

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 신규 등록 버튼 이벤트
    document.getElementById('registerBtn').addEventListener('click', fn_goRegisterForm);
    
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
    
    // 초기화 버튼 이벤트
    document.getElementById('resetBtn').addEventListener('click', fn_resetSearch);
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
    document.getElementById('useYnFilter').value = '';
    fn_searchDeptList();
}

/**
 * 부서 목록 조회
 */
function fn_searchDeptList() {
    const searchKeyword = document.getElementById('searchKeyword').value.trim();
    const useYn = document.getElementById('useYnFilter').value;
    
    const data = {
        useYn: Util.isEmpty(useYn) ? null : useYn,
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
            document.getElementById('deptTreeList').innerHTML = '<div class="dept-tree-empty">조회 결과가 없습니다.</div>';
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
        treeContainer.innerHTML = '<div class="dept-tree-empty">조회 결과가 없습니다.</div>';
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
    item.className = 'dept-tree-item';
    item.dataset.deptCd = dept.deptCd;
    item.dataset.level = dept.treeLevel || 0;
    item.dataset.hasChildren = dept.hasChildren || 0;
    
    const indentWidth = (dept.treeLevel || 0) * 20;
    const hasChildren = (dept.hasChildren || 0) > 0;
    const isExpanded = expandedNodes.includes(dept.deptCd);
    
    // 트리 행 생성
    const row = document.createElement('div');
    row.className = 'dept-tree-row';
    if (selectedDeptCd === dept.deptCd) {
        row.classList.add('selected');
    }
    
    // 들여쓰기
    const indent = document.createElement('span');
    indent.className = 'dept-tree-indent';
    indent.style.width = `${indentWidth}px`;
    row.appendChild(indent);
    
    // 토글 버튼
    const toggle = document.createElement('button');
    toggle.className = 'dept-tree-toggle';
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
        icon.textContent = isExpanded ? '−' : '+';
        toggle.appendChild(icon);
    }
    row.appendChild(toggle);
    
    // 라벨
    const label = document.createElement('span');
    label.className = 'dept-tree-label';
    label.onclick = () => fn_selectDept(dept.deptCd);
    
    const name = document.createElement('span');
    name.className = 'dept-name';
    name.textContent = dept.deptNm;
    label.appendChild(name);
    
    const code = document.createElement('span');
    code.className = 'dept-code';
    code.textContent = `(${dept.deptCd})`;
    label.appendChild(code);
    
    // 소속 인원수 표시
    if (dept.memberCnt && dept.memberCnt > 0) {
        const count = document.createElement('span');
        count.className = 'dept-count';
        count.textContent = `${dept.memberCnt}명`;
        label.appendChild(count);
    }
    
    row.appendChild(label);
    item.appendChild(row);
    
    // 하위 부서 컨테이너
    if (hasChildren) {
        const childrenContainer = document.createElement('div');
        childrenContainer.className = 'dept-tree-children';
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
    
    const childrenContainer = item.querySelector('.dept-tree-children');
    if (!childrenContainer) return;
    
    const toggleButton = item.querySelector('.dept-tree-toggle');
    const icon = toggleButton.querySelector('i');
    
    if (childrenContainer.classList.contains('expanded')) {
        // 축소
        childrenContainer.classList.remove('expanded');
        toggleButton.dataset.expanded = 'false';
        icon.className = 'icon-plus';
        icon.textContent = '+';
        
        const index = expandedNodes.indexOf(deptCd);
        if (index > -1) {
            expandedNodes.splice(index, 1);
        }
    } else {
        // 확장
        childrenContainer.classList.add('expanded');
        toggleButton.dataset.expanded = 'true';
        icon.className = 'icon-minus';
        icon.textContent = '−';
        
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
        const prevItem = document.querySelector(`[data-dept-cd="${selectedDeptCd}"] .dept-tree-row`);
        if (prevItem) {
            prevItem.classList.remove('selected');
        }
    }
    
    // 새 선택
    selectedDeptCd = deptCd;
    const currentItem = document.querySelector(`[data-dept-cd="${deptCd}"] .dept-tree-row`);
    if (currentItem) {
        currentItem.classList.add('selected');
    }
    
    // 상세 정보 조회
    fn_loadDeptDetail(deptCd);
}

/**
 * 부서 상세 정보 조회
 * @param deptCd 부서코드
 */
function fn_loadDeptDetail(deptCd) {
    const data = { deptCd: deptCd };
    const url = Util.getRequestUrl('/system/dept/getDeptDetail.do');
    
    callModule.call(url, data, (result) => {
        if (result && result.result) {
            const dept = result.result;
            fn_renderDeptDetail(dept);
        } else {
            document.getElementById('deptDetailArea').innerHTML = 
                '<div class="dept-detail-empty"><p class="txt-body">부서 정보를 조회할 수 없습니다.</p></div>';
        }
    }, true, 'POST');
}

/**
 * 부서 상세 정보 렌더링
 * @param dept 부서 정보
 */
function fn_renderDeptDetail(dept) {
    const detailArea = document.getElementById('deptDetailArea');
    
    let html = `
        <div class="dept-detail-content">
            <!-- 기본 정보 -->
            <div class="table-box type01">
                <div class="table-inner">
                    <table class="table-type01 form-table dept-detail-table">
                        <caption class="hidden">부서 기본 정보</caption>
                        <colgroup>
                            <col width="30%"/>
                            <col width="70%"/>
                        </colgroup>
                        <tbody>
                        <tr>
                            <th>부서코드</th>
                            <td>${dept.deptCd || '-'}</td>
                        </tr>
                        <tr>
                            <th>부서명</th>
                            <td>${dept.deptNm || '-'}</td>
                        </tr>
                        <tr>
                            <th>상위부서</th>
                            <td>${dept.upperDeptNm || '-'}</td>
                        </tr>
                        <tr>
                            <th>부서레벨</th>
                            <td>${dept.deptLevel || '-'}</td>
                        </tr>
                        <tr>
                            <th>부서순서</th>
                            <td>${dept.deptOrder || '-'}</td>
                        </tr>
                        <tr>
                            <th>부서담당자</th>
                            <td>${dept.deptMngrId || '-'}</td>
                        </tr>
                        <tr>
                            <th>사용여부</th>
                            <td>${dept.useYn === 'Y' ? '사용' : dept.useYn === 'N' ? '미사용' : '-'}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <!-- 통계 정보 -->
            <div class="dept-stat-info">
                <div class="stat-item">
                    <span class="stat-label">소속 인원수</span>
                    <span class="stat-value">${dept.memberCnt || 0}명</span>
                </div>
                <div class="stat-item">
                    <span class="stat-label">하위 부서 수</span>
                    <span class="stat-value">${(dept.hasChildren || 0) > 0 ? dept.hasChildren : 0}개</span>
                </div>
            </div>
            
            <!-- 시스템 정보 -->
            <div class="table-box type01 mt-20">
                <div class="table-inner">
                    <table class="table-type01 form-table dept-detail-table">
                        <caption class="hidden">시스템 정보</caption>
                        <colgroup>
                            <col width="30%"/>
                            <col width="70%"/>
                        </colgroup>
                        <tbody>
                        <tr>
                            <th>등록자ID</th>
                            <td class="readonly-field">${dept.registerId || '-'}</td>
                        </tr>
                        <tr>
                            <th>등록일시</th>
                            <td class="readonly-field">${dept.registDt || '-'}</td>
                        </tr>
                        <tr>
                            <th>수정자ID</th>
                            <td class="readonly-field">${dept.updusrId || '-'}</td>
                        </tr>
                        <tr>
                            <th>수정일시</th>
                            <td class="readonly-field">${dept.updtDt || '-'}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <!-- 액션 버튼 -->
            <div class="dept-detail-actions">
                <button type="button" class="outline-btn" onclick="fn_goEditForm('${dept.deptCd}')">수정</button>
                <button type="button" class="outline-btn" onclick="fn_goViewForm('${dept.deptCd}')">상세보기</button>
                <button type="button" class="outline-btn-Bck" onclick="fn_deleteDept('${dept.deptCd}')">삭제</button>
            </div>
        </div>
    `;
    
    detailArea.innerHTML = html;
}

/**
 * 신규 등록 화면으로 이동
 */
function fn_goRegisterForm() {
    location.href = '/system/dept/deptRegisterForm.do';
}

/**
 * 수정 화면으로 이동
 * @param deptCd 부서코드
 */
function fn_goEditForm(deptCd) {
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드가 없습니다.');
        return;
    }
    
    const data = { deptCd: deptCd };
    callModule.post(Util.getRequestUrl('/system/dept/deptEditForm.do'), data, 'post');
}

/**
 * 상세보기 화면으로 이동
 * @param deptCd 부서코드
 */
function fn_goViewForm(deptCd) {
    if (Util.isEmpty(deptCd)) {
        MessageUtil.error('부서코드가 없습니다.');
        return;
    }
    
    const data = { deptCd: deptCd };
    callModule.post(Util.getRequestUrl('/system/dept/deptViewForm.do'), data, 'post');
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
    
    MessageUtil.confirmed('정말 삭제하시겠습니까?', function() {
        // TODO: 삭제 API 호출 (추후 구현)
        MessageUtil.success('삭제 기능은 추후 구현 예정입니다.');
    });
}

