document.addEventListener('DOMContentLoaded', () => {
    fn_loadDetail();
    document.getElementById('listBtn').addEventListener('click', () => location.href = '/notice/noticeListForm.do');

    const editBtn = document.getElementById('editBtn');
    if (editBtn) {
        editBtn.addEventListener('click', () => callModule.post(Util.getRequestUrl('/notice/noticeEditForm.do'), { noticeSn: NOTICE_SN }));
    }

    const deleteBtn = document.getElementById('deleteBtn');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', fn_deleteNotice);
    }
});

function fn_loadDetail() {
    callModule.call(Util.getRequestUrl('/notice/getDetail.do'), { noticeSn: String(NOTICE_SN), increaseViewCnt: 'Y' }, (result) => {
        const notice = result.result;
        document.getElementById('noticeTitle').textContent = notice.noticeTitle || '-';
        document.getElementById('importantYn').textContent = notice.importantYn === 'Y' ? '중요' : '일반';
        document.getElementById('registerInfo').textContent = `${notice.registerNm || notice.registerId || '-'} / ${notice.registDt || '-'}`;
        document.getElementById('viewCnt').textContent = notice.viewCnt || 0;
        document.getElementById('noticeCn').textContent = notice.noticeCn || '-';
    }, true, 'POST');
}

function fn_deleteNotice() {
    callModule.call(Util.getRequestUrl('/notice/deleteNotice.do'), { noticeSn: String(NOTICE_SN) }, () => {
        location.href = '/notice/noticeListForm.do';
    }, true, 'POST');
}
