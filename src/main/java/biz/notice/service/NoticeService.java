package biz.notice.service;

import biz.notice.vo.NoticeVO;
import egovframework.com.cmm.response.ResultVO;

import java.util.List;

/**
 * 공지사항 Service
 */
public interface NoticeService {

    int getNoticeListCnt(NoticeVO noticeVO);

    List<NoticeVO> getNoticeList(NoticeVO noticeVO);

    NoticeVO getNoticeDetail(Long noticeSn, boolean increaseViewCnt);

    ResultVO saveNotice(NoticeVO noticeVO);

    ResultVO updateNotice(NoticeVO noticeVO);

    ResultVO deleteNotice(Long noticeSn);
}
