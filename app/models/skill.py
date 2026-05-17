import uuid
from datetime import datetime, timezone
from app import db


class Skill(db.Model):
    __tablename__ = 'skills'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    skill_type = db.Column(db.String(20), nullable=False)
    category = db.Column(db.String(100), nullable=False)
    current_version = db.Column(db.String(50), nullable=False, default='1.0.0')
    status = db.Column(db.String(30), nullable=False, default='draft')
    owner_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    developer_name = db.Column(db.String(100))
    download_count = db.Column(db.Integer, nullable=False, default=0)
    deploy_count = db.Column(db.Integer, nullable=False, default=0)
    avg_rating = db.Column(db.Numeric(3, 2), nullable=False, default=0)
    rating_count = db.Column(db.Integer, nullable=False, default=0)
    visibility = db.Column(db.String(20), nullable=False, default='company')
    file_size = db.Column(db.BigInteger, nullable=False, default=0)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    published_at = db.Column(db.DateTime(timezone=True))

    owner = db.relationship('User', backref=db.backref('skills', lazy='dynamic'))
    tags = db.relationship('Tag', secondary='skill_tags', backref=db.backref('skills', lazy='dynamic'))

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'skillType': self.skill_type,
            'category': self.category,
            'version': self.current_version,
            'status': self.status,
            'ownerId': self.owner_id,
            'developer': self.developer_name,
            'downloadCount': self.download_count,
            'deployCount': self.deploy_count,
            'avgRating': float(self.avg_rating) if self.avg_rating else 0,
            'ratingCount': self.rating_count,
            'visibility': self.visibility,
            'fileSize': self.file_size,
            'tags': [t.name for t in self.tags],
            'createdAt': self.created_at.isoformat() if self.created_at else None,
            'updatedAt': self.updated_at.isoformat() if self.updated_at else None,
            'publishedAt': self.published_at.isoformat() if self.published_at else None,
        }


class SkillVersion(db.Model):
    __tablename__ = 'skill_versions'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    version = db.Column(db.String(50), nullable=False)
    tag = db.Column(db.String(20))
    changelog = db.Column(db.Text)
    file_path = db.Column(db.String(500), nullable=False)
    file_size = db.Column(db.BigInteger, nullable=False, default=0)
    checksum = db.Column(db.String(64), nullable=False)
    created_by = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('versions', lazy='dynamic'))
    creator = db.relationship('User', backref=db.backref('created_versions', lazy='dynamic'))

    __table_args__ = (db.UniqueConstraint('skill_id', 'version', name='uk_skill_versions_skill_version'),)


class SkillFile(db.Model):
    __tablename__ = 'skill_files'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    version_id = db.Column(db.String(36), db.ForeignKey('skill_versions.id'), nullable=False)
    file_name = db.Column(db.String(255), nullable=False)
    file_path = db.Column(db.String(500), nullable=False)
    file_size = db.Column(db.BigInteger, nullable=False, default=0)
    file_type = db.Column(db.String(20), nullable=False)
    checksum = db.Column(db.String(64), nullable=False)
    chunk_total = db.Column(db.Integer, nullable=False, default=1)
    chunk_uploaded = db.Column(db.Integer, nullable=False, default=0)
    upload_status = db.Column(db.String(20), nullable=False, default='uploading')
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('files', lazy='dynamic'))
    version = db.relationship('SkillVersion', backref=db.backref('files', lazy='dynamic'))


class Tag(db.Model):
    __tablename__ = 'tags'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    name = db.Column(db.String(50), unique=True, nullable=False)
    usage_count = db.Column(db.Integer, nullable=False, default=0)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))


class SkillTag(db.Model):
    __tablename__ = 'skill_tags'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id', ondelete='CASCADE'), nullable=False)
    tag_id = db.Column(db.String(36), db.ForeignKey('tags.id'), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    __table_args__ = (db.UniqueConstraint('skill_id', 'tag_id', name='uk_skill_tags_skill_tag'),)


class SkillRelation(db.Model):
    __tablename__ = 'skill_relations'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    source_skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    target_skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    relation_type = db.Column(db.String(30), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    source_skill = db.relationship('Skill', foreign_keys=[source_skill_id], backref=db.backref('outgoing_relations', lazy='dynamic'))
    target_skill = db.relationship('Skill', foreign_keys=[target_skill_id], backref=db.backref('incoming_relations', lazy='dynamic'))


class PositionSkill(db.Model):
    __tablename__ = 'position_skills'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    position = db.Column(db.String(100), nullable=False)
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    relevance_score = db.Column(db.Numeric(3, 2), nullable=False, default=0)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('positions', lazy='dynamic'))
