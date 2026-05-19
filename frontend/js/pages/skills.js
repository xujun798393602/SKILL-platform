/* Skills List Page */
window.SkillsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="skills-mount"></div>';

        new Vue({
            el: '#skills-mount',
            template:
                '<div>' +
                    '<div class="page-header">' +
                        '<h3>SKILL管理</h3>' +
                        '<div style="display:flex;gap:10px">' +
                            '<el-button type="primary" icon="el-icon-upload2" @click="showUpload">上传SKILL</el-button>' +
                            '<el-button type="success" icon="el-icon-download" @click="downloadAll">批量下载</el-button>' +
                        '</div>' +
                    '</div>' +
                    '<el-form :inline="true" :model="filters" style="margin-bottom:16px">' +
                        '<el-form-item><el-input v-model="filters.keyword" placeholder="搜索SKILL" clearable @keyup.enter.native="load"></el-input></el-form-item>' +
                        '<el-form-item><el-select v-model="filters.skillType" placeholder="类型" clearable><el-option label="公开" value="public"></el-option><el-option label="代码审查" value="code-review"></el-option></el-select></el-form-item>' +
                        '<el-form-item><el-select v-model="filters.status" placeholder="状态" clearable><el-option label="草稿" value="draft"></el-option><el-option label="待审核" value="pending_review"></el-option><el-option label="已发布" value="published"></el-option></el-select></el-form-item>' +
                        '<el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>' +
                    '</el-form>' +
                    '<div class="table-wrap">' +
                    '<table class="data-table">' +
                        '<thead><tr>' +
                            '<th style="min-width:150px">名称</th>' +
                            '<th style="width:100px">类型</th>' +
                            '<th style="width:100px">分类</th>' +
                            '<th style="width:80px">版本</th>' +
                            '<th style="width:90px">状态</th>' +
                            '<th style="width:80px">评分</th>' +
                            '<th style="width:70px">下载</th>' +
                            '<th style="width:80px">大小</th>' +
                            '<th style="width:150px">创建时间</th>' +
                            '<th style="width:200px">操作</th>' +
                        '</tr></thead>' +
                        '<tbody>' +
                            '<tr v-if="loading"><td colspan="10" style="text-align:center;padding:40px;color:#909399">加载中...</td></tr>' +
                            '<tr v-else-if="!items.length"><td colspan="10" style="text-align:center;padding:40px;color:#909399">暂无数据</td></tr>' +
                            '<tr v-for="row in items" :key="row.id">' +
                                '<td><a href="javascript:void(0)" @click="goDetail(row)" class="link">{{ row.name }}</a></td>' +
                                '<td>{{ row.skillType }}</td>' +
                                '<td>{{ row.category }}</td>' +
                                '<td>{{ row.currentVersion }}</td>' +
                                '<td v-html="statusTag(row.status)"></td>' +
                                '<td>{{ row.avgRating ? row.avgRating.toFixed(1) : "-" }}</td>' +
                                '<td>{{ row.downloadCount }}</td>' +
                                '<td>{{ formatSize(row.fileSize) }}</td>' +
                                '<td>{{ formatDate(row.createdAt) }}</td>' +
                                '<td>' +
                                    '<el-button size="mini" type="primary" icon="el-icon-download" @click="download(row)">下载</el-button>' +
                                    '<el-button size="mini" type="danger" icon="el-icon-delete" @click="remove(row)">删除</el-button>' +
                                '</td>' +
                            '</tr>' +
                        '</tbody>' +
                    '</table>' +
                    '</div>' +
                    '<div style="text-align:right;margin-top:16px">' +
                        '<el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination>' +
                    '</div>' +
                    // Upload dialog
                    '<el-dialog title="上传SKILL" :visible.sync="uploadVisible" width="500px">' +
                        '<el-form :model="uploadForm" label-width="80px">' +
                            '<el-form-item label="文件"><input type="file" @change="onFileChange" accept=".json,.skill,.zip" /><div style="color:#909399;font-size:12px;margin-top:4px">支持 .json, .skill, .zip</div></el-form-item>' +
                            '<el-form-item label="名称"><el-input v-model="uploadForm.name"></el-input></el-form-item>' +
                            '<el-form-item label="类型"><el-select v-model="uploadForm.skillType"><el-option label="公开" value="public"></el-option><el-option label="代码审查" value="code-review"></el-option></el-select></el-form-item>' +
                            '<el-form-item label="版本"><el-input v-model="uploadForm.version" placeholder="1.0.0"></el-input></el-form-item>' +
                            '<el-form-item label="分类"><el-input v-model="uploadForm.category" placeholder="general"></el-input></el-form-item>' +
                            '<el-form-item label="描述"><el-input v-model="uploadForm.description" type="textarea" :rows="3"></el-input></el-form-item>' +
                        '</el-form>' +
                        '<span slot="footer"><el-button @click="uploadVisible=false">取消</el-button><el-button type="primary" :loading="uploading" @click="doUpload">上传</el-button></span>' +
                    '</el-dialog>' +
                '</div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false,
                    filters: { keyword: '', skillType: '', status: '' },
                    uploadVisible: false, uploading: false,
                    uploadForm: { name: '', skillType: 'public', version: '1.0.0', category: 'general', description: '' },
                    uploadFile: null
                };
            },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate,
                formatSize: Utils.formatSize,
                statusTag: Utils.statusTag,
                load: function() {
                    var self = this;
                    self.loading = true;
                    var params = { page: self.page, pageSize: self.pageSize };
                    if (self.filters.keyword) params.keyword = self.filters.keyword;
                    if (self.filters.skillType) params.skillType = self.filters.skillType;
                    if (self.filters.status) params.status = self.filters.status;
                    api.get('/skills', params).then(function(res) {
                        self.items = res.data.items || [];
                        self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); })
                    .finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                goDetail: function(row) { Router.navigate('/skills/' + row.id); },
                downloadAll: function() {
                    if (!this.items.length) { Utils.showWarning('暂无可下载的SKILL'); return; }
                    var self = this;
                    Utils.confirm('确认下载当前列表所有SKILL？').then(function(yes) {
                        if (!yes) return;
                        self.items.forEach(function(row, i) {
                            setTimeout(function() {
                                api.download('/skills/' + row.id + '/download').then(function(blob) {
                                    var url = URL.createObjectURL(blob);
                                    var a = document.createElement('a'); a.href = url; a.download = row.name + '.skill';
                                    a.click(); URL.revokeObjectURL(url);
                                });
                            }, i * 500);
                        });
                    });
                },
                download: function(row) {
                    api.download('/skills/' + row.id + '/download').then(function(blob) {
                        var url = URL.createObjectURL(blob);
                        var a = document.createElement('a'); a.href = url; a.download = row.name + '.skill';
                        a.click(); URL.revokeObjectURL(url);
                    });
                },
                remove: function(row) {
                    var self = this;
                    Utils.confirm('确定删除 ' + row.name + ' ?').then(function(yes) {
                        if (!yes) return;
                        api.delete('/skills/' + row.id).then(function() {
                            Utils.showSuccess('删除成功');
                            self.load();
                        }).catch(function(err) { Utils.showError(err.message); });
                    });
                },
                showUpload: function() { this.uploadVisible = true; },
                onFileChange: function(e) {
                    var file = e.target.files[0];
                    if (file) {
                        this.uploadFile = file;
                        this.uploadForm.name = file.name.replace(/\.[^.]+$/, '');
                    }
                },
                doUpload: function() {
                    var self = this;
                    if (!self.uploadFile) { Utils.showWarning('请选择文件'); return; }
                    var fd = new FormData();
                    fd.append('file', self.uploadFile);
                    fd.append('skillType', self.uploadForm.skillType);
                    fd.append('name', self.uploadForm.name);
                    fd.append('version', self.uploadForm.version);
                    fd.append('category', self.uploadForm.category);
                    fd.append('description', self.uploadForm.description);
                    self.uploading = true;
                    api.upload('/skills/upload', fd).then(function() {
                        Utils.showSuccess('上传成功');
                        self.uploadVisible = false;
                        self.load();
                    }).catch(function(err) { Utils.showError(err.message); })
                    .finally(function() { self.uploading = false; });
                }
            }
        });
    }
    return { render: render };
})();
