from flask import Blueprint, request, jsonify

from app.models.system import OperationLog
from app.utils.error_handlers import error_response
from app.utils.decorators import admin_required

logs_bp = Blueprint('logs', __name__)

VALID_LOG_TYPES = ['operation', 'error', 'security']


@logs_bp.route('', methods=['GET'])
@admin_required
def list_logs():
    log_type = request.args.get('type', 'operation')
    start_date = request.args.get('startDate', '')
    end_date = request.args.get('endDate', '')
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 20, type=int)

    if log_type not in VALID_LOG_TYPES:
        return error_response(
            f'无效的日志类型，支持的类型：{", ".join(VALID_LOG_TYPES)}',
            error_code='LOG002', status=400
        )

    if start_date and end_date:
        from datetime import datetime
        try:
            start = datetime.fromisoformat(start_date)
            end = datetime.fromisoformat(end_date)
            if (end - start).days > 90:
                return error_response('查询时间范围不能超过90天', error_code='LOG001', status=400)
        except ValueError:
            pass

    query = OperationLog.query
    total = query.count()
    items = query.order_by(OperationLog.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'logs': [log.to_dict() for log in items],
            'total': total,
            'page': page,
            'pageSize': page_size,
        }
    }), 200


@logs_bp.route('/export', methods=['POST'])
@admin_required
def export_logs():
    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'taskId': 'export-task-001',
            'status': 'processing',
            'message': '导出任务已创建，请稍后下载',
        }
    }), 200
