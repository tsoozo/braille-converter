# Claude Code-д өгөх ажлын төлөвлөгөө

> Энэ файлыг та өөрөө уншиж, Claude Code-ийн chat-д **үе шат бүрд** хуулж өгөөрэй.
> CLAUDE.md нь репо-д хадгалагдсан байгаа учир Claude автоматаар уншина.
> Та нэг үе шатыг гүйцэтгүүлээд, сэтгэлд нийцэх болсон үед л дараагийн үе шатанд шилждэг байгаарай.

---

## 📦 Бэлдэх алхам (Claude Code эхлүүлэхээс өмнө)

```bash
cd /path/to/braille-converter
# CLAUDE.md болон .gitignore-ийг repo root-д хуулна
cp /path/to/CLAUDE.md .
cp /path/to/.gitignore .

# Initial commit
git add CLAUDE.md .gitignore
git commit -m "chore: add Claude Code context and gitignore"
git push
```

---

## 🚀 PHASE 1: Backend skeleton (Spring Boot suurь)

### Prompt 1.1 — Spring Boot project үүсгэх

```
Сайн уу! Энэ репод Spring Boot 3.2.x backend project үүсгэх хэрэгтэй байна.

Шаардлага:
1. backend/ хавтас доор Spring Boot Maven project үүсгэ
2. Java 17, Maven 3.9.x ашиглана
3. groupId: mn.braille, artifactId: braille-converter
4. CLAUDE.md дэх "Backend" section дахь бүх dependency-уудыг pom.xml-д нэмнэ
5. Үндсэн package бүтэц үүсгэ: controller, service, repository, entity, dto, validator, security, config, exception
6. BrailleApplication.java main класс
7. application.yml файл — dev profile + prod profile, PostgreSQL config
8. application-test.yml — H2 in-memory тест-д
9. .env.example файл (DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET)

Code style: Lombok ашиглах, конструктор-ийн оронд @RequiredArgsConstructor

Бүх ажлыг хийсний дараа `cd backend && mvn clean compile` командыг ажиллуулж, амжилттай compile болж байгааг шалга. Алдаа гарвал засна.

Гүйцэтгэсэн ажлаа дараах форматаар тайлагнан өг:
- Үүсгэсэн файлуудын жагсаалт
- Шийдсэн асуудлууд (хэрэв байсан бол)
- Дараагийн алхамд хийх ёстой зүйлс
```

### Prompt 1.2 — Domain Entity болон Repository

```
Одоо domain layer бичнэ.

Entity-үүд (entity/ хавтаст):
1. BrailleMapping
   - id (Long, @Id @GeneratedValue)
   - cyrillic (String, unique, not null) — кирилл тэмдэгт
   - braille (String, not null) — Брайль Unicode тэмдэгт
   - type (CharacterType enum: LETTER, NUMBER, PUNCTUATION, SPECIAL)
   - createdAt, updatedAt (LocalDateTime, @CreationTimestamp/@UpdateTimestamp)

2. ConversionHistory
   - id (Long, @Id @GeneratedValue)
   - input (String, не columnDefinition = "TEXT")
   - output (String, columnDefinition = "TEXT")
   - inputLength, outputLength (int)
   - durationMs (long)
   - timestamp (LocalDateTime)
   - userId (String, nullable)

3. AppUser (Spring Security-ийн)
   - id, username, password (bcrypt), role (UserRole enum: ROLE_USER, ROLE_ADMIN)

Repository-ууд (repository/ хавтаст):
- BrailleMappingRepository extends JpaRepository<BrailleMapping, Long>
  - findByCyrillic(String c): Optional<BrailleMapping>
  - findByType(CharacterType type): List<BrailleMapping>
- ConversionHistoryRepository extends JpaRepository<ConversionHistory, Long>
- AppUserRepository extends JpaRepository<AppUser, Long>
  - findByUsername(String username): Optional<AppUser>

Lombok ашиглах: @Entity, @Data, @NoArgsConstructor, @AllArgsConstructor, @Builder

Compile хийж шалга. Бүх Entity-нд тестийн @SpringBootTest нэмж DB connection-ийг шалга (test profile).
```

