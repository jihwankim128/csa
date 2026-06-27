# 0001. Initial Architecture

## Status

Accepted

## Date

2026-06-26

## Context

The project is a CS study platform for writing, reviewing, and connecting CS learning articles.

The MVP needs the following capabilities:

- Login
- Markdown-based article writing
- Markdown preview
- Image upload
- Image slider blocks
- Article review lifecycle
- Review comments and replies
- Author workspace
- Reviewer workspace
- Published article pages
- Topic-based article recommendations

The project should also leave room for future extension:

- Topic graph
- Roadmap view
- Better search
- Semantic recommendation
- pgvector experiment
- OpenSearch or dedicated vector DB migration
- MCP integration

The project owner is strong with Spring Boot and wants to keep the backend practical, explicit, and not over-abstracted during MVP.

## Decision

Use a monorepo structure.

```text
root
├── docs
├── frontend
└── backend
```

Use Next.js for the frontend.

```text
Next.js 16.x Active LTS
React
TypeScript
Tailwind CSS
```

Use Spring Boot for the backend.

```text
Spring Boot 4.1.x LTS
Java 25
Gradle Multi Module
buildSrc
Spring Data JPA
Lombok
```

Use PostgreSQL as the MVP source-of-truth database.

Use this infrastructure baseline.

```text
Vercel: frontend
Railway: backend
Supabase PostgreSQL: database
Cloudflare R2: image storage
```

Use this backend module structure.

```text
backend
├── api
├── domain
└── infrastructure
```

The `domain` module is defined as the core business module during MVP.

It is not a pure domain-only module.

It may include:

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

Do not split `application` and `domain` into separate modules during MVP.

Spring Data JPA Repository may stay in the `domain` module during MVP.

External technology concerns should stay in `infrastructure`.

Examples:

- Cloudflare R2 client
- External API client
- Search engine client
- Complex QueryRepository
- Batch job
- Future vector DB client
- Future MCP integration

## Article Lifecycle

Article status follows this lifecycle.

```text
DRAFT
→ REVIEW
→ CHANGES_REQUESTED
→ REVIEW
→ PUBLISHED
```

Status transitions should be expressed through methods, not direct field mutation.

Examples:

```java
article.requestReview();
article.requestChanges();
article.revise();
article.publish();
```

## Topic Relationship

The MVP supports these topic relationship types.

```text
MAIN
PREREQUISITE
RELATED
NEXT
```

MVP recommendation is based on explicit topic relationships, not vector embeddings.

## Database Decision

PostgreSQL is selected for MVP.

Reasons:

- It is suitable for article, topic, review, comment, member, and media relationships.
- It can support future experiments with full-text search, JSON metadata, recursive topic queries, and pgvector.
- It allows the project to start simple and later separate search/recommendation responsibilities.
- It provides a natural path to experiment with PostgreSQL-based search first, then move to OpenSearch or a dedicated vector DB later.

PostgreSQL-specific features should not be overused in MVP.

Search and recommendation data should be treated as rebuildable read models or projections.

Source of truth:

```text
Article
Topic
ReviewComment
Member
Media
Reference
```

Possible future projections:

```text
ArticleSearchDocument
ArticleEmbedding
TopicGraphIndex
```

## Search and Recommendation Direction

MVP:

```text
PostgreSQL source data
Explicit topic relationship recommendation
Basic filtering/search
```

Future:

```text
PostgreSQL source data
→ ArticleSearchDocument
→ OpenSearch/Meilisearch/Elasticsearch
```

```text
PostgreSQL source data
→ ArticleEmbedding
→ pgvector or dedicated vector DB
```

This means later migration from pgvector or PostgreSQL search to OpenSearch/vector DB should be treated as reindexing projections, not replacing the source database.

## Consequences

### Positive

The architecture stays simple during MVP.

The backend can be implemented quickly using Spring Boot and Spring Data JPA.

Feature packages remain cohesive.

The project avoids premature hexagonal architecture boilerplate.

The project keeps a clear future path for search, vector search, graph UI, and MCP.

The PostgreSQL source-of-truth model can remain stable even if search or vector systems are replaced later.

### Negative

The `domain` module is not a strict pure-domain module.

Spring Data JPA Repository in `domain` may need refactoring if persistence complexity grows.

Some future migration work may be required if PostgreSQL-specific search/vector features are deeply used.

The project must be careful not to mix external infrastructure concerns into `domain`.

## Alternatives Considered

### MySQL

MySQL would also work for MVP.

It is strong for transactional workloads, indexes, locks, and common backend interview topics.

However, this project may later experiment with full-text search, topic graph queries, and vector search. PostgreSQL gives a smoother path for those experiments while still supporting standard relational modeling.

### Separate application/domain modules

This was rejected for MVP.

The project is still small, and splitting application/domain too early would add module and package overhead.

The decision can be revisited when feature complexity grows.

### Hexagonal architecture from the start

This was rejected for MVP.

It would introduce ports, adapters, and mapping layers before the project has enough complexity to justify them.

### OpenSearch or vector DB from the start

This was rejected for MVP.

The initial recommendation model is based on explicit topic relationships.

Search and semantic recommendation can be added later as rebuildable projections.

## Follow-up Decisions

Future ADRs may cover:

- Search engine introduction
- pgvector introduction
- Vector DB migration
- Topic graph/roadmap UI
- MCP integration
- Splitting domain/application modules
- Moving Spring Data JPA repositories out of domain
