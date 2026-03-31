document.addEventListener('DOMContentLoaded', () => {
    fn_init();

    document.getElementById('searchBtn').addEventListener('click', () => fn_searchData(1));
    document.getElementById('resetBtn').addEventListener('click', fn_resetSearch);
    document.getElementById('searchKeyword').addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            fn_searchData(1);
        }
    });
});

function fn_init() {
    fn_searchData(1);
}

function fn_resetSearch() {
    document.getElementById('searchType').value = 'url';
    document.getElementById('searchKeyword').value = '';
    document.getElementById('searchBgnDe').value = '';
    document.getElementById('searchEndDe').value = '';
    fn_searchData(1);
}

function fn_searchData(pageNo) {
    const data = {
        searchType: document.getElementById('searchType').value,
        searchKeyword: Util.isEmpty(document.getElementById('searchKeyword').value) ? null : document.getElementById('searchKeyword').value.trim(),
        searchBgnDe: Util.isEmpty(document.getElementById('searchBgnDe').value) ? null : document.getElementById('searchBgnDe').value,
        searchEndDe: Util.isEmpty(document.getElementById('searchEndDe').value) ? null : document.getElementById('searchEndDe').value,
        searchVO: {
            pageNo: Util.isEmpty(pageNo) ? 1 : pageNo
        }
    };

    callModule.call(Util.getRequestUrl('/system/wlg/getList.do'), data, (result) => {
        $('#webLogListBody').empty();

        if (result && result.result && result.result.list && result.result.list.length > 0) {
            const list = result.result.list.map((item, index) => {
                const rowNum = result.result.pagingVO.totalCount - ((result.result.pagingVO.pageNo - 1) * result.result.pagingVO.pageSize) - index;
                return {
                    ...item,
                    rowNum: rowNum
                };
            });

            $('#listTemplate').tmpl(list).appendTo('#webLogListBody');
            setPagination(result.result.pagingVO, document.getElementById('paginationArea'), 'fn_searchData');
            document.getElementById('totalCount').textContent = result.result.pagingVO.totalCount;
        } else {
            $('#emptyTemplate').tmpl().appendTo('#webLogListBody');
            document.getElementById('totalCount').textContent = '0';
            document.getElementById('paginationArea').innerHTML = '';
        }
    }, true, 'POST');
}
