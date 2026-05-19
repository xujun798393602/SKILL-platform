/* Deployments Page */
window.DeploymentsPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="deploy-mount"></div>';
        new Vue({
            el: '#deploy-mount',
            template:
                '<div><h2>部署管理</h2>' +
                '<el-alert title="输入部署ID查看详情或回滚" type="info" show-icon style="margin:16px 0"></el-alert>' +
                '<el-form :inline="true"><el-form-item label="部署ID">' +
                    '<el-input v-model="deployId" placeholder="输入部署ID"></el-input>' +
                '</el-form-item><el-form-item>' +
                    '<el-button type="primary" @click="load">查询</el-button>' +
                '</el-form-item></el-form>' +
                '<el-card v-if="detail" style="margin-top:16px">' +
                    '<el-descriptions :column="2" border>' +
                        '<el-descriptions-item label="部署ID">{{ detail.id }}</el-descriptions-item>' +
                        '<el-descriptions-item label="SKILL ID">{{ detail.skillId }}</el-descriptions-item>' +
                        '<el-descriptions-item label="状态"><span v-html="statusTag(detail.status)"></span></el-descriptions-item>' +
                        '<el-descriptions-item label="平台">{{ detail.platform }}</el-descriptions-item>' +
                        '<el-descriptions-item label="开始时间">{{ formatDate(detail.startedAt) }}</el-descriptions-item>' +
                        '<el-descriptions-item label="完成时间">{{ formatDate(detail.completedAt) }}</el-descriptions-item>' +
                        '<el-descriptions-item label="错误信息" :span="2">{{ detail.errorMessage || \'-\' }}</el-descriptions-item>' +
                    '</el-descriptions>' +
                    '<el-button type="warning" style="margin-top:12px" @click="rollback" v-if="detail.status!==\'rolled_back\'">回滚</el-button>' +
                '</el-card></div>',
            data: function() { return { deployId: '', detail: null }; },
            methods: {
                formatDate: Utils.formatDate, statusTag: Utils.statusTag,
                load: function() {
                    var self = this;
                    if (!self.deployId) { Utils.showWarning('请输入部署ID'); return; }
                    api.get('/deployments/' + self.deployId).then(function(res) {
                        self.detail = res.data;
                    }).catch(function(err) { Utils.showError(err.message); self.detail = null; });
                },
                rollback: function() {
                    var self = this;
                    Utils.confirm('确定回滚此部署?').then(function(yes) {
                        if (!yes) return;
                        api.post('/deployments/' + self.deployId + '/rollback').then(function() {
                            Utils.showSuccess('回滚成功'); self.load();
                        }).catch(function(err) { Utils.showError(err.message); });
                    });
                }
            }
        });
    }
    return { render: render };
})();
