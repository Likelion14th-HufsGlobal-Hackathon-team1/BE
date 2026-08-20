# MCM-Archiv

명품 핸드백을 위한 디지털 제품 여권(DPP) 서비스 백엔드입니다.
구매 후 정품 인증부터, 여행지에서 함께한 순간을 AI가 그려주는 참(Charm) 기록, AI 기반 가방 상태 진단까지를 하나의 흐름으로 연결합니다.

Likelion 14th HUFS Global 해커톤(SJF 트랙) 출품작 — 팀 원픽.

## 핵심 기능

- **DPP 정품 인증**: NFC/QR 태깅으로 제품 등록, 등록된 제품만 인증된 것으로 관리
- **AI 여정 인증 (Journey / Charm)**: 여행 국가·도시·기억을 바탕으로 AI가 그 지역 특색을 담은 참(Charm) 이미지 후보 3개를 생성, 사용자가 선택해 확정 저장
- **AI 가방 상태 진단 (Care)**: 가방 사진을 분석해 스크래치·오염·마모 점수 산출, 결과 기반 매장 케어 예약 연계
- **Discover**: 스타일링 추천 목록 조회

## 기술 스택

| 구분 | 내용 |
| --- | --- |
| Language / Framework | Java 21, Spring Boot 4.1.0 |
| DB | MySQL 8.0 (prod), H2 (test) |
| ORM | Spring Data JPA |
| 인증 | Spring Security + JWT (jjwt) |
| API 문서 | springdoc-openapi (Swagger UI) |
| 이미지 저장 | Cloudinary (FE가 직접 업로드, BE는 URL만 수신) |
| 배포 | Docker Compose, Gabia Cloud VM, Cloudflare Tunnel(HTTPS) |
| CI/CD | GitHub Actions (master push 시 자동 배포) |

## AI 스택

| 모델 | 용도 | 비고 |
| --- | --- | --- |
| **Google Gemini** (`gemini-2.5-flash-image`, Nano Banana) | 여정인증 시 참(Charm) 이미지 후보 3개 생성 | 도시별 랜드마크·여행 메모를 반영한 정사각형 일러스트, 텍스트/로고 미포함 프롬프트 설계, 사용자 여행 사진을 멀티모달 참고 이미지로 활용 |
| **OpenAI** (`gpt-4.1-mini`) | 케어 진단 시 가방 상태 비전 분석 | 업로드된 가방 사진에서 스크래치/오염/마모 점수 산출, 가방이 식별 안 되면 임의 점수 없이 에러 반환 |

두 모델 모두 이미지를 직접 받지 않고 **Cloudinary 공개 URL**만 입력으로 사용하며, 서버가 외부 URL을 직접 호출하는 지점(참고 이미지 fetch, 진단 이미지 검증)에는 `res.cloudinary.com` 호스트로 제한한 SSRF 방어가 적용되어 있습니다.

## 프로젝트 구조

도메인 패키지 단위로 구성되어 있으며, 도메인 간 참조는 소유 도메인의 Service를 통해서만 이루어집니다.

```
user/       회원가입, 로그인, 마이페이지, 대표가방
product/    DPP 제품 등록/조회, NFC·QR 스캔
journey/    여정인증, AI 참 생성/조회, 참 배치
care/       AI 가방 상태 진단, 케어 예약, 케어 알림
discover/   스타일링 추천 목록
global/     보안(JWT), 예외 처리 공통 모듈
```

## API 문서

전체 API 스펙은 [`docs/API계약서.md`](docs/API계약서.md)를 참고하세요. 실행 중인 서버에서는 Swagger UI로도 확인 가능합니다.

```
{BASE_URL}/swagger-ui/index.html
```

## 로컬 실행

```bash
./gradlew bootRun
```

`local` 프로필 기준으로 동작하며, 필요한 환경변수(`JWT_SECRET`, `OPENAI_API_KEY`, `GEMINI_API_KEY`, `CLOUDINARY_*` 등)는 `.env.prod.example`을 참고해 별도로 설정해야 합니다.

## 테스트

```bash
./gradlew test
```

외부 AI API(Gemini, OpenAI) 및 Cloudinary 업로드는 `MockitoBean`으로 대체하여 테스트합니다.

## 배포

```bash
docker compose --env-file .env -f compose.prod.yaml up -d --build
```

`master` 브랜치에 푸시되면 GitHub Actions가 자동으로 배포합니다.

## 팀

Likelion 14th HUFS Global 해커톤 팀 원픽 — 고선민(BE 팀장), 김다은, 김민지, 김지우, 윤도희
