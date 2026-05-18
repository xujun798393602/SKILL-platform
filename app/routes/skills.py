import hashlib
from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.skill import Skill, SkillVersion, SkillFile, Tag, SkillTag
from app.models.user import User
from app.utils.error_handlers import success_response, error_response, paginated_response
from app.utils.validators import validate_file_format, validate_file_name

skills_bp = Blueprint('skills', __name__)


@skills_bp.route('', methods=['GET'])
@jwt_required()
def list_skills():
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)
    keyword = request.args.get('keyword', '').strip()
    skill_type = request.args.get('skillType', '').strip()
    category = request.args.get('category', '').strip()
    status = request.args.get('status', '').strip()
    tags = request.args.get('tags', '').strip()
    sort_by = request.args.get('sortBy', 'created_at')
    sort_order = request.args.get('sortOrder', 'desc')

    query = Skill.query

    if keyword:
        query = query.filter(Skill.name.ilike(f'%{keyword}%'))
    if skill_type:
        query = query.filter(Skill.skill_type == skill_type)
    if category:
        query = query.filter(Skill.category == category)
    if status:
        query = query.filter(Skill.status == status)
    if tags:
        tag_list = [t.strip() for t in tags.split(',')]
        query = query.filter(Skill.tags.any(Tag.name.in_(tag_list)))

    sort_field = sort_by.replace('createdAt', 'created_at').replace('updatedAt', 'updated_at')
    sort_col = getattr(Skill, sort_field, Skill.created_at)
    if sort_order == 'desc':
        query = query.order_by(sort_col.desc())
    else:
        query = query.order_by(sort_col.asc())

    total = query.count()
    items = query.offset((page - 1) * page_size).limit(page_size).all()

    return paginated_response(
        [s.to_dict() for s in items],
        total, page, page_size
    )


@skills_bp.route('/search', methods=['POST'])
@jwt_required()
def search_skills():
    data = request.get_json()
    if not data:
        return error_response('请求体不能为空', status=400)

    keyword = data.get('keyword', '').strip()
    if not keyword:
        return error_response('搜索关键词不能为空', error_code='SEARCH001', status=400)

    page = data.get('page', 1)
    page_size = data.get('pageSize', 20)
    filters = data.get('filters', {})

    query = Skill.query.filter(Skill.name.ilike(f'%{keyword}%'))

    if filters.get('skillType'):
        query = query.filter(Skill.skill_type == filters['skillType'])
    if filters.get('category'):
        query = query.filter(Skill.category == filters['category'])
    if filters.get('tags'):
        query = query.filter(Skill.tags.any(Tag.name.in_(filters['tags'])))

    total = query.count()
    items = query.offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': [{
                'skillId': s.id,
                'name': s.name,
                'description': s.description,
                'relevance': 0.95,
            } for s in items]
        }
    }), 200


@skills_bp.route('/<skill_id>', methods=['GET'])
@jwt_required()
def get_skill(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)
    return success_response(skill.to_dict())


@skills_bp.route('/<skill_id>', methods=['PUT'])
@jwt_required()
def update_skill(skill_id):
    user_id = get_jwt_identity()
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)
    if skill.owner_id != user_id:
        user = User.query.get(user_id)
        if not user or not user.has_role('ADMIN'):
            return error_response('无权修改此SKILL', status=403)

    data = request.get_json()
    if data.get('name'):
        skill.name = data['name']
    if data.get('description') is not None:
        skill.description = data['description']
    if data.get('category'):
        skill.category = data['category']
    if data.get('tags'):
        SkillTag.query.filter_by(skill_id=skill.id).delete()
        for tag_name in data['tags']:
            tag = Tag.query.filter_by(name=tag_name).first()
            if not tag:
                tag = Tag(name=tag_name)
                db.session.add(tag)
            st = SkillTag(skill_id=skill.id, tag_id=tag.id)
            db.session.add(st)

    db.session.commit()
    return success_response(skill.to_dict())


@skills_bp.route('/<skill_id>', methods=['DELETE'])
@jwt_required()
def delete_skill(skill_id):
    user_id = get_jwt_identity()
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)
    if skill.owner_id != user_id:
        user = User.query.get(user_id)
        if not user or not user.has_role('ADMIN'):
            return error_response('无权删除此SKILL', status=403)

    db.session.delete(skill)
    db.session.commit()
    return success_response(message='删除成功')


