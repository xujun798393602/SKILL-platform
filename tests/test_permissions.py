"""Tests for F002 - 权限管理服务 (TC-5 ~ TC-8)"""


class TestTC5_AdminPermissionCheck:
    def test_admin_permission_check(self, client, admin_token, seed_users):
        resp = client.post('/api/v1/auth/check-permission', json={
            'permission': 'skill:upload',
        }, headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        data = resp.get_json()
        assert data['data']['allowed'] is True


class TestTC6_NormalUserAccessAdminEndpoint:
    def test_normal_user_forbidden(self, client, user_token):
        resp = client.get('/api/v1/users', headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403
        data = resp.get_json()
        assert data['code'] == 'PERM001'


class TestTC7_InvalidToken:
    def test_invalid_token(self, client):
        resp = client.post('/api/v1/auth/check-permission', json={
            'permission': 'skill:upload',
        }, headers={'Authorization': 'Bearer invalid-token'})
        assert resp.status_code == 401


class TestTC8_NoRoleUser:
    def test_no_role_user_permission(self, client, db, seed_roles):
        password_hash = bcrypt.hashpw('SecurePass123!'.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        from app.models.user import User
        user = User(employee_id='NOROLE', name='无角色', email='norole@example.com',
                     password_hash=password_hash, department='测试部', status='active')
        db.session.add(user)
        db.session.commit()

        resp = client.post('/api/v1/auth/login', json={
            'employeeId': 'NOROLE',
            'password': 'SecurePass123!',
        })
        token = resp.get_json()['data']['accessToken']

        resp = client.post('/api/v1/auth/check-permission', json={
            'permission': 'skill:view',
        }, headers={'Authorization': f'Bearer {token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['allowed'] is False


import bcrypt
