package biz.notice.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 공지사항 VO
 */
@Getter
@Setter
public class NoticeVO {

    private Long noticeSn;
    private String noticeTitle;
    private String noticeCn;
    private String importantYn;
    private Integer viewCnt;
    private String useYn;
    private String registerId;
    private String registerNm;
    private String registDt;
    private String updusrId;
    private String updtDt;
    private Integer rowNum;

    private ComDefaultVO searchVO = new ComDefaultVO();
    private String searchType;
    private String searchKeyword;
}
