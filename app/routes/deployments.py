from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app.models.deployment import Deployment
from app.utils.error_handlers import success_response, error_response

deployments_bp = Blueprint('deployments', __name__)


@deployments_bp.route('/<deployment_id>', methods=['GET'])
@jwt_required()
def get_deployment(deployment_id):
    deployment = Deployment.query.get(deployment_id)
    if not deployment:
        return error_response('部署记录不存在', status=404)
    return success_response(deployment.to_dict())


@deployments_bp.route('/<deployment_id>/rollback', methods=['POST'])
@jwt_required()
def rollback_deployment(deployment_id):
    deployment = Deployment.query.get(deployment_id)
    if not deployment:
        return error_response('部署记录不存在', status=404)

    deployment.status = 'rolled_back'
    from app import db
    db.session.commit()

    return success_response({'deploymentId': deployment_id, 'status': 'rolled_back'}, message='回滚成功')
