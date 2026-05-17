import uuid
from datetime import datetime, timezone
from app import db


class OperationLog(db.Model):
    __tablename__ = 'operation_logs'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'))
    action = db.Column(db.String(100), nullable=False)
    resource_type = db.Column(db.String(50))
    resource_id = db.Column(db.String(36))
    detail = db.Column(db.Text)
    ip_address = db.Column(db.String(50))
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    user = db.relationship('User', backref=db.backref('operation_logs', lazy='dynamic'))

    def to_dict(self):
        return {
            'logId': self.id,
            'userId': self.user_id,
            'action': self.action,
            'resourceType': self.resource_type,
            'resourceId': self.resource_id,
            'detail': self.detail,
            'ipAddress': self.ip_address,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }


class SystemConfig(db.Model):
    __tablename__ = 'system_configs'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    config_key = db.Column(db.String(100), unique=True, nullable=False)
    config_value = db.Column(db.Text, nullable=False)
    description = db.Column(db.String(500))
    is_sensitive = db.Column(db.Boolean, nullable=False, default=False)
    is_readonly = db.Column(db.Boolean, nullable=False, default=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    def to_dict(self):
        return {
            'configKey': self.config_key,
            'value': '******' if self.is_sensitive else self.config_value,
            'description': self.description,
            'isSensitive': self.is_sensitive,
            'updatedAt': self.updated_at.isoformat() if self.updated_at else None,
        }


class HelpDoc(db.Model):
    __tablename__ = 'help_docs'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    title = db.Column(db.String(200), nullable=False)
    content = db.Column(db.Text, nullable=False)
    doc_type = db.Column(db.String(20), nullable=False, default='guide')
    category = db.Column(db.String(100))
    is_published = db.Column(db.Boolean, nullable=False, default=True)
    created_by = db.Column(db.String(36), db.ForeignKey('users.id'))
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    creator = db.relationship('User', backref=db.backref('help_docs', lazy='dynamic'))

    def to_dict(self):
        return {
            'docId': self.id,
            'title': self.title,
            'type': self.doc_type,
            'summary': self.content[:200] if self.content else '',
            'createdAt': self.created_at.isoformat() if self.created_at else None,
            'updatedAt': self.updated_at.isoformat() if self.updated_at else None,
        }


class Feedback(db.Model):
    __tablename__ = 'feedbacks'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    type = db.Column(db.String(30), nullable=False)
    title = db.Column(db.String(200), nullable=False)
    content = db.Column(db.Text, nullable=False)
    status = db.Column(db.String(20), nullable=False, default='pending')
    reply = db.Column(db.Text)
    replied_by = db.Column(db.String(36), db.ForeignKey('users.id'))
    replied_at = db.Column(db.DateTime(timezone=True))
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    user = db.relationship('User', foreign_keys=[user_id], backref=db.backref('feedbacks', lazy='dynamic'))
    replier = db.relationship('User', foreign_keys=[replied_by])

    def to_dict(self):
        return {
            'feedbackId': self.id,
            'type': self.type,
            'title': self.title,
            'content': self.content,
            'status': self.status,
            'userId': self.user_id,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }
