# API 계약서 — MCM-Archiv (SJF 트랙 해커톤)

> 작성: BE 팀장 선민님 · 2026-08-12
> 이 문서와 `BE_개발컨벤션_해커톤.md`가 충돌하면 **이 문서가 우선**한다.
> FE 연동 목표일: 8/16. 그 전까지 이 문서의 엔드포인트·필드명은 최대한 고정한다.

---

## §0. 공통 규약

| 항목 | 규칙 |
| --- | --- |
| Base URL | `TBD` (배포 시 확정) |
| 인증 | `Authorization: Bearer <JWT>` — `POST /auth`, `POST /users`(회원가입), `GET /health-check` 제외 전부 필수 (2026-08-12 수정: 회원가입은 토큰 발급 전이라 인증 불필요하게 정정) |
| 날짜/시간 | datetime은 `Instant`(UTC, `Z` 접미) 예: `2026-08-12T05:00:00Z`. 날짜만 필요한 값은 `YYYY-MM-DD` |
| 성공 응답 | **raw 객체** 그대로 반환. 공통 래퍼(`ApiResponse<T>`) 없음 |
| 에러 응답 | `{ "error": { "code": "...", "message": "..." } }` 하나로 통일 |
| 인증 실패 | `401` |
| 타인 리소스 접근 | `403` 대신 **`404`** (존재 여부 은닉) |
| 검증 실패 | `400 VALIDATION_FAILED` |
| `userId` | Request Body/쿼리로 절대 받지 않음. JWT에서 서버가 추출 |

### 공통 에러 코드

| code | status | 설명 |
| --- | --- | --- |
| `UNAUTHORIZED` | 401 | 로그인 필요 |
| `NOT_FOUND` | 404 | 리소스 없음 / 타인 소유 |
| `VALIDATION_FAILED` | 400 | 입력값 오류 |
| `INTERNAL_ERROR` | 500 | 서버 오류 |

---

## §1. Auth / User — 담당 선민님

### `POST /auth`
로그인. (소셜 로그인 여부 미확정 — 일단 자체 아이디/비번 기준으로 작성, 화면 시안과 일치)

- 인증: 불필요
- Request
```json
{ "loginId": "journey0811", "password": "string" }
```
- Response `200`
```json
{ "userId": 1, "accessToken": "string", "nickname": "존나비" }
```
- 에러: `401 UNAUTHORIZED` (아이디/비번 불일치)

### `POST /users`
회원가입.

- 인증: 불필요
- Request
```json
{
  "name": "string", "nickname": "string", "email": "string",
  "loginId": "string", "password": "string"
}
```
- Response `201`
```json
{ "userId": 1, "nickname": "string" }
```
- 에러: `400 VALIDATION_FAILED` (형식 오류, 중복 아이디/이메일)

### `GET /users/me`
마이페이지 조회.

- Response `200`
```json
{
  "userId": 1, "name": "김OO", "nickname": "존나비",
  "email": "journey@mcm.com", "loginId": "journey0811",
  "profileImage": "url", "representativeBagId": 1
}
```

### `PATCH /users/me`
마이페이지 수정 (닉네임/프로필이미지/비밀번호 등 부분 수정).

- Request (변경할 필드만)
```json
{ "nickname": "string", "profileImage": "url", "password": "string" }
```
- Response `200`: `GET /users/me`와 동일 형태

### `DELETE /users/me`
회원탈퇴.

- Response `204`

### `GET /representative-bags`
대표가방 목록 조회 (지금은 1개, 추후 선택 확장용).

- Response `200`
```json
{ "bags": [ { "bagId": 1, "name": "Aren East West Shoulder Bag", "imageUrl": "url", "isDefault": true } ] }
```

---

## §2. Product — 담당 도희님 (단, `GET /products/scan`은 **선민님** 담당 — 2026-08-12 변경)

### `POST /products`
DPP 제품 등록. NFC/QR 태깅 후 신규 제품이면 이 API 호출.

- Request
```json
{ "productCode": "MCM-XXXXX", "productName": "Aren Shopper", "purchaseDate": "2026-08-11" }
```
- Response `201`
```json
{
  "productId": 1, "productCode": "MCM-XXXXX", "productName": "Aren Shopper",
  "isVerified": true, "registeredAt": "2026-08-11T00:00:00Z"
}
```
- 비고: 최소 1개 제품 등록이 온보딩 필수 조건 (기능명세 확정사항)

### `GET /products`
내 제품 목록 조회 (여정인증 화면 드롭다운용).

- Response `200`
```json
{ "products": [ { "productId": 1, "productName": "Aren Shopper", "productImage": "url" } ] }
```

### `GET /products/{productId}`
제품 상세 (Certificate of Authenticity 화면).

- Response `200`
```json
{
  "productId": 1, "productName": "Aren Shopper", "productCode": "MCM-XXXXX",
  "productImage": "url", "purchaseDate": "2026-08-11", "registeredAt": "2026-08-11T00:00:00Z",
  "isVerified": true
}
```
- 에러: `404` (없거나 타인 제품)

### `GET /products/scan?code={productCode}` — 담당 선민님

NFC/QR 태깅 시 분기 조회. 신규/기존 여부 프론트에서 이 응답으로 판단.

