package egovframework.com.cmm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 응답 결과(리스트) VO
 */
@Getter
@AllArgsConstructor
public class ResultListVO<T> {

    // 조회 목록
    private List<T> list;
    
    // 페이지 정보
    private PagingVO pagingVO;
}
