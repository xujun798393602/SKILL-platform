/* Suites Page */
window.SuitesPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="suites-mount"></div>';
        new Vue({
            el: '#suites-mount',
            template:
                '<div><div style="display:flex;justify-content:space-between;align-items:center"><h2>套件管理</h2>' +
                '<el-button type="primary" @click="showCreate">创建套件</el-button></div>' +
                '<el-table :data="items" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column prop="name" label="名称" min-width="150"></el-table-column>' +
                    '<el-table-column prop="description" label="描述" min-width="200"></el-table-column>' +
                    '<el-table-column prop="category" label="分类" width="100"></el-table-column>' +
                    '<el-table-column prop="skillCount" label="SKILL数" width="90"></el-table-column>' +
                    '<el-table-column label="操作" width="180">' +
                        '<template slot-scope="s">' +
                            '<el-button size="mini" type="text" @click="deploy(s.row)">部署</el-button>' +
                            '<el-button size="mini" type="text" @click="viewDetail(s.row)">详情</el-button>' +
                        '</template>' +
                    '</el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div>' +
                '<el-dialog title="创建套件" :visible.sync="createVisible" width="500px">' +
                    '<el-form :model="form" label-width="80px">' +
                        '<el-form-item label="名称"><el-input v-model="form.name"></el-input></el-form-item>' +
                        '<el-form-item label="描述"><el-input v-model="form.description" type="textarea"></el-input></el-form-item>' +
                        '<el-form-item label="分类"><el-input v-model="form.category"></el-input></el-form-item>' +
                        '<el-form-item label="SKILL IDs"><el-input v-model="form.skillsStr" placeholder="逗号分隔的SKILL ID"></el-input></el-form-item>' +
                    '</el-form>' +
                    '<span slot="footer"><el-button @click="createVisible=false">取消</el-button><el-button type="primary" @click="doCreate">创建</el-button></span>' +
                '</el-dialog>' +
                // Detail dialog
                '<el-dialog title="套件详情" :visible.sync="detailVisible" width="600px">' +
                    '<el-descriptions :column="2" border v-if="detail">' +
                        '<el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>' +
                        '<el-descriptions-item label="分类">{{ detail.category }}</el-descriptions-item>' +
                        '<el-descriptions-item label="描述" :span="2">{{ detail.description }}</el-descriptions-item>' +
                    '</el-descriptions>' +
                '</el-dialog></div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false,
                    createVisible: false, detailVisible: false, detail: null,
                    form: { name: '', description: '', category: 'general', skillsStr: '' }
                };
            },
            mounted: function() { this.load(); },
            methods: {
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/suites', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                showCreate: function() { this.form = { name: '', description: '', category: 'general', skillsStr: '' }; this.createVisible = true; },
                doCreate: function() {
                    var self = this;
                    var skills = self.form.skillsStr.split(',').map(function(s, i) { return { skillId: s.trim(), order: i + 1 }; }).filter(function(s) { return s.skillId; });
                    if (skills.length < 2) { Utils.showWarning('至少需要2个SKILL'); return; }
                    api.post('/suites', { name: self.form.name, description: self.form.description, category: self.form.category, skills: skills }).then(function() {
                        Utils.showSuccess('创建成功'); self.createVisible = false; self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                deploy: function(row) {
                    api.post('/suites/' + row.id + '/deploy').then(function() { Utils.showSuccess('部署任务已创建'); }).catch(function(err) { Utils.showError(err.message); });
                },
                viewDetail: function(row) {
                    var self = this;
                    api.get('/suites/' + row.id).then(function(res) { self.detail = res.data; self.detailVisible = true; });
                }
            }
        });
    }
    return { render: render };
})();
