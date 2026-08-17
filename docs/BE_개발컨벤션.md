# BE 개발 컨벤션 — MCM-Archiv (SJF 트랙 해커톤)

> 멋쟁이사자처럼 14기 해커톤 · SJF(성주재단) 트랙
> 작성: BE 팀장 선민님 · 2026-08-12
> 대상: BE 3명 (선민님 · 민지님 · 도희님)

---

## §0. 이 문서의 위상

| 구분            | 문서                   | 내용                           |
| ------------- | -------------------- | ---------------------------- |
| **계약 (What)** | `API계약서.md` (별도 작성 예정) | 무엇을 주고받나 — 엔드포인트·필드명·타입·상태코드 |
| **컨벤션 (How)** | **이 문서**             | 어떻게 짜나 — 패키지·네이밍·계층·에러·Git   |

- **충돌 시 계약이 우선한다.** 이 문서가 계약과 어긋나면 이 문서가 틀린 것이다.
- **변경 절차**
  - 컨벤션 변경 → BE 팀장(선민님) 승인 후 이 문서 갱신
  - 계약 변경 → FE 조율 + API 계약서 갱신 (임의 변경 금지)
- ⚠️ 해커톤 특성상(제출마감 8/21 금 10시) 절차보다 속도가 우선인 지점이 있다. 애매하면 팀 채팅으로 바로 확인하고 진행한다.

---

## §1. 기술 스택 (확정)

| 항목      | 확정                                  |
| ------- | ----------------------------------------- |
| 언어      | Java 21 (LTS) — Spring Boot 4.1.0은 Java 17 최소, Java 26까지 지원 |
| 프레임워크   | **Spring Boot 4.1.0** (3.3.x는 EOL, 4.1이 신규 프로젝트 공식 권장 버전) |
| 빌드      | Gradle 8.x (Groovy DSL)                       |
| 웹       | Spring MVC                                 |
| 영속성     | Spring Data JPA                           |
| 인증      | Spring Security + 자체 JWT (Boot4=Spring Security 최신 문법, 예전 3.x 튜토리얼과 필터체인 API가 다를 수 있음 — 공식 문서 기준으로 작성) |
| 검증      | Spring Validation (Bean Validation)       |
| DB      | MySQL 8 (로컬 = `compose.yaml`)               |
| 보일러플레이트 | Lombok                                    |
| API 문서화   | springdoc-openapi-starter-webmvc-ui **3.1.0** (Spring Boot 4.x 전용 라인 — 2.x는 Boot 3용이라 호환 안 됨) |
| AI 연동   | Charm 이미지 생성용 외부 AI API (모델·엔드포인트는 민지님이 journey 도메인 착수 시 확정) |

- **H2 콘솔 없음.** 로컬 개발 DB는 `docker compose up`으로 MySQL을 띄운다.
- **H2는 `testRuntimeOnly`로만 남긴다** — Docker 없이 단위/슬라이스 테스트를 돌리기 위함. (§11-4 dialect 주의사항 참고)

### 1-1. Swagger 의존성 (build.gradle)

```groovy
dependencies {
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0'
}
```

- 접속 경로: `/swagger-ui/index.html` (또는 `/swagger-ui.html` 리다이렉트)
- Spring Security 적용 후 Swagger가 401로 막히는 게 제일 흔한 사고 지점이다. `SecurityConfig`에서 아래 경로를 반드시 permitAll 처리한다.

```java
"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
```

- JWT 인증 버튼을 Swagger UI에 노출하려면 `OpenAPIConfig`에 `SecurityScheme(type = HTTP, scheme = "bearer", bearerFormat = "JWT")` 등록 — 이거 빠뜨리면 Swagger에서 인증 필요한 API를 테스트할 수 없다.

---

## §2. 패키지 구조 — 도메인형

