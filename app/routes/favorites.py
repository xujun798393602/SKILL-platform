from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.social import SkillFavorite
from app.models.skill import Skill
from app.utils.error_handlers import success_response, error_response

favorites_bp = Blueprint('favorites', __name__)

MAX_FAVORITES = 100


@favorites_bp.route('', methods=['POST'])
@jwt_required()
def add_favorite():
    user_id = get_jwt_identity()
    data = request.get_json()
    skill_id = data.get('skillId', '')

    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', error_code='SKILL001', status=404)

    existing = SkillFavorite.query.filter_by(user_id=user_id, skill_id=skill_id).first()
    if existing:
        return error_response('已收藏该SKILL', status=409)

    count = SkillFavorite.query.filter_by(user_id=user_id).count()
    if count >= MAX_FAVORITES:
        return error_response('收藏数量已达上限（100个），请先取消部分收藏后再试', error_code='FAV002', status=400)

    fav = SkillFavorite(user_id=user_id, skill_id=skill_id)
    db.session.add(fav)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '收藏成功',
        'data': fav.to_dict(),
    }), 201


@favorites_bp.route('/<skill_id>', methods=['DELETE'])
@jwt_required()
def remove_favorite(skill_id):
    user_id = get_jwt_identity()
    fav = SkillFavorite.query.filter_by(user_id=user_id, skill_id=skill_id).first()
    if not fav:
        return error_response('您尚未收藏该SKILL', error_code='FAV001', status=404)

    db.session.delete(fav)
    db.session.commit()
    return success_response(message='已取消收藏')


@favorites_bp.route('', methods=['GET'])
@jwt_required()
def list_favorites():
    user_id = get_jwt_identity()
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    query = SkillFavorite.query.filter_by(user_id=user_id)
    total = query.count()
    items = query.order_by(SkillFavorite.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

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
