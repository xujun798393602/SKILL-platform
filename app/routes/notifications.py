from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.notification import Notification
from app.utils.error_handlers import success_response, error_response

notifications_bp = Blueprint('notifications', __name__)


@notifications_bp.route('', methods=['GET'])
@jwt_required()
def list_notifications():
    user_id = get_jwt_identity()
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 10, type=int)

    query = Notification.query.filter_by(user_id=user_id)
    total = query.count()
    items = query.order_by(Notification.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [n.to_dict() for n in items],
        }
    }), 200


@notifications_bp.route('/<notification_id>/read', methods=['PUT'])
@jwt_required()
def mark_read(notification_id):
    user_id = get_jwt_identity()
    notification = Notification.query.filter_by(id=notification_id, user_id=user_id).first()
    if not notification:
        return error_response('通知不存在或已被删除', error_code='NOTIFY001', status=404)

    notification.is_read = True
    db.session.commit()
    return success_response(message='已标记为已读')


@notifications_bp.route('/read-all', methods=['PUT'])
@jwt_required()
def mark_all_read():
    user_id = get_jwt_identity()
    count = Notification.query.filter_by(user_id=user_id, is_read=False).update({'is_read': True})
    db.session.commit()
    return success_response({'updatedCount': count}, message='已全部标记为已读')
