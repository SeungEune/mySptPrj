package biz.notice.dao;

import biz.notice.vo.NoticeVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공지사항 DAO
 */
@Repository("noticeDAO")
public class NoticeDAO extends EgovAbstractMapper {

    public int selectNoticeListCnt(NoticeVO noticeVO) {
        return selectOne("noticeDAO.selectNoticeListCnt", noticeVO);
    }

    public List<NoticeVO> selectNoticeList(NoticeVO noticeVO) {
        return selectList("noticeDAO.selectNoticeList", noticeVO);
    }

    public NoticeVO selectNoticeDetail(Long noticeSn) {
        return selectOne("noticeDAO.selectNoticeDetail", noticeSn);
    }

    public int insertNotice(NoticeVO noticeVO) {
        return insert("noticeDAO.insertNotice", noticeVO);
    }

    public int updateNotice(NoticeVO noticeVO) {
        return update("noticeDAO.updateNotice", noticeVO);
    }

    public int deleteNotice(Long noticeSn) {
        return update("noticeDAO.deleteNotice", noticeSn);
    }

    public int incrementViewCnt(Long noticeSn) {
        return update("noticeDAO.incrementViewCnt", noticeSn);
    }
}
