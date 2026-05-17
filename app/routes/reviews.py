from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.skill import Skill
from app.models.review import SkillReview
from app.models.user import User
from app.models.notification import Notification
from app.utils.error_handlers import success_response, error_response

reviews_bp = Blueprint('reviews', __name__)


@reviews_bp.route('/reviews', methods=['GET'])
@jwt_required()
def list_reviews():
    status_filter = request.args.get('status', '')
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    query = SkillReview.query
    if status_filter:
        query = query.join(Skill).filter(Skill.status == status_filter)

    total = query.count()
    items = query.order_by(SkillReview.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [r.to_dict() for r in items],
        }
    }), 200


@reviews_bp.route('/skills/<skill_id>/review', methods=['POST'])
@jwt_required()
def review_skill(skill_id):
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    if not user or not user.has_role('ADMIN'):
        return error_response('您没有审核权限，无法执行此操作', error_code='PERM001', status=403)

    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    if skill.status not in ('pending_review', 'draft'):
        return error_response('该SKILL审核流程已关闭，无法重复审核', error_code='REVIEW001', status=409)

    data = request.get_json()
    decision = data.get('decision', '')
    comment = data.get('comment', '')

    if decision not in ('approved', 'rejected'):
        return error_response('审核决定必须为 approved 或 rejected', status=400)

    review = SkillReview(
        skill_id=skill_id,
        action=decision,
        comment=comment,
        reviewer_id=user_id,
    )
    db.session.add(review)

    if decision == 'approved':
        skill.status = 'published'
        skill.published_at = datetime.now(timezone.utc)
    else:
        skill.status = 'rejected'

    notification = Notification(
        user_id=skill.owner_id,
        type='review_result',
        title='SKILL审核结果通知',
        content=f'您提交的SKILL「{skill.name}」已{"通过" if decision == "approved" else "被拒绝"}审核',
        related_id=skill_id,
    )
    db.session.add(notification)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '审核完成',
        'data': review.to_dict(),
    }), 200
