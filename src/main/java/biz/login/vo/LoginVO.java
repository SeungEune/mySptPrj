package biz.login.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.validation.constraints.Email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Class Name : LoginVO.java
 * @Description : Login VO class
 * @Modification Information
 * @
 * @  수정일         수정자                   수정내용
 * @ -------    --------    ---------------------------
 * @ 2024.12.20    시스템          신규 DB 스키마 적용
 *
 *  @author 시스템
 *  @since 2024.12.20
 *  @version 1.0
 *  @see
 *  
 */
@Getter
@Setter
public class LoginVO implements Serializable{
	
	private static final long serialVersionUID = -8274004534207618049L;
	
	// 사용자ID
	private String userId;
	
	// 사용자명
	private String userNm;
	
	// 비밀번호 (폼에서 받는 필드)
	private String password;
	
	// 비밀번호 (DB 저장 필드)
	private String userPassword;
	
	// 이메일주소
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
	private String emailAdres;
	
	// 휴대전화번호
	private String moblphonNo;
	
	// 부서코드
	private String deptCd;
	
	// 부서명
	private String deptNm;
	
	// 사용자상태코드 (NORMAL:정상, REST:휴직, QUIT:퇴사, STOP:정지)
	private String userSttusCd;
	
	// 성별구분코드
	private String sexdstnCd;
	
	// 생년월일
	private Date brthdy;
	
	// 직급코드
	private String jbgdCd;
	
	// 직책코드
	private String jssfcCd;
	
	// 입사일자
	private Date joinDe;
	
	// 비밀번호변경일자
	private Date passwordChangeDe;
	
	// 최종로그인일시
	private Timestamp lastLoginDt;
	
	// 로그인실패횟수
	private Integer loginFailCo;
	
	// 계정잠금여부
	private String acntLockYn;
	
	// 사용여부
	private String useYn;
	
	// 등록자ID
	private String registerId;
	
	// 등록일시
	private Timestamp registDt;
	
	// 수정자ID
	private String updusrId;
	
	// 수정일시
	private Timestamp updtDt;
	
	// 역할코드 (여러 역할 보유 가능)
	private String roleCd;
	
	// 역할명
	private String roleNm;
	
	// 로그인 후 이동할 페이지
	private String url;
	
	// 사용자 IP정보
	private String ip;
	
	// 로그인 실패 사유
	private String loginFailResn;
	
	// 로그인 성공 여부
	private String loginSuccessYn;
	
	// 세션ID
	private String sessionId;
}
