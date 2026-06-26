-- Users (인하대학교 - 같은 학과끼리 공격 가능)
INSERT IGNORE INTO users (id, nickname, department, university, xp, gp, profile_image_url) VALUES
(1, '김지호', '컴퓨터공학과', '인하대학교', 980, 2350, NULL),
(2, '박지훈', '컴퓨터공학과', '인하대학교', 870, 1980, NULL),
(3, '최유진', '환경공학과', '인하대학교', 640, 1760, NULL),
(4, '정민수', '전기공학과', '인하대학교', 920, 1480, NULL),
(5, '임지후', '컴퓨터공학과', '인하대학교', 750, 1350, NULL),
(6, '권도윤', '컴퓨터공학과', '인하대학교', 760, 1200, NULL),
(7, '이서연', '기계공학과', '인하대학교', 510, 900, NULL),
(8, '한지우', '신소재공학과', '인하대학교', 300, 600, NULL),
(9, '김하늘', '경영학과', '인하대학교', 1100, 1800, NULL),
(10, '홍길동', '컴퓨터공학과', '인하대학교', 680, 1250, NULL);

-- Quests (DAILY: 오늘의 퀘스트, CAMPUS: 캠퍼스 퀘스트)
INSERT IGNORE INTO quests (id, title, description, type, reward_xp, reward_gp) VALUES
(1, '텀블러사용하기', '카페에서 텀블러로 음료 구매한 사진을 찍어주세요', 'DAILY', 15, 20),
(2, '분리수거하기', '올바르게 분리수거한 사진을 찍어주세요', 'DAILY', 15, 20),
(3, '안쓰는 멀티탭 뽑기', '사용하지 않는 멀티탭 전원을 끈 사진을 찍어주세요', 'DAILY', 15, 20),
(4, '음식물 남기지 않기', '식사 후 잔반 없이 깨끗하게 먹은 사진을 찍어주세요', 'DAILY', 15, 20),
(5, '대중교통 이용하기', '대중교통을 이용한 사진을 찍어주세요', 'DAILY', 15, 20),
(6, '캠퍼스 플로깅', '학교 내에서 쓰레기를 주운 사진을 찍어주세요', 'CAMPUS', 30, 50),
(7, '에너지 절약 캠페인', '학교 에너지 절약 캠페인에 참여한 사진을 찍어주세요', 'CAMPUS', 30, 50),
(8, '자전거 출퇴근', '자전거로 등하교한 사진을 찍어주세요', 'CAMPUS', 25, 40);

-- Shop Items
INSERT IGNORE INTO shop_items (id, name, description, price, image_url) VALUES
(1, '브라운 크로스백', '깔끔한 브라운 크로스백 아이템', 200, NULL),
(2, '초록 모자', '귀여운 나뭇잎 모양 모자', 150, NULL),
(3, '에코 티셔츠', '재활용 소재로 만든 티셔츠', 300, NULL),
(4, '숲 배경', '초록 숲 배경 테마', 400, NULL),
(5, '바다 배경', '깨끗한 바다 배경 테마', 400, NULL),
(6, '지구 배지', '지구를 지키자 배지', 100, NULL);

-- Attacks (같은 학과끼리만)
INSERT IGNORE INTO attacks (id, attacker_id, target_id, quest_id, completed, deadline, created_at) VALUES
(1, 2, 1, 1, false, DATE_ADD(NOW(), INTERVAL 20 HOUR), NOW()),
(2, 5, 6, 2, false, DATE_ADD(NOW(), INTERVAL 18 HOUR), NOW()),
(3, 1, 2, 3, true, DATE_ADD(NOW(), INTERVAL 24 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- Timeline Posts (퀘스트 인증 성공 게시글)
INSERT IGNORE INTO timeline_posts (id, user_id, quest_title, image_url, like_count, comment_count, created_at) VALUES
(1, 1, '텀블러사용하기', NULL, 12, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 3, '음식물 남기지 않기', NULL, 8, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 2, '분리수거하기', NULL, 5, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(4, 5, '안쓰는 멀티탭 뽑기', NULL, 3, 0, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, 6, '대중교통 이용하기', NULL, 7, 2, DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- User Items (구매한 아이템)
INSERT IGNORE INTO user_items (id, user_id, item_id, purchased_at) VALUES
(1, 1, 1, NOW()),
(2, 1, 2, NOW()),
(3, 2, 3, NOW());
