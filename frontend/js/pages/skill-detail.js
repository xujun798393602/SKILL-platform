/* Skill Detail Page */
window.SkillDetailPage = (function() {
    function render(container, params) {
        var skillId = params.id;
        container.innerHTML = '<div id="detail-mount"></div>';

        new Vue({
            el: '#detail-mount',
            template:
                '<div v-loading="loading">' +
                    '<div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">' +
                        '<div><el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button> <h2 style="display:inline">{{ skill.name }}</h2></div>' +
                        '<div>' +
                            '<el-button type="success" icon="el-icon-download" @click="download">下载</el-button>' +
                            '<el-button type="primary" icon="el-icon-upload" @click="deploy">部署</el-button>' +
                            '<el-button icon="el-icon-share" @click="share">分享</el-button>' +
                            '<el-button icon="el-icon-star-off" @click="favorite">收藏</el-button>' +
                        '</div>' +
                    '</div>' +
                    '<el-tabs v-model="activeTab">' +
                        '<el-tab-pane label="基本信息" name="info">' +
                            '<el-descriptions :column="2" border>' +
                                '<el-descriptions-item label="名称">{{ skill.name }}</el-descriptions-item>' +
                                '<el-descriptions-item label="类型">{{ skill.skillType }}</el-descriptions-item>' +
                                '<el-descriptions-item label="分类">{{ skill.category }}</el-descriptions-item>' +
                                '<el-descriptions-item label="版本">{{ skill.currentVersion }}</el-descriptions-item>' +
                                '<el-descriptions-item label="状态"><span v-html="statusTag(skill.status)"></span></el-descriptions-item>' +
                                '<el-descriptions-item label="评分">{{ skill.avgRating ? skill.avgRating.toFixed(1) : \'-\' }} ({{ skill.ratingCount || 0 }}条)</el-descriptions-item>' +
                                '<el-descriptions-item label="下载次数">{{ skill.downloadCount || 0 }}</el-descriptions-item>' +
                                '<el-descriptions-item label="文件大小">{{ formatSize(skill.fileSize) }}</el-descriptions-item>' +
                                '<el-descriptions-item label="开发者">{{ skill.developerName }}</el-descriptions-item>' +
                                '<el-descriptions-item label="创建时间">{{ formatDate(skill.createdAt) }}</el-descriptions-item>' +
                                '<el-descriptions-item label="描述" :span="2">{{ skill.description || \'-\' }}</el-descriptions-item>' +
                            '</el-descriptions>' +
                            // Validation
                            '<div style="margin-top:20px"><h3>校验状态</h3>' +
                                '<el-tag v-for="c in validation.checks" :key="c.type" :type="c.passed?\'success\':\'danger\'" style="margin-right:8px">{{ c.type }}: {{ c.passed?"通过":"失败" }}</el-tag>' +
                            '</div>' +
                            // Rating
                            '<div style="margin-top:20px"><h3>评价</h3>' +
                                '<el-rate v-model="rating.score" :max="5"></el-rate>' +
                                '<el-input v-model="rating.comment" type="textarea" placeholder="评价内容" style="margin:10px 0"></el-input>' +
                                '<el-button type="primary" size="small" @click="submitRating">提交评价</el-button>' +
                            '</div>' +
                            // Edit
                            '<div style="margin-top:20px" v-if="isOwner"><h3>编辑</h3>' +
                                '<el-form :model="editForm" label-width="80px">' +
                                    '<el-form-item label="名称"><el-input v-model="editForm.name"></el-input></el-form-item>' +
                                    '<el-form-item label="描述"><el-input v-model="editForm.description" type="textarea"></el-input></el-form-item>' +
                                    '<el-form-item label="分类"><el-input v-model="editForm.category"></el-input></el-form-item>' +
                                    '<el-form-item><el-button type="primary" @click="saveEdit">保存</el-button></el-form-item>' +
                                '</el-form>' +
                            '</div>' +
                        '</el-tab-pane>' +
                        '<el-tab-pane label="版本管理" name="versions">' +
                            '<el-table :data="versions" stripe>' +
                                '<el-table-column prop="version" label="版本" width="120"></el-table-column>' +
                                '<el-table-column label="状态" width="100"><template slot-scope="s"><el-tag :type="s.row.isActive?\'success\':\'info\'" size="small">{{ s.row.isActive?"当前":"历史" }}</el-tag></template></el-table-column>' +
                                '<el-table-column prop="tag" label="标签" width="120"></el-table-column>' +
                                '<el-table-column label="创建时间"><template slot-scope="s">{{ formatDate(s.row.createdAt) }}</template></el-table-column>' +
                                '<el-table-column label="操作" width="200">' +
                                    '<template slot-scope="s">' +
                                        '<el-button size="mini" type="text" @click="rollback(s.row)" v-if="!s.row.isActive">回滚</el-button>' +
                                        '<el-button size="mini" type="text" @click="showTagDialog(s.row)">设置标签</el-button>' +
                                    '</template>' +
                                '</el-table-column>' +
                            '</el-table>' +
                        '</el-tab-pane>' +
                        '<el-tab-pane label="关联关系" name="relations">' +
                            '<el-button type="primary" size="small" @click="showAddRelation" style="margin-bottom:12px">添加关联</el-button>' +
                            '<el-table :data="relations" stripe>' +
                                '<el-table-column prop="targetSkillName" label="目标SKILL"></el-table-column>' +
                                '<el-table-column prop="relationType" label="关系类型" width="120"></el-table-column>' +
                                '<el-table-column prop="depth" label="深度" width="80"></el-table-column>' +
                            '</el-table>' +
                        '</el-tab-pane>' +
                    '</el-tabs>' +
                    // Tag dialog
                    '<el-dialog title="设置版本标签" :visible.sync="tagDialogVisible" width="400px">' +
                        '<el-input v-model="tagValue" placeholder="标签名称"></el-input>' +
                        '<span slot="footer"><el-button @click="tagDialogVisible=false">取消</el-button><el-button type="primary" @click="setTag">确定</el-button></span>' +
                    '</el-dialog>' +
                    // Add relation dialog
                    '<el-dialog title="添加关联" :visible.sync="relDialogVisible" width="400px">' +
                        '<el-form :model="relForm" label-width="80px">' +
                            '<el-form-item label="目标ID"><el-input v-model="relForm.targetSkillId"></el-input></el-form-item>' +
                            '<el-form-item label="类型"><el-select v-model="relForm.relationType"><el-option label="依赖" value="depends_on"></el-option><el-option label="相关" value="related_to"></el-option></el-select></el-form-item>' +
                        '</el-form>' +
                        '<span slot="footer"><el-button @click="relDialogVisible=false">取消</el-button><el-button type="primary" @click="addRelation">确定</el-button></span>' +
                    '</el-dialog>' +
                '</div>',
            data: function() {
                return {
                    skillId: skillId, skill: {}, loading: false, activeTab: 'info',
                    validation: { checks: [] }, versions: [], relations: [],
                    rating: { score: 5, comment: '' },
                    editForm: { name: '', description: '', category: '' },
                    isOwner: false,
                    tagDialogVisible: false, tagValue: '', tagVersion: '',
                    relDialogVisible: false, relForm: { targetSkillId: '', relationType: 'depends_on' }
                };
            },
            mounted: function() { this.loadAll(); },
            methods: {
                formatDate: Utils.formatDate, formatSize: Utils.formatSize, statusTag: Utils.statusTag,
                loadAll: function() {
                    this.loadSkill(); this.loadValidation(); this.loadVersions(); this.loadRelations();
                },
                loadSkill: function() {
                    var self = this;
                    self.loading = true;
                    api.get('/skills/' + self.skillId).then(function(res) {
                        self.skill = res.data || {};
                        self.editForm = { name: self.skill.name, description: self.skill.description, category: self.skill.category };
                        var user = Utils.getUserInfo();
                        self.isOwner = self.skill.ownerId === user.id || Utils.isAdmin();
                    }).catch(function(err) { Utils.showError(err.message); })
                    .finally(function() { self.loading = false; });
                },
                loadValidation: function() {
                    var self = this;
                    api.get('/skills/' + self.skillId + '/validation').then(function(res) {
                        self.validation = res.data || { checks: [] };
                    });
                },
                loadVersions: function() {
                    var self = this;
                    api.get('/skills/' + self.skillId + '/versions').then(function(res) {
                        self.versions = (res.data || {}).versions || [];
                    });
                },
                loadRelations: function() {
                    var self = this;
                    api.get('/skills/' + self.skillId + '/relations').then(function(res) {
                        self.relations = (res.data || {}).relations || [];
                    });
                },
                download: function() {
                    api.download('/skills/' + this.skillId + '/download').then(function(blob) {
                        var url = URL.createObjectURL(blob);
                        var a = document.createElement('a'); a.href = url; a.download = 'skill.skill';
                        a.click(); URL.revokeObjectURL(url);
                    });
                },
                deploy: function() {
                    api.post('/skills/' + this.skillId + '/deploy', { platform: 'docker' }).then(function(res) {
                        Utils.showSuccess('部署任务已创建: ' + res.data.deploymentId);
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                share: function() {
                    api.post('/skills/' + this.skillId + '/share', { scope: 'public' }).then(function(res) {
                        Utils.showSuccess('分享链接已生成: ' + res.data.shareToken);
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                favorite: function() {
                    api.post('/favorites', { skillId: this.skillId }).then(function() {
                        Utils.showSuccess('收藏成功');
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                submitRating: function() {
                    api.post('/skills/' + this.skillId + '/ratings', this.rating).then(function() {
                        Utils.showSuccess('评价成功');
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                saveEdit: function() {
                    var self = this;
                    api.put('/skills/' + self.skillId, self.editForm).then(function() {
                        Utils.showSuccess('保存成功');
                        self.loadSkill();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                rollback: function(row) {
                    var self = this;
                    Utils.confirm('确定回滚到版本 ' + row.version + ' ?').then(function(yes) {
                        if (!yes) return;
                        api.post('/skills/' + self.skillId + '/versions/rollback', { targetVersion: row.version }).then(function() {
                            Utils.showSuccess('回滚成功');
                            self.loadAll();
                        }).catch(function(err) { Utils.showError(err.message); });
                    });
                },
                showTagDialog: function(row) { this.tagVersion = row.version; this.tagValue = row.tag || ''; this.tagDialogVisible = true; },
                setTag: function() {
                    var self = this;
                    api.put('/skills/' + self.skillId + '/versions/' + self.tagVersion + '/tag', { tag: self.tagValue }).then(function() {
                        Utils.showSuccess('标签设置成功');
                        self.tagDialogVisible = false;
                        self.loadVersions();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                showAddRelation: function() { this.relDialogVisible = true; },
                addRelation: function() {
                    var self = this;
                    api.post('/skills/' + self.skillId + '/relations', self.relForm).then(function() {
                        Utils.showSuccess('关联创建成功');
                        self.relDialogVisible = false;
                        self.loadRelations();
                    }).catch(function(err) { Utils.showError(err.message); });
                },
                goBack: function() { Router.navigate('/skills'); }
            }
        });
    }
    return { render: render };
})();