```text
com.example.mcmarchiv
├── McmArchivApplication.java
│
├── global/                     ← 공용.
│   ├── config/                 SecurityConfig, JpaAuditingConfig, WebConfig
│   ├── error/                  ErrorCode, BusinessException, GlobalExceptionHandler, ErrorResponse
│   ├── security/                JwtProvider, JwtAuthenticationFilter, @LoginUser, LoginUserArgumentResolver
│   ├── health/                  HealthCheckController, HealthCheckResponse
│   ├── entity/                  BaseTimeEntity
│   └── util/                    TimeUtils
│
├── auth/                        ← 선민님
│   ├── controller/ service/ dto/
│
├── user/                        ← 선민님
│   ├── controller/ service/ repository/ entity/ dto/
│   (User, RepresentativeBag 포함)
│
├── product/                     ← 도희님
│   ├── controller/ service/ repository/ entity/ dto/
│   (Product — DPP 등록/조회. journey·care 양쪽에서 참조되는 공용 도메인. care와 같은 사람이 맡아 대기시간 최소화)
│
├── journey/                     ← 민지님
│   ├── controller/ service/ repository/ entity/ dto/
│   (Charm, CharmImage — AI 참 생성 로직 포함, 비중이 커서 product는 배정하지 않음)
│
├── care/                        ← 도희님 (단, CareReport AI진단 파트는 민지님 — 2026-08-12 변경)
│   ├── controller/ service/ repository/ entity/ dto/
│   (CareReport, Store, CareReservation, CareNotification)
│
└── discover/                    ← 담당 미정(3순위, 시간 남으면)
    ├── controller/ service/ repository/ entity/ dto/
```

**규칙**

1. 도메인 패키지 내부는 `controller · service · repository · entity · dto` 5개로 고정한다. 임의의 하위 패키지를 만들지 않는다.
2. **패키지 = 오너십 경계.** 남의 도메인 패키지를 수정하는 PR은 **해당 오너를 리뷰어로** 추가한다.
3. 도메인 간 호출은 **Service → Service**로만 한다. 남의 도메인 Repository·Entity를 직접 주입하지 않는다.

### 2-1. `product/` 패키지 — journey·care 두 도메인이 공유하는 지점

`charms.product_id`(journey 소유)와 `care_reports.product_id`(care 소유)가 둘 다 `products` 테이블을 참조한다. **Product 자체의 CRUD는 `product/` 도메인이 소유**하고, journey·care는 `ProductService`를 호출만 한다 (Repository 직접 주입 금지).

```text
product/service/
└── ProductService.java   ← Product 조회·검증(findByIdAndUserId 등)의 단일 창구, 도희님 소유
```

journey(민지님)은 `ProductService`를 호출만 하고, product 테이블·엔티티에는 직접 접근하지 않는다.

> **예외 (2026-08-12):** `GET /products/scan`(NFC/QR 분기 조회)만 선민님이 담당한다. `product/` 패키지·엔티티·`ProductService` 소유권은 여전히 도희님에게 있으므로, 선민님은 `ProductController`에 `scan` 메서드를 추가하거나 `ProductService` 조회 메서드를 호출하는 선에서 작업하고, 컨트롤러 파일을 같이 건드리게 되면 §2 규칙대로 도희님을 리뷰어로 추가한다.

> **예외 (2026-08-12):** `care/` 패키지 중 AI 진단(`POST /care/reports`, `GET /care/reports/{careId}`)은 민지님이 담당한다. journey에서 이미 AI 이미지 생성 연동을 하고 있어 작업 성격이 비슷해서 옮겼다. `care/` 패키지·엔티티(`Store`, `CareReservation`, `CareNotification`) 소유권은 여전히 도희님에게 있으므로, 민지님은 `CareController`·`CareService`에서 진단 관련 메서드만 작업하고 나머지 파일을 같이 건드리면 §2 규칙대로 도희님을 리뷰어로 추가한다.
>
> 이 업무 분배는 2026-08-12 기준 최종안이지만, 진행하면서 필요하면 언제든 재조정할 수 있다.

---

## §3. 네이밍

| 대상             | 규칙                          | 예시                                               |
| -------------- | --------------------------- | ------------------------------------------------ |
| 클래스            | PascalCase                  | `CharmService`                              |
| 메서드 / 변수       | camelCase                   | `findAllByUserId()`                             |
| 상수             | UPPER_SNAKE_CASE            | `MAX_CHARM_CANDIDATES`                             |
| 패키지            | 소문자 단수                      | `charm`이 아니라 `journey`, `care`, `product`                       |
| 테이블            | snake_case **복수형**          | `charms`, `care_reports`                   |
| 컬럼             | snake_case                  | `product_id`, `target_date`                   |
| Entity 클래스     | **단수** PascalCase           | `Charm`, `CareReport`, `RepresentativeBag` |
| JSON 필드        | camelCase — **계약서 필드명 그대로** | `productId`, `travelDate`                   |
| Controller 메서드 | 행위 기반                       | `create` / `findAll` / `delete`        |
| Repository 메서드 | Spring Data 규약              | `findAllByUserIdOrderByCreatedAtDesc`            |
| 테스트 메서드        | 한글 서술 허용                    | `제품_미등록시_참_생성이_거부된다`                          |

