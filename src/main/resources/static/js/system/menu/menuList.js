/**
 * 메뉴 및 권한 관리 화면 JavaScript
 */

// 전역 변수
let menuData = [];           // 전체 메뉴 데이터 (계층 구조)
let expandedNodes = new Set(); // 확장된 노드 ID 목록
let selectedMenuId = null;   // 선택된 메뉴ID
let roleList = [];           // 현재 메뉴의 권한 그룹 목록
let userMenuAuthList = [];   // 현재 메뉴에 개별 권한이 부여된 사용자 목록
let changedAuthList = [];    // 변경된 권한 매핑 정보 배열
let changedUserAuthList = []; // 변경된 사용자 권한 매핑 정보 배열

document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    
    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => fn_searchMenuList());
    
    // 검색어 입력 후 Enter 키 이벤트
    document.getElementById('searchKeyword').addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            fn_searchMenuList();
        }
    });
    
    // 신규 등록 버튼 이벤트
    document.getElementById('registerBtn').addEventListener('click', fn_switchToRegisterMode);
    
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
    const searchKeyword = document.getElementById('searchKeyword').value;
    
    const data = {
        searchKeyword: Util.isEmpty(searchKeyword) ? null : searchKeyword
    };

    const callback = function(result) {
        menuData = result.result || [];
        
        // 검색어가 있으면 모든 노드 펼침
        if (!Util.isEmpty(searchKeyword)) {
            expandedNodes.clear();
            fn_expandAllNodes(menuData);
        }
        
        fn_renderTree();
        
        // 전체 메뉴 개수 업데이트
        const totalCount = fn_countTotalMenus(menuData);
        document.getElementById('treeTotalCount').textContent = `(${totalCount})`;
    };
    
    callModule.call(Util.getRequestUrl('/system/menu/getMenuList.do'), data, callback, true, 'POST');
}

/**
 * 모든 노드 펼침 (재귀)
 */
function fn_expandAllNodes(menus) {
    if (!menus) return;
    menus.forEach(menu => {
        if (menu.subMenuList && menu.subMenuList.length > 0) {
            expandedNodes.add(menu.menuId);
            fn_expandAllNodes(menu.subMenuList);
        }
    });
}

/**
 * 전체 메뉴 개수 계산 (재귀)
 */
function fn_countTotalMenus(menus) {
    let count = 0;
    if (!menus) return count;
    
    menus.forEach(menu => {
        count++;
        if (menu.subMenuList && menu.subMenuList.length > 0) {
            count += fn_countTotalMenus(menu.subMenuList);
        }
    });
    return count;
}

/**
 * 트리 렌더링
 */
