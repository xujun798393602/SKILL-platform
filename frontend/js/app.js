/* Main application - layout, navigation, auth */
(function() {
    var isAdmin = Utils.isAdmin();

    var navItems = [
        { path: '/dashboard',    icon: 'el-icon-data-board', label: '仪表盘',   admin: true },
        { path: '/skills',       icon: 'el-icon-folder',    label: 'SKILL管理' },
        { path: '/reviews',      icon: 'el-icon-check',     label: '审核管理',   admin: true },
        { path: '/favorites',    icon: 'el-icon-star-off',  label: '我的收藏' },
        { path: '/suites',       icon: 'el-icon-collection',label: '套件管理' },
        { path: '/deployments',  icon: 'el-icon-upload',    label: '部署管理' },
        { path: '/notifications',icon: 'el-icon-bell',      label: '消息通知' },
        { path: '/statistics',   icon: 'el-icon-pie-chart', label: '数据统计',   admin: true },
        { path: '/logs',         icon: 'el-icon-document',  label: '操作日志',   admin: true },
        { path: '/configs',      icon: 'el-icon-setting',   label: '系统配置',   admin: true },
        { path: '/help-docs',    icon: 'el-icon-question',  label: '帮助文档' },
        { path: '/feedbacks',    icon: 'el-icon-chat-dot-round', label: '反馈管理' },
        { path: '/users',        icon: 'el-icon-user',      label: '用户管理',   admin: true },
        { path: '/roles',        icon: 'el-icon-s-check',   label: '角色管理',   admin: true },
    ];

    function renderLayout() {
        var user = Utils.getUserInfo();
        isAdmin = Utils.isAdmin();

        var sidebarHtml = navItems.filter(function(item) {
            return !item.admin || isAdmin;
        }).map(function(item) {
            return '<a class="nav-item" href="#' + item.path + '" data-path="' + item.path + '">' +
                '<i class="' + item.icon + '"></i><span>' + item.label + '</span></a>';
        }).join('');

        var html =
            '<div class="app-layout">' +
                '<div class="sidebar" id="app-sidebar">' +
                    '<div class="sidebar-header">' +
                        '<h2>SKILL平台</h2>' +
                    '</div>' +
                    '<nav class="sidebar-nav">' + sidebarHtml + '</nav>' +
                    '<div class="sidebar-resize-handle" id="sidebar-resize"></div>' +
                '</div>' +
                '<div class="main-area">' +
                    '<header class="header-bar">' +
                        '<div class="header-left"></div>' +
                        '<div class="header-right">' +
                            '<div class="user-menu-wrap">' +
                                '<span class="user-dropdown" onclick="this.parentElement.classList.toggle(\'open\')">' +
                                    '<i class="el-icon-user-solid"></i> ' +
                                    (user.name || user.employeeId || '用户') +
                                    '<i class="el-icon-arrow-down"></i>' +
                                '</span>' +
                                '<div class="user-menu-dropdown">' +
                                    '<div class="user-menu-item" onclick="showPasswordDialog()"><i class="el-icon-lock"></i> 修改密码</div>' +
                                    '<div class="user-menu-item user-menu-logout" onclick="doLogout()"><i class="el-icon-switch-button"></i> 退出登录</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</header>' +
                    '<main class="content-area" id="page-content"></main>' +
                '</div>' +
            '</div>';

        return html;
    }

    function highlightNav() {
        var hash = window.location.hash.slice(1);
        document.querySelectorAll('.nav-item').forEach(function(el) {
            el.classList.toggle('active', hash.startsWith(el.getAttribute('data-path')));
        });
    }

    // Register routes
    Router.register('/login', window.LoginPage.render);
    Router.register('/dashboard', window.DashboardPage.render);
    Router.register('/skills', window.SkillsPage.render);
    Router.register('/skills/:id', window.SkillDetailPage.render);
    Router.register('/reviews', window.ReviewsPage.render);
    Router.register('/notifications', window.NotificationsPage.render);
    Router.register('/favorites', window.FavoritesPage.render);
    Router.register('/suites', window.SuitesPage.render);
    Router.register('/deployments', window.DeploymentsPage.render);
    Router.register('/statistics', window.StatisticsPage.render);
    Router.register('/logs', window.LogsPage.render);
    Router.register('/configs', window.ConfigsPage.render);
    Router.register('/help-docs', window.HelpDocsPage.render);
    Router.register('/feedbacks', window.FeedbacksPage.render);
    Router.register('/users', window.UsersPage.render);
    Router.register('/roles', window.RolesPage.render);

    // Boot
    function boot() {
        var hash = window.location.hash.slice(1);
        if (!hash || hash === '/login') {
            if (localStorage.getItem('accessToken')) {
                Router.navigate('/dashboard');
                return;
            }
        }

        if (hash !== '/login' && !localStorage.getItem('accessToken')) {
            Router.navigate('/login');
            return;
        }

        if (hash === '/login') {
            Router.handleRoute();
            return;
        }

        // Render main layout
        var app = document.getElementById('app');
        app.innerHTML = renderLayout();

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!e.target.closest('.user-menu-wrap')) {
                document.querySelectorAll('.user-menu-wrap.open').forEach(function(el) {
                    el.classList.remove('open');
                });
            }
        });

        // Sidebar drag resize
        initSidebarResize();

        highlightNav();
        window.addEventListener('hashchange', highlightNav);
        Router.handleRoute();
    }

    function initSidebarResize() {
        var handle = document.getElementById('sidebar-resize');
        var sidebar = document.getElementById('app-sidebar');
        if (!handle || !sidebar) return;

        var startX, startWidth;
        var minWidth = 160;
        var maxWidth = 400;

        function onMouseDown(e) {
            e.preventDefault();
            startX = e.clientX;
            startWidth = sidebar.offsetWidth;
            document.addEventListener('mousemove', onMouseMove);
            document.addEventListener('mouseup', onMouseUp);
            document.body.style.cursor = 'col-resize';
            document.body.style.userSelect = 'none';
        }

        function onMouseMove(e) {
            var newWidth = startWidth + (e.clientX - startX);
            if (newWidth < minWidth) newWidth = minWidth;
            if (newWidth > maxWidth) newWidth = maxWidth;
            sidebar.style.width = newWidth + 'px';
        }

        function onMouseUp() {
            document.removeEventListener('mousemove', onMouseMove);
            document.removeEventListener('mouseup', onMouseUp);
            document.body.style.cursor = '';
            document.body.style.userSelect = '';
        }

        handle.addEventListener('mousedown', onMouseDown);
    }

    window.doLogout = function() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('userInfo');
        Router.navigate('/login');
    };

    window.showPasswordDialog = function() {
        var div = document.createElement('div');
        div.id = 'password-dialog';
        document.body.appendChild(div);
        new Vue({
            el: '#password-dialog',
            template: '<el-dialog title="修改密码" :visible="true" width="400px" @close="close">' +
                '<el-form :model="form" label-width="80px">' +
                    '<el-form-item label="旧密码"><el-input v-model="form.oldPassword" type="password" show-password></el-input></el-form-item>' +
                    '<el-form-item label="新密码"><el-input v-model="form.newPassword" type="password" show-password></el-input></el-form-item>' +
                '</el-form>' +
                '<span slot="footer"><el-button @click="close">取消</el-button><el-button type="primary" @click="submit">确定</el-button></span>' +
                '</el-dialog>',
            data: function() { return { form: { oldPassword: '', newPassword: '' } }; },
            methods: {
                close: function() { this.$destroy(); div.remove(); },
                submit: function() {
                    var self = this;
                    api.put('/auth/password', this.form).then(function() {
                        Utils.showSuccess('密码修改成功');
                        self.close();
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    };

    // Start
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot);
    } else {
        boot();
    }
})();
