import uuid
from datetime import datetime, timezone
from app import db


class SkillRating(db.Model):
    __tablename__ = 'skill_ratings'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    rating = db.Column(db.Integer, nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('ratings', lazy='dynamic'))
    user = db.relationship('User', backref=db.backref('ratings', lazy='dynamic'))

    __table_args__ = (db.UniqueConstraint('skill_id', 'user_id', name='uk_skill_ratings_skill_user'),)

    def to_dict(self):
        return {
            'ratingId': self.id,
            'skillId': self.skill_id,
            'userId': self.user_id,
            'score': self.rating,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }


class SkillComment(db.Model):
    __tablename__ = 'skill_comments'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    content = db.Column(db.Text, nullable=False)
    parent_id = db.Column(db.String(36), db.ForeignKey('skill_comments.id'))
    is_deleted = db.Column(db.Boolean, nullable=False, default=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    updated_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('comments', lazy='dynamic'))
    user = db.relationship('User', backref=db.backref('comments', lazy='dynamic'))


class SkillFavorite(db.Model):
    __tablename__ = 'skill_favorites'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    user_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('favorites', lazy='dynamic'))
    user = db.relationship('User', backref=db.backref('favorites', lazy='dynamic'))

    __table_args__ = (db.UniqueConstraint('skill_id', 'user_id', name='uk_skill_favorites_skill_user'),)

    def to_dict(self):
        return {
            'favoriteId': self.id,
            'skillId': self.skill_id,
            'userId': self.user_id,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
        }


class SkillShare(db.Model):
    __tablename__ = 'skill_shares'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    share_token = db.Column(db.String(64), unique=True, nullable=False)
    share_type = db.Column(db.String(20), nullable=False, default='link')
    created_by = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    expires_at = db.Column(db.DateTime(timezone=True), nullable=False)
    access_count = db.Column(db.Integer, nullable=False, default=0)
    is_active = db.Column(db.Boolean, nullable=False, default=True)
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('shares', lazy='dynamic'))
    creator = db.relationship('User', backref=db.backref('shares', lazy='dynamic'))

    def to_dict(self):
        return {
            'shareId': self.id,
            'shareToken': self.share_token,
            'skillId': self.skill_id,
            'scope': self.share_type,
            'createdAt': self.created_at.isoformat() if self.created_at else None,
            'expiresAt': self.expires_at.isoformat() if self.expires_at else None,
        }
