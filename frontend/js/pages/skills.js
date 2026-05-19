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
                    '<el-table :data="items" v-loading="loading" stripe>' +
                        '<el-table-column prop="name" label="名称" min-width="150">' +
                            '<template slot-scope="scope"><a href="javascript:void(0)" @click="goDetail(scope.row)" style="color:#409eff">{{ scope.row.name }}</a></template>' +
                        '</el-table-column>' +
                        '<el-table-column prop="skillType" label="类型" width="100"></el-table-column>' +
                        '<el-table-column prop="category" label="分类" width="100"></el-table-column>' +
                        '<el-table-column prop="currentVersion" label="版本" width="80"></el-table-column>' +
                        '<el-table-column label="状态" width="90"><template slot-scope="scope"><span v-html="statusTag(scope.row.status)"></span></template></el-table-column>' +
                        '<el-table-column label="评分" width="80"><template slot-scope="scope">{{ scope.row.avgRating ? scope.row.avgRating.toFixed(1) : '-' }}</template></el-table-column>' +
                        '<el-table-column prop="downloadCount" label="下载" width="70"></el-table-column>' +
                        '<el-table-column label="大小" width="80"><template slot-scope="scope">{{ formatSize(scope.row.fileSize) }}</template></el-table-column>' +
                        '<el-table-column label="创建时间" width="150"><template slot-scope="scope">{{ formatDate(scope.row.createdAt) }}</template></el-table-column>' +
                        '<el-table-column label="操作" width="180" fixed="right">' +
                            '<template slot-scope="scope">' +
                                '<el-button size="small" type="primary" icon="el-icon-download" plain @click="download(scope.row)">下载</el-button>' +
                                '<el-button size="small" type="danger" icon="el-icon-delete" plain @click="remove(scope.row)">删除</el-button>' +
                            '</template>' +
                        '</el-table-column>' +
                    '</el-table>' +
                    '<div style="text-align:right;margin-top:16px">' +
                        '<el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination>' +
                    '</div>' +
                    // Upload dialog
                    '<el-dialog title="上传SKILL" :visible.sync="uploadVisible" width="500px">' +
                        '<el-form :model="uploadForm" label-width="80px">' +
                            '<el-form-item label="文件"><el-upload action="" :auto-upload="false" :on-change="onFileChange" :limit="1"><el-button size="small" type="primary">选择文件</el-button><div slot="tip" class="el-upload__tip">支持 .json, .skill, .zip</div></el-upload></el-form-item>' +
                            '<el-form-item label="名称"><el-input v-model="uploadForm.name"></el-input></el-form-item>' +
                            '<el-form-item label="类型"><el-select v-model="uploadForm.skillType"><el-option label="公开" value="public"></el-option><el-option label="代码审查" value="code-review"></el-option></el-select></el-form-item>' +
                            '<el-form-item label="版本"><el-input v-model="uploadForm.version" placeholder="1.0.0"></el-input></el-form-item>' +
                            '<el-form-item label="分类"><el-input v-model="uploadForm.category" placeholder="general"></el-input></el-form-item>' +
                            '<el-form-item label="描述"><el-input v-model="uploadForm.description" type="textarea"></el-input></el-form-item>' +
                        '</el-form>' +
                        '<span slot="footer"><el-button @click="uploadVisible=false">取消</el-button><el-button type="primary" :loading="uploading" @click="doUpload">上传</el-button></span>' +
                    '</el-dialog>' +
                    // Batch upload dialog
                    '<el-dialog title="批量上传" :visible.sync="batchVisible" width="500px">' +
                        '<el-upload action="" :auto-upload="false" :on-change="onBatchFileChange" :file-list="batchFiles" multiple><el-button type="primary">选择文件</el-button></el-upload>' +
                        '<span slot="footer"><el-button @click="batchVisible=false">取消</el-button><el-button type="primary" :loading="uploading" @click="doBatchUpload">上传</el-button></span>' +
                    '</el-dialog>' +
                '</div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false,
                    filters: { keyword: '', skillType: '', status: '' },
                    uploadVisible: false, batchVisible: false, uploading: false,
                    uploadForm: { name: '', skillType: 'public', version: '1.0.0', category: 'general', description: '' },
                    uploadFile: null, batchFiles: []
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
                onFileChange: function(file) { this.uploadFile = file.raw; this.uploadForm.name = file.name.replace(/\.[^.]+$/, ''); },
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
                },
                showBatchUpload: function() { this.batchVisible = true; this.batchFiles = []; },
                onBatchFileChange: function(file, fileList) { this.batchFiles = fileList; },
                doBatchUpload: function() {
                    var self = this;
                    if (!self.batchFiles.length) { Utils.showWarning('请选择文件'); return; }
                    var fd = new FormData();
                    self.batchFiles.forEach(function(f) { fd.append('files', f.raw); });
                    self.uploading = true;
                    api.upload('/skills/batch-upload', fd).then(function(res) {
                        Utils.showSuccess('批量上传完成: 成功' + res.data.successCount + '个');
                        self.batchVisible = false;
                        self.load();
                    }).catch(function(err) { Utils.showError(err.message); })
                    .finally(function() { self.uploading = false; });
                }
            }
        });
    }
    return { render: render };
})();
