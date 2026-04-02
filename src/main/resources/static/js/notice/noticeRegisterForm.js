document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('saveBtn').addEventListener('click', fn_saveNotice);
    document.getElementById('cancelBtn').addEventListener('click', () => location.href = '/notice/noticeListForm.do');
});

function fn_saveNotice() {
    const data = {
        noticeTitle: document.getElementById('noticeTitle').value.trim(),
        noticeCn: document.getElementById('noticeCn').value.trim(),
        importantYn: document.querySelector('input[name="importantYn"]:checked').value
    };

    callModule.call(Util.getRequestUrl('/notice/saveNotice.do'), data, () => {
        location.href = '/notice/noticeListForm.do';
    }, true, 'POST');
}
