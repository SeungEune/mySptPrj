class MessageUtil {
    static alert(message, callback) {
        Swal.fire({
            html: message,
            icon: 'info',
            allowOutsideClick: false,
            didDestroy: () => {
                if (callback && callback instanceof Function) {
                    callback();
                }
            }
        });
    };
    
    static confirm(message, callback, confirmText, cancelText) {
        Swal.fire({
            title: message,
            icon: 'question',
            allowOutsideClick: false,
            showCancelButton: true,
            confirmButtonText: confirmText ? confirmText : '확인',
            cancelButtonText: cancelText ? cancelText : '취소',
            customClass: {
                popup: 'dr-customPopup max420'
            },
        }).then((result) => {
            if (callback && callback instanceof Function) {
                callback(result.value === true ? true : false);
            }
        });
    };

    static confirmed(message, callback, confirmText, cancelText, subMessage) {
        Swal.fire({
            title: message,
            html: subMessage,
            icon: 'question',
            allowOutsideClick: false,
            showCancelButton: true,
            confirmButtonText: confirmText ? confirmText : '확인',
            cancelButtonText: cancelText ? cancelText : '취소',
            customClass: {
                popup: 'dr-customPopup max420'
            },
        }).then(result => {
            if(result.isConfirmed){
                if (callback && callback instanceof Function) {
                    callback();
                }
            }
        });
    };

    static titleConfirm(title,message, callback, confirmText, cancelText) {
        Swal.fire({
            title : title,
            html: message,
            icon: 'question',
            allowOutsideClick: false,
            showCancelButton: true,
            confirmButtonText: confirmText ? confirmText : '확인',
            cancelButtonText: cancelText ? cancelText : '취소'
        }).then((result) => {
            if(result.isConfirmed){
                if (callback && callback instanceof Function) {
                    callback();
                }
            }
        });
    };
    
    static confirmPromise(message, confirmText, cancelText, subMessage) {
        return new Promise((resolve, reject) => {
            Swal.fire({
                title: message,
                html: subMessage,
                icon: 'question',
                allowOutsideClick: false,
                showCancelButton: true,
                confirmButtonText: confirmText ? confirmText : '확인',
                cancelButtonText: cancelText ? cancelText : '취소',
                customClass: {
                    popup: 'dr-customPopup max420'
                },
            }).then((result) => {
                if (result.isConfirmed) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            });
        });
    };
    
    static success(message, callback, callbackValue) {
        Swal.fire({
            title: message,
            icon: 'success',
            allowOutsideClick: false,
            customClass: {
                popup: 'dr-customPopup max420'
            },
        }).then((result) => {
            if(result.isConfirmed){
                if (callback && callback instanceof Function) {
                    callback(callbackValue);
                }
            }
        });
    };
    
    static error(message, callback) {
        Swal.fire({
            title: message,
            icon: 'error',
            allowOutsideClick: false,
            customClass: {
                popup: 'dr-customPopup max420'
            },
        }).then((result) => {
            if (callback && callback instanceof Function) {
                callback();
            }
        });
    };

    static warning(message, callback, html) {
        Swal.fire({
            title: message,
            html : html,
            icon: 'warning',
            allowOutsideClick: false,
            customClass: {
                popup: 'dr-customPopup max420'
            },
        }).then(() => {
            if (callback && callback instanceof Function) {
                callback();
            }
        });
    };

    
    static toast(message, timer) {
        Swal.fire({
            html: message,
            toast: true,
            position: 'center',
            showConfirmButton: false,
            timer: (timer??1500),
            timerProgressBar: true,
            customClass: {
                container: 'swal2-toast-container',
                popup: 'swal2-toast-popup',
                htmlContainer: 'swal2-toast-html-container',
                timerProgressBar: 'swal2-toast-progress'
            },
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
                toast.addEventListener('click', Swal.closeToast);
            }
        });
    };
};
