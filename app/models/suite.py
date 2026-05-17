import uuid
from datetime import datetime, timezone
from app import db


class Suite(db.Model):
    __tablename__ = 'suites'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    version = db.Column(db.String(50), nullable=False, default='1.0.0')
    category = db.Column(db.String(100), nullable=False)
    visibility = db.Column(db.String(20), nullable=False, default='company')
    status = db.Column(db.String(20), nullable=False, default='draft')
    owner_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    skill_count = db.Column(db.Integer, nullable=False, default=0)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    owner = db.relationship('User', backref=db.backref('suites', lazy='dynamic'))

    def to_dict(self):
        return {
            'suiteId': self.id,
            'name': self.name,
            'description': self.description,
            'version': self.version,
            'category': self.category,
            'status': self.status,
            'skillCount': self.skill_count,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }


class SuiteSkill(db.Model):
    __tablename__ = 'suite_skills'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    suite_id = db.Column(db.String(36), db.ForeignKey('suites.id'), nullable=False)
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    skill_order = db.Column(db.Integer, nullable=False, default=0)
    is_required = db.Column(db.Boolean, nullable=False, default=True)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    suite = db.relationship('Suite', backref=db.backref('suite_skills', lazy='dynamic'))
    skill = db.relationship('Skill', backref=db.backref('suite_skills', lazy='dynamic'))
