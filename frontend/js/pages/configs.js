/* System Configs Page */
window.ConfigsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="configs-mount"></div>';
        new Vue({
            el: '#configs-mount',
            template:
                '<div><h2>系统配置</h2>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:250px">配置项</th>' +
                        '<th>值</th>' +
                        '<th style="width:200px">描述</th>' +
                        '<th style="width:80px">只读</th>' +
                        '<th style="width:120px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="5" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!configs.length"><td colspan="5" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in configs" :key="row.configKey">' +
                            '<td>{{ row.configKey }}</td>' +
                            '<td><span v-if="row.isSensitive">******</span><span v-else>{{ row.configValue }}</span></td>' +
                            '<td>{{ row.description }}</td>' +
                            '<td><el-tag :type="row.isReadonly?\'danger\':\'success\'" size="small">{{ row.isReadonly?"是":"否" }}</el-tag></td>' +
                            '<td><el-button size="mini" type="text" @click="edit(row)" v-if="!row.isReadonly && !row.isSensitive">编辑</el-button></td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
                '<el-dialog title="编辑配置" :visible.sync="editVisible" width="400px">' +
                    '<el-form label-width="80px">' +
                        '<el-form-item label="配置项">{{ editKey }}</el-form-item>' +
                        '<el-form-item label="值"><el-input v-model="editValue"></el-input></el-form-item>' +
                    '</el-form>' +
                    '<span slot="footer"><el-button @click="editVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></span>' +
                '</el-dialog></div>',
            data: function() {
                return { configs: [], loading: false, editVisible: false, editKey: '', editValue: '' };
            },
            mounted: function() { this.load(); },
            methods: {
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/configs').then(function(res) {
                        self.configs = (res.data || {}).configs || [];
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                edit: function(row) { this.editKey = row.configKey; this.editValue = row.configValue; this.editVisible = true; },
                save: function() {
                    var self = this;
                    api.put('/configs/' + self.editKey, { value: self.editValue }).then(function() {
                        Utils.showSuccess('保存成功'); self.editVisible = false; self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
