-- =====================================================
-- SKILL Management Platform - Initial Schema Migration
-- Database: PostgreSQL 16
-- Version: V1
-- =====================================================

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    last_login_at TIMESTAMPTZ,
    login_fail_count INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- User roles
CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, role_id)
);

-- Permissions
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Role permissions
CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(role_id, permission_id)
);

-- Skills table
CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    skill_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    owner_id UUID NOT NULL REFERENCES users(id),
    version VARCHAR(20),
    file_path VARCHAR(500),
    file_size BIGINT,
    checksum VARCHAR(64),
    download_count INT NOT NULL DEFAULT 0,
    avg_rating DECIMAL(3,2) DEFAULT 0,
    rating_count INT NOT NULL DEFAULT 0,
    published_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Skill versions
CREATE TABLE skill_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    version VARCHAR(20) NOT NULL,
    changelog TEXT,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    checksum VARCHAR(64),
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(skill_id, version)
);

-- Skill files
CREATE TABLE skill_files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    checksum VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tags
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Skill tags
CREATE TABLE skill_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(skill_id, tag_id)
);

-- Skill reviews
CREATE TABLE skill_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(20) NOT NULL,
    comment TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Skill ratings
CREATE TABLE skill_ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(skill_id, user_id)
);

-- Skill comments
CREATE TABLE skill_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    parent_id UUID REFERENCES skill_comments(id),
    content VARCHAR(1000) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Skill favorites
CREATE TABLE skill_favorites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(skill_id, user_id)
);

-- Skill shares
CREATE TABLE skill_shares (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    share_token VARCHAR(64) NOT NULL UNIQUE,
    share_type VARCHAR(20) NOT NULL DEFAULT 'public',
    expires_at TIMESTAMPTZ NOT NULL,
    access_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Suites
CREATE TABLE suites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    visibility VARCHAR(20) NOT NULL DEFAULT 'private',
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    owner_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Suite skills
CREATE TABLE suite_skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    suite_id UUID NOT NULL REFERENCES suites(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(suite_id, skill_id)
);

-- Deployments
CREATE TABLE deployments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id),
    version VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    target_type VARCHAR(20) NOT NULL DEFAULT 'docker',
    endpoint VARCHAR(500),
    config JSONB,
    deployed_by UUID REFERENCES users(id),
    deployed_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Deployment configs
CREATE TABLE deployment_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deployment_id UUID NOT NULL REFERENCES deployments(id) ON DELETE CASCADE,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(deployment_id, config_key)
);

-- Download logs
CREATE TABLE download_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_id UUID NOT NULL REFERENCES skills(id),
    user_id UUID NOT NULL REFERENCES users(id),
    version VARCHAR(20),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Operation logs
CREATE TABLE operation_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    log_type VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    user_id UUID REFERENCES users(id),
    user_name VARCHAR(100),
    resource_type VARCHAR(50),
    resource_id UUID,
    resource_name VARCHAR(200),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    result VARCHAR(20),
    detail TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    data JSONB,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- User notification settings
CREATE TABLE user_notification_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, notification_type)
);

-- System configs
CREATE TABLE system_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    description VARCHAR(500),
    is_sensitive BOOLEAN NOT NULL DEFAULT FALSE,
    is_read_only BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Help docs
CREATE TABLE help_docs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    doc_type VARCHAR(50),
    category VARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Feedbacks
CREATE TABLE feedbacks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    category VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    reply TEXT,
    replied_by UUID REFERENCES users(id),
    replied_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Skill relations
CREATE TABLE skill_relations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    target_skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    relation_type VARCHAR(50) NOT NULL,
    label VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(source_skill_id, target_skill_id, relation_type)
);

-- Position skills
CREATE TABLE position_skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    position VARCHAR(100) NOT NULL,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    importance VARCHAR(20) NOT NULL DEFAULT 'required',
    source VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(position, skill_id)
);

-- =====================================================
-- Indexes
-- =====================================================

-- Users
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_department ON users(department);

-- Skills
CREATE INDEX idx_skills_owner_id ON skills(owner_id);
CREATE INDEX idx_skills_status ON skills(status);
CREATE INDEX idx_skills_skill_type ON skills(skill_type);
CREATE INDEX idx_skills_category ON skills(category);
CREATE INDEX idx_skills_name ON skills(name);

-- Skill versions
CREATE INDEX idx_skill_versions_skill_id ON skill_versions(skill_id);

-- Skill files
CREATE INDEX idx_skill_files_skill_id ON skill_files(skill_id);

-- Skill tags
CREATE INDEX idx_skill_tags_skill_id ON skill_tags(skill_id);
CREATE INDEX idx_skill_tags_tag_id ON skill_tags(tag_id);

-- Skill reviews
CREATE INDEX idx_skill_reviews_skill_id ON skill_reviews(skill_id);
CREATE INDEX idx_skill_reviews_reviewer_id ON skill_reviews(reviewer_id);

-- Skill ratings
CREATE INDEX idx_skill_ratings_skill_id ON skill_ratings(skill_id);

-- Skill favorites
CREATE INDEX idx_skill_favorites_user_id ON skill_favorites(user_id);

-- Skill shares
CREATE INDEX idx_skill_shares_share_token ON skill_shares(share_token);

-- Deployments
CREATE INDEX idx_deployments_skill_id ON deployments(skill_id);
CREATE INDEX idx_deployments_status ON deployments(status);

-- Download logs
CREATE INDEX idx_download_logs_skill_id ON download_logs(skill_id);
CREATE INDEX idx_download_logs_user_id ON download_logs(user_id);

-- Operation logs
CREATE INDEX idx_operation_logs_user_id ON operation_logs(user_id);
CREATE INDEX idx_operation_logs_created_at ON operation_logs(created_at);
CREATE INDEX idx_operation_logs_log_type ON operation_logs(log_type);

-- Notifications
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- Feedbacks
CREATE INDEX idx_feedbacks_user_id ON feedbacks(user_id);
CREATE INDEX idx_feedbacks_status ON feedbacks(status);

-- Skill relations
CREATE INDEX idx_skill_relations_source ON skill_relations(source_skill_id);
CREATE INDEX idx_skill_relations_target ON skill_relations(target_skill_id);

-- Position skills
CREATE INDEX idx_position_skills_position ON position_skills(position);