### 3-1. DTO 네이밍

`{도메인}{행위}Request` / `{도메인}{행위}Response` 형식으로 통일한다.

| 엔드포인트 (예시)                      | Request                   | Response                                                            |
| -------------------------- | -------------------------- | -------------------------------------------------------------------- |
| `POST /auth`               | `AuthLoginRequest`        | `AuthLoginResponse`                                                 |
| `POST /products`           | `ProductRegisterRequest`  | `ProductRegisterResponse`                                           |
| `POST /journeys`           | `JourneyVerifyRequest`    | `JourneyVerifyResponse` (AI 생성 참 1개 포함)                             |
| `POST /charms`             | `CharmCreateRequest`      | `CharmCreateResponse`                                               |
| `GET /charms`              | —                          | `CharmListResponse`                                                 |
| `POST /care/reports`       | `CareReportCreateRequest` | `CareReportCreateResponse`                                          |
| `POST /care/reservations`  | `CareReservationRequest`  | `CareReservationResponse`                                           |

> 실제 엔드포인트 이름·경로는 API 계약서 작성 시 확정. 위는 명명 형식을 보여주기 위한 예시.

### 3-2. ❌ 절대 금지 — 계약 필드명 변경

계약서에 명시된 JSON 필드명은 **코드에서 바꿀 수 없다.** FE가 이 이름으로 이미 화면을 만들고 있다.

- `id` → 도메인 접두어 붙인 이름으로 통일 (`productId`, `charmId` 등, 전 엔드포인트 통일)
- 목록 래퍼 이름은 API 계약서에서 확정 후 통일 사용

---

## §4. 계층 규칙

```text
Controller  →  Service  →  Repository
  (얇게)     (트랜잭션·비즈니스)   (조회)
```

**규칙**

1. **Controller는 얇게.** `@Valid` 검증 → Service 호출 → 상태코드 반환. 비즈니스 분기(`if`)를 두지 않는다.
2. **Controller가 Entity를 반환하지 않는다.** 반드시 Response DTO로 변환한다.
3. **Entity → DTO 변환은 DTO의 `static from(...)`에 둔다.**
4. Repository는 **Service에서만** 호출한다. Controller에 직접 주입 금지.
5. 의존성 주입은 **생성자 주입 + `@RequiredArgsConstructor`**. `@Autowired` 필드 주입 금지.

---

## §5. Entity 규칙

### 5-1. 금지 사항

| 금지                                          | 이유                                                       |
| ------------------------------------------- | -------------------------------------------------------- |
| **`@Setter`**                               | 아무나 아무 때나 상태를 바꿀 수 있다                    |
| **`@Data`**                                 | `@Setter` 포함 여러 개를 한꺼번에 붙인다 |
| **`FetchType.EAGER`**                       | N+1 · 불필요한 조인. 전 연관관계 `LAZY` 고정                          |

상태 변경은 **의도가 드러나는 메서드**로 한다 (예: `CareReservation.confirm()`, `CareNotification.markAsRead()`).

