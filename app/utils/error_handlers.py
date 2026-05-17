from flask import jsonify


def success_response(data=None, message='操作成功', code='Success', status=200):
    resp = {'code': code, 'message': message, 'data': data}
    return jsonify(resp), status


def error_response(message, code='Error', status=400, error_code=None):
    resp = {'code': error_code or code, 'message': message, 'data': None}
    return jsonify(resp), status


def paginated_response(items, total, page, page_size, message='success'):
    return jsonify({
        'code': 'Success',
        'message': message,
        'data': {
            'total': total,
            'page': page,
            'pageSize': page_size,
            'items': items,
        }
    }), 200


def register_error_handlers(app):
    @app.errorhandler(400)
    def bad_request(e):
        return error_response(str(e), status=400)

    @app.errorhandler(401)
    def unauthorized(e):
        return error_response('未认证或认证已过期', error_code='AUTH001', status=401)

    @app.errorhandler(403)
    def forbidden(e):
        return error_response('权限不足，无法访问该资源', error_code='PERM001', status=403)

    @app.errorhandler(404)
    def not_found(e):
        return error_response('资源不存在', status=404)

    @app.errorhandler(409)
    def conflict(e):
        return error_response(str(e), status=409)

    @app.errorhandler(413)
    def payload_too_large(e):
        return error_response('文件大小超过限制', error_code='UPLOAD002', status=413)

    @app.errorhandler(429)
    def rate_limited(e):
        return error_response('请求过于频繁，请稍后重试', status=429)

    @app.errorhandler(500)
    def internal_error(e):
        return error_response('服务器内部错误', status=500)
