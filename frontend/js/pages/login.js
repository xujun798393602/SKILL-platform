/* Login & Register Page */
window.LoginPage = (function() {
    function render(container) {
        container.innerHTML =
            '<div class="login-container">' +
                '<div class="login-card">' +
                    '<div class="login-logo">' +
                        '<h1>SKILL管理平台</h1>' +
                        '<p>企业级AI技能管理与共享平台</p>' +
                    '</div>' +
                    '<div id="login-form-mount"></div>' +
                '</div>' +
            '</div>';

        new Vue({
            el: '#login-form-mount',
            template:
                '<div>' +
                    '<el-tabs v-model="tab">' +
                        '<el-tab-pane label="登录" name="login">' +
                            '<el-form :model="loginForm" @submit.native.prevent="doLogin">' +
                                '<el-form-item><el-input v-model="loginForm.employeeId" placeholder="工号" prefix-icon="el-icon-user"></el-input></el-form-item>' +
                                '<el-form-item><el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" show-password @keyup.enter.native="doLogin"></el-input></el-form-item>' +
                                '<el-form-item><el-button type="primary" :loading="loading" @click="doLogin" style="width:100%">登 录</el-button></el-form-item>' +
                            '</el-form>' +
                        '</el-tab-pane>' +
                        '<el-tab-pane label="注册" name="register">' +
                            '<el-form :model="regForm" @submit.native.prevent="doRegister">' +
                                '<el-form-item><el-input v-model="regForm.employeeId" placeholder="工号" prefix-icon="el-icon-user"></el-input></el-form-item>' +
                                '<el-form-item><el-input v-model="regForm.name" placeholder="姓名" prefix-icon="el-icon-postcard"></el-input></el-form-item>' +
                                '<el-form-item><el-input v-model="regForm.department" placeholder="部门" prefix-icon="el-icon-office-building"></el-input></el-form-item>' +
                                '<el-form-item><el-input v-model="regForm.email" placeholder="邮箱" prefix-icon="el-icon-message"></el-input></el-form-item>' +
                                '<el-form-item><el-input v-model="regForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" show-password></el-input></el-form-item>' +
                                '<el-form-item><el-button type="primary" :loading="loading" @click="doRegister" style="width:100%">注 册</el-button></el-form-item>' +
                            '</el-form>' +
                        '</el-tab-pane>' +
                    '</el-tabs>' +
                '</div>',
            data: function() {
                return {
                    tab: 'login',
                    loading: false,
                    loginForm: { employeeId: '', password: '' },
                    regForm: { employeeId: '', name: '', department: '', email: '', password: '' }
                };
            },
            methods: {
                doLogin: function() {
                    var self = this;
                    if (!self.loginForm.employeeId || !self.loginForm.password) {
                        Utils.showWarning('请输入工号和密码');
                        return;
                    }
                    self.loading = true;
                    api.post('/auth/login', self.loginForm).then(function(res) {
                        var data = res.data;
                        localStorage.setItem('accessToken', data.accessToken);
                        localStorage.setItem('refreshToken', data.refreshToken);
                        localStorage.setItem('userInfo', JSON.stringify(data.userInfo));
                        Utils.showSuccess('登录成功');
                        setTimeout(function() { window.location.reload(); }, 500);
                    }).catch(function(err) {
                        Utils.showError(err.message || '登录失败');
                    }).finally(function() { self.loading = false; });
                },
                doRegister: function() {
                    var self = this;
                    var f = self.regForm;
                    if (!f.employeeId || !f.name || !f.department || !f.email || !f.password) {
                        Utils.showWarning('请填写所有字段');
                        return;
                    }
                    self.loading = true;
                    api.post('/auth/register', f).then(function() {
                        Utils.showSuccess('注册成功，请登录');
                        self.tab = 'login';
                        self.loginForm.employeeId = f.employeeId;
                    }).catch(function(err) {
                        Utils.showError(err.message || '注册失败');
                    }).finally(function() { self.loading = false; });
                }
            }
        });
    }
    return { render: render };
})();
