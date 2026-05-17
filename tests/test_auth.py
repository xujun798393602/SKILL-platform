"""Tests for F001 - 用户认证服务 (TC-1 ~ TC-4)"""
import bcrypt
from app.models.user import User


class TestTC1_LoginSuccess:
    def test_login_success(self, client, seed_users):
        resp = client.post('/api/v1/auth/login', json={
            'employeeId': 'USER001',
            'password': 'SecurePass123!',
        })
        assert resp.status_code == 200
        data = resp.get_json()
        assert data['code'] == 'Success'
        assert 'accessToken' in data['data']
        assert 'refreshToken' in data['data']
        assert data['data']['expiresIn'] == 7200
        assert data['data']['userInfo']['email'] == 'testuser@example.com'


class TestTC2_LoginWrongPassword:
    def test_login_wrong_password(self, client, seed_users, db):
        resp = client.post('/api/v1/auth/login', json={
            'employeeId': 'USER001',
            'password': 'WrongPass999!',
        })
        assert resp.status_code == 401
        data = resp.get_json()
        assert data['code'] == 'AUTH001'


class TestTC3_AccountLockAfter5Failures:
    def test_account_lock(self, client, seed_users, db):
        user = User.query.filter_by(employee_id='USER001').first()
        user.login_fail_count = 4
        db.session.commit()

        resp = client.post('/api/v1/auth/login', json={
            'employeeId': 'USER001',
            'password': 'WrongPass999!',
        })
        assert resp.status_code == 423
        data = resp.get_json()
        assert data['code'] == 'AUTH003'

        db.session.refresh(user)
        assert user.status == 'locked'


class TestTC4_ExpiredRefreshToken:
    def test_expired_refresh_token(self, client, seed_users):
        resp = client.post('/api/v1/auth/refresh', json={
            'refreshToken': 'invalid-expired-token',
        })
        assert resp.status_code == 401
        data = resp.get_json()
        assert data['code'] == 'AUTH004'
