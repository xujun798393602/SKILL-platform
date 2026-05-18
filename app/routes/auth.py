from datetime import datetime, timezone, timedelta
from flask import Blueprint, request, jsonify
from flask_jwt_extended import (
    create_access_token, create_refresh_token, jwt_required,
    get_jwt_identity
)
import bcrypt

from app import db, limiter
from app.models.user import User, Role, UserRole
from app.utils.error_handlers import success_response, error_response
from app.utils.validators import validate_email, validate_password

auth_bp = Blueprint('auth', __name__)


@auth_bp.route('/login', methods=['POST'])
@limiter.limit('10 per minute')
def login():
    data = request.get_json()
    if not data:
        return error_response('请求体不能为空', status=400)

    employee_id = data.get('employeeId', '').strip()
    password = data.get('password', '')

    if not employee_id or not password:
        return error_response('工号和密码不能为空', error_code='AUTH001', status=401)

    user = User.query.filter_by(employee_id=employee_id).first()
    if not user:
        return error_response('用户名或密码错误', error_code='AUTH001', status=401)

    if user.locked_until and user.locked_until > datetime.now(timezone.utc):
        return error_response('账户已锁定，请30分钟后重试', error_code='AUTH003', status=423)

    if user.status == 'disabled':
        return error_response('账号已被禁用', error_code='AUTH004', status=403)

    if not bcrypt.checkpw(password.encode('utf-8'), user.password_hash.encode('utf-8')):
        user.login_fail_count = (user.login_fail_count or 0) + 1
        if user.login_fail_count >= 5:
            user.status = 'locked'
            user.locked_until = datetime.now(timezone.utc) + timedelta(minutes=30)
            db.session.commit()
            return error_response('账户已锁定，请30分钟后重试', error_code='AUTH003', status=423)
        db.session.commit()
        return error_response('用户名或密码错误', error_code='AUTH001', status=401)

    user.login_fail_count = 0
    user.locked_until = None
    user.status = 'active'
    user.last_login_at = datetime.now(timezone.utc)
    db.session.commit()

    role_name = user.roles[0].name if user.roles else 'USER'
    access_token = create_access_token(
        identity=user.id,
        additional_claims={'role': role_name, 'name': user.name}
    )
    refresh_token = create_refresh_token(identity=user.id)

    return jsonify({
        'code': 'Success',
        'message': '登录成功',
        'data': {
            'accessToken': access_token,
            'refreshToken': refresh_token,
            'expiresIn': 7200,
            'userInfo': {
                'id': user.id,
                'employeeId': user.employee_id,
                'email': user.email,
                'name': user.name,
                'nickname': user.name,
                'department': user.department,
                'role': role_name,
            }
        }
    }), 200


@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    if not data:
        return error_response('请求体不能为空', status=400)

    employee_id = data.get('employeeId', '').strip()
    name = data.get('name', '').strip()
    department = data.get('department', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '')

    if not all([employee_id, name, department, email, password]):
        return error_response('所有字段均为必填', status=400)

    if not validate_email(email):
        return error_response('邮箱格式不正确', error_code='AUTH007', status=400)

    if User.query.filter_by(employee_id=employee_id).first():
        return error_response('工号已注册', error_code='AUTH006', status=409)

    if User.query.filter_by(email=email).first():
        return error_response('邮箱已注册', error_code='AUTH006', status=409)

    password_hash = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    user = User(
        employee_id=employee_id,
        name=name,
        email=email,
        password_hash=password_hash,
        department=department,
        status='active'
    )
    db.session.add(user)
    db.session.flush()

    user_role = Role.query.filter_by(name='USER').first()
    if user_role:
        ur = UserRole(user_id=user.id, role_id=user_role.id)
        db.session.add(ur)

    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '注册成功',
        'data': {'userId': user.id}
    }), 201


@auth_bp.route('/refresh', methods=['POST'])
def refresh():
    data = request.get_json()
    if not data or not data.get('refreshToken'):
        return error_response('刷新令牌不能为空', error_code='AUTH004', status=401)

    try:
        from flask_jwt_extended import decode_token
        decoded = decode_token(data['refreshToken'])
        user_id = decoded['sub']
        user = User.query.get(user_id)
        if not user:
            return error_response('用户不存在', error_code='AUTH004', status=401)

        role_name = user.roles[0].name if user.roles else 'USER'
        new_token = create_access_token(
            identity=user.id,
            additional_claims={'role': role_name, 'name': user.name}
        )
        return jsonify({
            'code': 'Success',
            'message': '令牌刷新成功',
            'data': {
                'accessToken': new_token,
                'expiresIn': 7200,
            }
        }), 200
    except Exception:
        return error_response('刷新令牌已过期，请重新登录', error_code='AUTH004', status=401)


@auth_bp.route('/me', methods=['GET'])
@jwt_required()
def get_me():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    if not user:
        return error_response('用户不存在', status=404)

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': user.to_dict()
    }), 200


@auth_bp.route('/password', methods=['PUT'])
@jwt_required()
def change_password():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    data = request.get_json()

    old_password = data.get('oldPassword', '')
    new_password = data.get('newPassword', '')

    if not bcrypt.checkpw(old_password.encode('utf-8'), user.password_hash.encode('utf-8')):
        return error_response('旧密码错误', status=400)

    if not validate_password(new_password):
        return error_response('新密码格式不符合要求', status=400)

    user.password_hash = bcrypt.hashpw(new_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
    db.session.commit()

    return success_response(message='密码修改成功')


@auth_bp.route('/check-permission', methods=['POST'])
@jwt_required()
def check_permission():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    if not user:
        return error_response('用户不存在', status=404)

    data = request.get_json()
    permission = data.get('permission', '')

    allowed = user.has_permission(permission)

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'allowed': allowed,
            'userId': user.id,
            'permission': permission,
        }
    }), 200
