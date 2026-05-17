"""Seed script to populate test data."""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import bcrypt
from datetime import datetime, timezone, timedelta
from app import create_app, db
from app.models.user import User, Role, UserRole, Permission, RolePermission
from app.models.skill import Skill, SkillVersion, SkillFile, Tag, SkillTag
from app.models.system import SystemConfig, HelpDoc


def seed():
    app = create_app('development')
    with app.app_context():
        # Create roles
        roles = {}
        for name, display, desc in [
            ('USER', '普通用户', '普通用户角色'),
            ('DEVELOPER', '开发者', '开发者角色'),
            ('ADMIN', '管理员', '系统管理员角色'),
        ]:
            role = Role.query.filter_by(name=name).first()
            if not role:
                role = Role(name=name, display_name=display, description=desc, is_system=True)
                db.session.add(role)
                db.session.flush()
            roles[name] = role

        # Create permissions
        perms = {}
        for code, name, resource, action in [
            ('skill:public:read', '查看公开SKILL', 'skill', 'read'),
            ('skill:upload', '上传SKILL', 'skill', 'write'),
            ('skill:delete', '删除SKILL', 'skill', 'delete'),
            ('user:manage', '管理用户', 'user', 'admin'),
            ('review:approve', '审核SKILL', 'review', 'admin'),
        ]:
            perm = Permission.query.filter_by(code=code).first()
            if not perm:
                perm = Permission(code=code, name=name, resource=resource, action=action)
                db.session.add(perm)
                db.session.flush()
            perms[code] = perm

        # Assign all perms to ADMIN
        for perm in perms.values():
            existing = RolePermission.query.filter_by(role_id=roles['ADMIN'].id, permission_id=perm.id).first()
            if not existing:
                db.session.add(RolePermission(role_id=roles['ADMIN'].id, permission_id=perm.id))

        # Assign read perm to USER
        existing = RolePermission.query.filter_by(role_id=roles['USER'].id, permission_id=perms['skill:public:read'].id).first()
        if not existing:
            db.session.add(RolePermission(role_id=roles['USER'].id, permission_id=perms['skill:public:read'].id))

        # Create test users
        password_hash = bcrypt.hashpw('SecurePass123!'.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

        test_users = [
            ('EMP001', '测试用户', 'testuser@example.com', '技术部', password_hash, 'USER'),
            ('EMP002', '管理员', 'admin@example.com', '管理部', password_hash, 'ADMIN'),
            ('EMP003', '普通用户', 'normaluser@example.com', '产品部', password_hash, 'USER'),
        ]

        for emp_id, name, email, dept, pwd, role_name in test_users:
            user = User.query.filter_by(employee_id=emp_id).first()
            if not user:
                user = User(
                    employee_id=emp_id,
                    name=name,
                    email=email,
                    password_hash=pwd,
                    department=dept,
                    status='active',
                )
                db.session.add(user)
                db.session.flush()
                existing = UserRole.query.filter_by(user_id=user.id, role_id=roles[role_name].id).first()
                if not existing:
                    db.session.add(UserRole(user_id=user.id, role_id=roles[role_name].id))

        db.session.commit()
        print('Seed data created successfully.')


if __name__ == '__main__':
    seed()
