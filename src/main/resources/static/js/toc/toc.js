/**
 * TOC (Table of Contents) 아코디언 메뉴 JavaScript
 */

(function() {
    'use strict';

    // DOM 로드 완료 후 실행
    document.addEventListener('DOMContentLoaded', function() {
        initTocAccordion();
    });

    /**
     * TOC 아코디언 초기화 (이벤트 위임 방식)
     */
    function initTocAccordion() {
        // sidebar-menu에 이벤트 위임
        const sidebarMenu = document.querySelector('.sidebar-menu');
        
        if (!sidebarMenu) {
            return;
        }
        
        sidebarMenu.addEventListener('click', function(e) {
            // .toc-menu-item 클래스를 가진 요소만 처리
            const menuItem = e.target.closest('.toc-menu-item');
            
            if (!menuItem) {
                return;
            }
            
            // 하위 메뉴가 있는지 확인
            if (!menuItem.classList.contains('has-submenu')) {
                return;
            }
            
            e.preventDefault();
            e.stopPropagation();
            
            // 같은 wrapper에서 하위 메뉴 리스트 찾기
            const wrapper = menuItem.parentElement;
            const submenuList = wrapper.querySelector('.submenu-list');
            
            if (!submenuList) {
                return;
            }
            
            // 현재 클릭된 메뉴의 아코디언 상태 확인
            const isOpen = submenuList.classList.contains('open');
            
            // 현재 메뉴의 아코디언 토글
            if (isOpen) {
                submenuList.classList.remove('open');
                menuItem.classList.remove('active');
            } else {
                submenuList.classList.add('open');
                menuItem.classList.add('active');
            }
        });
    }

})();


