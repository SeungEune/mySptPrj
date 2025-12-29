/**
 * 코드 그룹 등록 모달 JavaScript
 */

// 코드 그룹 등록 모달 전역 객체
const CodeGroupRegisterModal = {
    isCodeIdChecked: false,  // 코드 ID 중복 확인 완료 여부
    currentMode: 'register',  // 현재 모드: 'register' 또는 'edit'
    
    /**
     * 모달 열기 (등록 모드)
     */
    open: function() {
        this.currentMode = 'register';
        this.isCodeIdChecked = false;
        document.getElementById('codeGroupRegisterModal').classList.add('show');
        this.resetForm();
        this.setModeUI();
    },
    
    /**
     * 모달 열기 (수정 모드)
     * @param {string} codeId 코드 그룹 ID
     */
    openEdit: function(codeId) {
        if (Util.isEmpty(codeId)) {
            MessageUtil.warning('코드 ID가 필요합니다.');
            return;
        }
        
        this.currentMode = 'edit';
        this.isCodeIdChecked = true;  // 수정 모드에서는 중복 확인 불필요
        document.getElementById('codeGroupRegisterModal').classList.add('show');
        this.setModeUI();
        this.loadCodeGroupDetail(codeId);
    },
    
    /**
     * 모드에 따른 UI 설정
     */
    setModeUI: function() {
        const titleEl = document.getElementById('codeGroupModalTitle');
        const codeIdInput = document.getElementById('modalCodeId');
        const checkBtn = document.getElementById('modalCheckCodeIdBtn');
        
        if (this.currentMode === 'edit') {
            if (titleEl) titleEl.textContent = '코드 그룹 수정';
            if (codeIdInput) {
                codeIdInput.readOnly = true;
                codeIdInput.classList.add('readonly');
            }
            if (checkBtn) checkBtn.style.display = 'none';
        } else {
            if (titleEl) titleEl.textContent = '코드 그룹 등록';
            if (codeIdInput) {
                codeIdInput.readOnly = false;
                codeIdInput.classList.remove('readonly');
            }
            if (checkBtn) checkBtn.style.display = '';
        }
    },
    
    /**
     * 코드 그룹 상세 정보 로드
     * @param {string} codeId 코드 그룹 ID
     */
    loadCodeGroupDetail: function(codeId) {
        const data = { codeId: codeId };
        const url = Util.getRequestUrl('/system/code/getCodeGroupDetail.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result) {
                const codeGroup = result.result;
                document.getElementById('modalCodeId').value = codeGroup.codeId || '';
                document.getElementById('modalCodeIdNm').value = codeGroup.codeIdNm || '';
                document.getElementById('modalCodeDc').value = codeGroup.codeDc || '';
                
                // 사용여부 설정
                const useYn = codeGroup.useYn || 'Y';
                const useYnRadio = document.querySelector(`#codeGroupRegisterForm input[name="useYn"][value="${useYn}"]`);
                if (useYnRadio) {
                    useYnRadio.checked = true;
                }
            } else {
                MessageUtil.error('코드 그룹 정보를 불러올 수 없습니다.');
                this.close();
            }
        }, true, 'POST');
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('codeGroupRegisterModal').classList.remove('show');
        this.resetForm();
        this.isCodeIdChecked = false;
        this.currentMode = 'register';
    },
    
    /**
     * 폼 초기화
     */
    resetForm: function() {
        const form = document.getElementById('codeGroupRegisterForm');
        if (form) {
            form.reset();
            // 사용여부 기본값 설정
            const useYnY = document.querySelector('#codeGroupRegisterForm input[name="useYn"][value="Y"]');
            if (useYnY) {
                useYnY.checked = true;
            }
        }
        
        // 중복 확인 메시지 초기화
        const checkMsg = document.getElementById('modalCodeIdCheckMsg');
        if (checkMsg) {
            checkMsg.textContent = '';
            checkMsg.className = 'form-msg';
        }
        
        this.isCodeIdChecked = false;
    },
    
    /**
     * 코드 ID 중복 확인
     */
    checkCodeIdDuplicate: function() {
        const codeIdInput = document.getElementById('modalCodeId');
        const checkMsg = document.getElementById('modalCodeIdCheckMsg');
        
        if (!codeIdInput || !checkMsg) {
            MessageUtil.error('요소를 찾을 수 없습니다.');
            return;
        }
        
        const codeId = codeIdInput.value.trim();
        
        if (Util.isEmpty(codeId)) {
            MessageUtil.warning('코드 ID를 입력해주세요.');
            codeIdInput.focus();
            return;
        }
        
        if (codeId.length > 20) {
            MessageUtil.warning('코드 ID는 최대 20자까지 입력 가능합니다.');
            codeIdInput.focus();
            return;
        }
        
        const data = { codeId: codeId };
        const url = Util.getRequestUrl('/system/code/checkCodeIdDuplicate.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result) {
                const isDuplicate = result.result.duplicate;
                if (isDuplicate) {
                    checkMsg.textContent = '이미 사용 중인 코드 ID입니다.';
                    checkMsg.className = 'form-msg error';
                    this.isCodeIdChecked = false;
                } else {
                    checkMsg.textContent = '사용 가능한 코드 ID입니다.';
                    checkMsg.className = 'form-msg success';
                    this.isCodeIdChecked = true;
                }
            } else {
                checkMsg.textContent = '중복 확인 중 오류가 발생했습니다.';
                checkMsg.className = 'form-msg error';
                this.isCodeIdChecked = false;
            }
        }, true, 'POST');
    },
    
    /**
     * 코드 그룹 저장
     */
    saveCodeGroup: function() {
        const form = document.getElementById('codeGroupRegisterForm');
        if (!form) {
            MessageUtil.error('폼을 찾을 수 없습니다.');
            return;
        }
        
        // 필수값 검증
        const codeId = document.getElementById('modalCodeId').value.trim();
        const codeIdNm = document.getElementById('modalCodeIdNm').value.trim();
        
        if (Util.isEmpty(codeId)) {
            MessageUtil.error('코드 ID는 필수입니다.');
            document.getElementById('modalCodeId').focus();
            return;
        }
        
        if (codeId.length > 20) {
            MessageUtil.error('코드 ID는 최대 20자까지 입력 가능합니다.');
            document.getElementById('modalCodeId').focus();
            return;
        }
        
        if (Util.isEmpty(codeIdNm)) {
            MessageUtil.error('코드 그룹명은 필수입니다.');
            document.getElementById('modalCodeIdNm').focus();
            return;
        }
        
        if (codeIdNm.length > 100) {
            MessageUtil.error('코드 그룹명은 최대 100자까지 입력 가능합니다.');
            document.getElementById('modalCodeIdNm').focus();
            return;
        }
        
        // 등록 모드일 때만 중복 확인 체크
        if (this.currentMode === 'register' && !this.isCodeIdChecked) {
            MessageUtil.warning('코드 ID 중복 확인을 해주세요.');
            return;
        }
        
        // 코드 설명 길이 검증
        const codeDc = document.getElementById('modalCodeDc').value.trim();
        if (codeDc.length > 500) {
            MessageUtil.error('코드 설명은 최대 500자까지 입력 가능합니다.');
            document.getElementById('modalCodeDc').focus();
            return;
        }
        
        // 폼 데이터 수집
        const formData = {
            codeId: codeId,
            codeIdNm: codeIdNm,
            codeDc: Util.isEmpty(codeDc) ? null : codeDc,
            useYn: document.querySelector('input[name="useYn"]:checked')?.value || 'Y'
        };
        
        const url = Util.getRequestUrl('/system/code/saveCodeGroup.do');
        
        callModule.call(url, formData, (result) => {
            if (result && result.result && result.result.resultValue) {
                MessageUtil.success(result.status.message || '저장이 완료되었습니다.', () => {
                    // 모달 닫기
                    this.close();
                    
                    // 목록 리프레시
                    if (typeof fn_searchCodeGroup === 'function') {
                        fn_searchCodeGroup();
                    }
                });
            } else {
                MessageUtil.error((result && result.status && result.status.message) || '저장에 실패하였습니다.');
            }
        }, true, 'POST');
    }
};

