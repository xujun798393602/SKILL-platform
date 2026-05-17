from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_jwt_extended import JWTManager
from flask_cors import CORS
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

from config import config_map

db = SQLAlchemy()
migrate = Migrate()
jwt = JWTManager()
cors = CORS()
limiter = Limiter(key_func=get_remote_address)


def create_app(config_name='default'):
    app = Flask(__name__)
    app.config.from_object(config_map.get(config_name, config_map['default']))

    db.init_app(app)
    migrate.init_app(app, db)
    jwt.init_app(app)
    cors.init_app(app)
    limiter.init_app(app)

    # Configure JWT to return 401 instead of 422 for invalid tokens
    @jwt.invalid_token_loader
    def invalid_token_callback(error):
        return {'code': 'AUTH001', 'message': '无效的访问令牌'}, 401

    @jwt.expired_token_loader
    def expired_token_callback(jwt_header, jwt_payload):
        return {'code': 'AUTH004', 'message': '令牌已过期'}, 401

    @jwt.unauthorized_loader
    def unauthorized_callback(error):
        return {'code': 'AUTH001', 'message': '缺少访问令牌'}, 401

    from app.routes.auth import auth_bp
    from app.routes.skills import skills_bp
    from app.routes.reviews import reviews_bp
    from app.routes.notifications import notifications_bp
    from app.routes.favorites import favorites_bp
    from app.routes.shares import shares_bp
    from app.routes.suites import suites_bp
    from app.routes.deployments import deployments_bp
    from app.routes.graph import graph_bp
    from app.routes.statistics import statistics_bp
    from app.routes.logs import logs_bp
    from app.routes.configs import configs_bp
    from app.routes.help_docs import help_docs_bp
    from app.routes.feedbacks import feedbacks_bp
    from app.routes.users import users_bp
    from app.routes.roles import roles_bp

    app.register_blueprint(auth_bp, url_prefix='/api/v1/auth')
    app.register_blueprint(skills_bp, url_prefix='/api/v1/skills')
    app.register_blueprint(reviews_bp, url_prefix='/api/v1')
    app.register_blueprint(notifications_bp, url_prefix='/api/v1/notifications')
    app.register_blueprint(favorites_bp, url_prefix='/api/v1/favorites')
    app.register_blueprint(shares_bp, url_prefix='/api/v1')
    app.register_blueprint(suites_bp, url_prefix='/api/v1/suites')
    app.register_blueprint(deployments_bp, url_prefix='/api/v1/deployments')
    app.register_blueprint(graph_bp, url_prefix='/api/v1')
    app.register_blueprint(statistics_bp, url_prefix='/api/v1/statistics')
    app.register_blueprint(logs_bp, url_prefix='/api/v1/logs')
    app.register_blueprint(configs_bp, url_prefix='/api/v1/configs')
    app.register_blueprint(help_docs_bp, url_prefix='/api/v1/help-docs')
    app.register_blueprint(feedbacks_bp, url_prefix='/api/v1/feedbacks')
    app.register_blueprint(users_bp, url_prefix='/api/v1/users')
    app.register_blueprint(roles_bp, url_prefix='/api/v1/roles')

    from app.utils.error_handlers import register_error_handlers
    register_error_handlers(app)

    return app