### 5-2. 공통 시각 컬럼 — 테이블별 상속 여부

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {
    @CreatedDate @Column(updatable = false) private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
}
```

| 테이블 | `created_at` | `updated_at` | 상속 |
| --- | --- | --- | --- |
| `users` | ✓ | ✓ | `BaseTimeEntity` |
| `representative_bags` | — | — | 없음 |
| `products` | ✓ | — | 직접 선언 (`registered_at`이 별도 존재) |
| `charms` | ✓ | — | 직접 선언 |
| `charm_images` | — | — | 없음 |
| `care_reports` | — | — | 없음 (`analyzed_at`이 대신) |
| `stores` | — | — | 없음 |
| `care_reservations` | ✓ | — | 직접 선언 |
| `care_notifications` | ✓ | — | 직접 선언 |

> ERD 확정 전 초안이므로, 최종 ERD 문서와 다르면 **ERD가 기준**이다.

### 5-3. 스키마는 ERD가 기준

**테이블·컬럼명의 SSOT는 ERD 문서(DBML)다.** 컬럼 추가·타입 변경은 **ERD 문서 갱신을 동반**해야 PR이 승인된다.

**필수 제약**

| 대상                                       | 제약                                    |
| ---------------------------------------- | ------------------------------------- |
| `products.user_id`                    | **인덱스 필수** — 유저별 조회                   |
| `products.product_code`               | **unique** — NFC/QR 매칭키               |
| `charms.user_id`, `charms.product_id` | **인덱스 필수**                            |
| `care_reservations.care_id`           | **unique** — 한 진단(CareReport)당 예약 1개만 허용 (2026-08-15 확정) |

### 5-4. `ddl-auto`

| 프로파일 | 값 |
| --- | --- |
| local | `update` |
| prod (배포한다면) | **`validate`** |

---

## §6. 시간 규약

### 6-1. 저장·전송은 UTC, 타입은 `Instant`

`LocalDateTime` 대신 `Instant`를 쓴다 (Jackson이 `Z` 접미 포함해 직렬화). 날짜만 필요한 컬럼(`purchase_date`, `travel_date`, `target_date` 등)은 `LocalDate`를 쓴다.

### 6-2. KST 경계는 필요한 곳에서만

`care_notifications.target_date` 계산(`purchase_date + 6개월`) 등 날짜 연산에 KST가 필요하면 `global/util/TimeUtils`에 한 곳으로 모은다.

---

## §7. 에러 처리

### 7-1. 단일 봉투

```json
{ "error": { "code": "VALIDATION_FAILED", "message": "제품을 선택해주세요." } }
```

```java
@Getter @RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
    private final HttpStatus status;
    private final String defaultMessage;
}
```

- 예외는 `BusinessException(ErrorCode, message)` 하나로 통일.
- `GlobalExceptionHandler`가 모든 예외를 이 봉투로 변환. 각 Controller에서 `try-catch`로 응답 만들지 않는다.

### 7-2. 403 금지 — 타인 리소스는 404

다른 사용자의 리소스(제품·참·케어기록) 접근 시 **404**. `findByIdAndUserId` 패턴으로 조회 자체를 스코프화한다.

```java
// product 도메인
Product product = productRepository.findByIdAndUserId(productId, userId)
        .orElseThrow(() -> new BusinessException(NOT_FOUND, "제품을 찾을 수 없습니다."));

// care 도메인 — care_reports엔 user_id가 없으므로 product를 join
@Query("""
        select cr from CareReport cr
        join cr.product p
        where cr.id = :careId and p.user.id = :userId
        """)
Optional<CareReport> findByIdAndUserId(Long careId, Long userId);
```

> care 도메인의 모든 조회(`care_reports`, `care_reservations`, `care_notifications`)는 `product_id`를 경유해 소유자를 확인해야 한다 — **`product`를 join 없이 바로 조회하면 전역 조회가 된다.**

### 7-3. 성공 응답은 감싸지 않는다

`ApiResponse<T>` 같은 공통 성공 래퍼를 도입하지 않는다. 봉투는 **에러에만** 쓴다.

---

## §8. 인증 · 보안

### 8-1. JWT

- `POST /auth`, `GET /health-check` 제외 전 엔드포인트 JWT 필수.
- 로그인 방식(자체 아이디/비번 vs 소셜)은 회원가입 화면 시안 기준 **자체 로그인**으로 보임 — 확정 시 갱신.

### 8-2. 사용자 식별

**Request Body로 `userId`를 절대 받지 않는다.** 항상 JWT에서 `@LoginUser`로 추출한다.

### 8-3. 조회 스코프 — 전역 `findById` 금지

모든 조회·수정·삭제는 **현재 사용자 스코프**로 한다. product/journey 도메인은 `user_id` 직접 보유, care 도메인은 `product` join으로 스코프화 (§7-2 참고).

### 8-4. 시크릿

JWT secret · DB 비밀번호 · AI API 키는 환경변수로 주입한다. `application.yaml` 하드코딩 금지, 커밋 금지.

### 8-5. CORS

`global/config/WebConfig` 한 곳에서 관리. `allowedOrigins("*")` 금지, 오리진 명시.

| 환경 | 값 | 상태 |
| --- | --- | --- |
| local | `http://localhost:{FE_PORT}` | **TBD** |
| 배포(하는 경우) | `https://{FE_DOMAIN}` | **TBD** |

