from flask import Flask, send_from_directory
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_jwt_extended import JWTManager
from flask_cors import CORS
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
import os

from config import config_map

db = SQLAlchemy()
migrate = Migrate()
jwt = JWTManager()
cors = CORS()
limiter = Limiter(key_func=get_remote_address)


def create_app(config_name='default'):
    app = Flask(__name__,
                static_folder=os.path.join(os.path.dirname(os.path.dirname(__file__)), 'frontend'),
                static_url_path='')
    app.config.from_object(config_map.get(config_name, config_map['default']))

    db.init_app(app)
    migrate.init_app(app, db)
    jwt.init_app(app)
    cors.init_app(app)
    limiter.init_app(app)

    # Health check endpoint
    @app.route('/health')
    def health_check():
        return {'status': 'healthy', 'service': 'skill-platform'}, 200

    # 前端静态文件
    @app.route('/')
    def index():
        return send_from_directory(app.static_folder, 'index.html')

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

    # CLI: flask init-db
    @app.cli.command('init-db')
    def init_db():
        """Initialize database with default roles and admin user."""
        import bcrypt
        from app.models.user import User, Role, UserRole, Permission, RolePermission

        db.create_all()

        # Roles
        roles = {}
        for name, display in [('USER', '普通用户'), ('ADMIN', '管理员')]:
            role = Role.query.filter_by(name=name).first()
            if not role:
                role = Role(name=name, display_name=display, is_system=True)
                db.session.add(role)
                db.session.flush()
            roles[name] = role

        # Permissions
        default_perms = [
            ('skill:upload', '上传SKILL', 'skill', 'write'),
            ('skill:review', '审核SKILL', 'skill', 'review'),
            ('skill:deploy', '部署SKILL', 'skill', 'deploy'),
            ('user:manage', '管理用户', 'user', 'manage'),
            ('role:manage', '管理角色', 'role', 'manage'),
            ('config:manage', '管理配置', 'config', 'manage'),
        ]
        for code, name, resource, action in default_perms:
            perm = Permission.query.filter_by(code=code).first()
            if not perm:
                perm = Permission(code=code, name=name, resource=resource, action=action)
                db.session.add(perm)
                db.session.flush()
                db.session.add(RolePermission(role_id=roles['ADMIN'].id, permission_id=perm.id))

        # Admin user
        admin = User.query.filter_by(employee_id='ADMIN001').first()
        if not admin:
            pw = bcrypt.hashpw('Admin@123'.encode(), bcrypt.gensalt()).decode()
            admin = User(
                employee_id='ADMIN001', name='管理员',
                email='admin@skill-platform.com', password_hash=pw,
                department='管理部', status='active',
            )
            db.session.add(admin)
            db.session.flush()
            db.session.add(UserRole(user_id=admin.id, role_id=roles['ADMIN'].id))

        db.session.commit()
        print('Database initialized. Admin: ADMIN001 / Admin@123')

    return app
