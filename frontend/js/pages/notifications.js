/* Notifications Page */
window.NotificationsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="notif-mount"></div>';
        new Vue({
            el: '#notif-mount',
            template:
                '<div><div style="display:flex;justify-content:space-between;align-items:center"><h2>消息通知</h2>' +
                '<el-button size="small" @click="markAllRead">全部已读</el-button></div>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:80px">状态</th>' +
                        '<th style="width:120px">类型</th>' +
                        '<th>标题</th>' +
                        '<th>内容</th>' +
                        '<th style="width:160px">时间</th>' +
                        '<th style="width:100px">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="6" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td colspan="6" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td><el-tag :type="row.isRead?\'info\':\'danger\'" size="small">{{ row.isRead?"已读":"未读" }}</el-tag></td>' +
                            '<td>{{ row.type }}</td>' +
                            '<td>{{ row.title }}</td>' +
                            '<td>{{ row.content }}</td>' +
                            '<td>{{ formatDate(row.createdAt) }}</td>' +
                            '<td><el-button size="mini" type="text" @click="markRead(row)" v-if="!row.isRead">标记已读</el-button></td>' +
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