---

## §9. 유효성 검사

Bean Validation + `@Valid`. 예시:

```java
public record JourneyVerifyRequest(
        @NotNull Long productId,
        @NotBlank String country,
        @NotBlank String city,
        @Size(max = 500) String memo,
        @NotNull LocalDate travelDate) { }
```

`MethodArgumentNotValidException`은 `GlobalExceptionHandler`가 400 `VALIDATION_FAILED`로 변환한다.

---

## §10. 트랜잭션

**단일 트랜잭션이어야 하는 지점**

| 작업 | 한 트랜잭션 안에서 |
| --- | --- |
| `POST /journeys` (여정인증→참생성) | AI 이미지 생성 결과로 `charms` 저장 + `charm_images` 저장 (사진 여러 장) |
| `POST /products` (최초 등록) | `products` 저장 + (온보딩 첫 Charm 자동생성 로직이 있다면) `charms` FIRST 타입 동시 생성 |
| `POST /care/reservations` | `care_reservations` 저장 시 `care_id`·`store_id` 유효성 함께 확인 |

---

## §11. 테스트 (일정 촉박 — 최소선만)

| 대상          | 방식                        |
| ----------- | ------------------------- |
| 소유권 스코프 조회(`findByIdAndUserId`) | 단위/슬라이스 테스트 필수 — 보안 핵심 |
| Controller  | `@WebMvcTest` + `MockMvc` (핵심 엔드포인트만) |
| 코어 루프 E2E   | `@SpringBootTest` 1개: 제품등록→여정인증→참생성→조회 |

`/health-check`는 인증 없이 200 나오는지 검증한다.

---

## §12. Git · 협업 룰

### 12-1. 브랜치

`{type}/{도메인}-{내용}` — 예: `feat/journey-charm-create`, `feat/care-reservation-calendar`, `fix/product-nfc-scan`

### 12-2. 커밋

Conventional Commits, 제목은 한국어.

```text
feat: 여정인증 API 구현
fix: 케어알림 target_date 계산 오류 수정
refactor: ProductService 소유자 조회 스코프화
```

### 12-3. PR 체크리스트

```markdown
- [ ] API 계약서 필드명·상태코드를 그대로 지켰다
- [ ] 403을 쓰지 않았다 (타인 리소스 = 404)
- [ ] 성공 응답을 공통 래퍼로 감싸지 않았다
- [ ] 시크릿·키가 포함되지 않았다
- [ ] 남의 도메인 패키지를 수정했다면 해당 오너 리뷰를 받았다
- [ ] ERD를 바꿨다면 ERD 문서도 갱신했다
```

---

## 부록 A. 엔드포인트 → 패키지 · 오너 매핑 (초안)

| 영역 | 패키지 | 오너 | 비고 |
| ------ | -------------------------------- | ------- | --------------------------- |
| 로그인/회원가입 | `auth/` | 선민님 | |
| 마이페이지 | `user/` | 선민님 | RepresentativeBag 선택 포함 |
| DPP 제품등록/조회 | `product/` | 도희님 | journey·care 공유 도메인, care와 겸임. 단 `GET /products/scan`(NFC/QR)은 **선민님** 담당 (2026-08-12 변경) |
| 여정인증/Charm | `journey/` | 민지님 | AI 참 생성 3개 후보 중 1개만 저장 |
| AI 케어진단 | `care/` | 민지님 | 소유자 확인은 product join. 2026-08-12부터 도희님→민지님 |
| 케어 매장/예약/알림 | `care/` | 도희님 | 소유자 확인은 product join |
| 스타일링 추천 | `discover/` | 미정 (3순위) | 시간 남으면 |
| `/health-check` | `global/health/` | 선민님(팀장) | 인증 불필요 |

---

*작성: BE 팀장 선민님 · 2026-08-12. 변경은 팀장 승인 후 갱신으로만.*
