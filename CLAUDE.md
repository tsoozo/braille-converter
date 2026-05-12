# Кирилл-Брайль хөрвүүлэгч систем

> Энэ файл нь Claude Code-д төслийн тогтмол контекстийг өгөх зорилготой. Бүх chat session-д Claude Code эхлэхдээ үүнийг автоматаар уншина.

## Төслийн тойм

Монгол кирилл текстийг Монгол улсын Брайль стандарт (2019.12.03)-ын дагуу Брайль үсэгт хөрвүүлэх вэб-д суурилсан туслах технологи. Гол хэрэглэгч: хараагүй болон харааны бэрхшээлтэй иргэд, тэдний багш, орчуулагч мэргэжилтнүүд.

GitHub: https://github.com/tsoozo/braille-converter

## Technology Stack (хатуу мөрдөнө)

### Backend
- **Java 17** (LTS)
- **Spring Boot 3.2.x** — фреймворк
- **Spring Web** — REST API
- **Spring Data JPA** — ORM
- **Spring Security 6.x** — auth/authz
- **Spring Boot Actuator** — health endpoints
- **PostgreSQL 16** — production DB
- **H2** — testing only
- **Maven 3.9.x** — build
- **JUnit 5 + Mockito** — test
- **Lombok** — boilerplate reduction
- **Springdoc OpenAPI (Swagger)** — API docs
- **Bucket4j** — rate limiting

### Frontend
- **React 18+**
- **Vite** — build tool
- **Tailwind CSS** — styling
- **shadcn/ui** — UI components
- **Axios** — HTTP client
- **react-router-dom** — routing

### DevOps
- **Docker + Docker Compose** — containerization
- **GitHub Actions** — CI/CD (later)

## Архитектур

**Layered Architecture** (Багийн ажил №6 шийдвэрээр):

```
┌─────────────────────────────────────────┐
│  Presentation Layer (React + Tailwind)  │
├─────────────────────────────────────────┤
│  Controller Layer (@RestController)     │
├─────────────────────────────────────────┤
│  Service Layer (@Service)               │
├─────────────────────────────────────────┤
│  Repository Layer (Spring Data JPA)     │
├─────────────────────────────────────────┤
│  Database (PostgreSQL)                  │
└─────────────────────────────────────────┘
```

**Closed Layer Principle**: дээд давхарга шууд хамгийн доод давхаргад орохгүй; зөвхөн доор зэргэлдээ давхаргад хандана.

## Repo бүтэц

```
braille-converter/
├── backend/                         # Spring Boot
│   ├── src/main/java/mn/braille/
│   │   ├── BrailleApplication.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── validator/
│   │   ├── security/
│   │   ├── config/
│   │   └── exception/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── braille-mapping.json
│   ├── src/test/java/mn/braille/
│   └── pom.xml
├── frontend/                        # React + Vite
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── api/
│   │   ├── hooks/
│   │   └── App.jsx
│   ├── package.json
│   └── vite.config.js
├── docker/
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── docker-compose.yml
├── docs/                            # Хичээлийн баримтууд (Claude уншихгүй)
├── .claude/                         # Claude Code-ийн файлууд (gitignored)
├── .gitignore
├── CLAUDE.md
└── README.md
```

## Брайль стандартын дүрэм (хатуу мөрдөнө)

### Кирилл → Брайль харгалзуулалт (хэсэгчилэн)

| Кирилл | Брайль (Unicode) | Тайлбар |
|---|---|---|
| а | ⠁ | dot 1 |
| б | ⠃ | dot 1,2 |
| в | ⠺ | dot 2,4,5,6 |
| г | ⠛ | dot 1,2,4,5 |
| д | ⠙ | dot 1,4,5 |
| е | ⠑ | dot 1,5 |
| ё | ⠡ | dot 1,6 |
| ж | ⠚ | dot 2,4,5 |
| з | ⠵ | dot 1,3,5,6 |
| и | ⠊ | dot 2,4 |
| ... | ... | ... |

Бүх 35 кирилл үсгийн харгалзуулалт `braille-mapping.json`-д.

### Тусгай дүрмүүд

1. **Capital sign** (⠠): том үсгийн өмнө нэмэгдэнэ. "Монгол" → ⠠⠍⠕⠝⠛⠕⠇
2. **Number sign** (⠼): тооны өмнө нэмэгдэнэ. "2025" → ⠼⠃⠚⠃⠑
3. **Цэг таслал**: . → ⠲, , → ⠂, ? → ⠦, ! → ⠖
4. **Зай** (space): Брайль кодод энгийн зайгаар (\u2800 биш) шилжүүлнэ.

## Архитектурын шинж чанарууд (тэргүүлэх дараалал)

Бие даалт №1 болон Багийн ажил №4-ийн шийдвэрээр **шилдэг 3**:
1. **Simplicity** ★★★★★ — нэг хүний хүчинд тулгуурлах учир
2. **Accessibility** (WCAG 2.1 AA) — хараагүй хэрэглэгч гол
3. **Reliability** — нэг алдаа их нөлөөтэй

Гол NFR-ууд:
- Performance: API latency < 200ms (p95)
- Availability: 99.9% uptime
- Security: HTTPS, rate limit 100/min/IP, JWT auth
- Test coverage: ≥ 80%

## Кодын стандарт

