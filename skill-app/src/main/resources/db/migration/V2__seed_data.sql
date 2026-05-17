-- =====================================================
-- SKILL Management Platform - Seed Data Migration
-- Database: PostgreSQL 16
-- Version: V2
-- =====================================================

-- Roles
INSERT INTO roles (id, name, display_name, description, is_system) VALUES
('11111111-1111-1111-1111-111111111111', 'USER', '普通用户', '普通用户角色', true),
('22222222-2222-2222-2222-222222222222', 'DEVELOPER', '开发者', '开发者角色', true),
('33333333-3333-3333-3333-333333333333', 'ADMIN', '管理员', '管理员角色', true),
('44444444-4444-4444-4444-444444444444', 'SUPER_ADMIN', '超级管理员', '超级管理员角色', true);

-- Permissions
INSERT INTO permissions (id, code, resource, action, description) VALUES
('p001', 'skill:create', 'skill', 'create', '创建SKILL'),
('p002', 'skill:read', 'skill', 'read', '查看SKILL'),
('p003', 'skill:update', 'skill', 'update', '更新SKILL'),
('p004', 'skill:delete', 'skill', 'delete', '删除SKILL'),
('p005', 'skill:upload', 'skill', 'upload', '上传SKILL'),
('p006', 'skill:download', 'skill', 'download', '下载SKILL'),
('p007', 'skill:deploy', 'skill', 'deploy', '部署SKILL'),
('p008', 'skill:review', 'skill', 'review', '审核SKILL'),
('p009', 'user:read', 'user', 'read', '查看用户'),
('p010', 'user:update', 'user', 'update', '更新用户'),
('p011', 'user:delete', 'user', 'delete', '删除用户'),
('p012', 'config:read', 'config', 'read', '查看配置'),
('p013', 'config:update', 'config', 'update', '更新配置'),
('p014', 'log:read', 'log', 'read', '查看日志'),
('p015', 'log:export', 'log', 'export', '导出日志');

-- Role permissions (Super Admin has all)
INSERT INTO role_permissions (role_id, permission_id) VALUES
('44444444-4444-4444-4444-444444444444', 'p001'),
('44444444-4444-4444-4444-444444444444', 'p002'),
('44444444-4444-4444-4444-444444444444', 'p003'),
('44444444-4444-4444-4444-444444444444', 'p004'),
('44444444-4444-4444-4444-444444444444', 'p005'),
('44444444-4444-4444-4444-444444444444', 'p006'),
('44444444-4444-4444-4444-444444444444', 'p007'),
('44444444-4444-4444-4444-444444444444', 'p008'),
('44444444-4444-4444-4444-444444444444', 'p009'),
('44444444-4444-4444-4444-444444444444', 'p010'),
('44444444-4444-4444-4444-444444444444', 'p011'),
('44444444-4444-4444-4444-444444444444', 'p012'),
('44444444-4444-4444-4444-444444444444', 'p013'),
('44444444-4444-4444-4444-444444444444', 'p014'),
('44444444-4444-4444-4444-444444444444', 'p015');

-- Admin permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('33333333-3333-3333-3333-333333333333', 'p001'),
('33333333-3333-3333-3333-333333333333', 'p002'),
('33333333-3333-3333-3333-333333333333', 'p003'),
('33333333-3333-3333-3333-333333333333', 'p004'),
('33333333-3333-3333-3333-333333333333', 'p005'),
('33333333-3333-3333-3333-333333333333', 'p006'),
('33333333-3333-3333-3333-333333333333', 'p007'),
('33333333-3333-3333-3333-333333333333', 'p008'),
('33333333-3333-3333-3333-333333333333', 'p009'),
('33333333-3333-3333-3333-333333333333', 'p014');

-- Developer permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('22222222-2222-2222-2222-222222222222', 'p001'),
('22222222-2222-2222-2222-222222222222', 'p002'),
('22222222-2222-2222-2222-222222222222', 'p003'),
('22222222-2222-2222-2222-222222222222', 'p005'),
('22222222-2222-2222-2222-222222222222', 'p006'),
('22222222-2222-2222-2222-222222222222', 'p007');

-- User permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('11111111-1111-1111-1111-111111111111', 'p002'),
('11111111-1111-1111-1111-111111111111', 'p006');

-- System configs
INSERT INTO system_configs (config_key, config_value, description, is_sensitive, is_read_only) VALUES
('site.name', 'SKILL管理平台', '站点名称', false, true),
('site.version', '1.0.0', '系统版本', false, true),
('upload.max_file_size', '104857600', '最大文件上传大小(字节)', false, false),
('upload.allowed_extensions', '.json,.skill,.zip', '允许的文件扩展名', false, false),
('review.auto_approve', 'false', '是否自动审核', false, false),
('notification.enabled', 'true', '是否启用通知', false, false),
('security.password_min_length', '8', '密码最小长度', false, false),
('security.max_login_attempts', '5', '最大登录失败次数', false, false);
