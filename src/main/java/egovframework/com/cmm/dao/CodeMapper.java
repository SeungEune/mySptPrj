package egovframework.com.cmm.dao;

import egovframework.com.cmm.vo.CodeVO;
import egovframework.com.cmm.vo.RoleVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 코드 관련 DB 접근 Mapper
 */
@Repository(value = "codeMapper")
public class CodeMapper extends EgovAbstractMapper {

    /**
     * 공통코드 목록 조회
     * @return 공통코드 목록
     */
    public List<CodeVO> selectCmmnCodeList(String codeClcd) {
        return selectList("codeMapper.selectCmmnCodeList", codeClcd);
    }

    /**
     * 전체 코드ID 목록 조회
     * @return 사용 중인 모든 코드ID 목록
     */
    public List<String> selectAllCodeIdList() {
        return selectList("codeMapper.selectAllCodeIdList");
    }

    /**
     * 권한 목록 조회
     * @return 권한 목록
     */
    public List<RoleVO> selectRoleList() {
        return selectList("codeMapper.selectRoleList");
    }
}
