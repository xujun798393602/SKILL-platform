from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required

from app import db
from app.models.user import Role, Permission, RolePermission
from app.utils.error_handlers import success_response, error_response
from app.utils.decorators import admin_required

roles_bp = Blueprint('roles', __name__)


@roles_bp.route('', methods=['GET'])
@jwt_required()
def list_roles():
    roles = Role.query.all()
    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'roles': [{
                'id': r.id,
                'name': r.name,
                'displayName': r.display_name,
                'description': r.description,
                'isSystem': r.is_system,
            } for r in roles],
        }
    }), 200


@roles_bp.route('', methods=['POST'])
@admin_required
def create_role():
    data = request.get_json()
    name = data.get('name', '').strip().upper()
    display_name = data.get('displayName', '').strip()

    if not name or not display_name:
        return error_response('角色名称和显示名称不能为空', status=400)

    if Role.query.filter_by(name=name).first():
        return error_response('角色名称已存在', status=409)

    role = Role(
        name=name,
        display_name=display_name,
        description=data.get('description', ''),
    )
    db.session.add(role)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '角色创建成功',
        'data': {'id': role.id, 'name': role.name},
    }), 201


@roles_bp.route('/<role_id>', methods=['PUT'])
@admin_required
def update_role(role_id):
    role = Role.query.get(role_id)
    if not role:
        return error_response('角色不存在', status=404)

    data = request.get_json()
    if data.get('displayName'):
        role.display_name = data['displayName']
    if data.get('description') is not None:
        role.description = data['description']

    db.session.commit()
    return success_response(message='角色更新成功')


@roles_bp.route('/<role_id>', methods=['DELETE'])
@admin_required
def delete_role(role_id):
    role = Role.query.get(role_id)
    if not role:
        return error_response('角色不存在', status=404)
    if role.is_system:
        return error_response('系统内置角色不可删除', status=403)

    db.session.delete(role)
    db.session.commit()
    return success_response(message='角色删除成功')


@roles_bp.route('/<role_id>/permissions', methods=['PUT'])
@admin_required
def assign_permissions(role_id):
    role = Role.query.get(role_id)
    if not role:
        return error_response('角色不存在', status=404)

    data = request.get_json()
    permission_ids = data.get('permissionIds', [])

    RolePermission.query.filter_by(role_id=role_id).delete()
    for pid in permission_ids:
        perm = Permission.query.get(pid)
        if perm:
            rp = RolePermission(role_id=role_id, permission_id=pid)
            db.session.add(rp)

    db.session.commit()
    return success_response(message='权限分配成功')
