from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.system import Feedback
from app.utils.error_handlers import success_response, error_response
from app.utils.decorators import admin_required

feedbacks_bp = Blueprint('feedbacks', __name__)


@feedbacks_bp.route('', methods=['POST'])
@jwt_required()
def create_feedback():
    user_id = get_jwt_identity()
    data = request.get_json()

    fb_type = data.get('type', 'suggestion')
    title = data.get('title', '').strip()
    content = data.get('content', '').strip()

    if not content:
        return error_response('反馈内容不能为空', error_code='INVALID_INPUT', status=400)

    if len(content) > 1000:
        return error_response('反馈内容不能超过1000字', error_code='INVALID_INPUT', status=400)

    if not title:
        return error_response('反馈标题不能为空', status=400)

    feedback = Feedback(
        user_id=user_id,
        type=fb_type,
        title=title,
        content=content,
        status='pending',
    )
    db.session.add(feedback)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '反馈提交成功',
        'data': feedback.to_dict(),
    }), 201


@feedbacks_bp.route('', methods=['GET'])
@jwt_required()
def list_feedbacks():
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    query = Feedback.query
    total = query.count()
    items = query.order_by(Feedback.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [f.to_dict() for f in items],
        }
    }), 200


@feedbacks_bp.route('/<feedback_id>/reply', methods=['POST'])
@admin_required
def reply_feedback(feedback_id):
    feedback = Feedback.query.get(feedback_id)
    if not feedback:
        return error_response('反馈不存在', status=404)

    data = request.get_json()
    reply_content = data.get('content', '').strip()

    if not reply_content:
        return error_response('回复内容不能为空', status=400)

    feedback.reply = reply_content
    feedback.status = 'replied'
    from flask_jwt_extended import get_jwt_identity
    feedback.replied_by = get_jwt_identity()
    from datetime import datetime, timezone
    feedback.replied_at = datetime.now(timezone.utc)
    db.session.commit()

    return success_response(feedback.to_dict(), message='回复成功')
