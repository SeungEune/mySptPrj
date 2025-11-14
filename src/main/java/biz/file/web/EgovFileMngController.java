package biz.file.web;

import biz.login.vo.LoginVO;
import biz.file.service.FileMngService;
import biz.file.vo.FileVO;
import biz.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 파일 관리를 위한 컨트롤러 클래스
 * @author 공통서비스 개발팀
 * @since 2024.12.19
 * @version 1.0
 */
@Slf4j
@Controller
public class EgovFileMngController {

    @Resource(name = "FileMngService")
    private FileMngService fileService;

    /**
     * 파일 목록 조회
     * @param fileVO 파일 검색 조건
     * @param request HttpServletRequest
     * @param model Model 객체
     * @return 파일 목록 페이지 또는 로그인 페이지
     */
    @GetMapping("/cmm/fms/selectFileInfs.do")
    public String selectFileInfs(@ModelAttribute FileVO fileVO, HttpServletRequest request, Model model) {
        // SessionUtil을 사용한 로그인 정보 확인
        LoginVO loginVO = SessionUtil.getLoginUser();
        
        if (loginVO == null) {
            log.warn("인증되지 않은 사용자의 파일 목록 접근 시도");
            return "redirect:/login/loginForm.do";
        }

        try {
            // 파일 목록 조회
            List<FileVO> fileList = fileService.selectFileInfs(fileVO);
            model.addAttribute("fileList", fileList);
            model.addAttribute("loginVO", loginVO);
            
            log.info("파일 목록 조회: {}건", fileList.size());
            return "cmm/file/fileList";
            
        } catch (Exception e) {
            log.error("파일 목록 조회 중 오류 발생", e);
            model.addAttribute("error", "파일 목록을 불러오는 중 오류가 발생했습니다.");
            return "cmm/file/fileList";
        }
    }

    /**
     * 파일 상세 정보 조회
     * @param atchFileId 첨부파일 ID
     * @param fileSn 파일 순번
     * @param request HttpServletRequest
     * @param model Model 객체
     * @return 파일 상세 페이지 또는 로그인 페이지
     */
    @GetMapping("/cmm/fms/selectFileInf.do")
    public String selectFileInf(@RequestParam String atchFileId, 
                               @RequestParam String fileSn, 
                               HttpServletRequest request, 
                               Model model) {
        // SessionUtil을 사용한 로그인 정보 확인
        LoginVO loginVO = SessionUtil.getLoginUser();
        
        if (loginVO == null) {
            log.warn("인증되지 않은 사용자의 파일 상세 접근 시도");
            return "redirect:/login/loginForm.do";
        }

        try {
            FileVO fileVO = new FileVO();
            fileVO.setAtchFileId(atchFileId);
            fileVO.setFileSn(fileSn);
            
            FileVO resultVO = fileService.selectFileInf(fileVO);
            model.addAttribute("fileVO", resultVO);
            model.addAttribute("loginVO", loginVO);
            
            log.info("파일 상세 조회: {}", atchFileId);
            return "cmm/file/fileDetail";
            
        } catch (Exception e) {
            log.error("파일 상세 조회 중 오류 발생", e);
            model.addAttribute("error", "파일 정보를 불러오는 중 오류가 발생했습니다.");
            return "cmm/file/fileDetail";
        }
    }
}

