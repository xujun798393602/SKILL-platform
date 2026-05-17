import pytest
import bcrypt
from app import create_app, db as _db
from app.models.user import User, Role, UserRole, Permission, RolePermission


@pytest.fixture(scope='session')
def app():
    app = create_app('testing')
    return app


@pytest.fixture(scope='function')
def db(app):
    with app.app_context():
        _db.create_all()
        yield _db
        _db.session.rollback()
        _db.drop_all()


@pytest.fixture
def client(app, db):
    return app.test_client()


@pytest.fixture
def seed_roles(db):
    roles = {}
    for name, display in [('USER', '普通用户'), ('ADMIN', '管理员')]:
        role = Role(name=name, display_name=display, is_system=True)
        db.session.add(role)
        db.session.flush()
        roles[name] = role

    perm = Permission(code='skill:upload', name='上传SKILL', resource='skill', action='write')
    db.session.add(perm)
    db.session.flush()

    db.session.add(RolePermission(role_id=roles['ADMIN'].id, permission_id=perm.id))
    db.session.add(RolePermission(role_id=roles['USER'].id, permission_id=perm.id))
    db.session.commit()
    return roles


@pytest.fixture
def seed_users(db, seed_roles):
    password_hash = bcrypt.hashpw('SecurePass123!'.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    admin = User(
        employee_id='ADMIN001',
        name='管理员',
        email='admin@example.com',
        password_hash=password_hash,
        department='管理部',
        status='active',
    )
    db.session.add(admin)
    db.session.flush()
    db.session.add(UserRole(user_id=admin.id, role_id=seed_roles['ADMIN'].id))

    user = User(
        employee_id='USER001',
        name='测试用户',
        email='testuser@example.com',
        password_hash=password_hash,
        department='技术部',
        status='active',
    )
    db.session.add(user)
    db.session.flush()
    db.session.add(UserRole(user_id=user.id, role_id=seed_roles['USER'].id))

    db.session.commit()
    return {'admin': admin, 'user': user}


@pytest.fixture
def admin_token(client, seed_users):
    resp = client.post('/api/v1/auth/login', json={
        'employeeId': 'ADMIN001',
        'password': 'SecurePass123!',
    })
    json_data = resp.get_json()
    if json_data is None or 'data' not in json_data:
        raise RuntimeError(f'Login failed: {resp.status_code} {resp.data}')
    return json_data['data']['accessToken']


@pytest.fixture
def user_token(client, seed_users):
    resp = client.post('/api/v1/auth/login', json={
        'employeeId': 'USER001',
        'password': 'SecurePass123!',
    })
    json_data = resp.get_json()
    if json_data is None or 'data' not in json_data:
        raise RuntimeError(f'Login failed: {resp.status_code} {resp.data}')
    return json_data['data']['accessToken']
