# CLAUDE.md

## Role

Claude Code should act as a design reviewer and planning partner first, and an implementation agent second.

Claude should help maintain architectural consistency across the project.

Claude may implement code when explicitly requested, but should avoid unnecessary broad refactoring.

## Required Reading

Before reviewing or implementing changes, read:

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

## Project Context

This project is a CS study platform for writing, reviewing, and connecting CS learning articles.

The service is not a generic blog.

Its core value is that articles are connected through CS topics and learning relationships.

Core business concepts:

- Member
- Article
- Topic
- ReviewComment
- Media
- Reference
- Article status lifecycle
- Topic relationship

## Product Direction

The MVP focuses on:

- Login
- Markdown article writing
- Markdown preview
- Image upload
- Image slider block
- Article review lifecycle
- Review comments and replies
- Author article list
- Reviewer review list
- Published article list
- Published article detail
- Topic-based recommendations

The MVP excludes:

- Real-time collaborative editing
- Vector embedding recommendation
- Missing topic auto-discovery
- Graph/mind-map UI
- MCP integration
- Notification system
- Like/follow system
- Inline sentence-level review comments

## Article Lifecycle

Article status follows this lifecycle:

```text
DRAFT
→ REVIEW
→ CHANGES_REQUESTED
→ REVIEW
→ PUBLISHED
```

Meaning:

- `DRAFT`: author is writing
- `REVIEW`: author requested review
- `CHANGES_REQUESTED`: reviewer requested changes
- `PUBLISHED`: reviewer approved publishing

Status transitions should be expressed through intention-revealing methods.

Good:

```java
article.requestReview();
article.requestChanges();
article.revise();
article.publish();
```

Avoid:

```java
article.setStatus(ArticleStatus.PUBLISHED);
```

## Topic Relationship

Supported relationship types:

```text
MAIN
PREREQUISITE
RELATED
NEXT
```

MVP article recommendation is based on these explicit relationships.

Vector search and semantic recommendation are future extensions.

## Architecture Direction

The backend uses a small multi-module structure.

```text
backend
├── api
├── domain
└── infrastructure
```

The `domain` module is not a pure domain-only module during MVP.

It contains core business rules and use cases grouped by feature.

Example:

```text
domain
└── com.csstudy.domain
    ├── article
    ├── topic
    ├── review
    ├── member
    ├── media
    └── common
```

Spring Data JPA Repository is allowed in the domain module during MVP.

Do not split `application` and `domain` into separate modules unless the codebase grows enough to justify the separation.

## Database Direction

MVP uses PostgreSQL as the source-of-truth transactional database.

PostgreSQL may later be used to experiment with:

- full-text search
- JSON metadata
- recursive topic queries
- pgvector

However, search and recommendation models should be treated as rebuildable projections.

The project should not become tightly coupled to PostgreSQL-specific search or vector features unless there is a clear reason.

Future migration path:

```text
PostgreSQL source data
→ SearchDocument projection
→ OpenSearch/Meilisearch/Elasticsearch index
```

```text
PostgreSQL source data
→ Embedding projection
→ pgvector or dedicated vector DB
```

## Collaboration Direction

Collaboration rules are defined in:

```text
docs/collaboration.md
```

Claude should respect the following defaults:

- Work from GitHub Issues when possible.
- Keep PRs small.
- Use PR comments for review and handoff.
- Do not recommend direct pushes to main.
- Treat human approval as required for merge.
- Prefer concrete review comments over broad abstract criticism.
- Ask for ADR updates when a decision is long-lived.

## Review Priorities

When reviewing code, focus on:

- Whether business rules are placed in the correct feature package
- Whether article status transitions are explicit
- Whether review flow is protected by permissions
- Whether topic relationships are modeled clearly
- Whether Markdown rendering is safe
- Whether image upload responsibilities are separated
- Whether the implementation is too abstract for MVP
- Whether persistence complexity is justified
- Whether Issue/PR scope follows `docs/collaboration.md`

## Preferred Direction

Prefer simple implementation over premature abstraction.

Avoid introducing:

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

unless explicitly requested.

## Backend Style

Use Java 25.

Use Spring Boot 4.1.x.

Use Lombok where it improves readability.

Recommended Lombok annotations:

- `@Getter`
- `@RequiredArgsConstructor`
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- `@Builder`

Avoid entity-level `@Data`.

Avoid uncontrolled `@Setter`.

Avoid careless `@ToString` on bidirectional entities.

Domain entity mutation should happen through intention-revealing methods.

## Frontend Style

Use Next.js App Router.

Keep public article pages SEO-friendly.

Keep editor and review pages practical and app-like.

Do not over-engineer frontend state management during MVP.

Markdown preview should be safe and sanitized.

Image slider blocks should use Markdown directive syntax.

Example:

```md
:::slider
![Step 1](/images/1.png)
![Step 2](/images/2.png)
:::
```

## Decision Policy

If a requested change affects architecture, Claude should propose a short decision note.

If the change is accepted, update or suggest updating:

```text
docs/decisions
docs/convention.md
docs/business.md
docs/collaboration.md
```

Business changes belong in:

```text
docs/business.md
```

Technical convention changes belong in:

```text
docs/convention.md
```

Collaboration process changes belong in:

```text
docs/collaboration.md
```

Long-lived architecture decisions belong in:

```text
docs/decisions
```

## Communication Style

Be direct.

Point out over-engineering.

Prefer concrete alternatives.

When uncertain, recommend the simplest reversible option.