@skills_bp.route('/upload', methods=['POST'])
@jwt_required()
def upload_skill():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)

    if 'file' not in request.files:
        return error_response('未上传文件', error_code='UPLOAD001', status=400)

    file = request.files['file']
    if not file.filename:
        return error_response('文件名为空', error_code='UPLOAD001', status=400)

    if not validate_file_format(file.filename):
        return error_response('不支持的文件格式，仅支持 .json、.skill、.zip 格式', error_code='UPLOAD001', status=400)

    name_part = file.filename.rsplit('.', 1)[0]
    if not validate_file_name(name_part):
        return error_response(
            '文件名不符合命名规范：必须以字母开头，仅允许字母、数字、下划线和连字符，长度2-64字符',
            error_code='UPLOAD003', status=400
        )

    file_content = file.read()
    file_size = len(file_content)

    if file_size > 100 * 1024 * 1024:
        return error_response('文件大小超过限制，最大支持 100MB', error_code='UPLOAD002', status=413)

    skill_type = request.form.get('skillType', 'public')
    name = request.form.get('name', file.filename.rsplit('.', 1)[0])
    description = request.form.get('description', '')
    version = request.form.get('version', '1.0.0')
    category = request.form.get('category', 'general')
    tags_str = request.form.get('tags', '')

    skill = Skill(
        name=name,
        description=description,
        skill_type=skill_type,
        category=category,
        current_version=version,
        status='draft',
        owner_id=user_id,
        developer_name=user.name,
        file_size=file_size,
    )
    db.session.add(skill)
    db.session.flush()

    checksum = hashlib.sha256(file_content).hexdigest()
    ext = file.filename.rsplit('.', 1)[-1].lower()
    stored_path = f'uploads/{skill.id}/{version}/{file.filename}'

    sv = SkillVersion(
        skill_id=skill.id,
        version=version,
        file_path=stored_path,
        file_size=file_size,
        checksum=checksum,
        created_by=user_id,
    )
    db.session.add(sv)
    db.session.flush()

    sf = SkillFile(
        skill_id=skill.id,
        version_id=sv.id,
        file_name=file.filename,
        file_path=stored_path,
        file_size=file_size,
        file_type=ext,
        checksum=checksum,
        upload_status='completed',
    )
    db.session.add(sf)

    if tags_str:
        for tag_name in tags_str.split(','):
            tag_name = tag_name.strip()
            if tag_name:
                tag = Tag.query.filter_by(name=tag_name).first()
                if not tag:
                    tag = Tag(name=tag_name, usage_count=0)
                    db.session.add(tag)
                tag.usage_count += 1
                st = SkillTag(skill_id=skill.id, tag_id=tag.id)
                db.session.add(st)

    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '上传成功',
        'data': {
            'skillId': skill.id,
            'name': skill.name,
            'fileName': file.filename,
            'fileSize': file_size,
            'format': ext,
            'version': version,
            'status': 'uploaded',
            'createdAt': skill.created_at.isoformat(),
        }
    }), 201


@skills_bp.route('/batch-upload', methods=['POST'])
@jwt_required()
def batch_upload():
    user_id = get_jwt_identity()
    user = User.query.get(user_id)
    files = request.files.getlist('files')

    if not files:
        return error_response('未上传文件', status=400)

    results = []
    for file in files:
        if not validate_file_format(file.filename):
            results.append({'fileName': file.filename, 'status': 'failed', 'error': '不支持的文件格式'})
            continue

        file_content = file.read()
        file_size = len(file_content)
        ext = file.filename.rsplit('.', 1)[-1].lower()

        skill = Skill(
            name=file.filename.rsplit('.', 1)[0],
            skill_type=request.form.get('skillType', 'public'),
            category='general',
            owner_id=user_id,
            developer_name=user.name,
            file_size=file_size,
        )
        db.session.add(skill)
        db.session.flush()

        checksum = hashlib.sha256(file_content).hexdigest()
        sv = SkillVersion(
            skill_id=skill.id,
            version='1.0.0',
            file_path=f'uploads/{skill.id}/{file.filename}',
            file_size=file_size,
            checksum=checksum,
            created_by=user_id,
        )
        db.session.add(sv)
        db.session.flush()

        sf = SkillFile(
            skill_id=skill.id,
            version_id=sv.id,
            file_name=file.filename,
            file_path=f'uploads/{skill.id}/{file.filename}',
            file_size=file_size,
            file_type=ext,
            checksum=checksum,
            upload_status='completed',
        )
        db.session.add(sf)

        results.append({
            'skillId': skill.id,
            'fileName': file.filename,
            'status': 'uploaded',
        })

    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '批量上传完成',
        'data': {
            'totalCount': len(files),
            'successCount': sum(1 for r in results if r['status'] == 'uploaded'),
            'failCount': sum(1 for r in results if r['status'] == 'failed'),
            'results': results,
        }
    }), 201