### Prompt 1.3 — Брайль mapping JSON-г үүсгэх ба ачаалах

```
Брайль харгалзуулалтын мэдээллийг JSON хэлбэрээр кодоос тусгаарлаж хадгална.

1. src/main/resources/braille-mapping.json үүсгэ. Доорх агуулгатай байх:
{
  "version": "1.0",
  "standard": "Монгол улсын Брайль стандарт 2019.12.03",
  "letters": {
    "а": "⠁", "б": "⠃", "в": "⠺", "г": "⠛", "д": "⠙",
    "е": "⠑", "ё": "⠡", "ж": "⠚", "з": "⠵", "и": "⠊",
    "й": "⠯", "к": "⠅", "л": "⠇", "м": "⠍", "н": "⠝",
    "о": "⠕", "ө": "⠧", "п": "⠏", "р": "⠗", "с": "⠎",
    "т": "⠞", "у": "⠥", "ү": "⠳", "ф": "⠋", "х": "⠓",
    "ц": "⠉", "ч": "⠟", "ш": "⠱", "щ": "⠭", "ъ": "⠷",
    "ы": "⠮", "ь": "⠾", "э": "⠪", "ю": "⠳", "я": "⠫"
  },
  "numbers": {
    "0": "⠚", "1": "⠁", "2": "⠃", "3": "⠉", "4": "⠙",
    "5": "⠑", "6": "⠋", "7": "⠛", "8": "⠓", "9": "⠊"
  },
  "punctuation": {
    ".": "⠲", ",": "⠂", "?": "⠦", "!": "⠖",
    ":": "⠒", ";": "⠆", "-": "⠤", "\"": "⠦", "(": "⠐⠣", ")": "⠐⠜"
  },
  "special": {
    "CAPITAL_SIGN": "⠠",
    "NUMBER_SIGN": "⠼",
    "SPACE": " "
  }
}

ВАЖНО: Энэ нь жишээ мэдээлэл бөгөөд бодит стандарттай 100% таарахгүй байж магадгүй. 
Цаашид баталсан стандартаас хайж засна. Эхний MVP-д энэ хувилбараар ажиллана.

2. config/MappingLoader.java класс үүсгэ:
   - @Component, @Slf4j
   - @PostConstruct method-аар Spring startup үед JSON-г уншиж memory-д ачаалах
   - Jackson ObjectMapper ашиглах
   - Map<String, String> letters, numbers, punctuation, special хувьсагчтай
   - Getter методууд: getLetterMap(), getNumberMap(), getPunctuationMap(), getSpecialChar(String key)

3. Application startup хийгээд лог дээр "Loaded 35 letters, 10 numbers, ..." гэх зурвас гаргах.

Тест бичих: MappingLoaderTest — JSON ачаалагдаж байгааг шалгах.
```

### Prompt 1.4 — Validator класс

```
Оролтын текстийг шалгах validator класс үүсгэх хэрэгтэй.

validator/CyrillicValidator.java:
- @Component, @Slf4j, @RequiredArgsConstructor
- MappingLoader-ийг inject хийнэ
- Static Pattern: монгол кирилл (а-я, ё, ө, ү), тоо (0-9), цэг таслал (.,!?-;:()'\"'), зай 
- Method: validate(String input): ValidationResult
  - null/empty → INVALID with "Empty input"
  - 10000 тэмдэгтээс урт → INVALID with "Too long (max 10000)"
  - Кирилл бус тэмдэгт байвал → INVALID with details
  - Бүгд OK → VALID
- ValidationResult — record (Java 17 feature):
  - boolean valid
  - List<String> errors
  - List<Integer> invalidPositions

Тест: CyrillicValidatorTest:
- testValidInput
- testEmptyInput
- testTooLongInput
- testInvalidCharacter
- testMixedValidAndInvalid (хэрэглэгч "сайн hello" гэж бичсэн тохиолдол)

Бүгдийг compile, тест ажиллуулж шалга.
```

