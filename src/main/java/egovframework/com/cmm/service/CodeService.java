package egovframework.com.cmm.service;

import egovframework.com.cmm.dao.CodeMapper;
import egovframework.com.cmm.vo.CodeVO;
import biz.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service("codeService")
public class CodeService {

    private static CodeMapper codeMapper;   // 코드 관련 DB 접근 Mapper

    @Autowired
    private CodeService(CodeMapper codeMapper) {
        CodeService.codeMapper = codeMapper;
    }

    /**
     * 공통코드 목록조회
     *
     * @param codeClcd 코드 분류 코드
     * @return 조건이 있을경우 조건에 해당하는 공통코드 목록, 조건이 없을 경우 전체 공통코드 목록
     */
    public List<CodeVO> getCmmnCodeList(String codeClcd) {
        if (StringUtil.isEmpty(codeClcd)) {
            return new ArrayList<>();
        }
        return codeMapper.selectCmmnCodeList(codeClcd);
    }
}