### Java
- **Naming**: `camelCase` метод/хувьсагч, `PascalCase` класс, `UPPER_SNAKE_CASE` константууд
- **Package**: `mn.braille.<layer>` (e.g. `mn.braille.controller`)
- **Lombok**: `@Data`, `@RequiredArgsConstructor`, `@Slf4j` ашиглах
- **Тест**: метод бүрт `should<Action>When<Condition>` нэршил
- **Exception**: Custom `BrailleException` + `@ControllerAdvice` глобал handler

### React/JS
- **Components**: `PascalCase.jsx`
- **Hooks**: `useXxx.js`
- **Files**: kebab-case-ээс зайлсхийх, PascalCase нь нэгэн ижил
- **State management**: React useState/useContext (Redux хэрэггүй MVP-д)

### Git commits
- Conventional Commits: `feat:`, `fix:`, `docs:`, `test:`, `refactor:`, `chore:`
- Mongolian commit message OK, гэхдээ prefix-ийг англиар бичих

## Architectural Principles (хатуу мөрдөнө)

1. **Single Responsibility** — нэг класс нэг үүрэгтэй (Багийн ажил №2 God class анти-pattern-аас зайлсхийх)
2. **Low Coupling, High Cohesion** — модулиудын хооронд сул хамаарал
3. **Connascence минимум** — magic number/string-ээс ENUM руу шилжих (CoM → CoN)
4. **DTO давхарга** — Controller-аас Entity-г шууд буцаахгүй, ConvertRequest/ConvertResponse DTO-аар сольж дамжуулах
5. **Mapping config**: braille-mapping.json — кодоос тусгаарласан тохиргооны файл

## Security Tactics (Багийн ажил №8-аар хэрэгжүүлсэн)

| Тактик | Хэрэгжилт |
|---|---|
| Authenticate Users | JWT (access 15 мин + refresh 7 өдөр) |
| Authorize Users | `@PreAuthorize` + ROLE_USER / ROLE_ADMIN |
| Limit Access | Bucket4j rate limit 100/min/IP |
| Validate Input | Bean Validation + custom `CyrillicValidator` |
| Encrypt Data | HTTPS (TLS 1.3), bcrypt for passwords |
| Maintain Integrity | CSRF, CORS, CSP header |
| Detect Intrusion | Spring Security Filter logging |
| Audit Trail | AOP-аар чухал үйлдлийг log хийх |

## API endpoints (V1.0)

| Method | Path | Auth | Тайлбар |
|---|---|---|---|
| POST | `/api/v1/convert` | Optional | Кирилл → Брайль |
| POST | `/api/v1/validate` | None | Оролт шалгах |
| GET | `/api/v1/mapping` | None | Бүх mapping буцаах |
| GET | `/actuator/health` | None | Health check |
| POST | `/api/v1/auth/login` | None | Login → JWT |
| POST | `/api/v1/auth/refresh` | None | Refresh token |
| POST | `/api/admin/mapping` | ROLE_ADMIN | Mapping шинэчлэх |

## Брайль стандарт өөрчлөгдөх боломжтой

МУ-ын Засгийн газрын хувийн тогтоолоор шинэчлэгдэх боломжтой. Тиймээс **mapping-ийг JSON файлд хадгалж кодоос тусгаарлана**. Кодыг шинэчлэхгүйгээр JSON-г л өөрчилж шинэ хувилбар гаргах боломжтой байх ёстой.

## Тест бичих заавар

- **Unit test**: класс бүрд тус тусдаа `*Test.java`
- **Integration test**: `@SpringBootTest` + `@AutoConfigureMockMvc`
- **Test data**: бодит брайль стандартын жишээ ашиглах
- **Coverage хүрэх**: minimum 80% (NFR-10)

Гол test scenarios:
```
1. Энгийн кирилл текст → зөв Брайль гаралт
2. Том үсэг → capital sign + жижиг үсэг
3. Тоо → number sign + цифр
4. Холимог текст (үсэг + тоо + цэг таслал)
5. Хоосон оролт → BadRequest
6. Кирилл бус тэмдэгт → ValidationException
7. 5000 тэмдэгт оролт → 500ms доор гүйцэтгэх
```

## Хийгдэхгүй зүйлс (V1.0)

- ❌ Брайль → Кирилл reverse conversion (V1.2)
- ❌ Хэрэглэгчийн бүртгэл (бизнес шаардлагаар MVP-д үгүй)
- ❌ Social login
- ❌ Олон хэлний дэмжлэг (зөвхөн монгол хэл)
- ❌ Mobile app (зөвхөн responsive вэб)

## Зөвшөөрөгдөх гадаад dependency

Зөвшөөрсөн: Spring экосистем, Lombok, Bucket4j, JWT (jjwt), Mockito, JUnit 5, Tailwind, shadcn/ui, Axios, react-router-dom.

Зөвшөөрөхгүй: ORM (Hibernate шууд), Spring Data JPA-аар л ажиллана; Redux/Zustand (MVP-д); өөр UI library (Material UI, Ant Design); jQuery.

## Хөгжүүлэлтийн санамж

- Бүх SQL-ийг Spring Data JPA repository-аар л явуулна (raw JDBC бичихгүй)
- Configuration-ийг `application.yml` дотор, secret-ийг env variable-аар
- Хэрэв логик нь 1-2 классаас үргэлжилсэн бол shared service гарга
- Хайлт, transform, validate гэх ажлуудыг утга шинж тус бүрт жижиг класс болгож тусгаарлах
