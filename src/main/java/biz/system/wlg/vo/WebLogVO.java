package biz.system.wlg.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 웹로그 VO
 */
@Getter
@Setter
public class WebLogVO {

    private Long demandSn;
    private String url;
    private String rqesterId;
    private String rqesterIp;
    private String ocrnDt;
    private Integer rowNum;

    private String searchType;
    private String searchKeyword;
    private String searchBgnDe;
    private String searchEndDe;

    private ComDefaultVO searchVO = new ComDefaultVO();
}
