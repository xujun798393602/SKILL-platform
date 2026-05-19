/* Logs Page */
window.LogsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="logs-mount"></div>';
        new Vue({
            el: '#logs-mount',
            template:
                '<div><h2>操作日志</h2>' +
                '<el-form :inline="true" style="margin:16px 0">' +
                    '<el-form-item label="类型"><el-select v-model="filters.type" clearable><el-option label="操作" value="operation"></el-option><el-option label="错误" value="error"></el-option><el-option label="安全" value="security"></el-option></el-select></el-form-item>' +
                    '<el-form-item label="开始"><el-date-picker v-model="filters.startDate" type="date" value-format="yyyy-MM-dd"></el-date-picker></el-form-item>' +
                    '<el-form-item label="结束"><el-date-picker v-model="filters.endDate" type="date" value-format="yyyy-MM-dd"></el-date-picker></el-form-item>' +
                    '<el-form-item><el-button type="primary" @click="load">查询</el-button></el-form-item>' +
                    '<el-form-item><el-button @click="exportLogs">导出</el-button></el-form-item>' +
                '</el-form>' +
                '<el-table :data="items" v-loading="loading" stripe>' +
                    '<el-table-column prop="action" label="操作" width="150"></el-table-column>' +
                    '<el-table-column prop="resourceType" label="资源类型" width="120"></el-table-column>' +
                    '<el-table-column prop="resourceId" label="资源ID" width="280"></el-table-column>' +
                    '<el-table-column prop="detail" label="详情" min-width="200"></el-table-column>' +
                    '<el-table-column prop="ipAddress" label="IP" width="130"></el-table-column>' +
                    '<el-table-column label="时间" width="160"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                '</el-table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div></div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false,
                    filters: { type: 'operation', startDate: '', endDate: '' }
                };
            },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate,
                load: function() {
                    var self = this; self.loading = true;
                    var params = { page: self.page, pageSize: self.pageSize };
                    if (self.filters.type) params.type = self.filters.type;
                    if (self.filters.startDate) params.startDate = self.filters.startDate;
                    if (self.filters.endDate) params.endDate = self.filters.endDate;
                    api.get('/logs', params).then(function(res) {
                        self.items = res.data.logs || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                exportLogs: function() {
                    api.post('/logs/export', { type: this.filters.type, format: 'csv' }).then(function(res) {
                        Utils.showSuccess('导出任务已创建: ' + res.data.taskId);
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