### Prompt 1.5 — Гол ConverterService

```
Хөрвүүлэлтийн логик гол сервис.

service/CharacterMapper.java:
- @Service, @RequiredArgsConstructor
- MappingLoader-ийг inject
- mapCharacter(char c): String — нэг тэмдэгт хөрвүүлэх
- mapLetter, mapNumber, mapPunctuation private методууд

service/RuleProcessor.java:
- @Service, @RequiredArgsConstructor
- applyCapitalRule(String text): String — том үсгийн өмнө ⠠ нэмэх
- applyNumberRule(String text): String — тоон цуваагийн өмнө ⠼ нэмэх (зөвхөн эхэн дээр, дараагийн зайгаар л дуусна)
- processText(String text): String — бүх дүрмийг нэгтгэн хэрэгжүүлэх

service/ConverterService.java:
- @Service, @RequiredArgsConstructor, @Slf4j
- CharacterMapper, RuleProcessor, CyrillicValidator inject
- convert(String input): String
  - 1. Validate
  - 2. Apply rules (capital, number)
  - 3. Map each character
  - 4. Join result with space (Брайль зайгаар)
  - 5. Log latency (System.currentTimeMillis())
  - 6. Return result

ВАЖНО: 
- "Монгол" → ⠠⠍⠕⠝⠛⠕⠇ (capital sign + 6 letters)
- "2025 он" → ⠼⠃⠚⠃⠑ ⠕⠝ (number sign + 4 digits, space, 2 letters)
- "Сайн уу?" → ⠠⠎⠁⠊⠝ ⠥⠥⠦ (capital + 4 letters, space, 2 letters, ?)

Тестүүд: ConverterServiceTest — дээрх 3 жишээг шалгана. Бүгдийг compile, run.
```

### Prompt 1.6 — REST API Controller + DTO

```
REST API endpoint бичих.

dto/ хавтаст (record хэрэглэх):
- ConvertRequest(String text, ConvertOptions options) — @NotBlank, @Size(max=10000)
- ConvertOptions(boolean includeStats, String format)  // format: "unicode" | "brf"
- ConvertResponse(String result, int inputLength, int outputLength, long durationMs)
- ValidationResponse(boolean valid, List<String> errors)
- ErrorResponse(String message, String code, Instant timestamp)
- MappingResponse(Map<String, String> letters, Map<String, String> numbers, ...)

controller/ConvertController.java:
- @RestController, @RequestMapping("/api/v1"), @RequiredArgsConstructor, @Slf4j
- Endpoint-ууд:
  - POST /convert → ConvertResponse (@Valid ConvertRequest)
  - POST /validate → ValidationResponse
  - GET /mapping → MappingResponse

exception/ хавтаст:
- BrailleException (RuntimeException)
- ValidationException extends BrailleException
- GlobalExceptionHandler (@RestControllerAdvice)
  - @ExceptionHandler(MethodArgumentNotValidException.class)
  - @ExceptionHandler(BrailleException.class)
  - @ExceptionHandler(Exception.class) — generic 500

Тест: ConvertControllerTest — @WebMvcTest, MockMvc ашиглан 3 endpoint бүрд хариу шалгана.

OpenAPI docs үзэх:
- pom.xml-д springdoc-openapi-starter-webmvc-ui 2.3.x нэмэх
- application.yml-д springdoc тохиргоо нэмэх
- /swagger-ui.html үзэгдэж байгаа эсэхийг шалга

Бүгд compile, run, тест pass болсон эсэхийг баталгаажуулна.
```

### Prompt 1.7 — Security (Spring Security + JWT)

