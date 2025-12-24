/**
 * 사용자 검색 모달 JavaScript
 */

// 사용자 검색 모달 전역 객체
const UserSearchModal = {
    callback: null,              // 사용자 선택 시 콜백 함수
    userList: [],                // 전체 사용자 데이터
    selectedUsers: new Set(),     // 선택된 사용자 ID Set
    excludedUserIds: new Set(),   // 제외할 사용자 ID Set (이미 권한이 있는 사용자)
    currentPage: 1,               // 현재 페이지
    pagingVO: null,               // 페이징 정보
    
    /**
     * 모달 열기
     * @param callback 사용자 선택 시 호출될 콜백 함수 (selectedUsers 배열 전달)
     * @param excludedUserIds 제외할 사용자 ID 배열 (이미 권한이 있는 사용자)
     */
    open: function(callback, excludedUserIds) {
        this.callback = callback;
        this.selectedUsers.clear();
        this.excludedUserIds.clear();
        
        // 제외할 사용자 ID 설정
        if (excludedUserIds && Array.isArray(excludedUserIds)) {
            excludedUserIds.forEach(userId => {
                if (userId) {
                    this.excludedUserIds.add(userId);
                }
            });
        }
        
        // 모달 열기
        document.getElementById('userSearchModal').classList.add('show');
        
        // 검색 필드 초기화
        document.getElementById('modalUserSearchKeyword').value = '';
        document.getElementById('modalUserSearchType').value = 'userNm';
        document.getElementById('modalUserSelectAll').checked = false;
        
        // 초기 상태 표시 (로딩 중)
        const tbody = document.getElementById('modalUserListTbody');
        tbody.innerHTML = '<tr><td class="tc no-data-table" colspan="5">사용자 목록을 불러오는 중...</td></tr>';
        document.getElementById('modalUserPaginationArea').innerHTML = '';
        
        // 초기 사용자 목록 자동 조회
        this.searchUserList(1);
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('userSearchModal').classList.remove('show');
        this.callback = null;
        this.userList = [];
        this.selectedUsers.clear();
        this.excludedUserIds.clear();
        this.currentPage = 1;
        this.pagingVO = null;
    },
    
    /**
     * 사용자 목록 조회
     * @param pageNo 페이지 번호
     */
    searchUserList: function(pageNo) {
        const searchType = document.getElementById('modalUserSearchType').value;
        const searchKeyword = document.getElementById('modalUserSearchKeyword').value.trim();
        
        this.currentPage = pageNo || 1;
        
        const data = {
            searchVO: {
                searchCondition: searchType || 'userNm',
                searchKeyword: Util.isEmpty(searchKeyword) ? null : searchKeyword,
                pageNo: this.currentPage
            }
        };
        
        const url = Util.getRequestUrl('/system/user/getList.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result && result.result.list && result.result.list.length > 0) {
                this.userList = result.result.list;
                this.pagingVO = result.result.pagingVO;
                
                // 이미 권한이 있는 사용자 필터링
                const filteredList = this.userList.filter(user => {
                    return !this.excludedUserIds.has(user.userId);
                });
                
                this.renderUserList(filteredList);
                this.renderPagination();
            } else {
                const tbody = document.getElementById('modalUserListTbody');
                tbody.innerHTML = '<tr><td class="tc no-data-table" colspan="5">조회 결과가 없습니다.</td></tr>';
                document.getElementById('modalUserPaginationArea').innerHTML = '';
            }
        }, true, 'POST');
    },
    
    /**
     * 사용자 목록 테이블 렌더링
     * @param userList 사용자 목록
     */
    renderUserList: function(userList) {
        const tbody = document.getElementById('modalUserListTbody');
        tbody.innerHTML = '';
        
        if (!userList || userList.length === 0) {
            tbody.innerHTML = '<tr><td class="tc no-data-table" colspan="5">조회 결과가 없습니다.</td></tr>';
            return;
        }
        
        userList.forEach((user, index) => {
            const tr = document.createElement('tr');
            tr.dataset.userId = user.userId;
            
            const isSelected = this.selectedUsers.has(user.userId);
            const isExcluded = this.excludedUserIds.has(user.userId);
            
            // 체크박스
            const checkboxTd = document.createElement('td');
            checkboxTd.className = 'tc';
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = isSelected;
            checkbox.disabled = isExcluded;
            checkbox.onclick = (e) => {
                e.stopPropagation();
                this.toggleUserSelection(user.userId);
            };
            checkboxTd.appendChild(checkbox);
            tr.appendChild(checkboxTd);
            
            // 사용자명
            const userNmTd = document.createElement('td');
            userNmTd.className = 'text-left';
            userNmTd.textContent = user.userNm || '';
            tr.appendChild(userNmTd);
            
            // 사용자ID
            const userIdTd = document.createElement('td');
            userIdTd.className = 'text-left';
            userIdTd.textContent = user.userId || '';
            tr.appendChild(userIdTd);
            
            // 부서명
            const deptNmTd = document.createElement('td');
            deptNmTd.className = 'text-left';
            deptNmTd.textContent = user.deptNm || '-';
            tr.appendChild(deptNmTd);
            
            // 상태
            const statusTd = document.createElement('td');
            statusTd.className = 'text-left';
            statusTd.textContent = user.userSttusCdNm || '-';
            if (isExcluded) {
                statusTd.textContent = '(이미 권한 있음)';
                statusTd.style.color = '#95A5A6';
            }
            tr.appendChild(statusTd);
            
            // 제외된 사용자는 회색 처리
            if (isExcluded) {
                tr.style.opacity = '0.5';
                tr.style.backgroundColor = '#F8F9FA';
            }
            
            tbody.appendChild(tr);
        });
        
        // 전체 선택 체크박스 업데이트
        this.updateSelectAllCheckbox();
    },
    
    /**
     * 페이징 렌더링
     */
    renderPagination: function() {
        const paginationArea = document.getElementById('modalUserPaginationArea');
        if (this.pagingVO) {
            // pagination 클래스 추가
            paginationArea.className = 'pagination';
            setPagination(this.pagingVO, paginationArea, 'UserSearchModal.searchUserList');
        } else {
            paginationArea.innerHTML = '';
        }
    },
    
    /**
     * 사용자 선택/해제 토글
     * @param userId 사용자ID
     */
    toggleUserSelection: function(userId) {
        if (this.excludedUserIds.has(userId)) {
            return; // 이미 권한이 있는 사용자는 선택 불가
        }
        
        if (this.selectedUsers.has(userId)) {
            this.selectedUsers.delete(userId);
        } else {
            this.selectedUsers.add(userId);
        }
        
        // 체크박스 상태 업데이트
        const checkbox = document.querySelector(`#modalUserListTbody tr[data-user-id="${userId}"] input[type="checkbox"]`);
        if (checkbox) {
            checkbox.checked = this.selectedUsers.has(userId);
        }
        
        // 전체 선택 체크박스 업데이트
        this.updateSelectAllCheckbox();
    },
    
    /**
     * 전체 선택/해제
     * @param checked 선택 여부
     */
    toggleSelectAll: function(checked) {
        const tbody = document.getElementById('modalUserListTbody');
        const rows = tbody.querySelectorAll('tr[data-user-id]');
        
        rows.forEach(row => {
            const userId = row.dataset.userId;
            if (this.excludedUserIds.has(userId)) {
                return; // 제외된 사용자는 건너뛰기
            }
            
            if (checked) {
                this.selectedUsers.add(userId);
            } else {
                this.selectedUsers.delete(userId);
            }
            
            const checkbox = row.querySelector('input[type="checkbox"]');
            if (checkbox) {
                checkbox.checked = checked;
            }
        });
    },
    
    /**
     * 전체 선택 체크박스 상태 업데이트
     */
    updateSelectAllCheckbox: function() {
        const tbody = document.getElementById('modalUserListTbody');
        const rows = tbody.querySelectorAll('tr[data-user-id]');
        const selectAllCheckbox = document.getElementById('modalUserSelectAll');
        
        if (rows.length === 0) {
            selectAllCheckbox.checked = false;
            return;
        }
        
        let selectableCount = 0;
        let selectedCount = 0;
        
        rows.forEach(row => {
            const userId = row.dataset.userId;
            if (!this.excludedUserIds.has(userId)) {
                selectableCount++;
                if (this.selectedUsers.has(userId)) {
                    selectedCount++;
                }
            }
        });
        
        selectAllCheckbox.checked = (selectableCount > 0 && selectedCount === selectableCount);
    },
    
    /**
     * 선택된 사용자 목록 반환
     * @return 선택된 사용자 배열
     */
    getSelectedUsers: function() {
        const selectedUserList = [];
        this.selectedUsers.forEach(userId => {
            const user = this.userList.find(u => u.userId === userId);
            if (user) {
                selectedUserList.push({
                    userId: user.userId,
                    userNm: user.userNm
                });
            }
        });
        return selectedUserList;
    },
    
    /**
     * 선택 확인 및 콜백 호출
     */
    confirmSelection: function() {
        const selectedUsers = this.getSelectedUsers();
        
        if (selectedUsers.length === 0) {
            MessageUtil.alert('사용자를 선택하세요.');
            return;
        }
        
        if (this.callback && typeof this.callback === 'function') {
            this.callback(selectedUsers);
        }
        
        this.close();
    }
};

