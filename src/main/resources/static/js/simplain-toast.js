/**
 * SIMPLAIN MENSWEAR - GLOBAL DIRECT ON-SCREEN TOAST NOTIFICATION SYSTEM
 * Thay thế hoàn toàn alert() mặc định của trình duyệt bằng thông báo trực tiếp trên màn hình.
 * Hỗ trợ các kiểu: success, error, warning, info kèm animation mượt mà, tự động biến mất và thanh tiến trình.
 */

(function () {
    'use strict';

    // 1. Nhúng Style Animation vào document nếu chưa có
    function injectToastStyles() {
        if (document.getElementById('simplain-toast-styles')) return;
        const style = document.createElement('style');
        style.id = 'simplain-toast-styles';
        style.textContent = `
            #simplainToastContainer {
                position: fixed;
                top: 1.25rem;
                right: 1.25rem;
                z-index: 999999;
                display: flex;
                flex-direction: column;
                gap: 0.75rem;
                max-width: 24rem;
                width: calc(100% - 2.5rem);
                pointer-events: none;
            }
            @media (max-width: 640px) {
                #simplainToastContainer {
                    top: 1rem;
                    right: 0.75rem;
                    left: 0.75rem;
                    width: auto;
                    max-width: none;
                }
            }
            .simplain-toast-item {
                pointer-events: auto;
                position: relative;
                display: flex;
                align-items: flex-start;
                gap: 0.75rem;
                padding: 0.875rem 1rem;
                border-radius: 1rem;
                background: rgba(15, 23, 42, 0.95);
                backdrop-filter: blur(12px);
                -webkit-backdrop-filter: blur(12px);
                color: #ffffff;
                box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.25), 0 8px 10px -6px rgba(0, 0, 0, 0.25);
                overflow: hidden;
                border: 1px solid rgba(255, 255, 255, 0.1);
                opacity: 0;
                transform: translateY(-1rem) scale(0.95);
                transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
            }
            .simplain-toast-item.show {
                opacity: 1;
                transform: translateY(0) scale(1);
            }
            .simplain-toast-item.hide {
                opacity: 0;
                transform: translateX(2rem) scale(0.9);
            }
            .simplain-toast-progress {
                position: absolute;
                bottom: 0;
                left: 0;
                height: 3px;
                width: 100%;
                opacity: 0.8;
                transform-origin: left;
                transition: width linear;
            }
            /* Theme màu sắc tương ứng */
            .simplain-toast-success {
                border-color: rgba(16, 185, 129, 0.4);
            }
            .simplain-toast-success .toast-icon-box {
                background: rgba(16, 185, 129, 0.2);
                color: #34d399;
                border: 1px solid rgba(16, 185, 129, 0.3);
            }
            .simplain-toast-success .simplain-toast-progress {
                background: #10b981;
            }

            .simplain-toast-error {
                border-color: rgba(244, 63, 94, 0.45);
            }
            .simplain-toast-error .toast-icon-box {
                background: rgba(244, 63, 94, 0.2);
                color: #fb7185;
                border: 1px solid rgba(244, 63, 94, 0.35);
            }
            .simplain-toast-error .simplain-toast-progress {
                background: #f43f5e;
            }

            .simplain-toast-warning {
                border-color: rgba(245, 158, 11, 0.45);
            }
            .simplain-toast-warning .toast-icon-box {
                background: rgba(245, 158, 11, 0.2);
                color: #fbbf24;
                border: 1px solid rgba(245, 158, 11, 0.35);
            }
            .simplain-toast-warning .simplain-toast-progress {
                background: #f59e0b;
            }

            .simplain-toast-info {
                border-color: rgba(59, 130, 246, 0.4);
            }
            .simplain-toast-info .toast-icon-box {
                background: rgba(59, 130, 246, 0.2);
                color: #60a5fa;
                border: 1px solid rgba(59, 130, 246, 0.3);
            }
            .simplain-toast-info .simplain-toast-progress {
                background: #3b82f6;
            }
        `;
        document.head.appendChild(style);
    }

    // 2. Lấy hoặc tạo Toast Container
    function getToastContainer() {
        let container = document.getElementById('simplainToastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'simplainToastContainer';
            document.body.appendChild(container);
        }
        return container;
    }

    // Cấu hình nhãn & biểu tượng theo từng loại thông báo
    const TOAST_CONFIG = {
        success: {
            title: 'Thành công',
            icon: 'fa-solid fa-circle-check',
            className: 'simplain-toast-success'
        },
        error: {
            title: 'Thông báo lỗi',
            icon: 'fa-solid fa-circle-exclamation',
            className: 'simplain-toast-error'
        },
        warning: {
            title: 'Lưu ý',
            icon: 'fa-solid fa-triangle-exclamation',
            className: 'simplain-toast-warning'
        },
        info: {
            title: 'Thông báo',
            icon: 'fa-solid fa-circle-info',
            className: 'simplain-toast-info'
        }
    };

    /**
     * Hiển thị Toast thông báo trực tiếp lên màn hình
     * @param {string} message - Nội dung thông báo
     * @param {'success'|'error'|'warning'|'info'} type - Loại thông báo
     * @param {number} duration - Thời gian hiển thị tính bằng mili-giây (mặc định 4000ms)
     * @param {string|null} customTitle - Tiêu đề tùy chỉnh
     */
    function showToast(message, type = 'info', duration = 4000, customTitle = null) {
        if (!message) return;

        injectToastStyles();
        const container = getToastContainer();

        const safeType = TOAST_CONFIG[type] ? type : 'info';
        const cfg = TOAST_CONFIG[safeType];
        const title = customTitle || cfg.title;

        const toast = document.createElement('div');
        toast.className = `simplain-toast-item ${cfg.className}`;
        toast.setAttribute('role', 'alert');

        toast.innerHTML = `
            <div class="toast-icon-box w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 text-sm shadow-xs">
                <i class="${cfg.icon}"></i>
            </div>
            <div class="flex-grow min-w-0 pr-1">
                <div class="text-[11px] font-black tracking-wider uppercase opacity-90 mb-0.5 text-slate-300">
                    ${title}
                </div>
                <div class="text-xs text-white leading-relaxed font-medium break-words">
                    ${message}
                </div>
            </div>
            <button type="button" aria-label="Đóng" class="text-slate-400 hover:text-white p-1 rounded-lg transition-colors flex-shrink-0">
                <i class="fa-solid fa-xmark text-sm"></i>
            </button>
            <div class="simplain-toast-progress"></div>
        `;

        const closeBtn = toast.querySelector('button');
        const progressBar = toast.querySelector('.simplain-toast-progress');

        let isDismissed = false;
        let timer = null;
        let startTime = Date.now();
        let remaining = duration;

        function dismiss() {
            if (isDismissed) return;
            isDismissed = true;
            if (timer) clearTimeout(timer);
            toast.classList.remove('show');
            toast.classList.add('hide');
            setTimeout(() => {
                toast.remove();
            }, 360);
        }

        closeBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            dismiss();
        });

        // Tự động đóng sau duration
        function startTimer() {
            if (duration <= 0) return;
            startTime = Date.now();
            if (progressBar) {
                progressBar.style.transition = `width ${remaining}ms linear`;
                progressBar.style.width = '0%';
            }
            timer = setTimeout(dismiss, remaining);
        }

        function pauseTimer() {
            if (duration <= 0 || isDismissed) return;
            clearTimeout(timer);
            remaining -= (Date.now() - startTime);
            if (remaining < 500) remaining = 500;
            if (progressBar) {
                const currentWidth = (remaining / duration) * 100;
                progressBar.style.transition = 'none';
                progressBar.style.width = `${currentWidth}%`;
            }
        }

        // Tạm dừng khi di chuột vào thông báo để người dùng kịp đọc
        toast.addEventListener('mouseenter', pauseTimer);
        toast.addEventListener('mouseleave', startTimer);

        container.appendChild(toast);

        // Kích hoạt animation xuất hiện
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                toast.classList.add('show');
                startTimer();
            });
        });
    }

    // Các hàm helper tiện dụng
    function toastSuccess(msg, customTitle, duration) {
        showToast(msg, 'success', duration || 3500, customTitle);
    }
    function toastError(msg, customTitle, duration) {
        showToast(msg, 'error', duration || 5000, customTitle);
    }
    function toastWarning(msg, customTitle, duration) {
        showToast(msg, 'warning', duration || 4500, customTitle);
    }
    function toastInfo(msg, customTitle, duration) {
        showToast(msg, 'info', duration || 4000, customTitle);
    }

    // Gắn vào window toàn cục
    window.showToast = showToast;
    window.toastSuccess = toastSuccess;
    window.toastError = toastError;
    window.toastWarning = toastWarning;
    window.toastInfo = toastInfo;

    // Tương thích ngược với các tên hàm cũ trong template
    window.hienThiToast = showToast;
    window.hienThiToastStorefront = showToast;
    window.hienThiToastChiTiet = showToast;

    // =========================================================================
    // 3. THAY THẾ TOÀN DIỆN alert() MẶC ĐỊNH BẰNG THÔNG BÁO TRỰC TIẾP LÊN MÀN HÌNH
    // =========================================================================
    const originalAlert = window.alert;
    window.simplainOriginalAlert = originalAlert;

    window.alert = function (message) {
        if (message === undefined || message === null) return;
        const text = String(message).trim();
        if (!text) return;

        const lower = text.toLowerCase();
        let type = 'warning';
        let customTitle = 'Lưu ý';

        if (
            lower.includes('thành công') ||
            lower.includes('đã thêm') ||
            lower.includes('đã lưu') ||
            lower.includes('đã sao chép') ||
            lower.includes('hoàn tất')
        ) {
            type = 'success';
            customTitle = 'Thành công';
        } else if (
            lower.includes('lỗi') ||
            lower.includes('thất bại') ||
            lower.includes('không thể') ||
            lower.includes('rất tiếc') ||
            lower.includes('vượt quá') ||
            lower.includes('hết hàng') ||
            lower.includes('từ chối')
        ) {
            type = 'error';
            customTitle = 'Lỗi thao tác';
        } else if (
            lower.includes('vui lòng') ||
            lower.includes('chú ý') ||
            lower.includes('chỉ cho phép') ||
            lower.includes('hãy chọn')
        ) {
            type = 'warning';
            customTitle = 'Nhắc nhở';
        }

        showToast(text, type, 4500, customTitle);
    };

    // Tự động kiểm tra thông báo flash từ server (nếu có thẻ alert trên màn hình)
    document.addEventListener('DOMContentLoaded', function () {
        injectToastStyles();
        // Quét thông báo flash server nếu có dữ liệu data-toast
        document.querySelectorAll('[data-toast-message]').forEach(el => {
            const msg = el.getAttribute('data-toast-message');
            const type = el.getAttribute('data-toast-type') || 'info';
            if (msg) showToast(msg, type);
        });
    });

})();
