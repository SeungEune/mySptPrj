package biz.system.user.vo;

import egovframework.com.cmm.vo.ComDefaultVO;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보 VO
 * @author Spatialt 개발팀
 * @since 2025.11.24
 * @version 1.0
 */
@Getter
@Setter
public class UserVO {
    
    // 기본 정보
    private String userId;           // 사용자ID (PK)
    private String userNm;           // 사용자명
    private String userPassword;     // 비밀번호
    private String passwordConfirm;  // 비밀번호 확인 (등록/수정 시 검증용)
    private String emailAdres;       // 이메일주소
    private String moblphonNo;       // 휴대전화번호
    private String deptCd;           // 부서코드
    private String deptNm;           // 부서명 (조인용)
    private String userSttusCd;      // 사용자상태코드
    private String userSttusCdNm;    // 사용자상태코드명 (조인용)
    private String sexdstnCd;        // 성별코드
    private String brthdy;           // 생년월일
    private String jbgdCd;           // 직급코드
    private String jssfcCd;          // 직책코드
    private String joinDe;           // 입사일자
    private String roleCd;           // 권한코드 (등록/수정 시 사용)
    private String roleNm;           // 권한명 (조인용)
    
    // 시스템 정보
    private String passwordChangeDe; // 비밀번호변경일자
    private String lastLoginDt;      // 최종로그인일시
    private Integer loginFailCo;     // 로그인실패횟수
    private String acntLockYn;       // 계정잠금여부
    private String useYn;            // 사용여부
    private String registerId;       // 등록자ID
    private String registDt;         // 등록일시
    private String updusrId;         // 수정자ID
    private String updtDt;           // 수정일시
    
    // 목록 조회용 필드
    private Integer rowNum;          // 행 번호
    private ComDefaultVO searchVO = new ComDefaultVO(); // 검색 조건
    private String status;           // 상태 필터 (검색용)
}

