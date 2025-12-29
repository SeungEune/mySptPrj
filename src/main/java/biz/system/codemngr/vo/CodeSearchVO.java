package biz.system.codemngr.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 코드 검색 조건 VO
 * @author Spatialt 개발팀
 * @since 2025.12.25
 * @version 1.0
 */
@Getter
@Setter
public class CodeSearchVO extends ComDefaultVO {

    /** 검색 타입 */
    private String searchType;

    /** 검색 카테고리 (대분류 / 소분류) */
    private String searchCategory;

    /** 검색 키워드 */
    private String searchKeyword;
}
