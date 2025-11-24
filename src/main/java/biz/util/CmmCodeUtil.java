package biz.util;

import egovframework.com.cmm.service.CodeService;
import egovframework.com.cmm.vo.CmmCdNmVO;
import egovframework.com.cmm.vo.CodeVO;
import egovframework.com.cmm.vo.RoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class CmmCodeUtil {

    private final static Map<String, List<CodeVO>> codeMap = new HashMap<>();

    private static CodeService codeService;

    @Autowired
    private CodeService service;

    @PostConstruct
    public void init() {
        codeService = service;

        // 시스템 시작 시 모든 공통 코드를 미리 로드
        List<String> codeIdList = codeService.getAllCodeIdList();
        if (!ListUtil.isEmpty(codeIdList)) {
            for (String codeId : codeIdList) {
                List<CodeVO> codeList = codeService.getCmmnCodeList(codeId);
                if (!ListUtil.isEmpty(codeList)) {
                    codeMap.put(codeId, codeList);
                }
            }
        }
    }

    private static List<CodeVO> getCodeList(String codeId) {
        // 키가 없을 때 null 반환 (빈 리스트 대신)
        return codeMap.get(codeId);
    }

    public static List<CodeVO> getCmmnCodeList(String codeId) {
        List<CodeVO> codeList = getCodeList(codeId);
        // null 체크로 변경 (키가 없거나 빈 리스트인 경우)
        if (codeList == null || ListUtil.isEmpty(codeList)) {
            codeList = codeService.getCmmnCodeList(codeId);
            if (!ListUtil.isEmpty(codeList)) {
                CmmCodeUtil.setCmmnCode(codeId, codeList);
            }
        }
        return codeList != null ? codeList : new ArrayList<>();
    }

    public static Map<String, List<CodeVO>> getCmmnDetailCode(List<CmmCdNmVO> cdNmList) {
        Map<String, List<CodeVO>> map = new HashMap<>();

        if (!ListUtil.isEmpty(cdNmList)) {
            for (CmmCdNmVO cdNm : cdNmList) {
                List<CodeVO> codeList = getCodeList(cdNm.getCodeId());
                // null 체크로 변경
                if (codeList == null || ListUtil.isEmpty(codeList)) {
                    codeList = codeService.getCmmnCodeList(cdNm.getCodeId());
                    if (!ListUtil.isEmpty(codeList)) {
                        CmmCodeUtil.setCmmnCode(cdNm.getCodeId(), codeList);
                    }
                }
                map.put(cdNm.getCodeNm(), codeList != null ? codeList : new ArrayList<>());
            }
        }

        return map;
    }

    public static String getCodeNm(String codeId, String code) {
        if (StringUtil.chkNull(codeId) || StringUtil.chkNull(code)) {
            return StringUtil.STR_EMPTY;
        }

        List<CodeVO> codeList = getCodeList(codeId);
        // null 체크로 변경
        if (codeList == null || ListUtil.isEmpty(codeList)) {
            codeList = codeService.getCmmnCodeList(codeId);
            if (!ListUtil.isEmpty(codeList)) {
                CmmCodeUtil.setCmmnCode(codeId, codeList);
            }
        }

        if (codeList == null || ListUtil.isEmpty(codeList)) {
            return StringUtil.STR_EMPTY;
        }

        Optional<CodeVO> codeVO = codeList.stream().findFirst().filter(curCode -> curCode.getCode().equals(code));
        return codeVO.isPresent() ? codeVO.get().getCodeNm() : StringUtil.STR_EMPTY;
    }

    private static void setCmmnCode(String codeId, List<CodeVO> codeList) {
        codeMap.put(codeId, codeList);
    }

    public static void refreshCodeList(String codeId) {
        codeMap.remove(codeId);
        List<CodeVO> codeList = codeService.getCmmnCodeList(codeId);
        CmmCodeUtil.setCmmnCode(codeId, codeList);
    }

    /**
     * 권한 목록 조회
     * @return 권한 목록
     */
    public static List<RoleVO> getRoleList() {
        return codeService.getRoleList();
    }
}
