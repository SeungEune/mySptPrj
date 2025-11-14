package biz.util;

import egovframework.com.cmm.service.CodeService;
import egovframework.com.cmm.vo.CmmCdNmVO;
import egovframework.com.cmm.vo.CodeVO;
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
    }

    private static List<CodeVO> getCodeList(String codeId) {
        return codeMap.getOrDefault(codeId, new ArrayList<>());
    }

    public static List<CodeVO> getCmmnCodeList(String codeId) {
        List<CodeVO> codeList = getCodeList(codeId);
        if (ListUtil.isEmpty(codeList)) {
            codeList = codeService.getCmmnCodeList(codeId);
            CmmCodeUtil.setCmmnCode(codeId, codeList);
        }
        return codeList;
    }

    public static Map<String, List<CodeVO>> getCmmnDetailCode(List<CmmCdNmVO> cdNmList) {
        Map<String, List<CodeVO>> map = new HashMap<>();

        if (!ListUtil.isEmpty(cdNmList)) {
            for (CmmCdNmVO cdNm : cdNmList) {
                List<CodeVO> codeList = getCodeList(cdNm.getCodeId());
                if (ListUtil.isEmpty(codeList)) {
                    codeList = codeService.getCmmnCodeList(cdNm.getCodeId());
                    CmmCodeUtil.setCmmnCode(cdNm.getCodeId(), codeList);
                }
                map.put(cdNm.getCodeNm(), codeList);
            }
        }

        return map;
    }

    public static String getCodeNm(String codeId, String code) {
        if (StringUtil.chkNull(codeId) || StringUtil.chkNull(code)) {
            return StringUtil.STR_EMPTY;
        }

        List<CodeVO> codeList = getCodeList(codeId);
        if (ListUtil.isEmpty(codeList)) {
            codeList = codeService.getCmmnCodeList(codeId);
            CmmCodeUtil.setCmmnCode(codeId, codeList);
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
}
