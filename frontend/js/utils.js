/* Shared utility functions */
window.Utils = (function() {

    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        const pad = n => String(n).padStart(2, '0');
        return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    }

    function formatSize(bytes) {
        if (!bytes || bytes === 0) return '0 B';
        const units = ['B', 'KB', 'MB', 'GB'];
        let i = 0;
        let size = bytes;
        while (size >= 1024 && i < units.length - 1) { size /= 1024; i++; }
        return size.toFixed(i === 0 ? 0 : 1) + ' ' + units[i];
    }

    function showMessage(msg, type) {
        type = type || 'info';
        if (window.ElementUI) {
            ElementUI.Message({ message: msg, type: type, duration: 3000 });
        } else {
            alert(msg);
        }
    }

    function showSuccess(msg) { showMessage(msg, 'success'); }
    function showError(msg) { showMessage(msg, 'error'); }
    function showWarning(msg) { showMessage(msg, 'warning'); }

    function confirm(msg, title) {
        title = title || '确认';
        return new Promise(function(resolve) {
            if (window.ElementUI) {
                ElementUI.MessageBox.confirm(msg, title, {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(function() { resolve(true); }).catch(function() { resolve(false); });
            } else {
                resolve(confirm(msg));
            }
        });
    }

    function getUserInfo() {
        try {
            return JSON.parse(localStorage.getItem('userInfo') || '{}');
        } catch(e) {
            return {};
        }
    }

    function isAdmin() {
        var user = getUserInfo();
        return user.role === 'ADMIN';
    }

    function statusTag(status) {
        var map = {
            'draft': { label: '草稿', type: 'info' },
            'pending_review': { label: '待审核', type: 'warning' },
            'published': { label: '已发布', type: 'success' },
            'rejected': { label: '已拒绝', type: 'danger' },
            'active': { label: '正常', type: 'success' },
            'disabled': { label: '已禁用', type: 'danger' },
            'locked': { label: '已锁定', type: 'warning' },
            'deploying': { label: '部署中', type: 'warning' },
            'deployed': { label: '已部署', type: 'success' },
            'failed': { label: '失败', type: 'danger' },
            'rolled_back': { label: '已回滚', type: 'info' },
            'pending': { label: '待处理', type: 'warning' },
            'replied': { label: '已回复', type: 'success' },
            'approved': { label: '已通过', type: 'success' },
        };
        var info = map[status] || { label: status, type: 'info' };
        return '<el-tag size="small" type="' + info.type + '">' + info.label + '</el-tag>';
    }

    function renderPagination(total, page, pageSize, onChange) {
        return '<div style="text-align:right;margin-top:16px;">' +
            '<el-pagination background layout="total, prev, pager, next" ' +
            ':total="' + total + '" :current-page="' + page + '" :page-size="' + pageSize + '" ' +
            '@current-change="' + onChange + '"></el-pagination></div>';
    }

    return {
        formatDate, formatSize, showMessage, showSuccess, showError, showWarning,
        confirm, getUserInfo, isAdmin, statusTag, renderPagination
    };
})();
