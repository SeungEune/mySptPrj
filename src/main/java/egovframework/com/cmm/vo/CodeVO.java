package egovframework.com.cmm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * @author : 이승은
 * @since : 2025. 04. 24.
 * <p>
 * 공통 코드 관리 VO
 * == 수정사항 ==
 * ---------------------------------------
 * 2025. 04. 24.  이승은 최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeVO {

    private String codeId;
    private String code;
    private String codeNm;
    private String codeDc;
    private String useAt;
    private String frstRegistPnttm;
}
