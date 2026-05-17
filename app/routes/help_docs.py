from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity

from app import db
from app.models.system import HelpDoc
from app.models.user import User
from app.utils.error_handlers import success_response, error_response
from app.utils.decorators import admin_required

help_docs_bp = Blueprint('help_docs', __name__)


@help_docs_bp.route('', methods=['GET'])
@jwt_required()
def list_docs():
    page = request.args.get('page', 1, type=int)
    page_size = request.args.get('pageSize', 10, type=int)
    keyword = request.args.get('keyword', '').strip()

    query = HelpDoc.query.filter_by(is_published=True)
    if keyword:
        query = query.filter(HelpDoc.title.ilike(f'%{keyword}%'))

    total = query.count()
    items = query.order_by(HelpDoc.created_at.desc()).offset((page - 1) * page_size).limit(page_size).all()

    return jsonify({
        'code': 0,
        'message': 'success',
        'data': {
            'docs': [d.to_dict() for d in items],
            'total': total,
            'page': page,
            'pageSize': page_size,
        }
    }), 200


@help_docs_bp.route('/<doc_id>', methods=['GET'])
@jwt_required()
def get_doc(doc_id):
    doc = HelpDoc.query.get(doc_id)
    if not doc:
        return error_response('帮助文档不存在', error_code='DOC_NOT_FOUND', status=404)
    return jsonify({
        'code': 0,
        'message': 'success',
        'data': doc.to_dict(),
    }), 200


@help_docs_bp.route('/<doc_id>', methods=['DELETE'])
@admin_required
def delete_doc(doc_id):
    doc = HelpDoc.query.get(doc_id)
    if not doc:
        return error_response('帮助文档不存在', status=404)
    db.session.delete(doc)
    db.session.commit()
    return success_response(message='删除成功')
