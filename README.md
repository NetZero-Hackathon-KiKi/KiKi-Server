# NetZero - 탄소중립 실천 게이미피케이션 플랫폼

> **2026 INHA SW NET-ZERO 공동 해커톤** | 문제 B: 탄소중립에 기여하기 위한 스마트 캠퍼스 SW서비스 개발

대학생들이 일상 속 탄소중립 활동을 사진으로 인증하면 Google Gemini AI가 자동 검증하여 보상을 지급하는, AWS 클라우드 기반 게이미피케이션 모바일 서비스입니다.

---

## 1. 문제 인식 및 아이디어 구체화

### 문제 인식

대학생들은 탄소중립의 중요성을 인지하면서도, 실천 동기가 부족하고 참여를 지속할 유인이 없습니다. 기존 환경 캠페인은 일회성에 그치며, 개인의 실천이 어떤 영향을 미치는지 체감하기 어렵습니다.

### 해결 전략

**게이미피케이션(Gamification)** 기법을 통해 탄소중립 실천을 "게임처럼 재미있는 일상 습관"으로 전환합니다.

- 퀘스트 시스템으로 매일 실천할 수 있는 미션 제공
- AI 기반 사진 인증으로 실천 여부 자동 검증
- XP/GP 보상 체계와 랭킹 경쟁으로 지속적인 참여 동기 부여
- 공격 시스템으로 친구 간 참여 유도 (소셜 압력 활용)
- 타임라인을 통한 실천 공유로 공동체 의식 형성

---

## 2. 서비스 이용 타겟

| 대상 | 특성 |
|---|---|
| **주 타겟** | 인하대학교, 경기대학교, 아주대학교 재학생 |
| **사용 환경** | 모바일 앱 (iOS/Android) |
| **사용 시나리오** | 등교 시 대중교통 인증, 학식 후 잔반 제로 인증, 카페에서 텀블러 인증 등 |

학과 단위 랭킹과 공격 시스템을 통해 같은 학과 학생들 간의 선의의 경쟁을 유도하며, 캠퍼스 특화 퀘스트(플로깅, 자전거 출퇴근)로 대학 환경에 최적화된 서비스를 제공합니다.

---

## 3. 주요 기능 및 AWS 활용방식

### 3-1. AWS 클라우드 인프라 구성

```
[React Native (Expo) 모바일 앱]
              |
              v
[AWS EC2] Spring Boot API 서버 (:8080)
    |            |            |
    v            v            v
[AWS RDS]    [AWS S3]    [Google Gemini API]
 MySQL DB    이미지 저장    AI 사진 인증
```

| AWS 서비스 | 활용 목적 | 구체적 활용 내용 |
|---|---|---|
| **EC2** | 백엔드 서버 호스팅 | Spring Boot 애플리케이션 운영, Git 기반 CI/CD 파이프라인, CORS 정책 관리, 퍼블릭 IP를 통한 REST API 서비스 |
| **RDS (MySQL)** | 관계형 데이터 관리 | 유저/퀘스트/공격/타임라인/상점 등 8개 테이블의 관계형 데이터 저장, EC2와 동일 VPC 내 배치로 네트워크 지연 최소화, 보안 그룹을 통한 접근 제한 |
| **S3** | 정적 파일 저장소 | 퀘스트 인증 사진을 UUID 기반 고유 파일명으로 저장하여 충돌 방지, 퍼블릭 URL 생성을 통한 이미지 직접 서빙, Content-Type 지정으로 렌더링 호환성 보장 |

### 3-2. AI 기반 퀘스트 인증 시스템 (Gemini API)

Google Gemini 2.0 Flash 모델의 **멀티모달(텍스트 + 이미지) 분석 기능**을 활용하여, 사용자가 제출한 인증 사진이 해당 미션과 관련이 있는지 자동으로 판별합니다.

```
사용자가 인증 사진 촬영 (React Native)
    |
    v
EC2 서버로 multipart/form-data 전송
    |
    v
S3에 이미지 업로드 (UUID 파일명) -> 퍼블릭 URL 생성
    |
    v
이미지 Base64 인코딩 -> Gemini API로 검증 요청
    |
    v
Gemini 판정 결과에 따라:
    |-- TRUE  -> RDS에 XP/GP 보상 반영 + 타임라인 게시물 자동 생성
    |-- FALSE -> 인증 실패 응답 반환
    |
    v
클라이언트에 JSON 응답 (인증 결과 + 타임라인 게시물 정보 포함)
```

