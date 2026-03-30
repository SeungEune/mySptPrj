$(function () {

  function slideDownFlex($el, duration = 200) {
    $el.css({ display: 'flex', height: 'auto' });

    const height = $el.outerHeight();

    $el.css({ height: 0 }).stop(true, true).animate({ height }, duration);
  }

  function slideUpFlex($el, duration = 200) {
    $el.stop(true, true).animate({ height: 0 }, duration, function () {
      $el.css({ display: 'none', height: '' });
    });
  }

  $('.dep-1-menu, .dep-2-menu > li').removeClass('open');
  clearActive();
  setActiveByUrl();

  $(document).on('click', '.dep-1-item', function (e) {
    e.preventDefault();

    const $dep1 = $(this).closest('.dep-1-menu');
    const $dep2Menu = $dep1.children('.dep-2-menu');

    if (!$dep1.hasClass('has-submenu') || $dep2Menu.length === 0) {
      clearActive();
      $dep1.addClass('active');
      return;
    }

    if ($dep1.hasClass('open')) {
      slideUpFlex($dep2Menu);
      $dep1.removeClass('open');
      $dep1.find('.open').removeClass('open');
    } else {
      slideDownFlex($dep2Menu);
      $dep1.addClass('open');
    }
  });

  $(document).on('click', '.dep-2-item', function (e) {
    const $dep2Li = $(this).closest('.dep-2-menu > li');
    const $dep3Menu = $dep2Li.children('.dep-3-menu');

    if ($dep2Li.hasClass('has-submenu') && $dep3Menu.length > 0) {
      e.preventDefault();

      if ($dep2Li.hasClass('open')) {
        slideUpFlex($dep3Menu);
        $dep2Li.removeClass('open');
        $dep2Li.find('.active').removeClass('active');
      } else {
        slideDownFlex($dep3Menu);
        $dep2Li.addClass('open');
      }
      return;
    }

    clearActive();
    $dep2Li.addClass('active');
    $(this).addClass('active');
    $dep2Li.closest('.dep-1-menu').addClass('active open');
  });

  $(document).on('click', '.dep-3-item', function (e) {
    e.preventDefault();

    clearActive();

    const $dep3 = $(this);
    const $dep2Li = $dep3.closest('.dep-2-menu > li');
    const $dep1 = $dep3.closest('.dep-1-menu');

    $dep3.addClass('active');
    $dep2Li.addClass('active open');
    $dep1.addClass('active open');
  });

  function clearActive() {
    $('.dep-1-menu').removeClass('active');
    $('.dep-2-menu > li').removeClass('active');
    $('.dep-2-item').removeClass('active');
    $('.dep-3-item').removeClass('active');
  }

  function getPathDir(pathname) {
    const lastSlash = pathname.lastIndexOf('/');
    return lastSlash >= 0 ? pathname.substring(0, lastSlash + 1) : '/';
  }

  function setActiveByUrl() {
    const currentPath = window.location.pathname;
    const currentFull = window.location.pathname + window.location.search;

    let $activeLink = null;

    $('.dep-2-item, .dep-3-item').each(function () {
      const href = this.getAttribute('href');
      if (!href || href === '#') {
        return;
      }

      const url = new URL(href, window.location.origin);
      const targetFull = url.pathname + url.search;

      if (currentFull === targetFull || currentPath === url.pathname) {
        $activeLink = $(this);
        return false;
      }
    });

    if (!$activeLink) {
      const currentPathDir = getPathDir(currentPath);
      $('.dep-2-item').each(function () {
        const href = this.getAttribute('href');
        if (!href || href === '#') {
          return;
        }

        const url = new URL(href, window.location.origin);
        const menuPathDir = getPathDir(url.pathname);
        if (currentPathDir === menuPathDir) {
          $activeLink = $(this);
          return false;
        }
      });
    }

    if (!$activeLink) return;

    if ($activeLink.hasClass('dep-3-item')) {
      const $dep2Li = $activeLink.closest('.dep-2-menu > li');
      const $dep1 = $activeLink.closest('.dep-1-menu');

      $dep1.children('.dep-2-menu').css('display', 'flex');
      $dep2Li.children('.dep-3-menu').css('display', 'flex');

      $activeLink.addClass('active');
      $dep2Li.addClass('active open');
      $dep1.addClass('active open');
      return;
    }

    const $dep2Li = $activeLink.closest('.dep-2-menu > li');
    $dep2Li.addClass('active');
    $activeLink.addClass('active');
    $dep2Li.closest('.dep-1-menu').addClass('active open').children('.dep-2-menu').css('display', 'flex');
  }

});