function fn_renderTree() {
    const treeContainer = document.getElementById('menuTreeList');
    treeContainer.innerHTML = '';
    
    if (!menuData || menuData.length === 0) {
        treeContainer.innerHTML = '<div class="spt-tree-empty">조회 결과가 없습니다.</div>';
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
    itemDiv.className = 'spt-tree-item';
    
    const rowDiv = document.createElement('div');
    rowDiv.className = 'spt-tree-row';
    if (menu.menuId === selectedMenuId) {
        rowDiv.classList.add('selected');
    }
    rowDiv.dataset.id = menu.menuId;
    rowDiv.onclick = (e) => {
        if (e.target.classList.contains('spt-tree-toggle')) return;
        fn_selectMenu(menu.menuId);
    };
    
    // 들여쓰기 (레벨에 따른 패딩)
    const paddingLeft = (menu.menuLevel - 1) * 20 + 15;
    rowDiv.style.paddingLeft = `${paddingLeft}px`;

    // 토글 버튼
    const hasChildren = menu.subMenuList && menu.subMenuList.length > 0;
    const toggleBtn = document.createElement('button');
    toggleBtn.className = `spt-tree-toggle ${hasChildren ? '' : 'no-children'}`;
    if (hasChildren) {
        toggleBtn.onclick = (e) => {
            e.stopPropagation();
            fn_toggleNode(menu.menuId);
        };

      const icon = document.createElement('i');
      toggleBtn.appendChild(icon);
      icon.className = hasChildren ? (expandedNodes.has(menu.menuId) ? 'icon-minus' : 'icon-plus') : '';
    }
    rowDiv.appendChild(toggleBtn);
    
    // 라벨 (메뉴명 + ID)
    const labelDiv = document.createElement('div');
    labelDiv.className = 'spt-tree-label';
    
    const nameSpan = document.createElement('span');
    nameSpan.className = 'dep-name';
    nameSpan.textContent = menu.menuNm;
    
    // 미사용 메뉴 표시
    if (menu.useYn === 'N') {
        nameSpan.classList.add('dep-disabled');
        
        const disabledBadge = document.createElement('span');
        disabledBadge.className = 'dep-disabled-badge';
        disabledBadge.textContent = '미사용';
        labelDiv.appendChild(nameSpan);
        labelDiv.appendChild(disabledBadge);
    } else {
        labelDiv.appendChild(nameSpan);
    }
    
    const codeSpan = document.createElement('span');
    codeSpan.className = 'dep-code';
    codeSpan.textContent = `(${menu.menuId})`;
    
    labelDiv.appendChild(codeSpan);
    rowDiv.appendChild(labelDiv);
    
    itemDiv.appendChild(rowDiv);
    
    // 자식 컨테이너
    if (hasChildren) {
        const childrenDiv = document.createElement('div');
        childrenDiv.className = 'spt-tree-children';
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
    
    // 행 하이라이트 처리
    document.querySelectorAll('.spt-tree-row').forEach(r => r.classList.remove('selected'));
    const selectedRow = document.querySelector(`.spt-tree-row[data-id="${menuId}"]`);
    if (selectedRow) selectedRow.classList.add('selected');
    
    // 메뉴 상세 조회
    const data = { menuId: menuId };
    callModule.call(Util.getRequestUrl('/system/menu/getMenuDetail.do'), data, function(result) {
        const menu = result.result;
        fn_renderMenuDetail(menu);
    }, true, 'POST');
    
    // 권한 그룹 목록 조회
    fn_loadRoleListByMenu(menuId);
    
    // 개별 사용자 권한 목록 조회
    fn_loadUserListByMenu(menuId);
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
        tbody.innerHTML = '<tr><td class="no-data-table" colspan="6">권한 그룹이 없습니다.</td></tr>';
        return;
    }
    
    roleList.forEach((role, index) => {
        const tr = document.createElement('tr');
        tr.dataset.roleCd = role.roleCd;
        
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td class="text-left">${role.roleNm}</td>
            <td>
                <label for="" class="sr-only"></label>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="read" 
                       ${role.readAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'read', this.checked)"/>
            </td>
            <td>
                <label for="" class="sr-only"></label>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="creat" 
                       ${role.creatAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'creat', this.checked)"/>
            </td>
            <td>
                <label for="" class="sr-only"></label>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="updt" 
                       ${role.updtAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'updt', this.checked)"/>
            </td>
            <td>
                <label for="" class="sr-only"></label>
                <input type="checkbox" 
                       data-role-cd="${role.roleCd}" 
                       data-auth-type="delete" 
                       ${role.deleteAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeAuth('${role.roleCd}', 'delete', this.checked)"/>
            </td>
        `;
        
        tbody.appendChild(tr);
    });
}

/**
 * 메뉴에 개별 권한이 부여된 사용자 목록 조회
 */
function fn_loadUserListByMenu(menuId) {
    if (!menuId) {
        fn_clearUserList();
        return;
    }
    
    const data = { menuId: menuId };
    
    callModule.call(Util.getRequestUrl('/system/menu/getUserListByMenu.do'), data, function(result) {
        userMenuAuthList = result.result || [];
        fn_renderUserList();
    }, true, 'POST');
}

/**
 * 사용자 목록 렌더링 (개별 권한 기반)
 */
function fn_renderUserList() {
    const tbody = document.getElementById('userListTbody');
    tbody.innerHTML = '';
    
    if (!userMenuAuthList || userMenuAuthList.length === 0) {
        tbody.innerHTML = '<tr><td class="no-data-table" colspan="5">접근 가능한 사용자가 없습니다.</td></tr>';
        return;
    }
    
    userMenuAuthList.forEach(user => {
        const tr = document.createElement('tr');
        tr.dataset.userId = user.userId;
        tr.onclick = () => {
            // 행 선택 토글
            tr.classList.toggle('selected');
        };
        
        tr.innerHTML = `
            <td class="text-left">${user.userNm}</td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="read" 
                       ${user.readAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeUserAuth('${user.userId}', 'read', this.checked)"/>
            </td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="creat" 
                       ${user.creatAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeUserAuth('${user.userId}', 'creat', this.checked)"/>
            </td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="updt" 
                       ${user.updtAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeUserAuth('${user.userId}', 'updt', this.checked)"/>
            </td>
            <td>
                <input type="checkbox" 
                       data-user-id="${user.userId}" 
                       data-auth-type="delete" 
                       ${user.deleteAuthorYn === 'Y' ? 'checked' : ''}
                       onclick="event.stopPropagation(); fn_changeUserAuth('${user.userId}', 'delete', this.checked)"/>
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
    tbody.innerHTML = '<tr><td class="no-data-table" colspan="5">메뉴를 선택하세요</td></tr>';
    userMenuAuthList = [];
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
    
    // 권한 타입에 따른 필드명 매핑
    const authFieldMap = {
        'read': 'readAuthorYn',
        'creat': 'creatAuthorYn',
        'updt': 'updtAuthorYn',
        'delete': 'deleteAuthorYn'
    };
    
    if (existingIndex >= 0) {
        // 기존 변경 사항 업데이트
        changedAuthList[existingIndex][authFieldMap[authType]] = authValue;
    } else {
        // 새 변경 사항 추가
        const role = roleList.find(r => r.roleCd === roleCd);
        changedAuthList.push({
            roleCd: roleCd,
            menuId: selectedMenuId,
            readAuthorYn: authType === 'read' ? authValue : (role.readAuthorYn || 'N'),
            creatAuthorYn: authType === 'creat' ? authValue : (role.creatAuthorYn || 'N'),
            updtAuthorYn: authType === 'updt' ? authValue : (role.updtAuthorYn || 'N'),
            deleteAuthorYn: authType === 'delete' ? authValue : (role.deleteAuthorYn || 'N')
        });
    }
    
    // 권한 그룹 목록의 해당 항목도 업데이트
    const role = roleList.find(r => r.roleCd === roleCd);
    if (role) {
        if (authType === 'read') role.readAuthorYn = authValue;
        else if (authType === 'creat') role.creatAuthorYn = authValue;
        else if (authType === 'updt') role.updtAuthorYn = authValue;
        else if (authType === 'delete') role.deleteAuthorYn = authValue;
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
                    changedAuthList = [];
                    fn_loadRoleListByMenu(selectedMenuId);
                    MessageUtil.success('모든 권한이 저장되었습니다.');
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
 * 상위 메뉴 검색 모달 열기
 */
function fn_openMenuSearchModal() {
    MenuSearchModal.open(function(menuId, menuNm) {
        // 선택된 메뉴 ID를 상위 메뉴 입력 필드에 설정
        document.getElementById('upperMenuId').value = menuId;
        
        // 선택된 상위 메뉴의 하위 메뉴 개수 + 1로 정렬순서 자동 설정
        const subMenuCount = fn_countSubMenus(menuId);
        const menuOrderEl = document.getElementById('menuOrder');
        if (menuOrderEl) {
            menuOrderEl.value = subMenuCount + 1;
        }
    });
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
    
    // 정렬순서도 1로 초기화 (최상위 메뉴가 되므로)
    const menuOrderEl = document.getElementById('menuOrder');
    if (menuOrderEl) {
        menuOrderEl.value = '1';
    }
}

/**
 * 특정 메뉴의 하위 메뉴 개수 계산
 * @param upperMenuId 상위 메뉴 ID
 * @return 하위 메뉴 개수
 */
function fn_countSubMenus(upperMenuId) {
    const menu = fn_findMenuById(menuData, upperMenuId);
    if (menu && menu.subMenuList) {
        return menu.subMenuList.length;
    }
    return 0;
}

/**
 * 메뉴 ID로 메뉴 찾기 (재귀)
 * @param menus 메뉴 목록
 * @param menuId 찾을 메뉴 ID
 * @return 찾은 메뉴 또는 null
 */
function fn_findMenuById(menus, menuId) {
    if (!menus) return null;
    for (const menu of menus) {
        if (menu.menuId === menuId) {
            return menu;
        }
        if (menu.subMenuList && menu.subMenuList.length > 0) {
            const found = fn_findMenuById(menu.subMenuList, menuId);
            if (found) return found;
        }
    }
    return null;
}

/**
 * 메뉴 저장 (등록/수정)
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
    
    // 모드 설정: selectedMenuId가 없으면 신규 등록, 있으면 수정
    data.mode = selectedMenuId ? 'update' : 'insert';
    
    // 권한 데이터 수집
    data.authList = fn_collectAuthData();
    
    callModule.call(Util.getRequestUrl('/system/menu/saveMenu.do'), data, function(result) {
        if (result && result.result && result.result.resultValue) {
            MessageUtil.success(result.result.message);
            fn_searchMenuList();
            fn_selectMenu(data.menuId);
        } else {
            MessageUtil.error(result.result.message);
        }
    }, true, 'POST');
}

/**
 * 권한 그룹 테이블에서 현재 권한 데이터 수집
 * @return 권한 데이터 배열
 */
function fn_collectAuthData() {
    const authList = [];
    
    if (!roleList || roleList.length === 0) {
        return authList;
    }
    
    roleList.forEach(role => {
        authList.push({
            roleCd: role.roleCd,
            readAuthorYn: role.readAuthorYn || 'N',
            creatAuthorYn: role.creatAuthorYn || 'N',
            updtAuthorYn: role.updtAuthorYn || 'N',
            deleteAuthorYn: role.deleteAuthorYn || 'N'
        });
    });
    
    return authList;
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
 * 사용자 권한 변경 (체크박스 변경 시)
 */
function fn_changeUserAuth(userId, authType, checked) {
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    // 변경 사항 추적
    const existingIndex = changedUserAuthList.findIndex(a => 
        a.userId === userId && a.menuId === selectedMenuId
    );
    
    const authValue = checked ? 'Y' : 'N';
    
    // 권한 타입에 따른 필드명 매핑
    const authFieldMap = {
        'read': 'readAuthorYn',
        'creat': 'creatAuthorYn',
        'updt': 'updtAuthorYn',
        'delete': 'deleteAuthorYn'
    };
    
    if (existingIndex >= 0) {
        // 기존 변경 사항 업데이트
        changedUserAuthList[existingIndex][authFieldMap[authType]] = authValue;
    } else {
        // 새 변경 사항 추가
        const user = userMenuAuthList.find(u => u.userId === userId);
        changedUserAuthList.push({
            userId: userId,
            menuId: selectedMenuId,
            readAuthorYn: authType === 'read' ? authValue : (user ? user.readAuthorYn || 'N' : 'N'),
            creatAuthorYn: authType === 'creat' ? authValue : (user ? user.creatAuthorYn || 'N' : 'N'),
            updtAuthorYn: authType === 'updt' ? authValue : (user ? user.updtAuthorYn || 'N' : 'N'),
            deleteAuthorYn: authType === 'delete' ? authValue : (user ? user.deleteAuthorYn || 'N' : 'N')
        });
    }
    
    // 사용자 목록의 해당 항목도 업데이트
    const user = userMenuAuthList.find(u => u.userId === userId);
    if (user) {
        if (authType === 'read') user.readAuthorYn = authValue;
        else if (authType === 'creat') user.creatAuthorYn = authValue;
        else if (authType === 'updt') user.updtAuthorYn = authValue;
        else if (authType === 'delete') user.deleteAuthorYn = authValue;
    }
}

/**
 * 사용자 권한 저장 (일괄 저장)
 */
function fn_saveAllUserAuth() {
    if (changedUserAuthList.length === 0) {
        MessageUtil.alert('변경된 사용자 권한이 없습니다.');
        return;
    }
    
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    // 변경된 권한을 하나씩 저장
    let successCount = 0;
    let failCount = 0;
    
    changedUserAuthList.forEach((auth, index) => {
        callModule.call(Util.getRequestUrl('/system/menu/saveUserMenuAuth.do'), auth, function(result) {
            if (result && result.result && result.result.resultValue) {
                successCount++;
            } else {
                failCount++;
            }
            
            // 모든 저장이 완료되면
            if (index === changedUserAuthList.length - 1) {
                if (failCount === 0) {
                    MessageUtil.alert('모든 사용자 권한이 저장되었습니다.', function() {
                        changedUserAuthList = [];
                        fn_loadUserListByMenu(selectedMenuId);
                    });
                } else {
                    MessageUtil.alert(`${successCount}개 저장 성공, ${failCount}개 저장 실패`);
                }
            }
        }, true, 'POST');
    });
}

/**
 * 사용자 추가 (메뉴에 개별 권한 부여)
 */
function fn_addUserToRole() {
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    // 이미 권한이 있는 사용자 목록 조회 (필터링용)
    const data = { menuId: selectedMenuId };
    callModule.call(Util.getRequestUrl('/system/menu/getAllUsersForMenu.do'), data, function(result) {
        const allUsers = result.result || [];
        
        // 이미 권한이 부여된 사용자 ID 목록 추출
        const excludedUserIds = allUsers
            .filter(u => u.userMenuAuthorSn)
            .map(u => u.userId);
        
        // 모달 열기
        UserSearchModal.open(function(selectedUsers) {
            if (!selectedUsers || selectedUsers.length === 0) {
                return;
            }
            
            // 선택된 사용자들을 순회하며 권한 부여
            let successCount = 0;
            let failCount = 0;
            let completedCount = 0;
            
            selectedUsers.forEach((user, index) => {
                const userAuth = {
                    userId: user.userId,
                    menuId: selectedMenuId,
                    readAuthorYn: 'Y',
                    creatAuthorYn: 'N',
                    updtAuthorYn: 'N',
                    deleteAuthorYn: 'N'
                };
                
                callModule.call(Util.getRequestUrl('/system/menu/saveUserMenuAuth.do'), userAuth, function(result) {
                    completedCount++;
                    
                    if (result && result.result && result.result.resultValue) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                    
                    // 모든 요청 완료 후 처리
                    if (completedCount === selectedUsers.length) {
                        if (successCount > 0) {
                            MessageUtil.success(`${successCount}명의 사용자 권한이 부여되었습니다.`, function() {
                                fn_loadUserListByMenu(selectedMenuId);
                            });
                        }
                        if (failCount > 0) {
                            MessageUtil.error(`${failCount}명의 사용자 권한 부여에 실패했습니다.`);
                        }
                    }
                }, true, 'POST');
            });
        }, excludedUserIds);
    }, true, 'POST');
}

/**
 * 사용자 삭제 (메뉴에서 개별 권한 제거)
 */
function fn_deleteUserFromRole() {
    if (!selectedMenuId) {
        MessageUtil.alert('메뉴를 먼저 선택하세요.');
        return;
    }
    
    const selectedRows = document.querySelectorAll('#userListTable tbody tr.selected');
    if (selectedRows.length === 0) {
        MessageUtil.alert('삭제할 사용자를 선택하세요.');
        return;
    }
    
    const userIds = Array.from(selectedRows).map(row => row.dataset.userId);
    
    MessageUtil.confirm('선택한 사용자의 메뉴 접근 권한을 제거하시겠습니까?', function() {
        let successCount = 0;
        let failCount = 0;
        
        userIds.forEach((userId, index) => {
            const data = {
                userId: userId,
                menuId: selectedMenuId
            };
            
            callModule.call(Util.getRequestUrl('/system/menu/deleteUserMenuAuth.do'), data, function(result) {
                if (result && result.result && result.result.resultValue) {
                    successCount++;
                } else {
                    failCount++;
                }
                
                if (index === userIds.length - 1) {
                    if (failCount === 0) {
                        MessageUtil.alert('사용자 권한이 제거되었습니다.', function() {
                            fn_loadUserListByMenu(selectedMenuId);
                        });
                    } else {
                        MessageUtil.alert(`${successCount}개 제거 성공, ${failCount}개 제거 실패`);
                    }
                }
            }, true, 'POST');
        });
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
            if (result && result.result && result.result.resultValue) {
                fn_searchMenuList();
                // 상세 정보 초기화
                document.getElementById('menuDetailArea').innerHTML = '<div class="menu-detail-empty"><p class="txt-body">좌측 메뉴 목록에서 메뉴를 선택하세요</p></div>';
                selectedMenuId = null;

                MessageUtil.success(result.result.message);
            } else {
                MessageUtil.error(result.result.message);
            }
        }, true, 'POST');
    });
}

/**
 * 신규 등록 모드로 전환
 */
function fn_switchToRegisterMode() {
    selectedMenuId = null;
    changedAuthList = [];
    
    // 행 선택 해제
    document.querySelectorAll('.spt-tree-row').forEach(r => r.classList.remove('selected'));
    
    const detailArea = document.getElementById('menuDetailArea');
    const template = document.getElementById('menuDetailEditTemplate');
    const clone = template.content.cloneNode(true);
    
    // 기본값 설정
    clone.querySelector('#menuId').value = '';
    clone.querySelector('#menuNm').value = '';
    clone.querySelector('#upperMenuId').value = '';
    clone.querySelector('#menuUrl').value = '';
    clone.querySelector('#menuOrder').value = '1';
    clone.querySelector('input[name="useYn"][value="Y"]').checked = true;
    
    // prefix 표시
    clone.querySelector('#menuIdPrefix').style.display = 'inline';
    
    detailArea.innerHTML = '';
    detailArea.appendChild(clone);
    
    // 전체 권한 그룹 목록 조회 (모든 권한 'N'으로 초기화)
    fn_loadAllRoleListForNew();
}

/**
 * 신규 등록용 전체 권한 그룹 목록 조회
 */
function fn_loadAllRoleListForNew() {
    const data = { menuId: '' };  // 빈 문자열로 전달하면 모든 권한이 'N'으로 조회됨
    
    callModule.call(Util.getRequestUrl('/system/menu/getRoleListByMenu.do'), data, function(result) {
        roleList = result.result || [];
        fn_renderRoleList();
        fn_clearUserList();
    }, true, 'POST');
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
window.fn_changeUserAuth = fn_changeUserAuth;
window.fn_saveAllUserAuth = fn_saveAllUserAuth;
window.fn_addUserToRole = fn_addUserToRole;
window.fn_deleteUserFromRole = fn_deleteUserFromRole;
