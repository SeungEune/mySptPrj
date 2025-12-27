package biz.system.codemngr.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 코드 그룹(대분류) 정보 VO
 * tb_cmmn_code 테이블 매핑
 * @author Spatialt 개발팀
 * @since 2025.01.XX
 * @version 1.0
 */
@Getter
@Setter
public class CodeGroupVO {

    /** 코드 그룹 ID (PK) */
    private String codeId;

    /** 코드 그룹명 */
    private String codeIdNm;

    /** 코드 그룹 설명 */
    private String codeDc;

    /** 사용여부 */
    private String useYn;

    /** 등록자ID */
    private String registerId;

    /** 등록일시 */
    private String registDt;

    /** 수정자ID */
    private String updusrId;

    /** 수정일시 */
    private String updtDt;
}
