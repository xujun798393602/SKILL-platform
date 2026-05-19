/* Reviews Page */
window.ReviewsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="reviews-mount"></div>';
        new Vue({
            el: '#reviews-mount',
            template:
                '<div><h2>审核管理</h2>' +
                '<el-table :data="items" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column prop="skillId" label="SKILL ID" width="280"></el-table-column>' +
                    '<el-table-column prop="action" label="决定" width="100"><template slot-scope="s"><span v-html="statusTag(s.row.action)"></span></template></el-table-column>' +
                    '<el-table-column prop="comment" label="审核意见"></el-table-column>' +
                    '<el-table-column prop="reviewerId" label="审核人" width="200"></el-table-column>' +
                    '<el-table-column label="审核时间" width="160"><template slot-scope="s">{{ formatDate(s.row.reviewedAt) }}</template></el-table-column>' +
                    '<el-table-column label="操作" width="200">' +
                        '<template slot-scope="s">' +
                            '<el-button size="mini" type="success" @click="review(s.row.skillId,\'approved\')">通过</el-button>' +
                            '<el-button size="mini" type="danger" @click="review(s.row.skillId,\'rejected\')">拒绝</el-button>' +
                        '</template>' +
                    '</el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div>' +
                '</div>',
            data: function() { return { items: [], total: 0, page: 1, pageSize: 20, loading: false }; },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate, statusTag: Utils.statusTag,
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/reviews', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                review: function(skillId, decision) {
                    var self = this;
                    api.post('/skills/' + skillId + '/review', { decision: decision }).then(function() {
                        Utils.showSuccess('审核完成'); self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
