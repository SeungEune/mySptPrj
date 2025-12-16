/**
 * 메뉴 검색 모달 JavaScript (트리 구조)
 */

// 메뉴 검색 모달 전역 객체
const MenuSearchModal = {
    callback: null,      // 메뉴 선택 시 콜백 함수
    menuData: [],        // 전체 메뉴 데이터
    expandedNodes: [],   // 확장된 노드 목록
    
    /**
     * 모달 열기
     * @param callback 메뉴 선택 시 호출될 콜백 함수 (menuId, menuNm 파라미터 전달)
     */
    open: function(callback) {
        this.callback = callback;
        document.getElementById('menuSearchModal').classList.add('show');
        document.getElementById('modalMenuNm').value = '';
        this.searchMenuList();  // 모달 열 때 자동 조회
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('menuSearchModal').classList.remove('show');
        const modalTreeList = document.getElementById('modalMenuTreeList');
        if (modalTreeList) {
            modalTreeList.innerHTML = '<div class="menu-tree-loading">메뉴 정보를 불러오는 중...</div>';
        }
        this.callback = null;
        this.menuData = [];
        this.expandedNodes = [];
    },
    
    /**
     * 메뉴 목록 조회
     */
    searchMenuList: function() {
        const menuNm = document.getElementById('modalMenuNm').value.trim();
        
        const data = {
            searchKeyword: Util.isEmpty(menuNm) ? null : menuNm
        };
        
        const url = Util.getRequestUrl('/system/menu/getMenuList.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result && result.result.length > 0) {
                this.menuData = result.result;
                
                // 검색 키워드가 있을 때 모든 노드 자동 확장
                if (!Util.isEmpty(menuNm)) {
                    this.expandedNodes = [];
                    this.collectAllMenuIds(this.menuData);
                } else {
                    // 검색 키워드가 없으면 확장 상태 초기화
                    this.expandedNodes = [];
                }
                
                this.renderTree(this.menuData);
            } else {
                const modalTreeList = document.getElementById('modalMenuTreeList');
                const modalTotalCount = document.getElementById('modalMenuTotalCount');
                if (modalTreeList) {
                    modalTreeList.innerHTML = '<div class="menu-tree-empty">조회 결과가 없습니다.</div>';
                }
                if (modalTotalCount) {
                    modalTotalCount.textContent = '전체 0개';
                }
            }
        }, true, 'POST');
    },
    
    /**
     * 모든 메뉴 ID 수집 (확장용)
     * @param menuList 메뉴 목록
     */
    collectAllMenuIds: function(menuList) {
        if (!menuList) return;
        menuList.forEach(menu => {
            if (menu.subMenuList && menu.subMenuList.length > 0) {
                this.expandedNodes.push(menu.menuId);
                this.collectAllMenuIds(menu.subMenuList);
            }
        });
    },
    
    /**
     * 전체 메뉴 개수 계산 (재귀)
     * @param menuList 메뉴 목록
     * @return 전체 개수
     */
    countTotalMenus: function(menuList) {
        let count = 0;
        if (!menuList) return count;
        
        menuList.forEach(menu => {
            count++;
            if (menu.subMenuList && menu.subMenuList.length > 0) {
                count += this.countTotalMenus(menu.subMenuList);
            }
        });
        return count;
    },
    
    /**
     * 트리 구조로 메뉴 목록 렌더링
     * @param menuList 메뉴 목록
     */
    renderTree: function(menuList) {
        const treeContainer = document.getElementById('modalMenuTreeList');
        if (!treeContainer) {
            return;
        }
        
        treeContainer.innerHTML = '';
        
        if (!menuList || menuList.length === 0) {
            treeContainer.innerHTML = '<div class="menu-tree-empty">조회 결과가 없습니다.</div>';
            const modalTotalCount = document.getElementById('modalMenuTotalCount');
            if (modalTotalCount) {
                modalTotalCount.textContent = '전체 0개';
            }
            return;
        }
        
        // 1차 메뉴부터 렌더링
        menuList.forEach(menu => {
            const itemElement = this.createTreeItem(menu);
            treeContainer.appendChild(itemElement);
        });
        
        // 전체 개수 표시
        const modalTotalCount = document.getElementById('modalMenuTotalCount');
        if (modalTotalCount) {
            const totalCount = this.countTotalMenus(menuList);
            modalTotalCount.textContent = `전체 ${totalCount}개`;
        }
    },
    
    /**
     * 트리 아이템 생성 (재귀)
     * @param menu 메뉴 정보
     * @return 트리 아이템 DOM 요소
     */
    createTreeItem: function(menu) {
        const item = document.createElement('div');
        item.className = 'menu-modal-tree-item';
        item.dataset.menuId = menu.menuId;
        
        const indentWidth = ((menu.menuLevel || 1) - 1) * 20;
        const hasChildren = menu.subMenuList && menu.subMenuList.length > 0;
        const isExpanded = this.expandedNodes.includes(menu.menuId);
        
        // 트리 행 생성
        const row = document.createElement('div');
        row.className = 'menu-modal-tree-row';
        
        // 들여쓰기
        const indent = document.createElement('span');
        indent.className = 'menu-modal-tree-indent';
        indent.style.width = `${indentWidth}px`;
        row.appendChild(indent);
        
        // 토글 버튼
        const toggle = document.createElement('button');
        toggle.className = 'menu-modal-tree-toggle';
        toggle.type = 'button';
        if (!hasChildren) {
            toggle.classList.add('no-children');
        } else {
            toggle.dataset.expanded = isExpanded;
            toggle.onclick = (e) => {
                e.stopPropagation();
                this.toggleNode(menu.menuId);
            };
            toggle.textContent = isExpanded ? '−' : '+';
        }
        row.appendChild(toggle);
        
        // 라벨
        const label = document.createElement('span');
        label.className = 'menu-modal-tree-label';
        label.onclick = () => this.selectMenu(menu.menuId, menu.menuNm);
        
        const name = document.createElement('span');
        name.className = 'menu-modal-name';
        name.textContent = menu.menuNm;
        label.appendChild(name);
        
        const code = document.createElement('span');
        code.className = 'menu-modal-code';
        code.textContent = `(${menu.menuId})`;
        label.appendChild(code);
        
        row.appendChild(label);
        item.appendChild(row);
        
        // 하위 메뉴 컨테이너
        if (hasChildren) {
            const childrenContainer = document.createElement('div');
            childrenContainer.className = 'menu-modal-tree-children';
            if (isExpanded) {
                childrenContainer.classList.add('expanded');
            }
            
            // 하위 메뉴 추가
            menu.subMenuList.forEach(child => {
                const childItem = this.createTreeItem(child);
                childrenContainer.appendChild(childItem);
            });
            
            item.appendChild(childrenContainer);
        }
        
        return item;
    },
    
    /**
     * 노드 확장/축소 토글
     * @param menuId 메뉴ID
     */
    toggleNode: function(menuId) {
        // 모달 컨테이너 내부에서만 검색
        const modalContainer = document.getElementById('menuSearchModal');
        if (!modalContainer) return;
        
        const item = modalContainer.querySelector(`[data-menu-id="${menuId}"]`);
        if (!item) return;
        
        const childrenContainer = item.querySelector('.menu-modal-tree-children');
        if (!childrenContainer) return;
        
        const toggleButton = item.querySelector('.menu-modal-tree-toggle');
        
        if (childrenContainer.classList.contains('expanded')) {
            // 축소
            childrenContainer.classList.remove('expanded');
            toggleButton.dataset.expanded = 'false';
            toggleButton.textContent = '+';
            
            const index = this.expandedNodes.indexOf(menuId);
            if (index > -1) {
                this.expandedNodes.splice(index, 1);
            }
        } else {
            // 확장
            childrenContainer.classList.add('expanded');
            toggleButton.dataset.expanded = 'true';
            toggleButton.textContent = '−';
            
            if (!this.expandedNodes.includes(menuId)) {
                this.expandedNodes.push(menuId);
            }
        }
    },
    
    /**
     * 메뉴 선택
     * @param menuId 메뉴ID
     * @param menuNm 메뉴명
     */
    selectMenu: function(menuId, menuNm) {
        if (this.callback) {
            this.callback(menuId, menuNm);
        }
        this.close();
    }
};

