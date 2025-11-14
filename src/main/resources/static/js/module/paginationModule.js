/**
 * ajax 페이징 처리 공통 모듈
 */

/**
 * 페이징 처리
 * @param paging pageVO
 * @param target 페이징 처리 html target
 * @param fnName ajax 실행 fn name
 */
function setPagination(paging, target, fnName){

    let pageHtml = '';

    if(paging.totalCount <= 1){
        if (target instanceof jQuery) target.html(pageHtml);
        else if (target instanceof HTMLElement) target.innerHTML = pageHtml;
    }

    //이전
    pageHtml +=         `<a class="prev_page `;
    if(paging.pageNo !== 1 ){ //&& paging.startPageNo !== 0
        pageHtml +=     `" onclick="${fnName}('${paging.prevPageNo}')">`;
    } else {
        pageHtml +=         'disabled_prev_page">';
    }
    pageHtml +=         '이전 페이지</a>';


    //페이징
    pageHtml +=     '<ul>';
    let pageNo = 0;
    for (let i = 0; i < (paging.endPageNo - paging.startPageNo) + 1; i++){

        if(i === 0){
            pageNo = paging.startPageNo;
        } else {
            pageNo += 1;
        }


        if(paging.pageNo === pageNo){
            pageHtml +=     '<li class="active">';
            pageHtml +=     `    <a onclick="${fnName}('${pageNo}')" href="javascript:void(0);" class="active">`;
        }
        else {
            pageHtml +=     '<li>';
            pageHtml +=     `    <a onclick="${fnName}('${pageNo}')" >`;
        }
        pageHtml +=     `${pageNo === 0 ? 1:pageNo}`;
        pageHtml +=     '</a>';
        pageHtml += '</li>';
    }
    pageHtml +=     '</ul>';


    //다음
    pageHtml +=         `<a href='javascript:void(0)' class="next_page `;
    if(paging.finalPageNo !== paging.pageNo){
        pageHtml +=     `" onclick="${fnName}('${paging.nextPageNo}')">`;
    } else {
        pageHtml +=     'no-active">';
    }
    pageHtml +=         '다음 페이지</a>';


    if (target instanceof jQuery) target.html(pageHtml);
    else if (target instanceof HTMLElement) target.innerHTML = pageHtml;

}

/**
 * 빈 페이징 구성
 * @param target 페이징 처리 html target
 */
function setEmptyPagination(target) {
    let pageHtml = '';
    pageHtml += '<a class="disabled_prev_page">이전 페이지</a>';
    pageHtml += '   <ul>';
    pageHtml += '       <li class="active"><a>1</a></li>';
    pageHtml += '   </ul>';
    pageHtml += '<a class="disabled_next_page">다음 페이지</a>';

    target.html(pageHtml);
}