- Response `200` (이미 등록된 경우) — `GET /products/{productId}`와 동일 형태
- Response `404` (미등록 — 신규 등록 폼으로 이동)
- ⚠️ 재구매 시나리오(동일 유저가 같은 모델 추가구매) 분기 로직은 **팀 논의 필요 — 미해결 상태로 보류**

---

## §3. Journey / Charm — 담당 민지님

### `POST /journeys`
여정인증 + AI 참 후보 생성. (제품 선택 필수)

- Request
```json
{
  "productId": 1, "country": "Japan", "city": "Tokyo",
  "memo": "string", "travelDate": "2026-08-01",
  "imageUrls": ["url1", "url2"]
}
```
- Response `200`
```json
{
  "candidates": [
    { "candidateId": 1, "imageUrl": "url" },
    { "candidateId": 2, "imageUrl": "url" },
    { "candidateId": 3, "imageUrl": "url" }
  ]
}
```
- 비고: 후보 3개는 **이 시점엔 DB 미저장** (임시 생성물). 사용자가 고른 것만 `POST /charms`로 확정 저장

### `POST /charms`
참 최종 확정 저장 (후보 중 선택 + 메모).

- Request
```json
{
  "productId": 1, "country": "Japan", "city": "Tokyo",
  "memo": "string", "travelDate": "2026-08-01",
  "selectedImageUrl": "url", "imageUrls": ["url1", "url2"]
}
```
- Response `201`
```json
{ "charmId": 10, "aiImageUrl": "url", "createdAt": "2026-08-12T05:00:00Z" }
```
- 비고: 선택 안 된 나머지 후보 2개는 저장하지 않는다 (확정사항)

### `GET /charms`
나의 Journey / Charm 목록 (지도·통계용).

- Response `200`
```json
{
  "totalCountries": 3, "totalJourneys": 4,
  "charms": [
    { "charmId": 10, "aiImageUrl": "url", "country": "Japan", "city": "Tokyo", "travelDate": "2026-08-01" }
  ]
}
```

### `GET /charms/{charmId}`
참 상세 (클릭 시 당시 사진/메모/동반 제품 정보).

- Response `200`
```json
{
  "charmId": 10, "aiImageUrl": "url", "country": "Japan", "city": "Tokyo",
  "memo": "string", "travelDate": "2026-08-01",
  "product": { "productId": 1, "productName": "Aren Shopper" },
  "images": ["url1", "url2"]
}
```
- 에러: `404` (없거나 타인 참)

---

## §4. Care — 담당 도희님 (단, `POST /care/reports`, `GET /care/reports/{careId}`는 **민지님** 담당 — 2026-08-12 변경)

> 담당 분배는 2026-08-12 기준 최종안. 진행하다 필요하면 재조정 가능.

### `POST /care/reports` — 담당 민지님
AI 가방 상태 분석.

- Request
```json
{ "productId": 1, "imageUrl": "url" }
```
- Response `201`
```json
{
  "careId": 5, "totalScore": 86,
  "scratchScore": 90, "stainScore": 88, "wearScore": 70,
  "aiComment": "string", "analyzedAt": "2026-08-12T05:00:00Z"
}
```

### `GET /care/reports/{careId}` — 담당 민지님
진단 결과 상세 재조회.

- Response `200`: 위와 동일 형태
- 에러: `404`

### `GET /stores?lat={lat}&lng={lng}`
근처 매장 목록 (케어예약 화면 진입 시).

- Response `200`
```json
{ "stores": [ { "storeId": 1, "name": "레푸스 경기광주경안점", "address": "string", "phone": "string" } ] }
```

### `GET /stores/{storeId}/available-times?date={date}`
매장 예약 가능 시간 (캘린더 형식).

- Response `200`
```json
{ "date": "2026-08-12", "availableTimes": ["14:30", "15:00", "16:00"] }
```

### `POST /care/reservations`
케어 예약 (진단결과 기준).

- Request
```json
{ "careId": 5, "storeId": 1, "reservationDate": "2026-08-14", "reservationTime": "15:00" }
```
- Response `201`
```json
{
  "reservationId": 3, "storeId": 1, "reservationDate": "2026-08-14",
  "reservationTime": "15:00", "status": "PENDING"
}
```
- 비고: **한 CareReport(`careId`)당 예약은 1개만 허용** (`care_id` unique, 2026-08-15 확정)
- 에러: `400 VALIDATION_FAILED` (이미 해당 `careId`로 예약이 존재하는 경우), `404` (careId/storeId가 없거나 타인 소유)

### `GET /care/notifications`
케어 알림 목록 (Care Reminder 팝업용).

- Response `200`
```json
{
  "notifications": [
    { "notificationId": 1, "productId": 1, "targetDate": "2026-08-11", "isRead": false }
  ]
}
```

### `PATCH /care/notifications/{notificationId}/read`
알림 읽음 처리.

- Response `204`

---

## §5. 아직 계약 미확정 (구현 순서상 후순위)

| 항목 | 상태 |
| --- | --- |
| Discover(스타일링 추천) | 담당 선민님, 3순위 — 계약 작성 보류 (2026-08-17 배정) |
| NFC/QR 재구매(기존 모델 추가등록) 분기 | 미해결, 팀 논의 필요 |
| 소셜 로그인 여부 | `POST /auth`를 자체 로그인 기준으로 임시 작성, 확정되면 갱신 |

---

*작성: BE 팀장 선민님 · 2026-08-12. 계약 변경은 FE 조율 후 이 문서 갱신으로만.*
