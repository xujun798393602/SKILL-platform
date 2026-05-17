import uuid
import secrets
from datetime import datetime, timezone, timedelta
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.social import SkillShare
from app.models.skill import Skill
from app.utils.error_handlers import success_response, error_response

shares_bp = Blueprint('shares', __name__)


@shares_bp.route('/skills/<skill_id>/share', methods=['POST'])
@jwt_required()
def create_share(skill_id):
    user_id = get_jwt_identity()
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    if skill.status != 'published':
        return error_response('私有状态的SKILL不可分享，请先发布后再试', error_code='SHARE001', status=403)

    data = request.get_json() or {}
    scope = data.get('scope', 'department')

    share_token = secrets.token_urlsafe(32)
    share = SkillShare(
        skill_id=skill_id,
        share_token=share_token,
        share_type=scope,
        created_by=user_id,
        expires_at=datetime.now(timezone.utc) + timedelta(days=7),
    )
    db.session.add(share)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '分享链接已生成',
        'data': share.to_dict(),
    }), 201


@shares_bp.route('/shared/<share_token>', methods=['GET'])
def access_share(share_token):
    share = SkillShare.query.filter_by(share_token=share_token).first()
    if not share:
        return error_response('分享链接不存在', status=404)

    if share.expires_at < datetime.now(timezone.utc):
        return error_response('分享链接已过期，请联系分享者重新生成', error_code='SHARE002', status=410)

    share.access_count += 1
    db.session.commit()

    skill = Skill.query.get(share.skill_id)
    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'skillId': share.skill_id,
            'skill': skill.to_dict() if skill else None,
        }
    }), 200
