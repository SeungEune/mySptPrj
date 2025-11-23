/**
 * 대시보드 JavaScript
 * 공지사항 위젯 및 기타 대시보드 기능
 */

(function() {
    'use strict';

    // DOM 로드 완료 후 실행
    document.addEventListener('DOMContentLoaded', function() {
        initNoticeWidget();
    });
    
    /**
     * 공지사항 위젯 초기화
     */
    function initNoticeWidget() {
        const noticeList = document.getElementById('noticeList');
        
        if (!noticeList) {
            return;
        }

        // 공지사항 목록 조회 (최대 5건)
        loadNoticeList();
    }

    /**
     * 공지사항 목록 조회
     */
    function loadNoticeList() {
        if (!document.getElementById('noticeList')) {
            return;
        }
        
        // TODO: 백엔드 API 연동 시 주석 해제
        /*
        fetch('/api/notice/recent')
            .then(response => response.json())
            .then(data => {
                renderNoticeList(data);
            })
            .catch(error => {
                console.error('공지사항 조회 실패:', error);
                renderNoticeEmpty();
            });
        */
        
        // 임시로 빈 상태 표시
        setTimeout(() => {
            renderNoticeEmpty();
        }, 500);
    }

    /**
     * 공지사항 목록 렌더링
     */
    function renderNoticeList(notices) {
        const noticeList = document.getElementById('noticeList');
        
        if (!noticeList) {
            return;
        }

        if (!Array.isArray(notices) || notices.length === 0) {
            renderNoticeEmpty();
            return;
        }

        const html = notices.map(notice => {
            const badgeClass = getNoticeBadgeClass(notice.noticeTyCd);
            const formattedDate = formatDate(notice.registDt);
            const noticeUrl = `/notice/noticeDetail.do?noticeSn=${notice.noticeSn}`;
            
            return `
                <tr class="cur-point" onclick="location.href='${noticeUrl}'">
                    <td class="tc">
                        <span class="chip ${badgeClass}">${getNoticeTypeName(notice.noticeTyCd)}</span>
                    </td>
                    <td>${escapeHtml(notice.noticeTitle)}</td>
                    <td class="tc">${formattedDate}</td>
                </tr>
            `;
        }).join('');

        noticeList.innerHTML = html;
    }

    /**
     * 빈 상태 렌더링
     */
    function renderNoticeEmpty() {
        const noticeList = document.getElementById('noticeList');
        if (!noticeList) {
            return;
        }
        noticeList.innerHTML = `
            <tr>
                <td class="tc" colspan="3">등록된 공지사항이 없습니다.</td>
            </tr>
        `;
    }

    /**
     * 공지사항 유형에 따른 배지 클래스 반환
     */
    function getNoticeBadgeClass(type) {
        switch(type) {
            case 'URGENT':
                return 'error';
            case 'IMPORTANT':
                return 'point';
            default:
                return 'primary';
        }
    }

    /**
     * 공지사항 유형명 반환
     */
    function getNoticeTypeName(type) {
        switch(type) {
            case 'NORMAL':
                return '일반';
            case 'IMPORTANT':
                return '중요';
            case 'URGENT':
                return '긴급';
            default:
                return '일반';
        }
    }

    /**
     * 날짜 포맷팅
     */
    function formatDate(dateString) {
        if (!dateString) return '';
        
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

        if (diffDays === 0) {
            return '오늘';
        } else if (diffDays === 1) {
            return '어제';
        } else if (diffDays < 7) {
            return `${diffDays}일 전`;
        } else {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}.${month}.${day}`;
        }
    }

    /**
     * HTML 이스케이프 처리
     */
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * 에러 처리
     */
    function handleError(error) {
        console.error('Dashboard error:', error);
        const noticeList = document.getElementById('noticeList');
        if (noticeList) {
            noticeList.innerHTML = `
                <tr>
                    <td class="tc" colspan="3">공지사항을 불러올 수 없습니다.</td>
                </tr>
            `;
        }
    }

    // 전역으로 내보내기 (필요시)
    window.dashboard = {
        loadNoticeList: loadNoticeList,
        renderNoticeList: renderNoticeList
    };

})();
