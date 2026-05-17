from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.suite import Suite, SuiteSkill
from app.models.skill import Skill
from app.utils.error_handlers import success_response, error_response

suites_bp = Blueprint('suites', __name__)


@suites_bp.route('', methods=['POST'])
@jwt_required()
def create_suite():
    user_id = get_jwt_identity()
    data = request.get_json()

    name = data.get('name', '').strip()
    description = data.get('description', '')
    skills_data = data.get('skills', [])

    if not name:
        return error_response('套件名称不能为空', status=400)

    if len(skills_data) < 2:
        return error_response('套件至少需要包含2个SKILL', error_code='SUITE001', status=400)

    suite = Suite(
        name=name,
        description=description,
        category=data.get('category', 'general'),
        owner_id=user_id,
        skill_count=len(skills_data),
    )
    db.session.add(suite)
    db.session.flush()

    for idx, sd in enumerate(skills_data):
        ss = SuiteSkill(
            suite_id=suite.id,
            skill_id=sd.get('skillId'),
            skill_order=sd.get('order', idx + 1),
            is_required=sd.get('required', True),
        )
        db.session.add(ss)

    db.session.commit()

    return jsonify({
        'code': 0,
        'message': '套件创建成功',
        'data': suite.to_dict(),
    }), 201


@suites_bp.route('', methods=['GET'])
@jwt_required()
def list_suites():
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    query = Suite.query
    total = query.count()
    items = query.order_by(Suite.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [s.to_dict() for s in items],
        }
    }), 200


@suites_bp.route('/<suite_id>', methods=['GET'])
@jwt_required()
def get_suite(suite_id):
    suite = Suite.query.get(suite_id)
    if not suite:
        return error_response('套件不存在', status=404)
    return success_response(suite.to_dict())


@suites_bp.route('/<suite_id>/deploy', methods=['POST'])
@jwt_required()
def deploy_suite(suite_id):
    suite = Suite.query.get(suite_id)
    if not suite:
        return error_response('套件不存在', status=404)

    suite_skills = SuiteSkill.query.filter_by(suite_id=suite_id).all()
    skill_ids = [ss.skill_id for ss in suite_skills]

    for sid in skill_ids:
        for tid in skill_ids:
            if sid != tid:
                from app.models.skill import SkillRelation
                cycle = SkillRelation.query.filter_by(
                    source_skill_id=sid, target_skill_id=tid
                ).first()
                if cycle:
                    return error_response(
                        '检测到循环依赖，无法部署',
                        error_code='SUITE002', status=400
                    )

    return jsonify({
        'code': 0,
        'message': '套件部署任务已创建',
        'data': {
            'suiteId': suite_id,
            'status': 'deploying',
        }
    }), 202