**데이터 활용 전략:**
- 퀘스트 제목과 설명을 프롬프트에 포함하여 미션별 맞춤 검증 수행
- `temperature: 0.1` 설정으로 일관된 판정 결과 보장
- JSON 응답 파싱을 통해 Gemini의 판정 텍스트만 정확히 추출

### 3-3. 핵심 기능 상세

#### 퀘스트 시스템
- **일일 퀘스트 (DAILY)**: 텀블러 사용, 분리수거, 멀티탭 뽑기, 잔반 줄이기, 대중교통 이용
- **캠퍼스 퀘스트 (CAMPUS)**: 캠퍼스 플로깅, 에너지 절약 캠페인, 자전거 출퇴근
- 유저별 퀘스트 완료 여부를 실시간 추적하여 중복 인증 방지

#### 공격 시스템
- 같은 학과 학생에게 퀘스트를 "공격"으로 부여하는 소셜 참여 메커니즘
- 제한 시간 내 미션 수행을 유도하여 실천율 향상
- 보낸/받은 공격 이력 관리로 상호작용 추적

#### 랭킹 시스템
- **학과 랭킹**: 같은 학과 내 XP 기준 순위 + 학과 타임라인
- **교내 랭킹**: 같은 대학교 내 XP 기준 순위 + 내 순위 표시
- 경쟁 심리를 활용한 지속적인 참여 동기 부여

#### 타임라인 & 상점
- 인증 성공 시 S3 이미지 URL + 캡션이 포함된 게시물 자동 생성
- GP를 활용한 캐릭터 아이템 구매 시스템으로 보상의 가시성 확보

---

## 4. 기술 구현

### 기술 스택

| 구분 | 기술 | 선택 이유 |
|---|---|---|
| **Backend** | Spring Boot 3.3.0, Java 17 | 안정적인 REST API 구축, JPA를 통한 효율적 데이터 관리 |
| **Database** | MySQL 8.x (AWS RDS) | 관계형 데이터의 정합성 보장, 트랜잭션 기반 보상 지급 |
| **Cloud** | AWS EC2, S3, RDS | 컴퓨팅/스토리지/DB를 역할별 분리하여 확장성 확보 |
| **AI** | Google Gemini 2.0 Flash | 멀티모달 이미지 인식을 통한 자동 인증 검증 |
| **Frontend** | React Native (Expo) | iOS/Android 동시 지원 크로스플랫폼 개발 |
| **API 문서** | Springdoc OpenAPI (Swagger) | 프론트엔드 팀과의 실시간 API 명세 공유 |

### 프로젝트 구조

```
src/main/java/com/netzero/
|-- config/          # S3, Swagger, CORS 설정
|-- controller/      # 7개 REST API 컨트롤러 (Swagger 어노테이션 포함)
|-- dto/             # 요청/응답 DTO (ApiResponse, QuestVerifyResponse 등)
|-- entity/          # 8개 JPA 엔티티 (User, Quest, Attack, TimelinePost 등)
|-- repository/      # Spring Data JPA 레포지토리
|-- service/         # 비즈니스 로직 (GeminiService, QuestService, S3Service 등)
```

### API 엔드포인트 (총 16개)

| 기능 | Method | Endpoint | 설명 |
|---|---|---|---|
| 홈 | GET | `/api/home` | 프로필 + 공격미션 + 일일퀘스트 |
| 프로필 | GET | `/api/users/me` | 레벨, XP, GP 정보 |
| 퀘스트 목록 | GET | `/api/quests` | 유저별 완료 여부 포함 |
| 퀘스트 인증 | POST | `/api/quests/{id}/verify` | AI 검증 + 보상 + 타임라인 자동 게시 |
| 공격 대상 | GET | `/api/attacks/targets` | 같은 학과 유저 목록 |
| 공격 실행 | POST | `/api/attacks` | 퀘스트 부여 |
| 받은 공격 | GET | `/api/attacks/received` | 받은 공격 목록 |
| 현재 공격 | GET | `/api/attacks/received/current` | 진행 중인 공격 |
| 보낸 공격 | GET | `/api/attacks/sent` | 보낸 공격 목록 |
| 공격 미션 | GET | `/api/attacks/missions` | 부여 가능한 미션 목록 |
| 학과 랭킹 | GET | `/api/ranking/department` | 학과 내 순위 + 타임라인 |
| 교내 랭킹 | GET | `/api/ranking/university` | 교내 순위 + 내 순위 |
| 상점 | GET | `/api/shop/items` | 아이템 목록 |
| 구매 | POST | `/api/shop/purchase` | GP로 아이템 구매 |
| 내 아이템 | GET | `/api/shop/my-items` | 보유 아이템 |
| 타임라인 | GET | `/api/timeline` | 전체 게시글 (최신순) |

