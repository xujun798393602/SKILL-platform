-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Seed default roles
INSERT INTO roles (id, name, display_name, description, is_system, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'USER', '普通用户', '普通用户角色', true, NOW(), NOW()),
    (gen_random_uuid(), 'DEVELOPER', '开发者', '开发者角色', true, NOW(), NOW()),
    (gen_random_uuid(), 'ADMIN', '管理员', '系统管理员角色', true, NOW(), NOW()),
    (gen_random_uuid(), 'SUPER_ADMIN', '超级管理员', '超级管理员角色', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Seed default permissions
INSERT INTO permissions (id, code, name, resource, action, description, created_at)
VALUES
    (gen_random_uuid(), 'skill:public:read', '查看公开SKILL', 'skill', 'read', '查看公开发布的SKILL', NOW()),
    (gen_random_uuid(), 'skill:upload', '上传SKILL', 'skill', 'write', '上传新的SKILL文件', NOW()),
    (gen_random_uuid(), 'skill:delete', '删除SKILL', 'skill', 'delete', '删除SKILL', NOW()),
    (gen_random_uuid(), 'user:manage', '管理用户', 'user', 'admin', '用户管理', NOW()),
    (gen_random_uuid(), 'review:approve', '审核SKILL', 'review', 'admin', '审核SKILL提交', NOW()),
    (gen_random_uuid(), 'config:manage', '管理配置', 'config', 'admin', '系统配置管理', NOW()),
    (gen_random_uuid(), 'log:read', '查看日志', 'log', 'read', '查看操作日志', NOW()),
    (gen_random_uuid(), 'stats:read', '查看统计', 'stats', 'read', '查看统计数据', NOW())
ON CONFLICT (code) DO NOTHING;

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (id, role_id, permission_id, created_at)
SELECT gen_random_uuid(), r.id, p.id, NOW()
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign basic permissions to USER role
INSERT INTO role_permissions (id, role_id, permission_id, created_at)
SELECT gen_random_uuid(), r.id, p.id, NOW()
FROM roles r, permissions p
WHERE r.name = 'USER' AND p.code IN ('skill:public:read')
ON CONFLICT DO NOTHING;

-- Seed system configs
INSERT INTO system_configs (id, config_key, config_value, description, is_sensitive, is_readonly, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'app.name', 'SKILL管理平台', '应用名称', false, false, NOW(), NOW()),
    (gen_random_uuid(), 'app.version', '1.0.0', '应用版本', false, true, NOW(), NOW()),
    (gen_random_uuid(), 'upload.maxSize', '104857600', '最大上传大小(字节)', false, false, NOW(), NOW())
ON CONFLICT (config_key) DO NOTHING;
