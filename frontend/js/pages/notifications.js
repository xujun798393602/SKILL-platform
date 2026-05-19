/* Notifications Page */
window.NotificationsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="notif-mount"></div>';
        new Vue({
            el: '#notif-mount',
            template:
                '<div><div style="display:flex;justify-content:space-between;align-items:center"><h2>消息通知</h2>' +
                '<el-button size="small" @click="markAllRead">全部已读</el-button></div>' +
                '<el-table :data="items" v-loading="loading" stripe style="margin-top:16px">' +
                    '<el-table-column label="状态" width="80"><template slot-scope="s"><el-tag :type="s.row.isRead?\'info\':\'danger\'" size="small">{{ s.row.isRead?"已读":"未读" }}</el-tag></template></el-table-column>' +
                    '<el-table-column prop="type" label="类型" width="120"></el-table-column>' +
                    '<el-table-column prop="title" label="标题" min-width="150"></el-table-column>' +
                    '<el-table-column prop="content" label="内容" min-width="200"></el-table-column>' +
                    '<el-table-column label="时间" width="160"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                    '<el-table-column label="操作" width="100">' +
                        '<template slot-scope="s"><el-button size="mini" type="text" @click="markRead(s.row)" v-if="!s.row.isRead">标记已读</el-button></template>' +
                    '</el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div></div>',
            data: function() { return { items: [], total: 0, page: 1, pageSize: 20, loading: false }; },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate,
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/notifications', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                markRead: function(row) {
                    var self = this;
                    api.put('/notifications/' + row.id + '/read').then(function() { self.load(); });
                },
                markAllRead: function() {
                    var self = this;
                    api.put('/notifications/read-all').then(function(res) {
                        Utils.showSuccess('已标记' + (res.data.updatedCount || 0) + '条为已读');
                        self.load();
                    });
                }
            }
        });
    }
    return { render: render };
})();
