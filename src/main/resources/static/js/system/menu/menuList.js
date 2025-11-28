/**
 * 메뉴 및 권한 관리 화면 JavaScript
 */

// 전역 변수
let menuData = [];           // 전체 메뉴 데이터 (계층 구조)
let expandedNodes = new Set(); // 확장된 노드 ID 목록
let selectedMenuId = null;   // 선택된 메뉴ID
let selectedRoleCd = null;   // 선택된 권한 그룹 코드
let roleList = [];           // 현재 메뉴의 권한 그룹 목록
let userList = [];           // 현재 권한 그룹의 사용자 목록
let changedAuthList = [];    // 변경된 권한 매핑 정보 배열

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 메뉴 상세 정보 접기/펼치기 관련 리스너 제거 (2행 레이아웃에서 불필요)
    
    // 사용자 추가/삭제 버튼 이벤트
    const addUserBtn = document.getElementById('addUserBtn');
    const deleteUserBtn = document.getElementById('deleteUserBtn');
    if (addUserBtn) {
        addUserBtn.addEventListener('click', fn_addUserToRole);
    }
    if (deleteUserBtn) {
        deleteUserBtn.addEventListener('click', fn_deleteUserFromRole);
    }
});

/**
 * 초기화
 */
function fn_init() {
    fn_searchMenuList();
}

/**
 * 메뉴 목록 조회
 */
function fn_searchMenuList() {
    const data = {
        searchKeyword: ''
    };

    const callback = function(result) {
        menuData = result.result || [];
        fn_renderTree();
    };
    
    callModule.call(Util.getRequestUrl('/system/menu/getMenuList.do'), data, callback, true, 'POST');
}

/**
 * 트리 렌더링
 */
function fn_renderTree() {
    const treeContainer = document.getElementById('menuTreeList');
    treeContainer.innerHTML = '';
    
    if (!menuData || menuData.length === 0) {
        treeContainer.innerHTML = '<div class="menu-tree-empty">조회 결과가 없습니다.</div>';
        return;
    }
    
    menuData.forEach(menu => {
        treeContainer.appendChild(createTreeItem(menu));
    });
}

/**
 * 트리 아이템 HTML 생성 (재귀)
 */
function createTreeItem(menu) {
    const itemDiv = document.createElement('div');
    itemDiv.className = 'menu-tree-item';
    
    const rowDiv = document.createElement('div');
    rowDiv.className = 'menu-tree-row';
    if (menu.menuId === selectedMenuId) {
        rowDiv.classList.add('selected');
    }
    rowDiv.dataset.id = menu.menuId;
    rowDiv.onclick = (e) => {
        if (e.target.classList.contains('menu-tree-toggle')) return;
        fn_selectMenu(menu.menuId);
    };
    
    // 들여쓰기 (레벨에 따른 패딩)
    const paddingLeft = (menu.menuLevel - 1) * 20 + 15;
    rowDiv.style.paddingLeft = `${paddingLeft}px`;

    // 토글 버튼
    const hasChildren = menu.subMenuList && menu.subMenuList.length > 0;
    const toggleBtn = document.createElement('div');
    toggleBtn.className = `menu-tree-toggle ${hasChildren ? '' : 'no-children'}`;
    toggleBtn.textContent = hasChildren ? (expandedNodes.has(menu.menuId) ? '-' : '+') : '';
    if (hasChildren) {
        toggleBtn.onclick = (e) => {
            e.stopPropagation();
            fn_toggleNode(menu.menuId);
        };
    }
    rowDiv.appendChild(toggleBtn);
    
    // 라벨 (메뉴명 + ID)
    const labelDiv = document.createElement('div');
    labelDiv.className = 'menu-tree-label';
    
    const nameSpan = document.createElement('span');
    nameSpan.className = 'menu-name';
    nameSpan.textContent = menu.menuNm;
    
    const codeSpan = document.createElement('span');
    codeSpan.className = 'menu-code';
    codeSpan.textContent = `(${menu.menuId})`;
    
    labelDiv.appendChild(nameSpan);
    labelDiv.appendChild(codeSpan);
    rowDiv.appendChild(labelDiv);
    
    itemDiv.appendChild(rowDiv);
    
    // 자식 컨테이너
    if (hasChildren) {
        const childrenDiv = document.createElement('div');
        childrenDiv.className = 'menu-tree-children';
        childrenDiv.id = `children-${menu.menuId}`;
        if (expandedNodes.has(menu.menuId)) {
            childrenDiv.classList.add('expanded');
        }
        
        menu.subMenuList.forEach(child => {
            childrenDiv.appendChild(createTreeItem(child));
        });
        itemDiv.appendChild(childrenDiv);
    }
    
    return itemDiv;
}

