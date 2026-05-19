/* Favorites Page */
window.FavoritesPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="fav-mount"></div>';
        new Vue({
            el: '#fav-mount',
            template:
                '<div><h2>我的收藏</h2>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th>SKILL ID</th>' +
                        '<th style="width:180px">收藏时间</th>' +
                        '<th style="width:120px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="3" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td colspan="3" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td>{{ row.skillId }}</td>' +
                            '<td>{{ formatDate(row.createdAt) }}</td>' +
                            '<td><el-button size="mini" type="danger" text @click="remove(row)">取消收藏</el-button></td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div></div>',
            data: function() { return { items: [], total: 0, page: 1, pageSize: 20, loading: false }; },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate,
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/favorites', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                remove: function(row) {
                    var self = this;
                    Utils.confirm('确定取消收藏?').then(function(yes) {
                        if (!yes) return;
                        api.delete('/favorites/' + row.skillId).then(function() { Utils.showSuccess('已取消收藏'); self.load(); });
                    });
                }
            }
        });
    }
    return { render: render };
})();
