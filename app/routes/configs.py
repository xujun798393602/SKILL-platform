from flask import Blueprint, request, jsonify

from app import db
from app.models.system import SystemConfig
from app.utils.error_handlers import error_response
from app.utils.decorators import admin_required

configs_bp = Blueprint('configs', __name__)


@configs_bp.route('', methods=['GET'])
@admin_required
def list_configs():
    configs = SystemConfig.query.all()
    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'configs': [c.to_dict() for c in configs],
            'total': len(configs),
        }
    }), 200


@configs_bp.route('/<config_key>', methods=['GET'])
@admin_required
def get_config(config_key):
    config = SystemConfig.query.filter_by(config_key=config_key).first()
    if not config:
        return error_response('配置项不存在', status=404)
    return jsonify({
        'code': 0,
        'message': 'success',
        'data': config.to_dict(),
    }), 200


@configs_bp.route('/<config_key>', methods=['PUT'])
@admin_required
def update_config(config_key):
    config = SystemConfig.query.filter_by(config_key=config_key).first()
    if not config:
        return error_response('配置项不存在', status=404)

    if config.is_readonly:
        return error_response('该配置为只读，不允许修改', error_code='CONFIG001', status=403)

    data = request.get_json()
    config.config_value = data.get('value', config.config_value)
    db.session.commit()

    return jsonify({
        'code': 0,
        'message': '配置更新成功',
        'data': config.to_dict(),
    }), 200