```
JWT-д суурилсан security нэмнэ. CLAUDE.md дотор тусгасан security tactics-ийг хэрэгжүүлэх ёстой.

Dependency-д нэмэх (pom.xml):
- spring-boot-starter-security
- jjwt-api 0.12.x
- jjwt-impl 0.12.x (runtime)
- jjwt-jackson 0.12.x (runtime)
- bucket4j-core 8.x (rate limiting)

security/ хавтаст:
1. JwtUtil — token үүсгэх, parse хийх, verify
2. JwtAuthFilter — OncePerRequestFilter, Bearer token-аас user-г сэргээх
3. SecurityConfig — @Configuration, @EnableWebSecurity
   - csrf().disable()
   - sessionManagement → STATELESS
   - authorizeHttpRequests:
     - /api/v1/convert, /validate, /mapping → permitAll (V1.0)
     - /actuator/health → permitAll
     - /api/admin/** → ROLE_ADMIN
     - /api/v1/auth/** → permitAll
4. RateLimitFilter — Bucket4j ашиглан IP бүрт 100 req/min лимит
5. CustomUserDetailsService — AppUserRepository ашиглан хэрэглэгч ачаалах

controller/AuthController.java:
- POST /api/v1/auth/login → LoginRequest → JWT
- POST /api/v1/auth/refresh → refresh token → шинэ access token

dto-д LoginRequest, JwtResponse(accessToken, refreshToken, expiresIn) record нэмэх

ВАЖНО: 
- JWT secret-ийг env-ээс (application.yml-д ${JWT_SECRET:default-dev-only-secret})
- Access token 15 минут, refresh token 7 хоног
- Password-ыг bcrypt-ээр hash хийх (BCryptPasswordEncoder)

Тест: SecurityConfigTest, JwtUtilTest. Бүгдийг compile болон ажиллуул.

V1.0 MVP-д convert endpoint нь нийтийн (auth-гүй) байх боломжтой — гэхдээ admin endpoint-ууд (mapping шинэчлэх, статистик) ROLE_ADMIN шаардана.
```

### Prompt 1.8 — Docker + Health + Actuator

```
Backend-ийг containerize хийх.

1. docker/Dockerfile.backend:
   - Multi-stage build: maven build + jre runtime
   - FROM maven:3.9-eclipse-temurin-17 AS build
   - FROM eclipse-temurin:17-jre-alpine
   - HEALTHCHECK curl /actuator/health
   - EXPOSE 8080

2. docker/docker-compose.yml:
   - postgres:16 service (port 5432)
   - backend service (build from Dockerfile.backend, port 8080)
   - depends_on: postgres
   - volumes: pgdata
   - environment variables

3. application.yml-д Actuator тохируулах:
   - /actuator/health endpoint enable
   - /actuator/info, /actuator/metrics
   - management.endpoint.health.show-details: when_authorized

4. README.md шинэчлэх (хэрэв байхгүй бол үүсгэх):
   - Project description
   - Setup instructions (local Maven, Docker)
   - API documentation link
   - Tech stack table

`docker-compose up -d` командаар хоёр контейнер startup-аад http://localhost:8080/actuator/health дээр {"status":"UP"} буцаах ёстой.

Шалгасны дараа git commit хий.
```

---

## 🎨 PHASE 2: Frontend (React + Vite + Tailwind)

### Prompt 2.1 — Vite + React + Tailwind setup

```
Frontend-ийг бэлдэх.

1. frontend/ хавтаст Vite + React project үүсгэ:
   - npm create vite@latest . -- --template react
   - npm install
   - npm install -D tailwindcss@latest postcss autoprefixer
   - npx tailwindcss init -p

2. Tailwind config (tailwind.config.js):
   - content: ["./index.html", "./src/**/*.{js,jsx}"]
   - theme.extend.fontFamily.sans: ["Inter", "system-ui"]
   - dark mode тохиргоо: "class"

3. src/index.css-д @tailwind directives нэмэх

4. shadcn/ui setup:
   - npx shadcn@latest init
   - Default style, slate color, CSS variables
   - Initial component-ууд add хийнэ: button, input, textarea, card, alert, dialog

5. src бүтэц:
   - components/ — UI components
   - pages/ — full page components
   - api/ — Axios клиент
   - hooks/ — custom hooks
   - lib/ — utils (cn, formatters)

6. axios суулгаж api/client.js үүсгэх:
   - baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080"
   - JWT-ийг localStorage-аас Bearer header болгож нэмэх interceptor

7. .env.example үүсгэ: VITE_API_URL=http://localhost:8080

`npm run dev` ажиллуулж http://localhost:5173 дээр хоосон Vite default page харагдаж байгаа эсэхийг шалга.
```

