/* Feedbacks Page */
window.FeedbacksPage = (function() {
    function render(container) {
        container.innerHTML = '<div id="fb-mount"></div>';
        new Vue({
            el: '#fb-mount',
            template:
                '<div><div style="display:flex;justify-content:space-between;align-items:center"><h2>反馈管理</h2>' +
                '<el-button type="primary" @click="showSubmit">提交反馈</el-button></div>' +
                '<table class="data-table" style="margin-top:16px">' +
                    '<thead><tr>' +
                        '<th style="width:80px">类型</th>' +
                        '<th>标题</th>' +
                        '<th>内容</th>' +
                        '<th style="width:90px">状态</th>' +
                        '<th>回复</th>' +
                        '<th style="width:160px">时间</th>' +
                        '<th style="width:100px" v-if="isAdmin">操作</th>' +
                    '</tr></thead>' +
                    '<tbody>' +
                        '<tr v-if="loading"><td :colspan="isAdmin?7:6" style="text-align:center;padding:20px">加载中...</td></tr>' +
                        '<tr v-else-if="!items.length"><td :colspan="isAdmin?7:6" style="text-align:center;padding:20px;color:#999">暂无数据</td></tr>' +
                        '<template v-else><tr v-for="row in items" :key="row.id">' +
                            '<td>{{ row.type }}</td>' +
                            '<td>{{ row.title }}</td>' +
                            '<td>{{ row.content }}</td>' +
                            '<td><span v-html="statusTag(row.status)"></span></td>' +
                            '<td>{{ row.reply }}</td>' +
                            '<td>{{ formatDate(row.createdAt) }}</td>' +
                            '<td v-if="isAdmin"><el-button size="mini" type="text" @click="showReply(row)" v-if="row.status!==\'replied\'">回复</el-button></td>' +
                        '</tr></template>' +
                    '</tbody>' +
                '</table>' +
                '<div style="text-align:right;margin-top:16px"><el-pagination background layout="total, prev, pager, next" :total="total" :current-page="page" :page-size="pageSize" @current-change="onPage"></el-pagination></div>' +
                '<el-dialog title="提交反馈" :visible.sync="submitVisible" width="500px">' +
                    '<el-form :model="form" label-width="80px">' +
                        '<el-form-item label="类型"><el-select v-model="form.type"><el-option label="建议" value="suggestion"></el-option><el-option label="问题" value="bug"></el-option><el-option label="其他" value="other"></el-option></el-select></el-form-item>' +
                        '<el-form-item label="标题"><el-input v-model="form.title"></el-input></el-form-item>' +
                        '<el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" maxlength="1000" show-word-limit></el-input></el-form-item>' +
                    '</el-form>' +
                    '<span slot="footer"><el-button @click="submitVisible=false">取消</el-button><el-button type="primary" @click="doSubmit">提交</el-button></span>' +
                '</el-dialog>' +
                '<el-dialog title="回复反馈" :visible.sync="replyVisible" width="400px">' +
                    '<el-input v-model="replyContent" type="textarea" :rows="3" placeholder="回复内容"></el-input>' +
                    '<span slot="footer"><el-button @click="replyVisible=false">取消</el-button><el-button type="primary" @click="doReply">回复</el-button></span>' +
                '</el-dialog></div>',
            data: function() {
                return {
                    items: [], total: 0, page: 1, pageSize: 20, loading: false, isAdmin: Utils.isAdmin(),
                    submitVisible: false, replyVisible: false, replyId: '', replyContent: '',
                    form: { type: 'suggestion', title: '', content: '' }
                };
            },
            mounted: function() { this.load(); },
            methods: {
                formatDate: Utils.formatDate, statusTag: Utils.statusTag,
                load: function() {
                    var self = this; self.loading = true;
                    api.get('/feedbacks', { page: self.page, pageSize: self.pageSize }).then(function(res) {
                        self.items = res.data.items || []; self.total = res.data.total || 0;
                    }).catch(function(err) { Utils.showError(err.message); }).finally(function() { self.loading = false; });
                },
                onPage: function(p) { this.page = p; this.load(); },
                showSubmit: function() { this.form = { type: 'suggestion', title: '', content: '' }; this.submitVisible = true; },
                doSubmit: function() {
                    var self = this;
                    if (!self.form.title || !self.form.content) { Utils.showWarning('请填写标题和内容'); return; }
                    api.post('/feedbacks', self.form).then(function() {
                        Utils.showSuccess('提交成功'); self.submitVisible = false; self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                showReply: function(row) { this.replyId = row.id; this.replyContent = ''; this.replyVisible = true; },
                doReply: function() {
                    var self = this;
                    api.post('/feedbacks/' + self.replyId + '/reply', { content: self.replyContent }).then(function() {
                        Utils.showSuccess('回复成功'); self.replyVisible = false; self.load();
                    }).catch(function(err) { Utils.showError(err.message); });
                }
            }
        });
    }
    return { render: render };
})();
