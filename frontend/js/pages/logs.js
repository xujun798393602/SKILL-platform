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
                '<table class="data-table">' +
                    '<thead><tr>' +
                        '<th style="width:150px">操作</th>' +
                        '<th style="width:120px">资源类型</th>' +
                        '<th style="width:280px">资源ID</th>' +
                        '<th>详情</th>' +
                        '<th style="width:130px">IP</th>' +
                        '<th style="width:160px">时间</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td colspan="6" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td colspan="6" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td>{{ row.action }}</td>' +
                            '<td>{{ row.resourceType }}</td>' +
                            '<td>{{ row.resourceId }}</td>' +
                            '<td>{{ row.detail }}</td>' +
                            '<td>{{ row.ipAddress }}</td>' +
                            '<td>{{ formatDate(row.createdAt) }}</td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
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
