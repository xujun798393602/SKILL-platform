/* Users Management Page */
window.UsersPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="users-mount"></div>';
        new Vue({
            el: '#users-mount',
            template:
                '<div><h2>用户管理</h2>' +
                '<el-table :data="items" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column prop="employeeId" label="工号" width="120"></el-table-column>' +
                    '<el-table-column prop="name" label="姓名" width="120"></el-table-column>' +
                    '<el-table-column prop="email" label="邮箱" min-width="200"></el-table-column>' +
                    '<el-table-column prop="department" label="部门" width="120"></el-table-column>' +
                    '<el-table-column label="状态" width="90"><template slot-scope="s"><span v-html="statusTag(s.row.status)"></span></template></el-table-column>' +
                    '<el-table-column label="注册时间" width="160"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                    '<el-table-column label="操作" width="200">' +
                        '<template slot-scope="s">' +
                            '<el-button size="mini" type="text" @click="showRoles(s.row)">角色</el-button>' +
                            '<el-button size="mini" type="text" @click="showPerms(s.row)">权限</el-button>' +
                        '</template>' +
                    '</el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div>' +
                '<el-dialog title="分配角色" :visible.sync="roleDialogVisible" width="500px">' +
                    '<p>用户: {{ selectedUser.name }} ({{ selectedUser.employeeId }})</p>' +
                    '<el-checkbox-group v-model="selectedRoleIds">' +
                        '<el-checkbox v-for="r in allRoles" :key="r.id" :label="r.id">{{ r.displayName || r.name }}</el-checkbox>' +
                    '</el-checkbox-group>' +
                    '<span slot="footer"><el-button @click="roleDialogVisible=false">取消</el-button><el-button type="primary" @click="saveRoles">保存</el-button></span>' +
                '</el-dialog>' +
                '<el-dialog title="用户权限" :visible.sync="permDialogVisible" width="500px">' +
                    '<el-tag v-for="p in userPerms" :key="p" style="margin:4px">{{ p }}</el-tag>' +
                    '<p v-if="!userPerms.length" style="color:#999">暂无权限</p>' +
                '</el-dialog></div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false,
                    roleDialogVisible: false, permDialogVisible: false,
                    selectedUser: {}, selectedRoleIds: [], allRoles: [], userPerms: []
                };
            },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate, statusTag: Utils.statusTag,
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/users', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                showRoles: function(user) {
                    var self = this;
                    self.selectedUser = user;
                    api.get('/users/' + user.id + '/roles').then(function(res) {
                        self.selectedRoleIds = (res.data.roles || []).map(function(r) { return r.id; });
                    });
                    api.get('/roles').then(function(res) {
                        self.allRoles = (res.data || {}).roles || [];
                    });
                    self.roleDialogVisible = true;
                },
                saveRoles: function() {
                    var self = this;
                    api.put('/users/' + self.selectedUser.id + '/roles', { roleIds: self.selectedRoleIds }).then(function() {
                        Utils.showSuccess('角色分配成功'); self.roleDialogVisible = false;
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                showPerms: function(user) {
                    var self = this;
                    self.selectedUser = user;
                    api.get('/users/' + user.id + '/permissions').then(function(res) {
                        self.userPerms = res.data.permissions || [];
                    });
                    self.permDialogVisible = true;
                }
            }
        });
    }
    return { render: render };
})();
