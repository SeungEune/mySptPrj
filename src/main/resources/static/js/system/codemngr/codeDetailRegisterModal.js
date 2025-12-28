/**
 * 코드 상세값 등록 모달 JavaScript
 */

// 코드 상세값 등록 모달 전역 객체
const CodeDetailRegisterModal = {
    isCodeChecked: false,  // 코드 중복 확인 완료 여부
    currentCodeId: null,   // 현재 선택된 코드 그룹 ID
    
    /**
     * 모달 열기
     * @param {string} codeId 선택된 코드 그룹 ID
     */
    open: function(codeId) {
        if (Util.isEmpty(codeId)) {
            MessageUtil.warning('대분류를 먼저 선택하세요.');
            return;
        }
        
        this.currentCodeId = codeId;
        this.isCodeChecked = false;
        document.getElementById('codeDetailRegisterModal').classList.add('show');
        this.resetForm(codeId);
    },
    
    /**
     * 모달 닫기
     */
    close: function() {
        document.getElementById('codeDetailRegisterModal').classList.remove('show');
        this.resetForm(null);
        this.isCodeChecked = false;
        this.currentCodeId = null;
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
        
        // 중복 확인 체크
        if (!this.isCodeChecked) {
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

