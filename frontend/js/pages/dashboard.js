/* Dashboard Page - Admin Statistics */
window.DashboardPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="dashboard-mount"></div>';

        new Vue({
            el: '#dashboard-mount',
            template:
                '<div>' +
                    '<h2>仪表盘</h2>' +
                    '<el-row :gutter="20" style="margin-top:20px">' +
                        '<el-col :span="8" v-for="card in cards" :key="card.label" style="margin-bottom:20px">' +
                            '<el-card shadow="hover">' +
                                '<div class="stat-card">' +
                                    '<div class="stat-icon" :style="{background:card.color}"><i :class="card.icon"></i></div>' +
                                    '<div class="stat-info"><div class="stat-value">{{ card.value }}</div><div class="stat-label">{{ card.label }}</div></div>' +
                                '</div>' +
                            '</el-card>' +
                        '</el-col>' +
                    '</el-row>' +
                '</div>',
            data: function() {
                return {
                    stats: {},
                    cards: [
                        { label: 'SKILL总数', value: 0, icon: 'el-icon-folder', color: '#409eff' },
                        { label: '用户总数', value: 0, icon: 'el-icon-user', color: '#67c23a' },
                        { label: '下载总数', value: 0, icon: 'el-icon-download', color: '#e6a23c' },
                        { label: '部署总数', value: 0, icon: 'el-icon-upload', color: '#f56c6c' },
                        { label: '待审核', value: 0, icon: 'el-icon-check', color: '#909399' },
                        { label: '今日活跃', value: 0, icon: 'el-icon-s-data', color: '#b37feb' }
                    ]
                };
            },
            mounted: function() { this.load(); },
            methods: {
                load: function() {
                    var self = this;
                    api.get('/statistics/dashboard').then(function(res) {
                        var d = res.data;
                        self.cards[0].value = d.totalSkills || 0;
                        self.cards[1].value = d.totalUsers || 0;
                        self.cards[2].value = d.totalDownloads || 0;
                        self.cards[3].value = d.totalDeployments || 0;
                        self.cards[4].value = d.pendingReviews || 0;
                        self.cards[5].value = d.todayActiveUsers || 0;
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