/**
 * 노드 토글 (확장/축소)
 */
function fn_toggleNode(menuId) {
    if (expandedNodes.has(menuId)) {
        expandedNodes.delete(menuId);
    } else {
        expandedNodes.add(menuId);
    }
    fn_renderTree();
}

/**
 * 메뉴 선택 (상세 조회 및 권한 그룹 조회)
 */
function fn_selectMenu(menuId) {
    selectedMenuId = menuId;
    selectedRoleCd = null; // 권한 그룹 선택 초기화
    
    // 행 하이라이트 처리
    document.querySelectorAll('.menu-tree-row').forEach(r => r.classList.remove('selected'));
    const selectedRow = document.querySelector(`.menu-tree-row[data-id="${menuId}"]`);
    if (selectedRow) selectedRow.classList.add('selected');
    
    // 메뉴 상세 조회
    const data = { menuId: menuId };
    callModule.call(Util.getRequestUrl('/system/menu/getMenuDetail.do'), data, function(result) {
        const menu = result.result;
        fn_renderMenuDetail(menu);
    }, true, 'POST');
    
    // 권한 그룹 목록 조회
    fn_loadRoleListByMenu(menuId);
    
    // 사용자 목록 초기화
    fn_clearUserList();
}

/**
 * 메뉴 상세 정보 렌더링 (조회 모드)
 */
function fn_renderMenuDetail(menu) {
    const detailArea = document.getElementById('menuDetailArea');
    const template = document.getElementById('menuDetailViewTemplate');
    const clone = template.content.cloneNode(true);
    
    // 데이터 바인딩
    clone.querySelector('[data-bind="menuId"]').value = menu.menuId || '';
    clone.querySelector('[data-bind="upperMenuId"]').value = menu.upperMenuId || '';
    clone.querySelector('[data-bind="menuOrder"]').value = menu.menuOrder || '';
    clone.querySelector('[data-bind="menuNm"]').value = menu.menuNm || '';
    clone.querySelector('[data-bind="menuUrl"]').value = menu.menuUrl || '';
    
    // 라디오 버튼 처리
    const useYnRadios = clone.querySelectorAll('input[name="useYn"]');
    useYnRadios.forEach(r => {
        if (r.value === menu.useYn) r.checked = true;
    });
    
    // 이미 템플릿에 버튼이 포함되어 있으므로 별도 추가 로직 제거
    
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

/**
 * 메뉴 상세 정보 수정 모드로 전환
 */
function fn_switchToMenuEditMode() {
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 선택하세요.');
        return;
    }
    
    const data = { menuId: selectedMenuId };
    callModule.call(Util.getRequestUrl('/system/menu/getMenuDetail.do'), data, function(result) {
        const menu = result.result;
        fn_renderMenuEditMode(menu);
    }, true, 'POST');
}

/**
 * 메뉴 상세 정보 렌더링 (수정 모드)
 */
