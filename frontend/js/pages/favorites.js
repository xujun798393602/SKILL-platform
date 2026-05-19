/* Favorites Page */
window.FavoritesPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="fav-mount"></div>';
        new Vue({
            el: '#fav-mount',
            template:
                '<div><h2>我的收藏</h2>' +
                '<el-table :data="items" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column prop="skillId" label="SKILL ID"></el-table-column>' +
                    '<el-table-column label="收藏时间" width="180"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                    '<el-table-column label="操作" width="120">' +
                        '<template slot-scope="s"><el-button size="mini" type="danger" text @click="remove(s.row)">取消收藏</el-button></template>' +
                    '</el-table-column>' +
                '</el-table>' +
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
