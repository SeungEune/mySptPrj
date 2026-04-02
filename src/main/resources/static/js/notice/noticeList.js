document.addEventListener('DOMContentLoaded', () => {
    fn_init();
    document.getElementById('searchBtn').addEventListener('click', () => fn_searchData(1));
    document.getElementById('resetBtn').addEventListener('click', fn_resetSearch);
    document.getElementById('searchKeyword').addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            fn_searchData(1);
        }
    });

    const registerBtn = document.getElementById('registerBtn');
    if (registerBtn) {
        registerBtn.addEventListener('click', () => location.href = '/notice/noticeRegisterForm.do');
    }
});

function fn_init() {
    fn_searchData(1);
}

function fn_resetSearch() {
    document.getElementById('searchType').value = 'noticeTitle';
    document.getElementById('searchKeyword').value = '';
    fn_searchData(1);
}

function fn_searchData(pageNo) {
    const data = {
        searchType: document.getElementById('searchType').value,
        searchKeyword: Util.isEmpty(document.getElementById('searchKeyword').value) ? null : document.getElementById('searchKeyword').value.trim(),
        searchVO: {
            pageNo: Util.isEmpty(pageNo) ? 1 : pageNo,
            pageSize: 10
        }
    };

    callModule.call(Util.getRequestUrl('/notice/getList.do'), data, (result) => {
        $('#noticeListBody').empty();
        if (result && result.result && result.result.list && result.result.list.length > 0) {

            console.log(result.result.list);

            const pagingVO = result.result.pagingVO;
            $('#listTemplate').tmpl(result.result.list).appendTo('#noticeListBody');
            setPagination(pagingVO, document.getElementById('paginationArea'), 'fn_searchData');
            document.getElementById('totalCount').textContent = pagingVO.totalCount;
        } else {
            $('#emptyTemplate').tmpl().appendTo('#noticeListBody');
            document.getElementById('totalCount').textContent = '0';
            document.getElementById('paginationArea').innerHTML = '';
        }
    }, true, 'POST');
}

function fn_goViewForm(noticeSn) {
    callModule.post(Util.getRequestUrl('/notice/noticeViewForm.do'), { noticeSn });
}
