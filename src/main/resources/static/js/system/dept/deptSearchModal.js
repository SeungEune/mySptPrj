/**
 * 부서 검색 모달 JavaScript (트리 구조)
 */

// 부서 검색 모달 전역 객체
const DeptSearchModal = {
    callback: null,      // 부서 선택 시 콜백 함수
    deptData: [],        // 전체 부서 데이터
    expandedNodes: [],   // 확장된 노드 목록
    
    /**
     * 모달 열기
     * @param callback 부서 선택 시 호출될 콜백 함수 (deptCd, deptNm 파라미터 전달)
     */
    open: function(callback) {
        this.callback = callback;
        document.getElementById('deptSearchModal').classList.add('show');
        document.getElementById('modalDeptNm').value = '';
        this.searchDeptList();  // 모달 열 때 자동 조회
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('deptSearchModal').classList.remove('show');
        document.getElementById('deptTreeList').innerHTML = '<div class="dept-tree-loading">부서 정보를 불러오는 중...</div>';
        this.callback = null;
        this.deptData = [];
    },
    
    /**
     * 부서 목록 조회
     */
    searchDeptList: function() {
        const deptNm = document.getElementById('modalDeptNm').value.trim();
        
        const data = {
            useYn: 'Y',  // 사용중인 부서만 조회
            searchVO: {
                searchKeyword: Util.isEmpty(deptNm) ? null : deptNm
            }
        };
        
        const url = Util.getRequestUrl('/system/dept/getDeptModalList.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result && result.result.list) {
                this.deptData = result.result.list;
                this.renderTree(this.deptData);
            } else {
                document.getElementById('deptTreeList').innerHTML = '<div class="dept-tree-empty">조회 결과가 없습니다.</div>';
                document.getElementById('deptTotalCount').textContent = '전체 0개';
            }
        }, true, 'POST');
    },
    
    /**
     * 트리 구조로 부서 목록 렌더링
     * @param deptList 부서 목록
     */
    renderTree: function(deptList) {
        const treeContainer = document.getElementById('deptTreeList');
        treeContainer.innerHTML = '';
        
        if (!deptList || deptList.length === 0) {
            treeContainer.innerHTML = '<div class="dept-tree-empty">조회 결과가 없습니다.</div>';
            document.getElementById('deptTotalCount').textContent = '전체 0개';
            return;
        }
        
        // 최상위 부서만 필터링 (treeLevel === 0)
        const rootDepts = deptList.filter(d => d.treeLevel === 0);
        
        rootDepts.forEach(dept => {
            const itemElement = this.createTreeItem(dept, deptList);
            treeContainer.appendChild(itemElement);
        });
        
        // 전체 개수 표시
        document.getElementById('deptTotalCount').textContent = `전체 ${deptList.length}개`;
    },
    
    /**
     * 트리 아이템 생성
     * @param dept 부서 정보
     * @param allDepts 전체 부서 목록
     * @return 트리 아이템 DOM 요소
     */
    createTreeItem: function(dept, allDepts) {
        const item = document.createElement('div');
        item.className = 'dept-tree-item';
        item.dataset.deptCd = dept.deptCd;
        item.dataset.level = dept.treeLevel;
        item.dataset.hasChildren = dept.hasChildren || 0;
        
        const indentWidth = (dept.treeLevel || 0) * 20;
        const hasChildren = (dept.hasChildren || 0) > 0;
        const isExpanded = this.expandedNodes.includes(dept.deptCd);
        
        // 트리 행 생성
        const row = document.createElement('div');
        row.className = 'dept-tree-row';
        
        // 들여쓰기
        const indent = document.createElement('span');
        indent.className = 'dept-tree-indent';
        indent.style.width = `${indentWidth}px`;
        row.appendChild(indent);
        
        // 토글 버튼
        const toggle = document.createElement('button');
        toggle.className = 'dept-tree-toggle';
        if (!hasChildren) {
            toggle.classList.add('no-children');
        } else {
            toggle.dataset.expanded = isExpanded;
            toggle.onclick = () => this.toggleNode(dept.deptCd);
            
            const icon = document.createElement('i');
            icon.className = isExpanded ? 'icon-minus' : 'icon-plus';
            icon.textContent = isExpanded ? '−' : '+';
            toggle.appendChild(icon);
        }
        row.appendChild(toggle);
        
        // 라벨
        const label = document.createElement('span');
        label.className = 'dept-tree-label';
        label.onclick = () => this.selectDept(dept.deptCd, dept.deptNm);
        
        const name = document.createElement('span');
        name.className = 'dept-name';
        name.textContent = dept.deptNm;
        label.appendChild(name);
        
        const code = document.createElement('span');
        code.className = 'dept-code';
        code.textContent = `(${dept.deptCd})`;
        label.appendChild(code);
        
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
                const childItem = this.createTreeItem(child, allDepts);
                childrenContainer.appendChild(childItem);
            });
            
            item.appendChild(childrenContainer);
        }
        
        return item;
    },
    
    /**
     * 노드 확장/축소 토글
     * @param deptCd 부서코드
     */
    toggleNode: function(deptCd) {
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
            
            const index = this.expandedNodes.indexOf(deptCd);
            if (index > -1) {
                this.expandedNodes.splice(index, 1);
            }
        } else {
            // 확장
            childrenContainer.classList.add('expanded');
            toggleButton.dataset.expanded = 'true';
            icon.className = 'icon-minus';
            icon.textContent = '−';
            
            if (!this.expandedNodes.includes(deptCd)) {
                this.expandedNodes.push(deptCd);
            }
        }
    },
    
    /**
     * 부서 선택
     * @param deptCd 부서코드
     * @param deptNm 부서명
     */
    selectDept: function(deptCd, deptNm) {
        if (this.callback) {
            this.callback(deptCd, deptNm);
        }
        this.close();
    }
};
