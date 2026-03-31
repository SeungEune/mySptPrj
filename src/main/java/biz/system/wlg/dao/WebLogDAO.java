package biz.system.wlg.dao;

import biz.system.wlg.vo.WebLogVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 웹로그 DAO
 */
@Repository("webLogDAO")
public class WebLogDAO extends EgovAbstractMapper {

    public int insertWebLog(WebLogVO webLogVO) {
        return insert("webLogDAO.insertWebLog", webLogVO);
    }

    public List<WebLogVO> selectWebLogList(WebLogVO webLogVO) {
        return selectList("webLogDAO.selectWebLogList", webLogVO);
    }

    public int selectWebLogListCount(WebLogVO webLogVO) {
        return selectOne("webLogDAO.selectWebLogListCount", webLogVO);
    }
}
