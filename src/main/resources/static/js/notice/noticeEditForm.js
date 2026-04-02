document.addEventListener('DOMContentLoaded', () => {
    fn_loadDetail();
    document.getElementById('updateBtn').addEventListener('click', fn_updateNotice);
    document.getElementById('cancelBtn').addEventListener('click', () => callModule.post(Util.getRequestUrl('/notice/noticeViewForm.do'), { noticeSn: NOTICE_SN }));
});

function fn_loadDetail() {
    callModule.call(Util.getRequestUrl('/notice/getDetail.do'), { noticeSn: String(NOTICE_SN), increaseViewCnt: 'N' }, (result) => {
        const notice = result.result;
        document.getElementById('noticeTitle').value = notice.noticeTitle || '';
        document.getElementById('noticeCn').value = notice.noticeCn || '';
        document.querySelector(`input[name="importantYn"][value="${notice.importantYn || 'N'}"]`).checked = true;
    }, true, 'POST');
}

function fn_updateNotice() {
    const data = {
        noticeSn: NOTICE_SN,
        noticeTitle: document.getElementById('noticeTitle').value.trim(),
        noticeCn: document.getElementById('noticeCn').value.trim(),
        importantYn: document.querySelector('input[name="importantYn"]:checked').value
    };

    callModule.call(Util.getRequestUrl('/notice/updateNotice.do'), data, () => {
        callModule.post(Util.getRequestUrl('/notice/noticeViewForm.do'), { noticeSn: NOTICE_SN });
    }, true, 'POST');
}