### 데이터 모델

```
users ──< user_quests >── quests
  |                          |
  |──< attacks (attacker) >──|
  |──< attacks (target)
  |
  |──< timeline_posts
  |──< user_items >── shop_items
  |──< notifications
```

8개 엔티티 간 외래 키 관계를 통해 데이터 정합성을 보장하며, `@Transactional`을 활용하여 퀘스트 인증 시 보상 지급과 타임라인 게시가 원자적으로 처리됩니다.

---

## 5. 윤리성 및 책임감

| 항목 | 대응 방안 |
|---|---|
| **AI 윤리** | Gemini AI의 판정 결과를 사용자에게 투명하게 공개 (성공/실패 사유 전달) |
| **데이터 최소 수집** | 인증에 필요한 사진만 수집, 위치 정보 등 불필요한 개인정보 미수집 |
| **공정성** | 동일한 AI 프롬프트와 판정 기준을 모든 사용자에게 일관 적용 |
| **공공성** | 개인의 탄소중립 실천이 학과/대학 단위 랭킹으로 연결되어 공동체 차원의 환경 인식 확산에 기여 |
| **접근성** | 크로스플랫폼 (iOS/Android) 동시 지원으로 기기 제한 없이 참여 가능 |

---

## 6. 기대효과 및 발전가능성

### 기대효과
- **실천율 향상**: 게이미피케이션 요소(XP, GP, 랭킹, 공격)로 탄소중립 실천의 지속적 동기 부여
- **습관 형성**: 매일 반복되는 일일 퀘스트를 통해 환경 보호 행동의 습관화
- **공동체 의식**: 학과/대학 단위 랭킹과 타임라인을 통한 집단적 환경 인식 제고
- **데이터 축적**: AI 인증 기록을 통해 캠퍼스 내 탄소중립 실천 데이터 축적

### 발전가능성 및 확장성
- **타 대학 확장**: 대학교/학과 기반 구조로 설계되어 타 대학교로의 확장이 용이
- **기업 연계**: ESG 경영을 실천하는 기업과의 제휴를 통한 실물 보상 연동
- **지역사회 확대**: 캠퍼스 퀘스트를 지역사회 환경 캠페인으로 확대 가능
- **탄소 크레딧 연동**: 인증 데이터를 기반으로 탄소 감축량 정량화 및 크레딧 전환
- **AWS 인프라 확장**: 사용자 증가 시 EC2 Auto Scaling, CloudFront CDN, ElastiCache 등 AWS 서비스 추가 도입으로 안정적 확장

---

## 7. 환경 설정 및 실행

### 환경 변수

| 변수 | 설명 | 기본값 |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/netzero` |
| `DB_USERNAME` | DB 유저명 | `root` |
| `DB_PASSWORD` | DB 비밀번호 | `12345678` |
| `GEMINI_API_KEY` | Google Gemini API 키 | - |
| `S3_BUCKET` | S3 버킷명 | `netzero-08-quest-images` |
| `AWS_REGION` | AWS 리전 | `us-east-1` |

### 로컬 실행

```bash
# 1. MySQL 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE netzero;"

# 2. 빌드 및 실행
./gradlew bootRun
```

### EC2 배포

```bash
git stash && git pull
./gradlew clean build -x test
kill $(lsof -t -i:8080)
nohup java -jar build/libs/NetZero-0.0.1-SNAPSHOT.jar &
```

### API 문서

서버 실행 후 Swagger UI 접속: `http://<서버주소>:8080/swagger-ui/index.html`

---

## 팀 정보

| 항목 | 내용 |
|---|---|
| **팀명** | KiKi |
| **대회** | 2026 INHA SW NET-ZERO 공동 해커톤 |
| **문제** | B: 탄소중립에 기여하기 위한 스마트 캠퍼스 SW서비스 개발 |
| **GitHub** | NetZero-Hackathon-KiKi |
