/**
 * 코드 상세값 등록 모달 JavaScript
 */

// 코드 상세값 등록 모달 전역 객체
const CodeDetailRegisterModal = {
    isCodeChecked: false,  // 코드 중복 확인 완료 여부
    currentCodeId: null,   // 현재 선택된 코드 그룹 ID
    currentMode: 'register',  // 현재 모드: 'register' 또는 'edit'
    
    /**
     * 모달 열기 (등록 모드)
     * @param {string} codeId 선택된 코드 그룹 ID
     */
    open: function(codeId) {
        if (Util.isEmpty(codeId)) {
            MessageUtil.warning('대분류를 먼저 선택하세요.');
            return;
        }
        
        this.currentMode = 'register';
        this.currentCodeId = codeId;
        this.isCodeChecked = false;
        document.getElementById('codeDetailRegisterModal').classList.add('show');
        this.resetForm(codeId);
        this.setModeUI();
    },
    
    /**
     * 모달 열기 (수정 모드)
     * @param {string} codeId 코드 그룹 ID
     * @param {string} code 코드값
     */
    openEdit: function(codeId, code) {
        if (Util.isEmpty(codeId) || Util.isEmpty(code)) {
            MessageUtil.warning('코드 ID와 코드값이 필요합니다.');
            return;
        }
        
        this.currentMode = 'edit';
        this.currentCodeId = codeId;
        this.isCodeChecked = true;  // 수정 모드에서는 중복 확인 불필요
        document.getElementById('codeDetailRegisterModal').classList.add('show');
        this.setModeUI();
        this.loadCodeDetailOne(codeId, code);
    },
    
    /**
     * 모드에 따른 UI 설정
     */
    setModeUI: function() {
        const titleEl = document.getElementById('codeDetailModalTitle');
        const codeInput = document.getElementById('modalDetailCode');
        const checkBtn = document.getElementById('modalCheckCodeBtn');
        
        if (this.currentMode === 'edit') {
            if (titleEl) titleEl.textContent = '코드 상세값 수정';
            if (codeInput) {
                codeInput.readOnly = true;
                codeInput.classList.add('readonly');
            }
            if (checkBtn) checkBtn.style.display = 'none';
        } else {
            if (titleEl) titleEl.textContent = '코드 상세값 등록';
            if (codeInput) {
                codeInput.readOnly = false;
                codeInput.classList.remove('readonly');
            }
            if (checkBtn) checkBtn.style.display = '';
        }
    },
    
    /**
     * 코드 상세값 상세 정보 로드
     * @param {string} codeId 코드 그룹 ID
     * @param {string} code 코드값
     */
    loadCodeDetailOne: function(codeId, code) {
        const data = { codeId: codeId, code: code };
        const url = Util.getRequestUrl('/system/code/getCodeDetailOne.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result) {
                const codeDetail = result.result;
                document.getElementById('modalDetailCodeId').value = codeDetail.codeId || '';
                document.getElementById('modalDetailCode').value = codeDetail.code || '';
                document.getElementById('modalDetailCodeNm').value = codeDetail.codeNm || '';
                document.getElementById('modalDetailCodeDc').value = codeDetail.codeDc || '';
                document.getElementById('modalDetailCodeOrder').value = codeDetail.codeOrder || '';
                
                // 사용여부 설정
                const useYn = codeDetail.useYn || 'Y';
                const useYnRadio = document.querySelector(`#codeDetailRegisterForm input[name="useYn"][value="${useYn}"]`);
                if (useYnRadio) {
                    useYnRadio.checked = true;
                }
            } else {
                MessageUtil.error('코드 상세값 정보를 불러올 수 없습니다.');
                this.close();
            }
        }, true, 'POST');
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('codeDetailRegisterModal').classList.remove('show');
        this.resetForm(null);
        this.isCodeChecked = false;
        this.currentCodeId = null;
        this.currentMode = 'register';
    },
    
    /**
     * 폼 초기화
     * @param {string} codeId 코드 그룹 ID
     */
    resetForm: function(codeId) {
        const form = document.getElementById('codeDetailRegisterForm');
        if (form) {
            form.reset();
            // 사용여부 기본값 설정
            const useYnY = document.querySelector('#codeDetailRegisterForm input[name="useYn"][value="Y"]');
            if (useYnY) {
                useYnY.checked = true;
            }
        }
        
        // 코드 ID 설정
        const codeIdInput = document.getElementById('modalDetailCodeId');
        if (codeIdInput) {
            codeIdInput.value = codeId || '';
        }
        
        // 중복 확인 메시지 초기화
        const checkMsg = document.getElementById('modalCodeCheckMsg');
        if (checkMsg) {
            checkMsg.textContent = '';
            checkMsg.className = 'form-msg';
        }
        
        this.isCodeChecked = false;
    },
    
    /**
     * 코드 중복 확인 (codeId + code 조합)
     */
    checkCodeDuplicate: function() {
        const codeIdInput = document.getElementById('modalDetailCodeId');
        const codeInput = document.getElementById('modalDetailCode');
        const checkMsg = document.getElementById('modalCodeCheckMsg');
        
        if (!codeIdInput || !codeInput || !checkMsg) {
            MessageUtil.error('요소를 찾을 수 없습니다.');
            return;
        }
        
        const codeId = codeIdInput.value.trim();
        const code = codeInput.value.trim();
        
        if (Util.isEmpty(codeId)) {
            MessageUtil.warning('코드 ID가 설정되지 않았습니다.');
            return;
        }
        
        if (Util.isEmpty(code)) {
            MessageUtil.warning('코드를 입력해주세요.');
            codeInput.focus();
            return;
        }
        
        if (code.length > 20) {
            MessageUtil.warning('코드는 최대 20자까지 입력 가능합니다.');
            codeInput.focus();
            return;
        }
        
        const data = {
            codeId: codeId,
            code: code
        };
        const url = Util.getRequestUrl('/system/code/checkCodeDetailDuplicate.do');
        
        callModule.call(url, data, (result) => {
            if (result && result.result) {
                const isDuplicate = result.result.duplicate;
                if (isDuplicate) {
                    checkMsg.textContent = '이미 사용 중인 코드입니다.';
                    checkMsg.className = 'form-msg error';
                    this.isCodeChecked = false;
                } else {
                    checkMsg.textContent = '사용 가능한 코드입니다.';
                    checkMsg.className = 'form-msg success';
                    this.isCodeChecked = true;
                }
            } else {
                checkMsg.textContent = '중복 확인 중 오류가 발생했습니다.';
                checkMsg.className = 'form-msg error';
                this.isCodeChecked = false;
            }
        }, true, 'POST');
    },
    
    /**
     * 코드 상세값 저장
     */
    saveCodeDetail: function() {
        const form = document.getElementById('codeDetailRegisterForm');
        if (!form) {
            MessageUtil.error('폼을 찾을 수 없습니다.');
            return;
        }
        
        // 필수값 검증
        const codeId = document.getElementById('modalDetailCodeId').value.trim();
        const code = document.getElementById('modalDetailCode').value.trim();
        const codeNm = document.getElementById('modalDetailCodeNm').value.trim();
        
        if (Util.isEmpty(codeId)) {
            MessageUtil.error('코드 ID는 필수입니다.');
            return;
        }
        
        if (Util.isEmpty(code)) {
            MessageUtil.error('코드는 필수입니다.');
            document.getElementById('modalDetailCode').focus();
            return;
        }
        
        if (code.length > 20) {
            MessageUtil.error('코드는 최대 20자까지 입력 가능합니다.');
            document.getElementById('modalDetailCode').focus();
            return;
        }
        
        if (Util.isEmpty(codeNm)) {
            MessageUtil.error('코드명은 필수입니다.');
            document.getElementById('modalDetailCodeNm').focus();
            return;
        }
        
        if (codeNm.length > 100) {
            MessageUtil.error('코드명은 최대 100자까지 입력 가능합니다.');
            document.getElementById('modalDetailCodeNm').focus();
            return;
        }
        
        // 등록 모드일 때만 중복 확인 체크
        if (this.currentMode === 'register' && !this.isCodeChecked) {
            MessageUtil.warning('코드 중복 확인을 해주세요.');
            return;
        }
        
        // 코드 설명 길이 검증
        const codeDc = document.getElementById('modalDetailCodeDc').value.trim();
        if (codeDc.length > 500) {
            MessageUtil.error('코드 설명은 최대 500자까지 입력 가능합니다.');
            document.getElementById('modalDetailCodeDc').focus();
            return;
        }
        
        // 출력순서 검증
        const codeOrderInput = document.getElementById('modalDetailCodeOrder');
        let codeOrder = null;
        if (codeOrderInput && codeOrderInput.value.trim() !== '') {
            const orderValue = parseInt(codeOrderInput.value.trim());
            if (isNaN(orderValue) || orderValue < 1) {
                MessageUtil.error('출력순서는 1 이상의 숫자여야 합니다.');
                codeOrderInput.focus();
                return;
            }
            codeOrder = orderValue;
        }
        
        // 폼 데이터 수집
        const formData = {
            codeId: codeId,
            code: code,
            codeNm: codeNm,
            codeDc: Util.isEmpty(codeDc) ? null : codeDc,
            codeOrder: codeOrder,
            useYn: document.querySelector('#codeDetailRegisterForm input[name="useYn"]:checked')?.value || 'Y'
        };
        
        const url = Util.getRequestUrl('/system/code/saveCodeDetail.do');
        
        callModule.call(url, formData, (result) => {
            if (result && result.result && result.result.resultValue) {
                // 모달 닫기 전에 codeId 저장
                const codeId = this.currentCodeId;
                
                MessageUtil.success(result.status.message || '저장이 완료되었습니다.', () => {
                    // 모달 닫기
                    this.close();
                    
                    // 소분류 목록 리프레시
                    if (typeof fn_renderCodeDetailList === 'function' && codeId) {
                        fn_renderCodeDetailList(codeId);
                    }
                });
            } else {
                MessageUtil.error((result && result.status && result.status.message) || '저장에 실패하였습니다.');
            }
        }, true, 'POST');
    }
};

