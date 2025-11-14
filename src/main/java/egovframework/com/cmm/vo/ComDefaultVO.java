package egovframework.com.cmm.vo;

import biz.login.vo.LoginVO;
import egovframework.com.cmm.service.EgovProperties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @Class Name : ComDefaultVO.java
 * @Description : ComDefaultVO class
 * @Modification Information
 * @
 * @  수정일         수정자                   수정내용
 * @ -------    --------    ---------------------------
 * @ 2009.02.01    조재영         최초 생성
 *
 *  @author 공통서비스 개발팀 조재영
 *  @since 2009.02.01
 *  @version 1.0
 *  @see
 *
 */
@Getter
@Setter
public class ComDefaultVO extends LoginVO {

	/** 검색조건 */
    private String searchCondition = "";

    /** 검색Keyword */
    private String searchKeyword = "";

    /** 검색SubKeyword */
    private String subSearchKeyword = "";

    /** 검색정렬 */
    private String searchOrder = "";

    /** 검색사용여부 */
    private String searchUseYn = "";

    /** 현재페이지 */
    private int pageNo = 1;

    /** 페이지갯수 */
    private int pageUnit = Integer.parseInt(EgovProperties.getProperty("Globals.pageUnit"));

    /** 페이지사이즈 */
    private int pageSize = Integer.parseInt(EgovProperties.getProperty("Globals.pageSize"));

    /** firstIndex */
    private int firstIndex = 1;

    /** lastIndex */
    private int lastIndex = 1;

    /** recordCountPerPage */
    private int recordCountPerPage = 10;

    /** 검색KeywordFrom */
    private String searchKeywordFrom = "";

	/** 검색KeywordTo */
    private String searchKeywordTo = "";

    /** 검색설문 상태 코드 */
    private String suryStatusCode = "";

    /** 페이징 번호 */
    private int offset;

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void calculateOffset() {
        if (pageNo == 0) {
            this.pageNo = 1;
        }

        this.offset = pageSize * (pageNo - 1);
    }
}