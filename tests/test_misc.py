"""Tests for F016-F021 - 图谱/统计/日志/配置/帮助/反馈服务"""
from app.models.skill import Skill
from app.models.system import SystemConfig, HelpDoc


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
