# Smart Canteen – Online Food Ordering (Full Stack Development 22CSG73)

React (Vite) front end + Spring Boot REST back end (H2 locally, PostgreSQL on Render) with AI-assisted search and recommendations.

## Run
**Backend** (Java 17+, Maven):
```
cd backend
mvn spring-boot:run          # http://localhost:8080  (H2 console: /h2-console, JDBC url jdbc:h2:mem:canteen)
```
**Frontend** (Node 18+):
```
cd frontend
npm install
npm run dev                  # http://localhost:5173 (proxies /api and /data to :8080)
```
Demo logins: `student@canteen.com / student123`, `admin@canteen.com / admin123`.

## Deploy a free demo on Render

1. Push this project to a GitHub repository.
2. In Render, choose **New → Blueprint**, connect the repository, and apply the `render.yaml` blueprint.
3. Set `CANTEEN_ADMIN_PASSWORD` to a strong password when prompted. Use `admin@canteen.com` with that password to sign in as the seeded admin.
4. Open the deployed `canteen-app` URL.

The Docker image builds the Vite frontend and serves it from Spring Boot, so the browser and API share one URL. PostgreSQL keeps data across service restarts, and seeded menu items are not duplicated. Render's free PostgreSQL plan is temporary and expires after its free period; this setup is for demos, not durable production data. The app's in-memory login sessions are also cleared when the backend restarts.

## Syllabus mapping
| Unit | Where it is used |
|------|------------------|
| 1 React | Components, `useState`, `useEffect` (live validation in `Login.jsx`, filters in `Menu.jsx`), lifted cart state in `App.jsx`, forms, fetch API |
| 2 Spring Core | Constructor injection everywhere, `@Service`/`@Component`, singleton `PricingService` with `@PostConstruct`/`@PreDestroy`, prototype-scoped `OrderRefGenerator` via `ObjectProvider` |
| 3 MVC / REST | `@RestController`, `@RequestMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`, JSON exchange, `@RestControllerAdvice`, Java config in `AppConfig`, `@Value` |
| 4 Spring Boot | `application.properties`, logging levels, auto-configuration, Spring Data JPA, **Spring Data REST** (`/data/items`, `/data/items/search/findByCategory?category=Snacks`) |
| 5 DevOps / Git | `.github/workflows/ci.yml` (build + test on push/PR). Use feature branches and AI-generated commit messages |
| AI topic | `/api/ai/search` (natural-language search) and `/api/ai/recommend` (co-occurrence recommendations), consumed by React |

## Main endpoints
- `POST /api/auth/register`, `POST /api/auth/login`
- `GET /api/menu?category=&veg=&maxPrice=`; admin: `POST/PUT/DELETE /api/menu`
- `POST /api/orders`, `GET /api/orders/my`; admin: `GET /api/orders`, `PUT /api/orders/{id}/status?status=READY`, `GET /api/orders/stats`
- `GET /api/ai/search?q=spicy veg snacks under 60`, `GET /api/ai/recommend?itemId=5`

## Suggested Git workflow
`main` ← `develop` ← `feature/menu`, `feature/orders`, `feature/ai-search`. Commit often with clear messages.

## Notes
- Auth is an in-memory token map to keep the project simple; for production use Spring Security + JWT and a persistent DB (MySQL/PostgreSQL).
- To use a real LLM, replace the parsing logic in `AiService.search()` with an API call that returns the same filters.