### Prompt 2.2 — Гол хөрвүүлэгчийн UI

```
Гол хөрвүүлэгчийн хуудас бичнэ.

pages/ConverterPage.jsx:
- Дэлгэцийн дээд хэсэгт header: "Кирилл-Брайль хөрвүүлэгч"
- Дунд хэсэгт 2 баганатай layout (mobile дээр 1 багана):
  - Зүүн: Cyrillic input (Textarea, placeholder "Кирилл текстээ оруулна уу...")
  - Баруун: Braille output (Textarea, readonly, monospace font)
- Доор товчнууд:
  - "Хөрвүүлэх" — primary button
  - "Хуулах" — secondary, navigator.clipboard
  - "Цэвэрлэх" — outline, оролтыг нулть болгох
  - "BRF татах" — download .brf file

Custom hook: hooks/useConverter.js
- useState: input, output, loading, error, stats
- convert() async function:
  - POST /api/v1/convert
  - Loading state удирдах
  - Error catch хийх

UI requirements (WCAG 2.1 AA):
- Textarea-нд aria-label "Кирилл текст оруулах талбар"
- Output-д aria-label "Брайль гаралт"
- Бүх товчинд aria-describedby
- Keyboard navigation сайн ажиллана (Tab order зөв)
- Focus visible indicator
- Contrast ratio >= 4.5:1
- Screen reader-д аль ч элемент танигдсан байх

Real-time conversion (зөвлөмж, FR-10):
- input-нд debounce 300ms-ээр convert API дуудах
- Эсвэл "Хөрвүүлэх" товч дарахад л

Бүх UI монгол хэлээр харагдана. components/LanguageToggle.jsx гэдгээр хэлийг сэлгэх боломж нэмэх (хэрэв хүсвэл).

Тест: npm run dev. Backend ажиллаж байгаа эсэхийг шалгаад "Монгол" гэж бичээд "Хөрвүүлэх" товчинд дарахад ⠠⠍⠕⠝⠛⠕⠇ гаралт үзэх ёстой.
```

### Prompt 2.3 — BRF татах, accessibility, error handling

```
Гол функцийг өргөтгөнө.

1. BRF татах:
   - lib/brfExport.js — Брайль Unicode тэмдэгтийг BRF (Braille Ready Format) ASCII болгож хувиргах функц
   - Жишээ: ⠁ → "a", ⠃ → "b", ⠉ → "c" (BRF код)
   - "BRF татах" товч дарахад blob үүсгээд a.download = "result.brf"

2. Error handling:
   - components/ErrorAlert.jsx — shadcn Alert ашиглах
   - Network error → "Холболтын алдаа гарлаа. Дахин оролдоно уу."
   - Validation error → backend-ийн errors хүснэгтийг харуулна
   - 429 (Rate limit) → "Та хэт олон удаа хүсэлт илгээлээ. 1 минут хүлээнэ үү."

3. Accessibility improvements:
   - Skip to main content link
   - aria-live="polite" region — хөрвүүлэлт дууссан үед screen reader ажиллана ("Хөрвүүлэлт дууслаа, 8 тэмдэгт")
   - Focus management — convert хийсний дараа output руу focus
   - High contrast mode дэмжих (prefers-contrast: more)

4. Loading state:
   - Convert хийж буй үед button-нд spinner харагдана
   - Output-д "Хөрвүүлж байна..." гэсэн тэмдэг

5. Statistics card:
   - Хөрвүүлэлт дууссаны дараа stats харуулах:
     - "Оролт: X тэмдэгт"
     - "Гаралт: Y тэмдэгт"
     - "Хугацаа: Z мс"

`npm run dev`-ээр шалгана. NVDA screen reader суулгаад navigate хийж үзэхийг зөвлөж байна.
```

