from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required

from app.models.user import User
from app.utils.error_handlers import success_response, error_response
from app.utils.decorators import admin_required

users_bp = Blueprint('users', __name__)


@users_bp.route('', methods=['GET'])
@admin_required
def list_users():
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    query = User.query
    total = query.count()
    items = query.order_by(User.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [u.to_dict() for u in items],
        }
    }), 200


@users_bp.route('/<user_id>/roles', methods=['GET'])
@jwt_required()
def get_user_roles(user_id):
    user = User.query.get(user_id)
    if not user:
        return error_response('用户不存在', status=404)

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'userId': user_id,
            'roles': [{'id': r.id, 'name': r.name, 'displayName': r.display_name} for r in user.roles],
        }
    }), 200


@users_bp.route('/<user_id>/roles', methods=['PUT'])
@admin_required
def assign_roles(user_id):
    user = User.query.get(user_id)
    if not user:
        return error_response('用户不存在', status=404)

    from app import db
    from app.models.user import Role, UserRole
    data = request.get_json()
    role_ids = data.get('roleIds', [])

    UserRole.query.filter_by(user_id=user_id).delete()
    for role_id in role_ids:
        role = Role.query.get(role_id)
        if role:
            ur = UserRole(user_id=user_id, role_id=role_id)
            db.session.add(ur)

    db.session.commit()
    return success_response(message='角色分配成功')


@users_bp.route('/<user_id>/permissions', methods=['GET'])
@jwt_required()
def get_user_permissions(user_id):
    user = User.query.get(user_id)
    if not user:
        return error_response('用户不存在', status=404)

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'userId': user_id,
            'permissions': user.get_permissions(),
        }
    }), 200
