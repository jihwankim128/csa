# AGENTS.md

## Project Overview

This repository contains a CS study platform.

The service allows study members to write, review, and connect CS learning articles.

Core features:

- Markdown-based article writing
- Markdown preview
- Image slider blocks
- Article review lifecycle
- Review comments and replies
- Topic relationship management
- Related topic recommendations
- Author workspace
- Reviewer workspace

The project is managed as a monorepo.

```text
root
├── .github
│   ├── ISSUE_TEMPLATE
│   └── PULL_REQUEST_TEMPLATE.md
├── docs
├── frontend
└── backend
```

## Required Reading

Before making changes, read these documents:

```text
docs/business.md
docs/convention.md
docs/collaboration.md
```

When architectural decisions are relevant, also check:

```text
docs/decisions
```

Document roles:

```text
docs/business.md
- Product purpose, user roles, article lifecycle, MVP scope

docs/convention.md
- Tech stack, module structure, JPA/Lombok/Markdown/search extension policy

docs/collaboration.md
- Issue, PR, branch, commit, review, and AI Agent collaboration rules

docs/decisions/*.md
- Long-lived architecture decisions
```

## Tech Stack

### Frontend

```text
Next.js 16.x Active LTS
React
TypeScript
Tailwind CSS
CodeMirror
react-markdown
remark-gfm
remark-directive
rehype-sanitize
```

### Backend

```text
Spring Boot 4.1.x LTS
Java 25
Gradle Multi Module
buildSrc
Spring Security
Spring Data JPA
Flyway
Testcontainers
Lombok
```

### Infrastructure

```text
Vercel
Railway
Supabase PostgreSQL
Cloudflare R2
```

## Agent Role

Codex should act primarily as an implementation agent.

Codex is responsible for:

- Creating project structure
- Implementing requested features
- Writing tests
- Refactoring code
- Keeping changes minimal and reviewable
- Updating nearby documentation when implementation changes behavior or conventions

Codex should not make large product or architecture decisions without explicit instruction.

## Work Rules

Prefer simple implementation over premature abstraction.

Do not introduce the following unless explicitly requested:

- Hexagonal architecture boilerplate
- Persistence adapter layers
- Separate application/domain modules
- Elasticsearch
- OpenSearch
- Meilisearch
- pgvector
- Dedicated vector database
- MCP
- Graph UI
- Real-time collaboration

Follow collaboration rules in:

```text
docs/collaboration.md
```

In particular:

- Prefer Issue-first work.
- Keep PRs small.
- Do not push directly to main.
- Human approval is required for merge.
- Use GitHub Issue/PR comments as the shared work log when collaborating with agents.

## Backend Module Rules

The backend uses this module structure:

```text
backend
├── api
├── domain
└── infrastructure
```

### api module

Use for:

- Spring Boot application entry point
- Controller
- Request DTO
- Response DTO
- API exception handling
- Authentication user resolution

Do not place business rules in controllers.

### domain module

Use for core business rules and use cases.

The domain module is not a pure domain-only module in MVP.

It may contain:

- Entity
- Value Object
- Enum
- Domain Service
- Application Service
- Command
- Result
- Spring Data JPA Repository
- Domain Event
- State transition logic

Spring Data JPA Repository is allowed in the domain module during MVP.

Do not create persistence adapters unless explicitly requested.

### infrastructure module

Use for external technology concerns.

Examples:

- Cloudflare R2 client
- External API client
- Search engine integration
- Complex query repository
- Batch job
- Future OpenSearch/vector DB/MCP integration

## Backend Style

Use Java 25.

Use Spring Boot 4.1.x.

Use Lombok where it improves readability.

Recommended Lombok annotations:

- `@Getter`
- `@RequiredArgsConstructor`
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- `@Builder`

Avoid or use carefully:

- `@Data`
- `@Setter`
- `@EqualsAndHashCode`
- `@ToString`

Do not use uncontrolled setters on domain entities.

Prefer intention-revealing methods.

Good:

```java
article.requestReview();
article.requestChanges();
article.publish();
```

Avoid:

```java
article.setStatus(ArticleStatus.PUBLISHED);
```

## Article Lifecycle

Article status follows this lifecycle:

```text
DRAFT
→ REVIEW
→ CHANGES_REQUESTED
→ REVIEW
→ PUBLISHED
```

Status transitions must be explicit.

Controllers must not change status values directly.

## Topic Relationship Types

Supported relationship types:

```text
MAIN
PREREQUISITE
RELATED
NEXT
```

Topic relationships are the MVP basis for article recommendation.

Do not implement vector-based recommendation in MVP unless explicitly requested.

## Frontend Rules

Use Next.js App Router.

Public pages should be SEO-friendly.

Workspace pages can be client-heavy.

Public route examples:

- Article list
- Article detail
- Topic detail

Workspace route examples:

- Write article
- My articles
- Review list
- Review detail

Markdown preview should be implemented on the frontend.

Image slider should be represented using Markdown directive syntax.

Example:

```md
:::slider
![Step 1](/images/1.png)
![Step 2](/images/2.png)
:::
```

Do not allow arbitrary user HTML by default.

Use sanitize handling for Markdown rendering.

## Issue and PR Rules

Use templates under:

```text
.github/ISSUE_TEMPLATE
.github/PULL_REQUEST_TEMPLATE.md
```

Issue title format:

```text
[type] summary
```

Branch format:

```text
<type>/<issue-number>-<short-description>
```

PR title format:

```text
[type] summary
```

Detailed rules are defined in:

```text
docs/collaboration.md
```

## Testing Rules

When changing backend business logic, add or update tests.

Prioritize tests for:

- Article status transitions
- Review comment/reply rules
- Topic relationship rules
- Repository behavior
- API behavior

Use Testcontainers for PostgreSQL integration tests where useful.

## Search and Recommendation Rules

MVP recommendation is based on explicit topic relationships.

Do not introduce pgvector, OpenSearch, Elasticsearch, Meilisearch, or a dedicated vector database in MVP.

Search and recommendation data should be treated as a rebuildable read model/projection when introduced.

Keep the original source of truth in PostgreSQL.

## Documentation Rules

When a business flow changes, update:

```text
docs/business.md
```

When technical structure, module boundaries, stack choices, or extension policies change, update:

```text
docs/convention.md
```

When Issue, PR, branch, commit, review, or AI Agent collaboration rules change, update:

```text
docs/collaboration.md
```

When a long-lived architecture decision is made, add or update an ADR under:

```text
docs/decisions
```

## Output Style

Keep changes small.

Prefer readable code over clever code.

Avoid broad refactoring unless requested.

Explain meaningful trade-offs briefly in PR summaries or commit notes.
