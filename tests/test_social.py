"""Tests for F011-F015 - 审核/通知/评价/收藏/分享服务 (TC-301 ~ TC-320)"""
from app.models.skill import Skill
from app.models.user import User


class TestTC301_ApproveReview:
    def test_approve_skill(self, client, admin_token, db, seed_users):
        skill = Skill(name='review-skill', skill_type='public', category='test',
                      owner_id=seed_users['user'].id, status='pending_review')
        db.session.add(skill)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/review', json={
            'decision': 'approved',
            'comment': '审核通过',
        }, headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['decision'] == 'approved'


class TestTC302_NonReviewerForbidden:
    def test_normal_user_review(self, client, user_token, db, seed_users):
        skill = Skill(name='review-skill', skill_type='public', category='test',
                      owner_id=seed_users['user'].id, status='pending_review')
        db.session.add(skill)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/review', json={
            'decision': 'approved',
        }, headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403


class TestTC303_ReviewAlreadyClosed:
    def test_review_published_skill(self, client, admin_token, db, seed_users):
        skill = Skill(name='published', skill_type='public', category='test',
                      owner_id=seed_users['user'].id, status='published')
        db.session.add(skill)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/review', json={
            'decision': 'rejected',
        }, headers={'Authorization': f'Bearer {admin_token}'})
        assert resp.status_code == 409


class TestTC305_ListNotifications:
    def test_list_notifications(self, client, user_token, db, seed_users):
        from app.models.notification import Notification
        notif = Notification(user_id=seed_users['user'].id, type='review_result',
                           title='审核结果', content='通过')
        db.session.add(notif)
        db.session.commit()

        resp = client.get('/api/v1/notifications',
                         headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 200
        assert resp.get_json()['data']['total'] >= 1


class TestTC306_MarkNonexistentRead:
    def test_mark_nonexistent_read(self, client, user_token):
        resp = client.put('/api/v1/notifications/nonexistent/read',
                         headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 404


class TestTC309_SubmitRating:
    def test_submit_rating(self, client, user_token, db, seed_users):
        from app.models.deployment import DownloadLog
        skill = Skill(name='rated-skill', skill_type='public', category='test',
                      owner_id=seed_users['admin'].id, status='published')
        db.session.add(skill)
        db.session.flush()

        dl = DownloadLog(skill_id=skill.id, user_id=seed_users['user'].id)
        db.session.add(dl)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/ratings', json={
            'score': 5,
            'comment': '非常好用',
        }, headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 201


class TestTC313_AddFavorite:
    def test_add_favorite(self, client, user_token, db, seed_users):
        skill = Skill(name='fav-skill', skill_type='public', category='test',
                      owner_id=seed_users['admin'].id, status='published')
        db.session.add(skill)
        db.session.commit()

        resp = client.post('/api/v1/favorites', json={'skillId': skill.id},
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 201


class TestTC314_FavoriteNonexistent:
    def test_favorite_nonexistent(self, client, user_token):
        resp = client.post('/api/v1/favorites', json={'skillId': 'nonexistent'},
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 404


class TestTC316_FavoriteLimit:
    def test_favorite_limit(self, client, user_token, db, seed_users):
        from app.models.social import SkillFavorite
        for i in range(100):
            skill = Skill(name=f'limit-{i}', skill_type='public', category='test',
                         owner_id=seed_users['admin'].id)
            db.session.add(skill)
            db.session.flush()
            db.session.add(SkillFavorite(user_id=seed_users['user'].id, skill_id=skill.id))
        db.session.commit()

        skill = Skill(name='over-limit', skill_type='public', category='test',
                     owner_id=seed_users['admin'].id)
        db.session.add(skill)
        db.session.commit()

        resp = client.post('/api/v1/favorites', json={'skillId': skill.id},
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 400
        assert resp.get_json()['code'] == 'FAV002'


class TestTC317_CreateShare:
    def test_create_share(self, client, user_token, db, seed_users):
        skill = Skill(name='share-skill', skill_type='public', category='test',
                      owner_id=seed_users['admin'].id, status='published')
        db.session.add(skill)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/share', json={'scope': 'department'},
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 201
        assert 'shareToken' in resp.get_json()['data']


class TestTC318_SharePrivateSkill:
    def test_share_private(self, client, user_token, db, seed_users):
        skill = Skill(name='private-skill', skill_type='public', category='test',
                      owner_id=seed_users['admin'].id, status='draft')
        db.session.add(skill)
        db.session.commit()

        resp = client.post(f'/api/v1/skills/{skill.id}/share',
                          headers={'Authorization': f'Bearer {user_token}'})
        assert resp.status_code == 403
