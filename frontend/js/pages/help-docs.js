/* Help Docs Page */
window.HelpDocsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="docs-mount"></div>';
        new Vue({
            el: '#docs-mount',
            template:
                '<div><h2>帮助文档</h2>' +
                '<el-input v-model="keyword" placeholder="搜索文档" clearable style="width:300px;margin:16px 0" @keyup.enter.native="load"><el-button slot="append" icon="el-icon-search" @click="load"></el-button></el-input>' +
                '<el-table :data="items" v-loading="loading" stripe>' +
                    '<el-table-column prop="title" label="标题" min-width="200">' +
                        '<template slot-scope="s"><a href="javascript:void(0)" @click="viewDoc(s.row)" style="color:#409eff">{{ s.row.title }}</a></template>' +
                    '</el-table-column>' +
                    '<el-table-column prop="docType" label="类型" width="100"></el-table-column>' +
                    '<el-table-column prop="category" label="分类" width="100"></el-table-column>' +
                    '<el-table-column label="时间" width="160"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                    '<el-table-column label="操作" width="120">' +
                        '<template slot-scope="s"><el-button size="mini" type="danger" text @click="remove(s.row)" v-if="isAdmin">删除</el-button></template>' +
                    '</el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div>' +
                '<el-dialog :title="docDetail.title" :visible.sync="detailVisible" width="700px">' +
                    '<div style="white-space:pre-wrap;line-height:1.8">{{ docDetail.content }}</div>' +
                '</el-dialog></div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false, keyword: '',
                    detailVisible: false, docDetail: {}, isAdmin: Utils.isAdmin()
                };
            },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate,
                load: function() {
                    var self = this; self.loading = true;
                    var params = { page: self.page, pageSize: self.pageSize };
                    if (self.keyword) params.keyword = self.keyword;
                    api.get('/help-docs', params).then(function(res) {
                        self.items = res.data.docs || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                viewDoc: function(row) {
                    var self = this;
                    api.get('/help-docs/' + row.id).then(function(res) {
                        self.docDetail = res.data; self.detailVisible = true;
                    });
                },
                remove: function(row) {
                    var self = this;
                    Utils.confirm('确定删除?').then(function(yes) {
                        if (!yes) return;
                        api.delete('/help-docs/' + row.id).then(function() { Utils.showSuccess('删除成功'); self.load(); });
                    });
                }
            }
        });
    }
    return { render: render };
})();
