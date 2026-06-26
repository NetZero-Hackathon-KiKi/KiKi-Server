-- ============================================================
-- NetZero 더미 데이터 (홈 화면 와이어프레임 기준)
-- 기존 유저: id=1, 주니 (jj@naver.com)
-- ============================================================

-- ── 1. 그룹 ──
INSERT INTO `groups` (id, name, description, leader_id, created_at) VALUES
(1, '우리 과 탄소챌린지', '컴퓨터공학과 탄소중립 챌린지 그룹', NULL, NOW());

-- ── 2. 주니(id=1) 스탯을 와이어프레임에 맞게 업데이트 ──
UPDATE users SET
    level = 12,
    current_xp = 680,
    max_xp = 1200,
    green_point = 1250,
    group_id = 1
WHERE id = 1;

-- 그룹 리더를 주니로 설정
UPDATE `groups` SET leader_id = 1 WHERE id = 1;

-- ── 3. 친구 유저 12명 ──
INSERT INTO users (id, email, password, nickname, university, department, level, current_xp, max_xp, green_point, group_id, created_at) VALUES
( 2, 'chulsu@inha.ac.kr',   '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '철수',   '인하대학교', '경영학과',       8, 320, 1000,  980, 1, NOW()),
( 3, 'younghee@inha.ac.kr', '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '영희',   '인하대학교', '컴퓨터공학과',   7, 500, 1000,  870, 1, NOW()),
( 4, 'minsu@inha.ac.kr',    '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '민수',   '인하대학교', '전기공학과',    10, 200, 1200, 1480, 1, NOW()),
( 5, 'seohyun@inha.ac.kr',  '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '서현',   '인하대학교', '컴퓨터공학과',   6, 400,  900,  760, 1, NOW()),
( 6, 'junho@inha.ac.kr',    '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '준호',   '인하대학교', '컴퓨터공학과',   5, 150,  800,  620, 1, NOW()),
( 7, 'jihoon@inha.ac.kr',   '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '지훈',   '인하대학교', '컴퓨터공학과',   5, 100,  800,  540, 1, NOW()),
( 8, 'haneul@inha.ac.kr',   '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '김하늘', '인하대학교', '경영학과',      14, 900, 1500, 2350, 1, NOW()),
( 9, 'parkjh@inha.ac.kr',   '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '박지훈', '인하대학교', '컴퓨터공학과',  13, 700, 1400, 1980, 1, NOW()),
(10, 'yujin@inha.ac.kr',    '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '최유진', '인하대학교', '환경공학과',    11, 550, 1300, 1750, 1, NOW()),
(11, 'seoyeon@inha.ac.kr',  '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '이서연', '인하대학교', '기계공학과',    10, 300, 1200, 1620, 1, NOW()),
(12, 'jminsu@inha.ac.kr',   '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '정민수', '인하대학교', '전기공학과',     9, 250, 1100, 1480, 1, NOW()),
(13, 'jia@inha.ac.kr',      '$2a$10$cHehd2pAY8k5HiLydm37cuyzpWtFOfxeVOnOcGKDK2r/RqDusdVvy', '한지아', '인하대학교', '신소재공학과',   9, 180, 1100, 1350, 1, NOW());

-- ── 4. 친구 관계 (주니 ↔ 12명 = 친구 12) ──
INSERT INTO friendships (requester_id, receiver_id, status, created_at) VALUES
(1,  2, 'ACCEPTED', NOW()),
(1,  3, 'ACCEPTED', NOW()),
(1,  4, 'ACCEPTED', NOW()),
(1,  5, 'ACCEPTED', NOW()),
(1,  6, 'ACCEPTED', NOW()),
(1,  7, 'ACCEPTED', NOW()),
(1,  8, 'ACCEPTED', NOW()),
(1,  9, 'ACCEPTED', NOW()),
(1, 10, 'ACCEPTED', NOW()),
(1, 11, 'ACCEPTED', NOW()),
(1, 12, 'ACCEPTED', NOW()),
(1, 13, 'ACCEPTED', NOW());

-- ── 5. 퀘스트 ──
INSERT INTO quests (id, title, description, type, reward_gp, reward_xp, verification_method, location) VALUES
(1, '텀블러 사용 인증',           '텀블러를 사용하여 음료를 마시는 사진을 인증하세요.',   'DAILY',  20, 15, 'PHOTO', NULL),
(2, '분리수거 인증',             '분리수거를 올바르게 하는 사진을 인증하세요.',           'DAILY',  20, 15, 'PHOTO', NULL),
(3, '계단 이용 인증',            '엘리베이터 대신 계단을 이용하는 사진을 인증하세요.',   'DAILY',  20, 15, 'PHOTO', NULL),
(4, '하텍에서 텀블러로 음료 구매', '하텍 카페에서 텀블러로 음료를 구매하세요.',            'CAMPUS', 50, 30, 'PHOTO', '하텍'),
(5, '학생식당 다회용기 사용',     '학생식당에서 다회용기를 사용하세요.',                    'CAMPUS', 50, 30, 'PHOTO', '학생식당'),
(6, '도서관 빈 자리 조명 끄기',   '도서관에서 빈 자리의 조명을 끄세요.',                   'CAMPUS', 50, 30, 'PHOTO', '도서관');

-- ── 6. 공격 미션: 철수(2) → 주니(1), "하텍에서 텀블러로 음료 구매하기" ──
INSERT INTO attacks (id, attacker_id, defender_id, mission_quest_id, status, gp_at_stake, group_verification_count, required_group_verification, deadline, created_at) VALUES
(1, 2, 1, 4, 'PENDING', 50, 0, 2, DATE_ADD(NOW(), INTERVAL 13 HOUR), NOW());

-- ── 7. 알림 3개 (읽지 않음, 홈 화면 알림 배지 3) ──
INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES
(1, '공격 미션 도착!',   '철수가 당신에게 미션을 보냈어요!',       'ATTACK', 0, NOW()),
(1, '오늘의 퀘스트',     '오늘의 일일 퀘스트가 초기화되었습니다.', 'QUEST',  0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, '친구 요청',         '권도윤님이 친구 요청을 보냈습니다.',     'FRIEND', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- ── 8. 상점 아이템 ──
INSERT INTO shop_items (id, name, description, category, price) VALUES
(1, '불캡',          '멋진 불캡 모자',             'HAT',        300),
(2, '버킷햇',       '트렌디한 버킷햇',           'HAT',        300),
(3, '안경',          '스타일리시한 안경',         'ACCESSORY',  250),
(4, '후드티',       '따뜻한 후드티',             'CLOTHES',    500),
(5, '백팩',          '실용적인 백팩',             'ACCESSORY',  400),
(6, '스니커즈',     '편안한 스니커즈',           'CLOTHES',    400),
(7, '나무 친구',     '귀여운 나무 친구 악세서리', 'ACCESSORY',  350),
(8, '지구 풍선',     '지구 모양 풍선',            'ACCESSORY',  350),
(9, '배경 - 캠퍼스', '캠퍼스 배경',               'BACKGROUND', 300);

-- ── 9. 타임라인 게시글 (그룹 랭킹 화면용) ──
INSERT INTO timeline_posts (id, user_id, group_id, content, verification_type, verification_info, like_count, comment_count, created_at) VALUES
(1, 2, 1, '하텍에서 텀블러로 음료 구매',  'QUEST', '인증 1/2', 12, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 3, 1, '학생식당 다회용기 사용 완료!', 'QUEST', '인증 2/2',  8, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR));
