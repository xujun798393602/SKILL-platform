from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required

from app import db
from app.models.skill import Skill, SkillRelation
from app.utils.error_handlers import error_response

graph_bp = Blueprint('graph', __name__)


@graph_bp.route('/skills/<skill_id>/relations', methods=['GET'])
@jwt_required()
def get_relations(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', error_code='GRAPH001', status=404)

    relations = SkillRelation.query.filter_by(source_skill_id=skill_id).all()

    result = []
    for r in relations:
        target = Skill.query.get(r.target_skill_id)
        result.append({
            'relationId': r.id,
            'targetSkillId': r.target_skill_id,
            'targetSkillName': target.name if target else '',
            'relationType': r.relation_type,
            'depth': 1,
        })

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'skillId': skill_id,
            'skillName': skill.name,
            'relations': result,
            'total': len(result),
        }
    }), 200


@graph_bp.route('/skills/<skill_id>/relations', methods=['POST'])
@jwt_required()
def create_relation(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    data = request.get_json()
    target_id = data.get('targetSkillId', '')
    relation_type = data.get('relationType', 'depends_on')

    target = Skill.query.get(target_id)
    if not target:
        return error_response('目标SKILL不存在', status=404)

    existing_chain = SkillRelation.query.filter_by(source_skill_id=target_id).all()
    for rel in existing_chain:
        if rel.target_skill_id == skill_id:
            return error_response('创建关联会导致循环依赖', error_code='GRAPH002', status=400)

    relation = SkillRelation(
        source_skill_id=skill_id,
        target_skill_id=target_id,
        relation_type=relation_type,
    )
    db.session.add(relation)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '关联创建成功',
        'data': {'relationId': relation.id},
    }), 201


@graph_bp.route('/graph/positions/<position>', methods=['GET'])
@jwt_required()
def get_position_skills(position):
    from app.models.skill import PositionSkill
    ps = PositionSkill.query.filter_by(position=position).all()
    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'position': position,
            'skills': [{'skillId': p.skill_id, 'relevance': float(p.relevance_score)} for p in ps],
        }
    }), 200