### Prompt 2.4 — Frontend Docker + Production build

```
Frontend-ийг production-д бэлдэх.

1. docker/Dockerfile.frontend:
   - Multi-stage: node:20-alpine для build + nginx:alpine for serve
   - npm run build
   - COPY dist/ -> /usr/share/nginx/html

2. docker/nginx.conf:
   - SPA fallback: try_files $uri $uri/ /index.html
   - Gzip enabled
   - Cache headers for assets
   - /api proxy_pass to backend service

3. docker-compose.yml-д frontend service нэмэх (port 80)

4. Production environment variables:
   - VITE_API_URL=/api (nginx proxy-аар backend руу)

5. README.md шинэчлэх:
   - Development setup
   - Production deployment
   - Architecture diagram link

`docker-compose up --build` ажиллуулж 3 service (postgres, backend, frontend) бүгд ажилахыг шалга.
```

---

## 🧪 PHASE 3: Тест ба чанарын хяналт

### Prompt 3.1 — Backend тест coverage

```
Test coverage 80% хүртэл нэмэгдүүлэх (NFR-10).

1. JaCoCo plugin pom.xml-д нэмэх (test coverage tool):
   - jacoco-maven-plugin 0.8.x
   - prepare-agent, report goal
   - Minimum coverage rule: 80%

2. Missing тестүүд нэмэх:
   - ConverterServiceTest: 10+ test cases (edge cases оруулна)
   - CharacterMapperTest
   - RuleProcessorTest
   - All controller-нд @SpringBootTest integration test
   - Security test: JWT generation, validation
   - Repository test: @DataJpaTest

3. Integration test: BrailleConverterIntegrationTest:
   - Бүх stack-ийг H2 with @SpringBootTest
   - End-to-end: POST /api/v1/convert with cyrillic → expect braille

4. mvn clean test jacoco:report ажиллуулж target/site/jacoco/index.html-ийг шалгана. Coverage 80%-аас өндөр байх ёстой.

5. Хэрэв coverage хүрэхгүй бол шууд тест нэмж засна.
```

### Prompt 3.2 — Frontend тест

```
Frontend-д Vitest + React Testing Library тест нэмэх.

1. devDependencies:
   - vitest
   - @testing-library/react
   - @testing-library/jest-dom
   - @testing-library/user-event
   - jsdom

2. vite.config.js-д test config нэмэх

3. Гол componentэд тест бичих:
   - ConverterPage.test.jsx: input оруулах, convert click, output харагдах
   - useConverter.test.js: hook-ийн ажиллагаа
   - brfExport.test.js: BRF conversion

4. npm run test ажиллах ёстой.

5. CI/CD-д ашиглах: github actions workflow yml файл .github/workflows/ci.yml
   - On push to main:
     - Backend: mvn test
     - Frontend: npm test
     - Build Docker images
```

---

## 📚 PHASE 4: Дотоод сайжруулалт

### Prompt 4.1 — Audit log, statistics

```
Багийн ажил №8-ын Audit Trail tactic-ийг хэрэгжүүлэх.

1. service/AuditService.java:
   - logConversion(input, output, duration, userId, ipAddress)
   - logError(exception, context)
   - logAuthEvent(event, username, success)

2. AOP-аар тогтмол аудит:
   - @Aspect, @Component
   - @Around("execution(* mn.braille.controller.*.*(..))") — бүх controller method
   - Log: method name, parameters, duration

3. AuditLog entity, AuditLogRepository

4. Admin endpoint: GET /api/admin/audit/recent — сүүлийн 100 audit log

5. Statistics endpoint: GET /api/admin/stats:
   - Total conversions today/week/month
   - Average latency
   - Top 10 most common inputs (анonymized)

ROLE_ADMIN шаардлагатай.
```

### Prompt 4.2 — Mapping шинэчлэх admin UI

