package biz.system.codemngr.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 코드 상세값(소분류) 정보 VO
 * tb_cmmn_code_detail 테이블 매핑
 * @author Spatialt 개발팀
 * @since 2025.12.25
 * @version 1.0
 */
@Getter
@Setter
public class CodeDetailVO {

    /** 코드 그룹 ID (FK) */
    private String codeId;

    /** 상세 코드값 (PK 일부) */
    private String code;

    /** 코드명 */
    private String codeNm;

    /** 코드 설명 */
    private String codeDc;

    /** 출력순서 */
    private Integer codeOrder;

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
