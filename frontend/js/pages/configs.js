/* System Configs Page */
window.ConfigsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="configs-mount"></div>';
        new Vue({
            el: '#configs-mount',
            template:
                '<div><h2>系统配置</h2>' +
                '<el-table :data="configs" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column prop="configKey" label="配置项" width="250"></el-table-column>' +
                    '<el-table-column label="值" min-width="200">' +
                        '<template slot-scope="s">' +
                            '<span v-if="s.row.isSensitive">******</span>' +
                            '<span v-else>{{ s.row.configValue }}</span>' +
                        '</template>' +
                    '</el-table-column>' +
                    '<el-table-column prop="description" label="描述" width="200"></el-table-column>' +
                    '<el-table-column label="只读" width="80"><template slot-scope="s"><el-tag :type="s.row.isReadonly?\'danger\':\'success\'" size="small">{{ s.row.isReadonly?"是":"否" }}</el-tag></template></el-table-column>' +
                    '<el-table-column label="操作" width="120">' +
                        '<template slot-scope="s">' +
                            '<el-button size="mini" type="text" @click="edit(s.row)" v-if="!s.row.isReadonly && !s.row.isSensitive">编辑</el-button>' +
                        '</template>' +
                    '</el-table-column>' +
                '</el-table>' +
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
