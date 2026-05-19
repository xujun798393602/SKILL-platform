/* Users Management Page */
window.UsersPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="users-mount"></div>';
        new Vue({
            el: '#users-mount',
            template:
                '<div><h2>用户管理</h2>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:120px">工号</th>' +
                        '<th style="width:120px">姓名</th>' +
                        '<th>邮箱</th>' +
                        '<th style="width:120px">部门</th>' +
                        '<th style="width:90px">状态</th>' +
                        '<th style="width:160px">注册时间</th>' +
                        '<th style="width:200px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="7" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td colspan="7" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td>{{ row.employeeId }}</td>' +
                            '<td>{{ row.name }}</td>' +
                            '<td>{{ row.email }}</td>' +
                            '<td>{{ row.department }}</td>' +
                            '<td><span v-html="statusTag(row.status)"></span></td>' +
                            '<td>{{ formatDate(row.createdAt) }}</td>' +
                            '<td>' +
                                '<el-button size="mini" type="text" @click="showRoles(row)">角色</el-button>' +
                                '<el-button size="mini" type="text" @click="showPerms(row)">权限</el-button>' +
                            '</td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
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