function fn_renderMenuEditMode(menu) {
    const detailArea = document.getElementById('menuDetailArea');
    const template = document.getElementById('menuDetailEditTemplate');
    const clone = template.content.cloneNode(true);
    
    // 데이터 바인딩
    clone.querySelector('#menuId').value = menu.menuId || '';
    clone.querySelector('#upperMenuId').value = menu.upperMenuId || '';
    clone.querySelector('#menuOrder').value = menu.menuOrder || '';
    clone.querySelector('#menuNm').value = menu.menuNm || '';
    clone.querySelector('#menuUrl').value = menu.menuUrl || '';
    
    // 라디오 버튼 처리
    const useYnRadios = clone.querySelectorAll('input[name="useYn"]');
    useYnRadios.forEach(r => {
        if (r.value === menu.useYn) r.checked = true;
    });
    
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

/**
 * 권한 그룹 목록 조회
 */
function fn_loadRoleListByMenu(menuId) {
    const data = { menuId: menuId };
    
    callModule.call(Util.getRequestUrl('/system/menu/getRoleListByMenu.do'), data, function(result) {
        roleList = result.result || [];
        fn_renderRoleList();
    }, true, 'POST');
}

/**
 * 권한 그룹 목록 렌더링
 */
function fn_renderRoleList() {
    const tbody = document.getElementById('roleGroupTbody');
    tbody.innerHTML = '';
    
    if (!roleList || roleList.length === 0) {
        tbody.innerHTML = '<tr><td class="tc" colspan="5" style="height:350px;">권한 그룹이 없습니다.</td></tr>';
        return;
    }
    
    roleList.forEach((role, index) => {
        const tr = document.createElement('tr');
        tr.dataset.roleCd = role.roleCd;
        if (role.roleCd === selectedRoleCd) {
            tr.classList.add('selected');
        }
        tr.onclick = () => fn_selectRole(role.roleCd);
        
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td class="text-left">${role.roleNm}</td>
            <td>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="read" 
                       ${role.readAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'read', this.checked)">
            </td>
            <td>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="creat" 
                       ${role.creatAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'creat', this.checked)">
            </td>
            <td>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="updt" 
                       ${role.updtAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'updt', this.checked)">
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

/**
 * 권한 그룹 선택 (사용자 목록 조회)
 */
function fn_selectRole(roleCd) {
    selectedRoleCd = roleCd;
    
    // 행 하이라이트 처리
    document.querySelectorAll('#roleGroupTable tbody tr').forEach(r => r.classList.remove('selected'));
    const selectedRow = document.querySelector(`#roleGroupTable tbody tr[data-role-cd="${roleCd}"]`);
    if (selectedRow) selectedRow.classList.add('selected');
    
    // 사용자 목록 조회
    fn_loadUserListByRole(roleCd);
}

/**
 * 사용자 목록 조회
 */
function fn_loadUserListByRole(roleCd) {
    const data = { roleCd: roleCd };
    
    callModule.call(Util.getRequestUrl('/system/menu/getUserListByRole.do'), data, function(result) {
        userList = result.result || [];
        fn_renderUserList();
    }, true, 'POST');
}

/**
 * 사용자 목록 렌더링
 */
function fn_renderUserList() {
    const tbody = document.getElementById('userListTbody');
    tbody.innerHTML = '';
    
    if (!userList || userList.length === 0) {
        tbody.innerHTML = '<tr><td class="tc" colspan="4" style="height:350px;">사용자가 없습니다.</td></tr>';
        return;
    }
    
    // 권한 그룹의 메뉴 권한 정보를 사용자에게도 반영
    const roleAuth = roleList.find(r => r.roleCd === selectedRoleCd);
    
    userList.forEach(user => {
        const tr = document.createElement('tr');
        tr.dataset.userId = user.userId;
        
        tr.innerHTML = `
            <td class="text-left">${user.userNm}</td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="read" 
                       ${roleAuth && roleAuth.readAuthorYn === 'Y' ? 'checked' : ''}
                       disabled>
            </td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="creat" 
                       ${roleAuth && roleAuth.creatAuthorYn === 'Y' ? 'checked' : ''}
                       disabled>
            </td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="updt" 
                       ${roleAuth && roleAuth.updtAuthorYn === 'Y' ? 'checked' : ''}
                       disabled>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

/**
 * 사용자 목록 초기화
 */
function fn_clearUserList() {
    const tbody = document.getElementById('userListTbody');
    tbody.innerHTML = '<tr><td class="tc" colspan="4" style="height:350px;">권한 그룹을 선택하세요</td></tr>';
    userList = [];
}

/**
 * 권한 변경 (체크박스 변경 시)
 */
function fn_changeAuth(roleCd, authType, checked) {
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    // 변경 사항 추적
    const existingIndex = changedAuthList.findIndex(a => 
        a.roleCd === roleCd && a.menuId === selectedMenuId
    );
    
    const authValue = checked ? 'Y' : 'N';
    
    if (existingIndex >= 0) {
        // 기존 변경 사항 업데이트
        changedAuthList[existingIndex][authType === 'read' ? 'readAuthorYn' : 
                                       authType === 'creat' ? 'creatAuthorYn' : 'updtAuthorYn'] = authValue;
    } else {
        // 새 변경 사항 추가
        const role = roleList.find(r => r.roleCd === roleCd);
        changedAuthList.push({
            roleCd: roleCd,
            menuId: selectedMenuId,
            readAuthorYn: authType === 'read' ? authValue : (role.readAuthorYn || 'N'),
            creatAuthorYn: authType === 'creat' ? authValue : (role.creatAuthorYn || 'N'),
            updtAuthorYn: authType === 'updt' ? authValue : (role.updtAuthorYn || 'N'),
            deleteAuthorYn: role.deleteAuthorYn || 'N'
        });
    }
    
    // 권한 그룹 목록의 해당 항목도 업데이트
    const role = roleList.find(r => r.roleCd === roleCd);
    if (role) {
        if (authType === 'read') role.readAuthorYn = authValue;
        else if (authType === 'creat') role.creatAuthorYn = authValue;
        else if (authType === 'updt') role.updtAuthorYn = authValue;
    }
}

/**
 * 권한 저장 (일괄 저장)
 */
function fn_saveAllAuth() {
    if (changedAuthList.length === 0) {
        MessageUtil.alert('변경된 권한이 없습니다.');
        return;
    }
    
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    // 변경된 권한을 하나씩 저장
    let successCount = 0;
    let failCount = 0;
    
    changedAuthList.forEach((auth, index) => {
        callModule.call(Util.getRequestUrl('/system/menu/saveMenuAuth.do'), auth, function(result) {
            if (result && result.result && result.result.resultValue) {
                successCount++;
            } else {
                failCount++;
            }
            
            // 모든 저장이 완료되면
            if (index === changedAuthList.length - 1) {
                if (failCount === 0) {
                    MessageUtil.alert('모든 권한이 저장되었습니다.', function() {
                        changedAuthList = [];
                        fn_loadRoleListByMenu(selectedMenuId);
                    });
                } else {
                    MessageUtil.alert(`${successCount}개 저장 성공, ${failCount}개 저장 실패`);
                }
            }
        }, true, 'POST');
    });
}

/**
 * 메뉴 상세 정보 접기/펼치기 (제거됨)
 */
// function fn_toggleMenuDetail() {
//    const wrapper = document.querySelector('.menu-detail-wrapper');
//    wrapper.classList.toggle('collapsed');
// }

/**
 * 메뉴 검색 모달 (추후 구현)
 */
function fn_openMenuSearchModal() {
    MessageUtil.alert('메뉴 검색 모달은 추후 구현 예정입니다.');
}

/**
 * 상위 메뉴 초기화
 */
function fn_clearUpperMenu() {
    const inputs = ['upperMenuId', 'upperMenuIdHidden', 'upperMenuNm'];
    inputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

/**
 * 메뉴 저장 (등록/수정) - 기존 기능 유지
 */
function fn_saveMenu() {
    const form = document.getElementById('menuForm');
    if (!form) return;
    
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    
    // 유효성 검사
    if (!data.menuId) {
        MessageUtil.alert('메뉴ID를 입력하세요.');
        return;
    }
    
    // 메뉴ID에 'M' 접두사 자동 추가 (등록 모드일 때만)
    if (!data.menuId.startsWith('M')) {
        data.menuId = 'M' + data.menuId;
    }

    if (!data.menuNm) {
        MessageUtil.alert('메뉴명을 입력하세요.');
        return;
    }
    
    // 모드 설정
    data.mode = 'update'; // 상세 정보에서 수정하는 경우
    
    callModule.call(Util.getRequestUrl('/system/menu/saveMenu.do'), data, function(result) {
        MessageUtil.alert(result.message, function() {
            fn_searchMenuList();
            fn_selectMenu(data.menuId);
        });
    }, true, 'POST');
}

/**
 * 메뉴 수정 취소
 */
function fn_cancelMenuEdit() {
    if (selectedMenuId) {
        const data = { menuId: selectedMenuId };
        callModule.call(Util.getRequestUrl('/system/menu/getMenuDetail.do'), data, function(result) {
            const menu = result.result;
            fn_renderMenuDetail(menu);
        }, true, 'POST');
    }
}

/**
 * 사용자 추가 (권한 그룹에 사용자 추가)
 */
function fn_addUserToRole() {
    if (!selectedRoleCd) {
        MessageUtil.alert('권한 그룹을 먼저 선택하세요.');
        return;
    }
    MessageUtil.alert('사용자 추가 기능은 추후 구현 예정입니다.');
}

/**
 * 사용자 삭제 (권한 그룹에서 사용자 제거)
 */
function fn_deleteUserFromRole() {
    if (!selectedRoleCd) {
        MessageUtil.alert('권한 그룹을 먼저 선택하세요.');
        return;
    }
    
    const selectedRows = document.querySelectorAll('#userListTable tbody tr.selected');
    if (selectedRows.length === 0) {
        MessageUtil.alert('삭제할 사용자를 선택하세요.');
        return;
    }
    
    MessageUtil.confirm('선택한 사용자를 권한 그룹에서 제거하시겠습니까?', function() {
        MessageUtil.alert('사용자 삭제 기능은 추후 구현 예정입니다.');
    });
}

/**
 * 메뉴 삭제
 */
function fn_deleteMenu(menuId) {
    // 인자가 없으면 전역 변수 사용
    if (!menuId) menuId = selectedMenuId;
    
    if (!menuId) {
        MessageUtil.alert('삭제할 메뉴를 선택하세요.');
        return;
    }
    
    MessageUtil.confirm('메뉴를 삭제하시겠습니까?', function() {
        const data = { menuId: menuId };
        callModule.call(Util.getRequestUrl('/system/menu/deleteMenu.do'), data, function(result) {
            MessageUtil.alert(result.message, function() {
                fn_searchMenuList();
                // 상세 정보 초기화
                document.getElementById('menuDetailArea').innerHTML = '<div class="menu-detail-empty"><p class="txt-body">좌측 메뉴 목록에서 메뉴를 선택하세요</p></div>';
                selectedMenuId = null;
            });
        }, true, 'POST');
    });
}

/**
 * 신규 등록 모드로 전환
 */
function fn_switchToRegisterMode() {
    selectedMenuId = null;
    // 행 선택 해제
    document.querySelectorAll('.menu-tree-row').forEach(r => r.classList.remove('selected'));
    
    const detailArea = document.getElementById('menuDetailArea');
    const template = document.getElementById('menuDetailEditTemplate');
    const clone = template.content.cloneNode(true);
    
    // 기본값 설정
    clone.querySelector('#menuId').value = '';
    clone.querySelector('#menuNm').value = '';
    clone.querySelector('#menuOrder').value = '1';
    clone.querySelector('input[name="useYn"][value="Y"]').checked = true;
    
    // prefix 표시
    clone.querySelector('#menuIdPrefix').style.display = 'inline';
    
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
}

// 전역 함수로 노출 (HTML에서 호출 가능하도록)
window.fn_changeAuth = fn_changeAuth;
window.fn_saveAllAuth = fn_saveAllAuth;
window.fn_openMenuSearchModal = fn_openMenuSearchModal;
window.fn_clearUpperMenu = fn_clearUpperMenu;
window.fn_saveMenu = fn_saveMenu;
window.fn_cancelMenuEdit = fn_cancelMenuEdit;
window.fn_switchToMenuEditMode = fn_switchToMenuEditMode;
window.fn_deleteMenu = fn_deleteMenu;
window.fn_switchToRegisterMode = fn_switchToRegisterMode;
