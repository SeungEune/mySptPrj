package egovframework.com.cmm.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 공통 Paging 정보
 * @author : (주)스패셜티
 * @since : 2024. 01. 20.
 * <p>
 * == 수정사항 ==
 * ---------------------------------------
 * 2024. 01. 20.  김언중 최초 생성
 */
@Getter @Setter @ToString
public class PagingVO {

    private int pageSize;       // 게시 글 수
    private int pageCount;
    private int firstPageNo;    // 첫 번째 페이지 번호
    private int prevPageNo;    // 이전 페이지 번호
    private int startPageNo;    // 시작 페이지 (페이징 네비 기준)
    private int pageNo;       // 페이지 번호
    private int endPageNo;       // 끝 페이지 (페이징 네비 기준)
    private int nextPageNo;    // 다음 페이지 번호
    private int finalPageNo;    // 마지막 페이지 번호
    private int totalCount;    // 게시 글 전체 수

    private final int DEFAULT_PAGE_NO = 1;
    private final int DEFAULT_PAGE_INDEX = 6;

    public PagingVO() { }

    public PagingVO(int totalCount, int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        setTotalCount(totalCount);
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.makePaging();
    }

    /**
     * 페이징 생성
     */
    private void makePaging() {
        if (this.totalCount == 0) return; // 게시 글 전체 수가 없는 경우
        if (this.pageNo == 0) pageNo = DEFAULT_PAGE_NO; // 기본 값 설정

        int finalPage = (totalCount + (pageSize - 1)) / pageSize; // 마지막 페이지
        if (this.pageNo > finalPage) pageNo = finalPage; // 기본 값 설정

        if (this.pageNo < 0) this.pageNo = 1; // 현재 페이지 유효성 체크

        boolean isNowFirst = pageNo == 1; // 시작 페이지 (전체)
        boolean isNowFinal = pageNo == finalPage; // 마지막 페이지 (전체)

        int startPage = ((pageNo - 1) / DEFAULT_PAGE_INDEX) * DEFAULT_PAGE_INDEX + 1; // 시작 페이지 (페이징 네비 기준)
        int endPage = startPage + DEFAULT_PAGE_INDEX - 1; // 끝 페이지 (페이징 네비 기준)

        if (endPage > finalPage) { // [마지막 페이지 (페이징 네비 기준) > 마지막 페이지] 보다 큰 경우
            endPage = finalPage;
        }

        this.setFirstPageNo(1); // 첫 번째 페이지 번호

        if (isNowFirst) {
            this.setPrevPageNo(1); // 이전 페이지 번호
        } else {
            this.setPrevPageNo((Math.max((pageNo - 1), 1))); // 이전 페이지 번호
        }

        this.setStartPageNo(startPage); // 시작 페이지 (페이징 네비 기준)
        this.setEndPageNo(endPage); // 끝 페이지 (페이징 네비 기준)

        if (isNowFinal) {
            this.setNextPageNo(finalPage); // 다음 페이지 번호
        } else {
            this.setNextPageNo((Math.min((pageNo + 1), finalPage))); // 다음 페이지 번호
        }

        this.setFinalPageNo(finalPage); // 마지막 페이지 번호
    }
}
