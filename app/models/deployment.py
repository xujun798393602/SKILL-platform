import uuid
from datetime import datetime, timezone
from app import db


class Deployment(db.Model):
    __tablename__ = 'deployments'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    version_id = db.Column(db.String(36), db.ForeignKey('skill_versions.id'))
    status = db.Column(db.String(30), nullable=False, default='pending')
    platform = db.Column(db.String(20), nullable=False)
    endpoint = db.Column(db.String(500))
    config_snapshot = db.Column(db.Text)
    deployed_by = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    started_at = db.Column(db.DateTime(timezone=True))
    completed_at = db.Column(db.DateTime(timezone=True))
    error_message = db.Column(db.Text)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('deployments', lazy='dynamic'))
    deployer = db.relationship('User', backref=db.backref('deployments', lazy='dynamic'))

    def to_dict(self):
        return {
            'deploymentId': self.id,
            'skillId': self.skill_id,
            'status': self.status,
            'platform': self.platform,
            'endpoint': self.endpoint,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }


class DeploymentConfig(db.Model):
    __tablename__ = 'deployment_configs'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    platform = db.Column(db.String(20), nullable=False)
    config_json = db.Column(db.Text, nullable=False)
    created_by = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('deployment_configs', lazy='dynamic'))


class DownloadLog(db.Model):
    __tablename__ = 'download_logs'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    version_id = db.Column(db.String(36), db.ForeignKey('skill_versions.id'))
    downloaded_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('download_logs', lazy='dynamic'))
    user = db.relationship('User', backref=db.backref('download_logs', lazy='dynamic'))
