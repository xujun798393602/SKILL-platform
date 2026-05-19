/* Reviews Page */
window.ReviewsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="reviews-mount"></div>';
        new Vue({
            el: '#reviews-mount',
            template:
                '<div><h2>审核管理</h2>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:280px">SKILL ID</th>' +
                        '<th style="width:100px">决定</th>' +
                        '<th>审核意见</th>' +
                        '<th style="width:200px">审核人</th>' +
                        '<th style="width:160px">审核时间</th>' +
                        '<th style="width:200px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="6" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td colspan="6" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td>{{ row.skillId }}</td>' +
                            '<td><span v-html="statusTag(row.action)"></span></td>' +
                            '<td>{{ row.comment }}</td>' +
                            '<td>{{ row.reviewerId }}</td>' +
                            '<td>{{ formatDate(row.reviewedAt) }}</td>' +
                            '<td>' +
                                '<el-button size="mini" type="success" @click="review(row.skillId,\'approved\')">通过</el-button>' +
                                '<el-button size="mini" type="danger" @click="review(row.skillId,\'rejected\')">拒绝</el-button>' +
                            '</td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
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