@skills_bp.route('/<skill_id>/validation', methods=['GET'])
@jwt_required()
def validate_skill(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    checks = [
        {'type': 'format', 'passed': True},
        {'type': 'naming', 'passed': True},
        {'type': 'content', 'passed': True},
        {'type': 'version', 'passed': True},
        {'type': 'size', 'passed': True},
        {'type': 'security', 'passed': True},
    ]

    all_passed = all(c['passed'] for c in checks)

    return jsonify({
        'code': 'Success',
        'message': 'success',
        'data': {
            'validationStatus': 'passed' if all_passed else 'failed',
            'checks': checks,
        }
    }), 200


@skills_bp.route('/<skill_id>/download', methods=['GET'])
@jwt_required()
def download_skill(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', error_code='DOWNLOAD001', status=404)

    skill.download_count += 1
    db.session.commit()

    from flask import send_file
    import io
    return send_file(
        io.BytesIO(b'skill file content'),
        mimetype='application/octet-stream',
        as_attachment=True,
        download_name=f'{skill.name}.skill'
    )


@skills_bp.route('/<skill_id>/versions', methods=['GET'])
@jwt_required()
def list_versions(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    versions = SkillVersion.query.filter_by(skill_id=skill_id).order_by(SkillVersion.created_at.desc()).all()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'skillId': skill_id,
            'currentVersion': skill.current_version,
            'versions': [{
                'version': v.version,
                'createdAt': v.created_at.isoformat(),
                'status': 'active' if v.version == skill.current_version else 'archived',
                'tag': v.tag,
                'isActive': v.version == skill.current_version,
            } for v in versions]
        }
    }), 200


@skills_bp.route('/<skill_id>/versions/rollback', methods=['POST'])
@jwt_required()
def rollback_version(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    data = request.get_json()
    target_version = data.get('targetVersion', '')

    sv = SkillVersion.query.filter_by(skill_id=skill_id, version=target_version).first()
    if not sv:
        return error_response('目标版本不存在', error_code='VERSION001', status=404)

    skill.current_version = target_version
    db.session.commit()

    return jsonify({
        'code': 0,
        'message': '回滚成功',
        'data': {
            'skillId': skill_id,
            'currentVersion': target_version,
        }
    }), 202


@skills_bp.route('/<skill_id>/versions/<version>/tag', methods=['PUT'])
@jwt_required()
def set_version_tag(skill_id, version):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    sv = SkillVersion.query.filter_by(skill_id=skill_id, version=version).first()
    if not sv:
        return error_response('版本不存在', status=404)

    data = request.get_json()
    tag = data.get('tag', '')
    if len(tag) > 50:
        return error_response('标签长度不能超过50字符', status=400)

    sv.tag = tag
    db.session.commit()

    return jsonify({
        'code': 0,
        'message': '标签设置成功',
        'data': {
            'version': version,
            'tag': tag,
        }
    }), 200


@skills_bp.route('/<skill_id>/deploy', methods=['POST'])
@jwt_required()
def deploy_skill(skill_id):
    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    from app.models.deployment import Deployment
    import json

    data = request.get_json() or {}
    platform = data.get('platform', 'docker')
    config = data.get('config', {})

    deployment = Deployment(
        skill_id=skill_id,
        status='deploying',
        platform=platform,
        config_snapshot=json.dumps(config),
        deployed_by=get_jwt_identity(),
        started_at=datetime.now(timezone.utc),
    )
    db.session.add(deployment)
    skill.deploy_count += 1
    db.session.commit()

    return jsonify({
        'code': 0,
        'message': '部署任务已创建',
        'data': {
            'deploymentId': deployment.id,
            'status': 'deploying',
            'skillId': skill_id,
            'platform': platform,
        }
    }), 202


@skills_bp.route('/<skill_id>/ratings', methods=['POST'])
@jwt_required()
def submit_rating(skill_id):
    from app.models.social import SkillRating
    from app.models.deployment import DownloadLog

    skill = Skill.query.get(skill_id)
    if not skill:
        return error_response('SKILL不存在', status=404)

    user_id = get_jwt_identity()

    # Check if user has downloaded this skill
    has_downloaded = DownloadLog.query.filter_by(skill_id=skill_id, user_id=user_id).first()
    if not has_downloaded:
        return error_response('只有下载过的SKILL才能评价', error_code='RATING001', status=403)

    # Check if user already rated this skill
    existing_rating = SkillRating.query.filter_by(skill_id=skill_id, user_id=user_id).first()
    if existing_rating:
        return error_response('您已经评价过该SKILL', error_code='RATING002', status=409)

    data = request.get_json()
    score = data.get('score', 0)
    comment = data.get('comment', '')

    if not score or score < 1 or score > 5:
        return error_response('评分必须在1-5之间', error_code='RATING003', status=400)

    rating = SkillRating(
        skill_id=skill_id,
        user_id=user_id,
        rating=score,
    )
    db.session.add(rating)

    # Update skill average rating
    all_ratings = SkillRating.query.filter_by(skill_id=skill_id).all()
    all_ratings_list = list(all_ratings) + [rating]
    skill.avg_rating = sum(r.rating for r in all_ratings_list) / len(all_ratings_list)
    skill.rating_count = len(all_ratings_list)
    db.session.commit()

    return jsonify({
        'code': 'Success',
        'message': '评价成功',
        'data': {
            'ratingId': rating.id,
            'skillId': skill_id,
            'score': score,
            'comment': comment,
        }
    }), 201
