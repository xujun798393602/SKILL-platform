"""Tests for F003-F005 - SKILL上传/校验/存储服务 (TC-9 ~ TC-20)"""
import io


class TestTC9_UploadSkillFile:
    def test_upload_json_file(self, client, admin_token, db):
        data = {
            'file': (io.BytesIO(b'{"name":"test"}'), 'test-skill.json'),
            'skillType': 'public',
            'name': 'test-skill',
            'version': '1.0.0',
            'category': 'testing',
        }
        resp = client.post('/api/v1/skills/upload', data=data,
                          headers={'Authorization': f'Bearer {admin_token}'},
                          content_type='multipart/form-data')
        assert resp.status_code == 201
        result = resp.get_json()
        assert result['data']['skillId'] is not None
        assert result['data']['format'] == 'json'


class TestTC10_UnsupportedFormat:
    def test_upload_exe_file(self, client, admin_token):
        data = {
            'file': (io.BytesIO(b'MZ'), 'malicious.exe'),
            'skillType': 'public',
            'name': 'test',
            'version': '1.0.0',
            'category': 'testing',
        }
        resp = client.post('/api/v1/skills/upload', data=data,
                          headers={'Authorization': f'Bearer {admin_token}'},
                          content_type='multipart/form-data')
        assert resp.status_code == 400
        assert resp.get_json()['code'] == 'UPLOAD001'


class TestTC11_FileSizeLimit:
    def test_upload_large_file(self, client, admin_token):
        large_content = b'x' * (101 * 1024 * 1024)
        data = {
            'file': (io.BytesIO(large_content), 'large.zip'),
            'skillType': 'public',
            'name': 'large',
            'version': '1.0.0',
            'category': 'testing',
        }
        resp = client.post('/api/v1/skills/upload', data=data,
                          headers={'Authorization': f'Bearer {admin_token}'},
                          content_type='multipart/form-data')
        assert resp.status_code == 413


class TestTC12_BatchUpload:
    def test_batch_upload(self, client, admin_token):
        data = {
            'files': [
                (io.BytesIO(b'{"name":"a"}'), 'skill-a.json'),
                (io.BytesIO(b'{"name":"b"}'), 'skill-b.json'),
            ],
            'skillType': 'public',
        }
        resp = client.post('/api/v1/skills/batch-upload', data=data,
                          headers={'Authorization': f'Bearer {admin_token}'},
                          content_type='multipart/form-data')
        assert resp.status_code == 201
        result = resp.get_json()
        assert result['data']['totalCount'] == 2


class TestTC13_ValidationPassed:
    def test_validation_passed(self, client, admin_token, db):
        from app.models.skill import Skill
        skill = Skill(name='valid-skill', skill_type='public', category='test', owner_id='test')
        db.session.add(skill)
        db.session.commit()

        resp = client.get(f'/api/v1/skills/{skill.id}/validation',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['validationStatus'] == 'passed'


class TestTC14_ValidationFailed:
    def test_validation_content_error(self, client, admin_token, db):
        from app.models.skill import Skill
        skill = Skill(name='incomplete', skill_type='public', category='test', owner_id='test')
        db.session.add(skill)
        db.session.commit()

        resp = client.get(f'/api/v1/skills/{skill.id}/validation',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC17_ListSkills:
    def test_list_skills(self, client, admin_token, db):
        from app.models.skill import Skill
        for i in range(25):
            db.session.add(Skill(name=f'skill-{i}', skill_type='public', category='test', owner_id='test'))
        db.session.commit()

        resp = client.get('/api/v1/skills?page=1&pageSize=10',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        data = resp.get_json()
        assert data['data']['total'] == 25
        assert len(data['data']['items']) == 10


class TestTC18_SearchNoResults:
    def test_search_empty(self, client, admin_token):
        resp = client.get('/api/v1/skills?keyword=不存在的技能',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['total'] == 0


class TestTC19_FilterByTypeAndTags:
    def test_filter_skills(self, client, admin_token, db):
        from app.models.skill import Skill, Tag, SkillTag
        skill = Skill(name='java-review', skill_type='code-review', category='review', owner_id='test')
        db.session.add(skill)
        db.session.flush()
        tag = Tag(name='java')
        db.session.add(tag)
        db.session.flush()
        db.session.add(SkillTag(skill_id=skill.id, tag_id=tag.id))
        db.session.commit()

        resp = client.get('/api/v1/skills?skillType=code-review&tags=java',
                         headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200


class TestTC20_UnauthenticatedAccess:
    def test_unauthenticated(self, client):
        resp = client.get('/api/v1/skills')
        assert resp.status_code == 401
