# Бэкенд образовательной платформы

Spring Boot 3.4‑приложение для учебной платформы. Сервис отвечает за пользователей, курсы, модули и уроки, домашние задания, тесты, аутентификацию по JWT, а также предоставляет REST API поверх PostgreSQL. Управление схемой выполняется через Flyway.

## Требования

- Java 17+ (рекомендуется JDK 17)
- Gradle (используется обёртка `./gradlew`)
- Docker (для интеграционных тестов на Testcontainers)
- PostgreSQL 14+ или совместимый контейнер

## Структура проекта

- `src/main/java` — прикладной код (сущности, сервисы, контроллеры, безопасность)
- `src/main/resources` — конфигурация Spring Boot и SQL‑миграции (`db/migration`)
- `src/test/java` — интеграционные тесты с Testcontainers и целевые unit‑тесты
- `docs/` — дополнительные заметки по предметной области, миграциям и API (папку можно удалить, когда README будет полностью покрывать нужные инструкции)

## Конфигурация

Конфигурация разбита по профилям в файлах `application*.yml`:

- `application.yml` — базовые настройки (профиль по умолчанию, JPA/Flyway, параметры JWT)
- `application-dev.yml` — подключение к dev‑базе, настройки пула Hikari, расширенный лог SQL
- `application-test.yml` — профиль для тестов c Testcontainers и разрешённым `flyway.clean`

### Переменные окружения

| Переменная | Назначение | Значение по умолчанию |
| --- | --- | --- |
| `DEV_DB_URL` | JDBC‑URL локальной базы | `jdbc:postgresql://localhost:5432/edu_backend_dev` |
| `DEV_DB_USERNAME` | Пользователь базы | `edu_dev` |
| `DEV_DB_PASSWORD` | Пароль пользователя | `edu_dev` |
| `JWT_SECRET` | Секрет для подписи JWT (HS256) | `your-secret-key-with-at-least-256-bits-for-HS256-algorithm` |

## Миграции базы данных

Все SQL‑миграции находятся в `src/main/resources/db/migration`:

1. `V1__baseline.sql` — инициализация метаданных Flyway
2. `V2__create_core_schema.sql` — полная схема (пользователи, курсы, уроки, задания, тесты и т.д.)
3. `V3__seed_demo_data.sql` — демо‑данные для разработки и тестов

При запуске приложения Flyway автоматически применяет миграции. Пользователь БД должен иметь права на создание/изменение схемы.

### Ручной запуск миграций

```bash
./gradlew flywayMigrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/edu_backend_dev \
  -Dflyway.user=edu_dev \
  -Dflyway.password=edu_dev
```

## Запуск приложения

1. Убедитесь, что PostgreSQL запущен и доступен с нужными реквизитами.
2. При необходимости переопределите переменные окружения (см. таблицу выше).
3. Запустите Spring Boot:

```bash
./gradlew bootRun
```

По умолчанию API доступен на `http://localhost:8080`. Для большинства операций требуется JWT‑авторизация. Получить токены можно через эндпоинты `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh`.

## Тестирование

Используются JUnit 5 и Testcontainers (PostgreSQL) для интеграционных сценариев, а также отдельные unit‑тесты.

```bash
JAVA_HOME=$(usr/libexec/java_home -v 17) ./gradlew test
```

> Важно: Docker должен быть запущен, иначе Testcontainers не стартует контейнер базы. Профиль `test` автоматически накатывает демо‑данные через Flyway.

### Основные тестовые наборы

- `EnrollmentControllerIntegrationTest` — проверка эндпоинтов управления записями на курс и правил доступа
- `QuizServiceIntegrationTest` — сценарии старта/прохождения тестов и начисления баллов
- `JwtTokenProviderTest` — генерация и валидация JWT без запуска Spring‑контекста

## Обзор API

Основные REST‑эндпоинты (детали — в контроллерах `src/main/java/.../web/controller`):

- `/api/auth/**` — аутентификация, регистрация и выдача токенов
- `/api/courses/**` — каталог и управление курсами
- `/api/modules/**`, `/api/lessons/**` — модульная структура курса
- `/api/assignments/**`, `/api/submissions/**` — работа с заданиями и решениями
- `/api/quizzes/**` — тесты, вопросы и попытки
- `/api/enrollments/**` — оформление, актуализация и отмена записей на курс

Для просмотра курсов/модулей/уроков достаточно GET‑запросов без авторизации; остальные операции требуют ролей `STUDENT`, `TEACHER` или `ADMIN`.

## Docker и деплой

- Dockerfile и docker-compose будут добавлены в рамках задачи `phase6-devops`
- Планируется интеграция CI/CD после завершения DevOps‑этапа

## Полезные команды

```bash
# Компиляция без запуска тестов
./gradlew compileJava

# Запуск конкретного тестового класса
./gradlew test --tests "com.masters.edu.backend.service.quiz.QuizServiceIntegrationTest"

# Сборка исполняемого JAR
./gradlew bootJar
```

## Устранение неполадок

- **Ошибки валидации Flyway** — убедитесь, что БД пуста или соответствует текущему baseline
- **Проблемы старта Testcontainers** — проверьте, что Docker запущен и доступ в интернет для загрузки образов есть
- **Сбой проверки JWT** — секрет `JWT_SECRET` должен быть не короче 32 байт (HS256)


