from functools import wraps
from flask import jsonify
from flask_jwt_extended import get_jwt_identity, verify_jwt_in_request
from app.models.user import User


def admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        user_id = get_jwt_identity()
        user = User.query.get(user_id)
        if not user or not user.has_role('ADMIN'):
            return jsonify({'code': 'PERM001', 'message': '权限不足，需要管理员权限', 'data': None}), 403
        return fn(*args, **kwargs)
    return wrapper


def permission_required(permission_code):
    def decorator(fn):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            verify_jwt_in_request()
            user_id = get_jwt_identity()
            user = User.query.get(user_id)
            if not user or not user.has_permission(permission_code):
                return jsonify({'code': 'PERM001', 'message': '权限不足，无法访问该资源', 'data': None}), 403
            return fn(*args, **kwargs)
        return wrapper
    return decorator
