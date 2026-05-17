import uuid
from datetime import datetime, timezone
from app import db


class SkillReview(db.Model):
    __tablename__ = 'skill_reviews'

    id = db.Column(db.String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    skill_id = db.Column(db.String(36), db.ForeignKey('skills.id'), nullable=False)
    version_id = db.Column(db.String(36), db.ForeignKey('skill_versions.id'))
    action = db.Column(db.String(20), nullable=False)
    comment = db.Column(db.Text)
    reviewer_id = db.Column(db.String(36), db.ForeignKey('users.id'), nullable=False)
    reviewed_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))
    created_at = db.Column(db.DateTime(timezone=True), nullable=False, default=lambda: datetime.now(timezone.utc))

    skill = db.relationship('Skill', backref=db.backref('reviews', lazy='dynamic'))
    reviewer = db.relationship('User', backref=db.backref('reviews', lazy='dynamic'))

    def to_dict(self):
        return {
            'reviewId': self.id,
            'skillId': self.skill_id,
            'reviewerId': self.reviewer_id,
            'decision': self.action,
            'comment': self.comment,
            'reviewedAt': self.reviewed_at.isoformat() if self.reviewed_at else None,
        }
