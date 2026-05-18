"""Tests for F016-F021 - 图谱/统计/日志/配置/帮助/反馈服务"""
from app.models.skill import Skill
from app.models.system import SystemConfig, HelpDoc, Feedback


class TestTC25_GetRelations:
    def test_get_relations(self, client, admin_token, db):
        skill = Skill(name='graph-skill', skill_type='public', category='test', owner_id='test')
        db.session.add(skill)
        db.session.commit()

        resp = client.get(f'/api/v1/skills/{skill.id}/relations',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC26_RelationsNonexistent:
    def test_relations_nonexistent(self, client, admin_token):
        resp = client.get('/api/v1/skills/nonexistent/relations',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 404


class TestTC29_Dashboard:
    def test_dashboard(self, client, admin_token):
        resp = client.get('/api/v1/statistics/dashboard',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        data = resp.get_json()['data']
        assert 'totalSkills' in data
        assert 'totalUsers' in data


class TestTC31_NormalUserDashboard:
    def test_normal_user_dashboard(self, client, user_token):
        resp = client.get('/api/v1/statistics/dashboard',
                         headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403


class TestTC33_QueryLogs:
    def test_query_logs(self, client, admin_token):
        resp = client.get('/api/v1/logs',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC35_InvalidLogType:
    def test_invalid_log_type(self, client, admin_token):
        resp = client.get('/api/v1/logs?type=invalid_type',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 400


class TestTC37_GetConfigs:
    def test_get_configs(self, client, admin_token, db):
        config = SystemConfig(config_key='test.key', config_value='test', description='测试')
        db.session.add(config)
        db.session.commit()

        resp = client.get('/api/v1/configs',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC38_UpdateReadonlyConfig:
    def test_update_readonly(self, client, admin_token, db):
        config = SystemConfig(config_key='app.version', config_value='1.0.0',
                            is_readonly=True, description='版本')
        db.session.add(config)
        db.session.commit()

        resp = client.put('/api/v1/configs/app.version', json={'value': '2.0.0'},
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 403


class TestTC40_SensitiveConfig:
    def test_sensitive_config_masked(self, client, admin_token, db):
        config = SystemConfig(config_key='db.password', config_value='secret123',
                            is_sensitive=True, description='数据库密码')
        db.session.add(config)
        db.session.commit()

        resp = client.get('/api/v1/configs/db.password',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['value'] == '******'


class TestTC41_ListHelpDocs:
    def test_list_help_docs(self, client, admin_token, db):
        doc = HelpDoc(title='帮助文档', content='内容', doc_type='guide', created_by='test')
        db.session.add(doc)
        db.session.commit()

        resp = client.get('/api/v1/help-docs',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC42_HelpDocNotFound:
    def test_help_doc_not_found(self, client, admin_token):
        resp = client.get('/api/v1/help-docs/nonexistent',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 404


class TestTC45_SubmitFeedback:
    def test_submit_feedback(self, client, user_token):
        resp = client.post('/api/v1/feedbacks', json={
            'type': 'suggestion',
            'title': '建议',
            'content': '希望能增加批量导出功能',
        }, headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 201


class TestTC46_FeedbackTooLong:
    def test_feedback_too_long(self, client, user_token):
        resp = client.post('/api/v1/feedbacks', json={
            'type': 'suggestion',
            'title': '建议',
            'content': 'x' * 1001,
        }, headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 400


class TestTC48_EmptyFeedback:
    def test_empty_feedback(self, client, user_token):
        resp = client.post('/api/v1/feedbacks', json={
            'type': 'suggestion',
            'title': '建议',
            'content': '',
        }, headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 400


class TestTC27_CircularDependencyRejected:
    def test_circular_dependency(self, client, admin_token, db):
        skill_a = Skill(name='skill-a', skill_type='public', category='test', owner_id='test')
        skill_b = Skill(name='skill-b', skill_type='public', category='test', owner_id='test')
        db.session.add_all([skill_a, skill_b])
        db.session.flush()

        from app.models.skill import SkillRelation
        rel = SkillRelation(source_skill_id=skill_a.id, target_skill_id=skill_b.id, relation_type='depends_on')
        db.session.add(rel)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill_b.id}/relations', json={
            'targetSkillId': skill_a.id,
            'relationType': 'depends_on',
        }, headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 400
        assert resp.get_json()['code'] == 'GRAPH002'


class TestTC28_QueryDepth3Relations:
    def test_depth_relations(self, client, admin_token, db):
        skills = []
        for i in range(4):
            s = Skill(name=f'depth-skill-{i}', skill_type='public', category='test', owner_id='test')
            db.session.add(s)
            skills.append(s)
        db.session.flush()

        from app.models.skill import SkillRelation
        for i in range(3):
            db.session.add(SkillRelation(
                source_skill_id=skills[i].id,
                target_skill_id=skills[i + 1].id,
                relation_type='depends_on'
            ))
        db.session.commit()

        resp = client.get(f'/api/v1/skills/{skills[0].id}/relations?maxDepth=3',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC30_InvalidTimeRangeTrends:
    def test_invalid_date_range(self, client, admin_token):
        resp = client.get('/api/v1/statistics/trends?startDate=2026-06-01&endDate=2026-05-01',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 400


class TestTC32_NoDataTimeRangeHotSkills:
    def test_empty_hot_skills(self, client, admin_token):
        resp = client.get('/api/v1/statistics/hot-skills?startDate=2030-01-01&endDate=2030-01-31',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        data = resp.get_json()['data']
        assert data['total'] == 0


class TestTC34_TimeRangeOver90Days:
    def test_over_90_days(self, client, admin_token):
        resp = client.get('/api/v1/logs?type=operation&startDate=2026-01-01&endDate=2026-05-17',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 400
        assert resp.get_json()['code'] == 'LOG001'


class TestTC36_ExportLogs:
    def test_export_logs(self, client, admin_token):
        resp = client.post('/api/v1/logs/export', json={
            'type': 'operation',
            'startDate': '2026-04-17',
            'endDate': '2026-05-17',
            'format': 'csv',
        }, headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert 'taskId' in resp.get_json()['data']


class TestTC39_ConcurrentUpdateConfig:
    def test_config_update(self, client, admin_token, db):
        config = SystemConfig(config_key='upload.maxSize', config_value='100MB', description='上传大小限制')
        db.session.add(config)
        db.session.commit()

        resp = client.put('/api/v1/configs/upload.maxSize', json={'value': '200MB'},
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC43_NonAdminDeleteHelpDoc:
    def test_non_admin_delete_help_doc(self, client, user_token, db):
        doc = HelpDoc(title='待删除文档', content='内容', doc_type='guide', created_by='test')
        db.session.add(doc)
        db.session.commit()

        resp = client.delete(f'/api/v1/help-docs/{doc.id}',
                            headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403


class TestTC44_SearchNoMatchingHelpDocs:
    def test_search_no_match(self, client, admin_token):
        resp = client.get('/api/v1/help-docs?keyword=xyzabc123notexist',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['total'] == 0


class TestTC47_NonAdminReplyFeedback:
    def test_non_admin_reply(self, client, user_token, db):
        fb = Feedback(user_id='test', type='suggestion', title='测试', content='内容', status='pending')
        db.session.add(fb)
        db.session.commit()

        resp = client.post(f'/api/v1/feedbacks/{fb.id}/reply', json={'content': '回复'},
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403
