-- ============================================================
-- NetZero 더미 데이터
-- 기존 유저: id=1, 주니 (jj@naver.com)
-- ============================================================

-- ── 1. 그룹 (리더 없이) ──
INSERT INTO `groups` (id, name, description, leader_id, created_at) VALUES
(1, '환경 지킴이', '친구들과 함께하는 탄소중립 챌린지', NULL, NOW());

-- ── 2. 주니(id=1) 스탯 업데이트 ──
UPDATE users SET
    level = 12,
    current_xp = 680,
    max_xp = 1200,
    green_point = 1250,
    group_id = 1
WHERE id = 1;

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

-- ── 4. 친구 관계 ──
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

-- ── 5. 퀘스트 20개 ──
INSERT INTO quests (id, title, description, type, reward_gp, reward_xp, verification_method, location) VALUES
-- 캠퍼스 퀘스트 10개
(1,  '텀블러 사용',          '텀블러를 사용하여 음료를 마시는 사진을 인증하세요.',            'CAMPUS', 30,  30, 'PHOTO', NULL),
(2,  '대중교통 이용',        '대중교통을 이용하는 사진을 인증하세요.',                        'CAMPUS', 80,  80, 'PHOTO', NULL),
(3,  '자전거 이용',          '자전거를 이용하는 사진을 인증하세요.',                          'CAMPUS', 50,  50, 'PHOTO', NULL),
(4,  '이면지 활용',          '이면지를 활용하는 사진을 인증하세요.',                          'CAMPUS', 30,  30, 'PHOTO', NULL),
(5,  '학식 다먹기',          '학생식당에서 음식을 남기지 않은 식판 사진을 인증하세요.',       'CAMPUS', 30,  30, 'PHOTO', '학생식당'),
(6,  '개인 손수건 사용',     '일회용 휴지 대신 손수건을 사용하는 사진을 인증하세요.',         'CAMPUS', 30,  30, 'PHOTO', NULL),
(7,  '컴퓨터 절전기능 사용', '컴퓨터 절전 모드를 설정한 화면을 인증하세요.',                 'CAMPUS', 50,  50, 'PHOTO', NULL),
(8,  '양치할 때 컵쓰기',     '양치 시 컵을 사용하는 사진을 인증하세요.',                     'CAMPUS', 30,  30, 'PHOTO', NULL),
(9,  '메일함 비우기',        '이메일 메일함을 비운 화면 캡처를 인증하세요.',                 'CAMPUS', 30,  30, 'PHOTO', NULL),
(10, '빈 강의실 불끄기',     '빈 강의실의 불을 끄는 사진을 인증하세요.',                     'CAMPUS', 50,  50, 'PHOTO', '강의실'),
-- 일일 퀘스트 10개
(11, '다회용기로 음식 포장',  '다회용기를 이용하여 음식을 포장하는 사진을 인증하세요.',      'DAILY', 20, 20, 'PHOTO', NULL),
(12, '분리수거',              '분리수거를 올바르게 하는 사진을 인증하세요.',                  'DAILY', 30, 30, 'PHOTO', NULL),
(13, '리필 제품 구매',        '리필 제품을 구매하는 사진을 인증하세요.',                      'DAILY', 30, 30, 'PHOTO', NULL),
(14, '안쓰는 멀티탭 뽑기',   '사용하지 않는 멀티탭을 뽑은 사진을 인증하세요.',              'DAILY', 30, 30, 'PHOTO', NULL),
(15, '친환경 인증 식품 구매', '친환경 인증 마크가 있는 식품을 구매하는 사진을 인증하세요.',  'DAILY', 20, 20, 'PHOTO', NULL),
(16, '냉난방기 온도 조절',    '여름 26도 이상, 겨울 20도 이하로 설정한 화면을 인증하세요.',  'DAILY', 30, 30, 'PHOTO', NULL),
(17, '장바구니 이용',         '비닐봉지 대신 장바구니를 사용하는 사진을 인증하세요.',         'DAILY', 20, 20, 'PHOTO', NULL),
(18, '저탄소 식단',           '채소 위주의 저탄소 식단을 먹는 사진을 인증하세요.',            'DAILY', 30, 30, 'PHOTO', NULL),
(19, '음식물 남기지 않기',    '음식을 남기지 않은 빈 그릇 사진을 인증하세요.',               'DAILY', 20, 20, 'PHOTO', NULL),
(20, '계단 이용',             '엘리베이터 대신 계단을 이용하는 사진을 인증하세요.',           'DAILY', 20, 20, 'PHOTO', NULL);

-- ── 6. 공격 미션: 철수(2) → 주니(1) ──
INSERT INTO attacks (id, attacker_id, defender_id, mission_quest_id, status, gp_at_stake, group_verification_count, required_group_verification, deadline, created_at) VALUES
(1, 2, 1, 1, 'PENDING', 50, 0, 2, DATE_ADD(NOW(), INTERVAL 13 HOUR), NOW());

-- ── 7. 알림 3개 ──
INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES
(1, '공격 미션 도착!',   '철수가 당신에게 미션을 보냈어요!',       'ATTACK', 0, NOW()),
(1, '오늘의 퀘스트',     '오늘의 일일 퀘스트가 초기화되었습니다.', 'QUEST',  0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, '친구 요청',         '권도윤님이 친구 요청을 보냈습니다.',      'FRIEND', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- ── 8. 상점 아이템 ──
INSERT INTO shop_items (id, name, description, category, price) VALUES
(1,  '새싹 모자',       '귀여운 새싹 모양 모자',       'HAT',       300),
(2,  '그린 모자',       '시원한 그린 컬러 모자',       'HAT',       300),
(3,  '하늘 모자',       '청량한 하늘색 모자',          'HAT',       300),
(4,  '브라운 모자',     '따뜻한 브라운 모자',          'HAT',       300),
(5,  '하늘 후드티',     '청량한 하늘색 후드티',        'CLOTHES',   500),
(6,  '그린 후드티',     '싱그러운 그린 후드티',        'CLOTHES',   500),
(7,  '아이보리 후드티', '깔끔한 아이보리 후드티',      'CLOTHES',   500),
(8,  '텀블러',          '친환경 텀블러 악세서리',      'ACCESSORY', 350),
(9,  '검은 가방',       '시크한 블랙 가방',            'ACCESSORY', 350),
(10, '브라운 가방',     '따뜻한 브라운 가방',          'ACCESSORY', 350);

-- ── 9. 타임라인 게시글 3개 ──
INSERT INTO timeline_posts (id, user_id, group_id, content, verification_type, verification_info, like_count, comment_count, created_at) VALUES
(1, 2, 1, '오늘 텀블러 챙겨서 카페 다녀왔어요 ☕',  'QUEST', NULL, 12, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 3, 1, '학식에서 밥 싹 다 먹었다 🍚 잔반 제로!', 'QUEST', NULL,  8, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(3, 4, 1, '오늘은 계단으로 5층까지 올라갔습니다 💪', 'QUEST', NULL,  5, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR));