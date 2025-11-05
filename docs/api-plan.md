# Phase 5 API & Testing Plan

## 1. REST API Scope
### 1.1 Authentication & Security
- Use Spring Security with JWT bearer tokens.
- Endpoints:
  - `POST /api/auth/register` (student self-signup) — open.
  - `POST /api/auth/login` — returns access/refresh tokens.
  - `POST /api/auth/refresh` — refresh token flow.
  - `POST /api/auth/logout` — optional token revoke (in-memory / DB blacklist for MVP).
- Roles:
  - `STUDENT`: access to browse courses, enroll, submit assignments/quizzes.
  - `TEACHER`: manage courses/modules/assignments, grade submissions.
  - `ADMIN`: user management, global settings.
- Password hashing via BCrypt; store refresh tokens in DB table `refresh_tokens` (to be added in migrations if required).

### 1.2 Core Resources
| Resource | Methods | Roles |
| --- | --- | --- |
| Courses `/api/courses` | `GET` list/filter, `GET` detail, `POST` create, `PUT` update, `PATCH` publish/archive, `DELETE` soft-delete | `GET` open, `POST/PUT/PATCH/DELETE` TEACHER/ADMIN |
| Modules `/api/courses/{courseId}/modules` | CRUD | TEACHER |
| Lessons `/api/modules/{moduleId}/lessons` | CRUD | TEACHER |
| Assignments `/api/lessons/{lessonId}/assignments` | `GET` all, `POST` create, `PUT` update, `PATCH` publish, `POST /{id}/submissions` (student), `PATCH /submissions/{submissionId}` (grade) | Mixed (student/teacher) |
| Quizzes `/api/quizzes` | Manage, start attempts, submit answers | Students/Teachers |
| Enrollments `/api/courses/{courseId}/enrollments` | `POST` enroll, `GET` students, `PATCH` status | TEACHER/ADMIN |
| Reviews `/api/courses/{courseId}/reviews` | students can add/get | STUDENT (owner) |

### 1.3 DTO Strategy
- Use `record` DTOs or Lombok `@Value` for requests/responses to decouple from entities.
- MapStruct for mapping between entity/service models and DTOs (add dependency).

### 1.4 Validation
- Bean Validation annotations on DTOs (`@NotBlank`, `@Email`, `@Positive`).
- Custom validators for slug uniqueness and due date validations.

## 2. Security Architecture
- Stateless JWT; `AuthenticationFilter` for login endpoint; `JwtAuthenticationFilter` for validating tokens on each request.
- `SecurityConfig` to configure `HttpSecurity` per role.
- CORS: allow SPA origin (config via properties).
- Add `UserDetailsService` backed by `UserRepository`.

## 3. Integration Tests
- Use `@SpringBootTest` with Testcontainers for PostgreSQL.
- For REST layer use `@SpringBootTest` + `MockMvc` or `@WebMvcTest` (with slices for controllers).
- Provide `TestDataFactory` utilities for creating sample users/courses in tests.
- Use Flyway migrations to prepare schema before tests; optionally load sample data via SQL scripts or builder methods.
- Test scenarios:
  - Auth flow (register/login/refresh).
  - Course listing/publishing + access control (teacher vs student).
  - Enrollment (student enroll, duplicate prevention).
  - Assignment submission and grading workflow.
  - Quiz attempt lifecycle.

## 4. Implementation Steps
1. **Security foundation**: add dependencies (`spring-boot-starter-security`, `jjwt`/`nimbus-jose-jwt`), create security config, JWT utilities, `UserDetailsService`.
2. **DTO + Mapper setup**: add MapStruct dependency, define DTO packages, create mappers.
3. **Controller implementation**: build controllers per resource with request/response models.
4. **Exception handling**: global `@ControllerAdvice` with error envelope.
5. **Testing**: configure test profile, add integration tests for each major flow. Consider using `@Sql` or factory methods.
6. **Documentation**: integrate SpringDoc OpenAPI (optional) for API spec.

## 5. Additional Tasks
- Update `.gitignore` and project dependencies (MapStruct, Security, JWT).
- Adjust Flyway plan to include tables for auth (refresh tokens, revocation if needed).
- Provide README section describing API usage and authentication flow.

## 6. Risks & Mitigations
- **Token storage**: start with stateless access tokens; refresh token persistence optional but recommended.
- **Lazy loading**: ensure controllers use DTOs and service methods that fetch needed relations to avoid `LazyInitializationException`.
- **Test performance**: use shared Testcontainers instance with `@DynamicPropertySource` for faster test runs.

## 7. Deliverables
- Security config + auth endpoints.
- REST controllers + DTOs/mappers.
- Integration tests covering main flows.
- Updated documentation (README/API spec).
