/* Roles Management Page */
window.RolesPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="roles-mount"></div>';
        new Vue({
            el: '#roles-mount',
            template:
                '<div><div style="display:flex;justify-content:space-between;align-items:center"><h2>角色管理</h2>' +
                '<el-button type="primary" @click="showCreate">创建角色</el-button></div>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:120px">标识</th>' +
                        '<th style="width:150px">显示名</th>' +
                        '<th>描述</th>' +
                        '<th style="width:90px">系统角色</th>' +
                        '<th style="width:250px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="5" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!roles.length"><td colspan="5" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in roles" :key="row.id">' +
                            '<td>{{ row.name }}</td>' +
                            '<td>{{ row.displayName }}</td>' +
                            '<td>{{ row.description }}</td>' +
                            '<td><el-tag :type="row.isSystem?\'warning\':\'info\'" size="small">{{ row.isSystem?"是":"否" }}</el-tag></td>' +
                            '<td>' +
                                '<el-button size="mini" type="text" @click="showEdit(row)">编辑</el-button>' +
                                '<el-button size="mini" type="text" @click="showPerm(row)">权限</el-button>' +
                                '<el-button size="mini" type="text" style="color:#f56c6c" @click="remove(row)" v-if="!row.isSystem">删除</el-button>' +
                            '</td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
                '<el-dialog :title="isEdit?\'编辑角色\':\'创建角色\'" :visible.sync="dialogVisible" width="400px">' +
                    '<el-form :model="form" label-width="80px">' +
                        '<el-form-item label="标识"><el-input v-model="form.name" :disabled="isEdit"></el-input></el-form-item>' +
                        '<el-form-item label="显示名"><el-input v-model="form.displayName"></el-input></el-form-item>' +
                        '<el-form-item label="描述"><el-input v-model="form.description" type="textarea"></el-input></el-form-item>' +
                    '</el-form>' +
                    '<span slot="footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="doSave">保存</el-button></span>' +
                '</el-dialog>' +
                '<el-dialog title="权限配置" :visible.sync="permVisible" width="500px">' +
                    '<p>角色: {{ permRole.displayName || permRole.name }}</p>' +
                    '<div style="max-height:400px;overflow-y:auto">' +
                        '<div v-for="group in permGroups" :key="group.resource" style="margin-bottom:12px">' +
                            '<h4>{{ group.resource }}</h4>' +
                            '<el-checkbox-group v-model="selectedPermIds">' +
                                '<el-checkbox v-for="p in group.perms" :key="p.id" :label="p.id" style="display:block;margin:4px 0">{{ p.name }} ({{ p.code }})</el-checkbox>' +
                            '</el-checkbox-group>' +
                        '</div>' +
                    '</div>' +
                    '<span slot="footer"><el-button @click="permVisible=false">取消</el-button><el-button type="primary" @click="savePerms">保存</el-button></span>' +
                '</el-dialog></div>',
            data: function() {
                return {
                    roles: [], loading: false,
                    dialogVisible: false, isEdit: false, editId: '',
                    form: { name: '', displayName: '', description: '' },
                    permVisible: false, permRole: {}, permGroups: [], selectedPermIds: []
                };
            },
            mounted: function() { this.load(); },
            methods: {
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/roles').then(function(res) {
                        self.roles = (res.data || {}).roles || [];
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                showCreate: function() { this.isEdit = false; this.form = { name: '', displayName: '', description: '' }; this.dialogVisible = true; },
                showEdit: function(row) { this.isEdit = true; this.editId = row.id; this.form = { name: row.name, displayName: row.displayName, description: row.description }; this.dialogVisible = true; },
                doSave: function() {
                    var self = this;
                    var promise = self.isEdit ? api.put('/roles/' + self.editId, self.form) : api.post('/roles', self.form);
                    promise.then(function() {
                        Utils.showSuccess('保存成功'); self.dialogVisible = false; self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                remove: function(row) {
                    var self = this;
                    Utils.confirm('确定删除角色 ' + row.name + ' ?').then(function(yes) {
                        if (!yes) return;
                        api.delete('/roles/' + row.id).then(function() { Utils.showSuccess('删除成功'); self.load(); }).catch(function(err) { Utils.showError(err.message); });
                    });
                },
                showPerm: function(row) {
                    var self = this;
                    self.permRole = row;
                    api.get('/roles').then(function() {
                        self.permGroups = [
                            { resource: 'SKILL', perms: [
                                { id: 'p1', code: 'skill:upload', name: '上传SKILL' },
                                { id: 'p2', code: 'skill:download', name: '下载SKILL' },
                                { id: 'p3', code: 'skill:delete', name: '删除SKILL' },
                                { id: 'p4', code: 'skill:review', name: '审核SKILL' },
                            ]},
                            { resource: '系统', perms: [
                                { id: 'p5', code: 'user:manage', name: '用户管理' },
                                { id: 'p6', code: 'role:manage', name: '角色管理' },
                                { id: 'p7', code: 'config:manage', name: '配置管理' },
                                { id: 'p8', code: 'log:view', name: '查看日志' },
                            ]}
                        ];
                        self.selectedPermIds = [];
                        self.permVisible = true;
                    });
                },
                savePerms: function() {
                    var self = this;
                    api.put('/roles/' + self.permRole.id + '/permissions', { permissionIds: self.selectedPermIds }).then(function() {
                        Utils.showSuccess('权限配置成功'); self.permVisible = false;
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
