/* Statistics Page */
window.StatisticsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="stats-mount"></div>';
        new Vue({
            el: '#stats-mount',
            template:
                '<div><h2>数据统计</h2>' +
                '<el-row :gutter="20" style="margin-top:16px">' +
                    '<el-col :span="6" v-for="card in cards" :key="card.label" style="margin-bottom:16px">' +
                        '<el-card shadow="hover"><div style="text-align:center"><div style="font-size:28px;font-weight:bold;color:#409eff">{{ card.value }}</div><div style="color:#999;margin-top:8px">{{ card.label }}</div></div></el-card>' +
                    '</el-col>' +
                '</el-row>' +
                '<el-card style="margin-top:16px"><h3 slot="header">热门SKILL</h3>' +
                    '<el-table :data="hotSkills" stripe>' +
                        '<el-table-column prop="name" label="名称"></el-table-column>' +
                        '<el-table-column prop="downloadCount" label="下载次数" width="120"></el-table-column>' +
                    '</el-table>' +
                '</el-card>' +
                '<el-card style="margin-top:16px"><h3 slot="header">趋势数据</h3>' +
                    '<el-form :inline="true" :model="dateRange" style="margin-bottom:12px">' +
                        '<el-form-item label="开始"><el-date-picker v-model="dateRange.startDate" type="date" value-format="yyyy-MM-dd"></el-date-picker></el-form-item>' +
                        '<el-form-item label="结束"><el-date-picker v-model="dateRange.endDate" type="date" value-format="yyyy-MM-dd"></el-date-picker></el-form-item>' +
                        '<el-form-item><el-button type="primary" @click="loadTrends">查询</el-button></el-form-item>' +
                    '</el-form>' +
                    '<el-table :data="trends" stripe>' +
                        '<el-table-column prop="date" label="日期"></el-table-column>' +
                        '<el-table-column prop="uploads" label="上传数"></el-table-column>' +
                        '<el-table-column prop="downloads" label="下载数"></el-table-column>' +
                    '</el-table>' +
                '</el-card></div>',
            data: function() {
                return {
                    cards: [
                        { label: 'SKILL总数', value: 0 }, { label: '用户总数', value: 0 },
                        { label: '下载总数', value: 0 }, { label: '待审核', value: 0 }
                    ],
                    hotSkills: [], trends: [], dateRange: { startDate: '', endDate: '' }
                };
            },
            mounted: function() { this.loadDashboard(); this.loadHotSkills(); },
            methods: {
                loadDashboard: function() {
                    var self = this;
                    api.get('/statistics/dashboard').then(function(res) {
                        var d = res.data;
                        self.cards[0].value = d.totalSkills || 0;
                        self.cards[1].value = d.totalUsers || 0;
                        self.cards[2].value = d.totalDownloads || 0;
                        self.cards[3].value = d.pendingReviews || 0;
                    });
                },
                loadHotSkills: function() {
                    var self = this;
                    api.get('/statistics/hot-skills').then(function(res) {
                        self.hotSkills = (res.data || {}).hotSkills || [];
                    });
                },
                loadTrends: function() {
                    var self = this;
                    api.get('/statistics/trends', self.dateRange).then(function(res) {
                        self.trends = (res.data || {}).trends || [];
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
