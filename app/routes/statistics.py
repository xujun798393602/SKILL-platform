from datetime import datetime, timezone
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required

from app.models.skill import Skill
from app.models.user import User
from app.models.deployment import Deployment
from app.models.review import SkillReview
from app.utils.error_handlers import error_response
from app.utils.decorators import admin_required

statistics_bp = Blueprint('statistics', __name__)


@statistics_bp.route('/dashboard', methods=['GET'])
@admin_required
def dashboard():
    total_skills = Skill.query.count()
    total_users = User.query.count()
    total_downloads = sum(s.download_count or 0 for s in Skill.query.all())
    total_deployments = Deployment.query.count()
    pending_reviews = SkillReview.query.join(Skill).filter(Skill.status == 'pending_review').count()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'totalSkills': total_skills,
            'totalUsers': total_users,
            'totalDownloads': total_downloads,
            'totalDeployments': total_deployments,
            'todayUploads': 0,
            'todayActiveUsers': 0,
            'pendingReviews': pending_reviews,
            'timestamp': datetime.now(timezone.utc).isoformat(),
        }
    }), 200


@statistics_bp.route('/trends', methods=['GET'])
@jwt_required()
def trends():
    start_date = request.args.get('startDate', '')
    end_date = request.args.get('endDate', '')

    if start_date and end_date and start_date > end_date:
        return error_response('开始日期不能晚于结束日期', error_code='INVALID_DATE_RANGE', status=400)

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {'trends': []}
    }), 200


@statistics_bp.route('/hot-skills', methods=['GET'])
@jwt_required()
def hot_skills():
    start_date = request.args.get('startDate', '')
    end_date = request.args.get('endDate', '')

    skills = Skill.query.order_by(Skill.download_count.desc()).limit(10).all()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'hotSkills': [{'skillId': s.id, 'name': s.name, 'downloadCount': s.download_count} for s in skills],
            'total': len(skills),
            'timeRange': {'startDate': start_date, 'endDate': end_date} if start_date else None,
        }
    }), 200