```
Админ хэрэглэгч Брайль mapping-ийг засах боломж өгөх.

Backend:
1. controller/AdminMappingController.java — @PreAuthorize("hasRole('ADMIN')")
   - GET /api/admin/mapping — list all
   - PUT /api/admin/mapping/{cyrillic} — update braille for character
   - POST /api/admin/mapping — add new mapping
   - DELETE /api/admin/mapping/{id} — soft delete
   - POST /api/admin/mapping/reload — reload from JSON file

2. service/MappingService.java — CRUD ажиллагаа

Frontend:
1. pages/AdminPage.jsx — login required
2. components/MappingTable.jsx — editable table, inline edit
3. components/AdminLogin.jsx — login form, JWT хадгална

Login flow:
- /admin → AdminLogin
- POST /api/v1/auth/login
- accessToken → localStorage
- Дараагийн API call-ууд автоматаар JWT-тэй явна
```

---

## 🚢 PHASE 5: Deployment

### Prompt 5.1 — Production бэлдэх

```
Production-ready production setup.

1. application-prod.yml:
   - logging.level.mn.braille: INFO
   - logging.file.name: /var/log/braille.log
   - server.compression.enabled: true
   - spring.datasource.hikari.maximum-pool-size: 10

2. docker-compose.prod.yml — production-ready:
   - postgres-ийн root password-ийг secret-ээс
   - backend, frontend volume-гүй (immutable)
   - restart: unless-stopped

3. .env.prod.example файл

4. Nginx reverse proxy + Let's Encrypt SSL setup гарын авлага README.md-д

5. Backup стратеги: pg_dump cron job — docker container хэлбэрээр
```

---

## 🎓 Чухал сануулга

### Claude Code-тэй ажиллахдаа

1. **Үе шат тус бүрд phase-ийг бүхэлд нь өгөхгүй** — нэг prompt өгөөд гүйцэтгэхийг хүлээ, тэгээд commit хийсний дараа л дараагийн prompt-ийг өг.

2. **Claude Code-аас тестийг ажиллуулсан байх ёстой** — хэрэв "compile болсон" гэвэл, тестийг ажиллуулж pass болсон эсэхийг шалгуулах.

3. **Git commit-ийг үе шат бүрд хийнэ** — Phase 1.1 → commit, Phase 1.2 → commit гэх мэтээр. Хэрэв алдаа гарвал revert хийх боломжтой.

4. **CLAUDE.md-г шинэчилж байх** — хэрэв архитектурын шийдвэр өөрчлөгдвөл CLAUDE.md-г шинэчлэх. Claude Code дараагийн session-д шинэ контекстийг ашиглана.

5. **Шинэ файл үүсгэхээс өмнө байгаа эсэхийг шалгуулах** — "Хэрэв CharacterMapper аль хэдийн байгаа бол шинэчилнэ, байхгүй бол үүсгэнэ" гэх мэт зааварчилгаа өгөх.

6. **Connascence минимум** — Багийн ажил №2-т авч үзсэн анти-pattern-уудаас зайлсхийх: God class, magic number, олон параметртэй method.

### Хичээлийн баримтын тооцоо

Энэхүү хөгжүүлэлт нь дараахи багийн ажлуудтай уялдаатай:
- **Багийн ажил №1, Бие даалт №1** — Шаардлагууд, юзкейс, класс диаграмм
- **Багийн ажил №2** — Modularity: God class анти-pattern-аас зайлсхийх
- **Багийн ажил №3** — Компонент диаграмм: яг ийм бүтэцтэй (Frontend/Backend/Data/Cross-cut)
- **Багийн ажил №4** — Availability tactics: Actuator health, retry
- **Багийн ажил №6** — Layered Architecture: яг хэрэгжүүлж байна
- **Багийн ажил №8** — Security tactics: JWT, rate limit, validation, audit
- **Лабораторийн ажил №3** — Connascence: magic string-ыг enum-аар сэлгэх

Хичээлийн багш репог нээж шалгахад **бүх баримтын дагуу хэрэгжүүлсэн** гэдгийг харна.
