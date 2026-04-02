package biz.notice.service.impl;

import biz.notice.dao.NoticeDAO;
import biz.notice.service.NoticeService;
import biz.notice.vo.NoticeVO;
import biz.util.AuthService;
import biz.util.StringUtil;
import egovframework.com.cmm.response.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 공지사항 Service 구현체
 */
@Slf4j
@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {

    @Resource(name = "noticeDAO")
    private NoticeDAO noticeDAO;

    @Resource
    private AuthService authService;

    @Override
    public int getNoticeListCnt(NoticeVO noticeVO) {
        return noticeDAO.selectNoticeListCnt(noticeVO);
    }

    @Override
    public List<NoticeVO> getNoticeList(NoticeVO noticeVO) {
        if (noticeVO.getSearchVO() != null) {
            noticeVO.getSearchVO().calculateOffset();
        }
        return noticeDAO.selectNoticeList(noticeVO);
    }

    @Override
    public NoticeVO getNoticeDetail(Long noticeSn, boolean increaseViewCnt) {
        if (noticeSn == null) {
            return null;
        }
        if (increaseViewCnt) {
            noticeDAO.incrementViewCnt(noticeSn);
        }
        return noticeDAO.selectNoticeDetail(noticeSn);
    }

    @Override
    public ResultVO saveNotice(NoticeVO noticeVO) {
        ResultVO resultVO = new ResultVO();
        try {
            if (!authService.canManageNotice()) {
                resultVO.setResultValue(false);
                resultVO.setMessage("공지사항 등록 권한이 없습니다.");
                return resultVO;
            }

            if (StringUtil.isEmpty(noticeVO.getNoticeTitle())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("제목은 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(noticeVO.getNoticeCn())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("내용은 필수입니다.");
                return resultVO;
            }

            noticeVO.setImportantYn(StringUtil.isEmpty(noticeVO.getImportantYn()) ? "N" : noticeVO.getImportantYn());
            noticeVO.setRegisterId(authService.getCurrentUserId());
            noticeDAO.insertNotice(noticeVO);
            resultVO.setResultValue(true);
            resultVO.setMessage("공지사항이 등록되었습니다.");
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("공지사항 등록 중 오류가 발생했습니다.");
        }
        return resultVO;
    }

    @Override
    public ResultVO updateNotice(NoticeVO noticeVO) {
        ResultVO resultVO = new ResultVO();
        try {
            if (!authService.canManageNotice()) {
                resultVO.setResultValue(false);
                resultVO.setMessage("공지사항 수정 권한이 없습니다.");
                return resultVO;
            }
            if (noticeVO.getNoticeSn() == null) {
                resultVO.setResultValue(false);
                resultVO.setMessage("공지사항 번호는 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(noticeVO.getNoticeTitle())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("제목은 필수입니다.");
                return resultVO;
            }
            if (StringUtil.isEmpty(noticeVO.getNoticeCn())) {
                resultVO.setResultValue(false);
                resultVO.setMessage("내용은 필수입니다.");
                return resultVO;
            }

            noticeVO.setImportantYn(StringUtil.isEmpty(noticeVO.getImportantYn()) ? "N" : noticeVO.getImportantYn());
            noticeVO.setUpdusrId(authService.getCurrentUserId());
            int updated = noticeDAO.updateNotice(noticeVO);
            if (updated <= 0) {
                resultVO.setResultValue(false);
                resultVO.setMessage("존재하지 않는 공지사항입니다.");
                return resultVO;
            }
            resultVO.setResultValue(true);
            resultVO.setMessage("공지사항이 수정되었습니다.");
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("공지사항 수정 중 오류가 발생했습니다.");
        }
        return resultVO;
    }

    @Override
    public ResultVO deleteNotice(Long noticeSn) {
        ResultVO resultVO = new ResultVO();
        try {
            if (!authService.canManageNotice()) {
                resultVO.setResultValue(false);
                resultVO.setMessage("공지사항 삭제 권한이 없습니다.");
                return resultVO;
            }
            if (noticeSn == null) {
                resultVO.setResultValue(false);
                resultVO.setMessage("공지사항 번호는 필수입니다.");
                return resultVO;
            }
            int deleted = noticeDAO.deleteNotice(noticeSn);
            if (deleted <= 0) {
                resultVO.setResultValue(false);
                resultVO.setMessage("존재하지 않는 공지사항입니다.");
                return resultVO;
            }
            resultVO.setResultValue(true);
            resultVO.setMessage("공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생", e);
            resultVO.setResultValue(false);
            resultVO.setMessage("공지사항 삭제 중 오류가 발생했습니다.");
        }
        return resultVO;
    }
}
