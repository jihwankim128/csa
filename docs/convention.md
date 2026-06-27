# CS Study Platform Convention Document

## 1. 문서 목적

이 문서는 CS Study Platform의 기술 스택, 프로젝트 구조, 모듈 경계, 코드 작성 규칙, 확장 정책을 정의한다.

이 프로젝트는 MVP 단계에서 과도한 추상화를 피하고, 서비스가 성장하면서 검색, 추천, 벡터 검색, 그래프 시각화, MCP 연동 등을 점진적으로 확장할 수 있도록 설계한다.

## 2. 프로젝트 구조

프로젝트는 하나의 루트 저장소에서 프론트엔드와 백엔드를 함께 관리한다.

```text
root
├── .github
│   ├── ISSUE_TEMPLATE
│   │   ├── task.md
│   │   ├── feature.md
│   │   └── bug.md
│   └── PULL_REQUEST_TEMPLATE.md
├── docs
│   ├── business.md
│   ├── convention.md
│   ├── collaboration.md
│   ├── roadmap.md
│   └── decisions
├── AGENTS.md
├── CLAUDE.md
├── frontend
└── backend
```

### 2.1 문서 역할

```text
docs/business.md
- 서비스 목적
- 사용자 역할
- 글 작성/리뷰 생명주기
- MVP 범위

docs/convention.md
- 기술 스택
- 모듈 구조
- JPA/Lombok/Markdown/검색 확장 정책

docs/collaboration.md
- Issue/PR/branch/commit/review/agent 협업 규칙

docs/roadmap.md
- MVP 이후 단계별 확장 계획
- 아직 필수 문서는 아니며, 필요해질 때 작성한다.

docs/decisions/*.md
- 장기적으로 유지할 아키텍처 의사결정 기록
```

### 2.2 작업 전 참고 문서

작업자는 작업 성격에 따라 다음 문서를 확인한다.

```text
기능/비즈니스 변경:
- docs/business.md

기술 구조/컨벤션 변경:
- docs/convention.md

Issue/PR/협업 방식 변경:
- docs/collaboration.md

아키텍처 의사결정 확인:
- docs/decisions
```

AI Agent는 작업 전 다음 문서를 우선 확인한다.

```text
docs/business.md
docs/convention.md
docs/collaboration.md
AGENTS.md
CLAUDE.md
```

## 3. 기술 스택

### 3.1 Frontend

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

Next.js는 LTS 정책을 기준으로 현재 Active LTS 메이저 버전을 사용한다.

패치 버전은 프로젝트 생성 시점의 최신 안정 버전을 사용한다.

프론트엔드는 다음 역할을 담당한다.

- 공개 게시글 페이지 렌더링
- SEO 친화적인 글 상세 페이지 제공
- Markdown 작성/미리보기
- 이미지 슬라이드 렌더링
- 작성자 작업 화면
- 리뷰어 작업 화면
- 주제 기반 탐색 화면
- 향후 로드맵/그래프 UI

### 3.2 Backend

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

Spring Boot는 4.1.x 라인을 사용한다.

Java는 25를 사용한다.

백엔드는 다음 역할을 담당한다.

- 인증/인가
- 글 작성/수정/조회
- 글 상태 전이
- 리뷰 댓글/대댓글
- 주제 관계 관리
- 이미지 업로드용 URL 발급
- 주제 기반 추천
- 검색 API
- 향후 검색/추천 인덱스 연동

### 3.3 Database

MVP에서는 PostgreSQL을 사용한다.

PostgreSQL은 원본 트랜잭션 저장소로 사용한다.

초기에는 PostgreSQL 전용 기능에 과하게 의존하지 않고, 표준적인 관계형 모델과 JPA 중심으로 구현한다.

PostgreSQL을 선택하는 이유는 다음과 같다.

- 글, 주제, 리뷰, 댓글, 작성자 관계를 관계형 모델로 표현하기 적합하다.
- 향후 full-text search, JSON, recursive query, pgvector 등을 실험하기 쉽다.
- 검색, 추천, 벡터 검색 기능을 하나의 DB에서 시작해볼 수 있다.
- 서비스가 성장하면 검색/추천 책임을 별도 시스템으로 분리하는 경험을 만들 수 있다.

MVP에서는 벡터 검색과 전문 검색 고도화를 핵심 기능으로 보지 않는다.

### 3.4 Infrastructure

```text
Vercel
Railway
Supabase PostgreSQL
Cloudflare R2
```

Vercel은 Next.js 프론트엔드 배포에 사용한다.

Railway는 Spring Boot 백엔드 배포에 사용한다.

Supabase는 PostgreSQL 데이터베이스로 사용한다.

Cloudflare R2는 이미지 파일 저장소로 사용한다.

## 4. Backend 모듈 구조

초기 백엔드는 다음 모듈로 구성한다.

```text
backend
├── api
├── domain
└── infrastructure
```

### 4.1 api

외부 요청과 응답을 담당한다.

포함 대상:

- Spring Boot Application 실행 클래스
- Controller
- Request DTO
- Response DTO
- 인증 사용자 해석
- API 예외 응답 변환
- API 문서화 관련 설정

비포함 대상:

- 핵심 비즈니스 규칙
- 상태 전이 규칙
- 파일 저장소 구현
- 외부 API Client
- 복잡한 영속성 구현

### 4.2 domain

서비스의 핵심 비즈니스 규칙과 유스케이스를 담당한다.

이 프로젝트의 `domain` 모듈은 순수 도메인 레이어만을 의미하지 않는다.

초기 규모에서는 `application`과 `domain`을 별도 모듈로 분리하지 않고, feature 단위로 핵심 규칙과 유스케이스를 함께 관리한다.

포함 대상:

- Entity
- Value Object
- Enum
- Domain Service
- Application Service
- Command
- Result
- Spring Data JPA Repository
- Domain Event
- 상태 전이 규칙

예시 구조:

```text
domain
└── src/main/java/com/csstudy/domain
    ├── article
    │   ├── Article.java
    │   ├── ArticleStatus.java
    │   ├── ArticleService.java
    │   ├── ArticleRepository.java
    │   ├── command
    │   ├── result
    │   └── event
    ├── topic
    ├── review
    ├── member
    ├── media
    └── common
```

### 4.3 infrastructure

외부 기술과의 연결을 담당한다.

포함 대상:

- Cloudflare R2 Client
- 외부 API Client
- 검색 엔진 연동
- 메일/알림 연동
- 복잡한 QueryRepository
- 배치 작업
- 기술 설정
- 향후 OpenSearch, Vector DB, MCP 연동

MVP에서는 Spring Data JPA Repository를 domain 모듈에 둔다.

서비스가 커지면서 조회 요구사항이 복잡해지면 QueryRepository 또는 persistence adapter를 infrastructure로 분리한다.

## 5. 의존 방향

기본 의존 방향은 다음과 같다.

```text
api → domain
infrastructure → domain
api → infrastructure
```

`api → infrastructure` 의존은 파일 업로드, 보안 설정, 외부 기술 조립 등 필요한 경우에만 허용한다.

도메인 규칙은 infrastructure에 의존하지 않는다.

## 6. JPA 정책

MVP에서는 개발 속도와 단순성을 위해 Spring Data JPA Repository를 domain 모듈에 둔다.

도메인 객체에 JPA 매핑 어노테이션을 허용한다.

허용 예시:

- `@Entity`
- `@Embeddable`
- `@Enumerated`
- `@OneToMany`
- `@ManyToOne`
- `@Table`
- `@Column`

domain 모듈에 허용하는 것:

- Entity
- Value Object
- Spring Data JPA Repository
- 도메인 이벤트
- 상태 전이 메서드
- 유스케이스 서비스

domain 모듈에 두지 않는 것:

- Controller
- Request DTO
- Response DTO
- Cloudflare R2 Client
- 외부 API Client
- 검색 엔진 Client
- 보안 설정 클래스

복잡한 조회가 생기면 다음처럼 분리한다.

```text
ArticleRepository
- 저장
- 단건 조회
- 도메인 규칙에 필요한 조회

ArticleQueryRepository
- 목록 조회
- 검색
- 필터
- 리뷰어 화면 조회
- 작성자 화면 조회
- 연관 글 추천 조회
```

## 7. Lombok 정책

Lombok은 허용한다.

Lombok 사용 목적은 반복 코드 제거다.

권장 사용 대상:

- `@Getter`
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- `@RequiredArgsConstructor`
- `@Builder`

주의해서 사용할 대상:

- `@Data`
- `@Setter`
- `@EqualsAndHashCode`
- `@ToString`

도메인 엔티티에는 무분별한 `@Setter` 사용을 금지한다.

상태 변경은 의도를 드러내는 메서드로 처리한다.

권장 예시:

```java
article.requestReview();
article.requestChanges();
article.publish();
```

지양 예시:

```java
article.setStatus(ArticleStatus.PUBLISHED);
```

양방향 연관관계가 있는 Entity에는 `@ToString` 사용을 주의한다.

## 8. 상태 전이 정책

Article 상태 전이는 명시적 메서드로 처리한다.

상태 값은 Controller에서 직접 변경하지 않는다.

예시:

```text
requestReview()
requestChanges()
revise()
publish()
```

상태 전이 실패는 비즈니스 예외로 처리한다.

Article 상태는 다음 흐름을 따른다.

```text
DRAFT
→ REVIEW
→ CHANGES_REQUESTED
→ REVIEW
→ PUBLISHED
```

## 9. Frontend 구조

프론트엔드는 Next.js App Router를 사용한다.

권장 구조:

```text
frontend/src
├── app
│   ├── (public)
│   │   ├── articles
│   │   └── topics
│   ├── (workspace)
│   │   ├── write
│   │   ├── review
│   │   └── my
│   └── layout.tsx
├── components
├── features
│   ├── article
│   ├── topic
│   ├── review
│   └── auth
├── lib
│   ├── api
│   ├── markdown
│   └── storage
└── types
```

### 9.1 public route

검색엔진에 노출될 수 있는 페이지다.

예시:

- 게시글 목록
- 게시글 상세
- 주제 상세

### 9.2 workspace route

로그인이 필요한 작업 페이지다.

예시:

- 글 작성
- 내 글 목록
- 리뷰 목록
- 리뷰 상세

## 10. Markdown 정책

본문은 Markdown 문자열로 저장한다.

미리보기는 프론트엔드에서 렌더링한다.

지원 문법:

- 기본 Markdown
- GitHub Flavored Markdown
- 코드 블록
- 표
- 이미지
- 링크
- 이미지 슬라이드 directive

이미지 슬라이드 문법:

```md
:::slider
![caption 1](/images/1.png)
![caption 2](/images/2.png)
:::
```

사용자 입력 HTML은 기본적으로 허용하지 않는다.

필요한 HTML 또는 확장 문법은 Markdown directive로 구현한다.

Markdown 렌더링 시 sanitize 처리를 적용한다.

## 11. 이미지 업로드 정책

이미지 파일은 Cloudflare R2에 저장한다.

DB에는 이미지 메타데이터만 저장한다.

저장 대상:

- 파일 URL
- 원본 파일명
- 파일 크기
- MIME type
- 업로드 사용자
- 생성일

이미지 업로드는 가능한 경우 presigned URL 방식으로 처리한다.

## 12. buildSrc 정책

백엔드는 Gradle Multi Module로 구성한다.

규모가 작기 때문에 buildSrc를 사용해 버전과 공통 플러그인을 중앙 관리한다.

관리 대상:

- Java version
- Spring Boot version
- Dependency Management version
- Lombok version
- Testcontainers version
- Flyway version
- 공통 Gradle plugin
- 공통 test 설정

## 13. 테스트 정책

MVP에서 우선하는 테스트는 다음과 같다.

- Article 상태 전이 단위 테스트
- Review 댓글/대댓글 규칙 테스트
- Topic 관계 생성 테스트
- Repository 통합 테스트
- API 인수 테스트

Testcontainers를 사용해 PostgreSQL 기반 통합 테스트를 작성한다.

상태 전이, 권한, 리뷰 플로우는 단순 CRUD보다 우선해서 테스트한다.

## 14. 검색/추천 확장 정책

MVP에서는 명시적 주제 관계를 기반으로 추천한다.

MVP 추천 기준:

- 주요 주제
- 선행 주제
- 연관 주제
- 다음 학습 주제

MVP에서는 다음 기술을 도입하지 않는다.

- OpenSearch
- Elasticsearch
- Meilisearch
- pgvector
- 전용 Vector DB
- MCP

### 14.1 PostgreSQL 기반 시작

초기 검색은 PostgreSQL 기반으로 시작한다.

가능한 선택지:

- 제목 검색
- 요약 검색
- 주제 필터
- 상태 필터
- 작성자 필터
- 간단한 본문 검색

초기에는 검색 품질보다 기능 완성과 데이터 모델 안정성을 우선한다.

### 14.2 검색 read model

검색과 추천은 원본 데이터가 아니라 재생성 가능한 read model 또는 projection으로 다룬다.

원본 데이터:

```text
Article
Topic
ReviewComment
Member
Media
```

파생 데이터:

```text
ArticleSearchDocument
ArticleEmbedding
TopicGraphIndex
```

파생 데이터는 원본 데이터에서 다시 만들 수 있어야 한다.

### 14.3 pgvector 도입 기준

pgvector는 다음 필요가 생겼을 때 도입한다.

- 명시적 주제 관계만으로 유사 글 추천이 부족하다.
- 본문 의미 기반 추천을 실험하고 싶다.
- 하나의 PostgreSQL 안에서 벡터 검색을 가볍게 실험하고 싶다.

pgvector를 도입하더라도 원본 데이터와 검색/추천 projection을 구분한다.

예시:

```text
article_embeddings
- article_id
- chunk_id
- embedding_model
- embedding_dimension
- content_hash
- embedding_vector
- created_at
```

### 14.4 OpenSearch / Vector DB 분리 기준

OpenSearch, Elasticsearch, Meilisearch, Qdrant, Milvus, Weaviate 등은 다음 필요가 생겼을 때 검토한다.

- 한국어 검색 품질이 중요해진다.
- 검색 랭킹 튜닝이 필요하다.
- 검색 인덱스와 원본 DB의 책임을 분리하고 싶다.
- 벡터 검색 성능 또는 운영 요구가 커진다.
- pgvector 기반 추천 구현이 서비스 요구에 부족하다.
- 검색/추천 모델을 별도 시스템으로 마이그레이션하는 경험을 만들고 싶다.

이때 원본 PostgreSQL 데이터를 버리는 것이 아니라, 검색/추천 projection을 새 시스템에 재색인한다.

권장 흐름:

```text
PostgreSQL 원본 데이터
→ SearchDocument 생성
→ OpenSearch/Meilisearch 색인

PostgreSQL 원본 데이터
→ Embedding 생성
→ pgvector 또는 Vector DB 색인
```

### 14.5 검색/추천 인터페이스

검색/추천 구현체가 특정 기술에 강하게 묶이지 않도록 인터페이스를 둔다.

예시:

```java
public interface ArticleSearch {
    List<ArticleSearchResult> search(ArticleSearchCondition condition);
}
```

```java
public interface ArticleSemanticSearch {
    List<ArticleSearchResult> findSimilarArticles(Long articleId, int limit);
    List<ArticleSearchResult> searchByMeaning(String query, int limit);
}
```

초기 구현체:

```text
PostgresArticleSearch
```

향후 구현체:

```text
PgVectorArticleSemanticSearch
OpenSearchArticleSearch
QdrantArticleSemanticSearch
```

## 15. 그래프/로드맵 확장 정책

MVP에서는 그래프 UI를 만들지 않는다.

다만 주제 관계 데이터는 향후 그래프/로드맵으로 확장 가능하게 저장한다.

주제 관계 타입:

```text
MAIN
PREREQUISITE
RELATED
NEXT
```

향후 필요 시 다음 화면을 추가한다.

- 주제 상세 페이지
- 주제 간 관계 그래프
- CS 분야별 로드맵
- 선행 주제 기반 커리큘럼
- 작성되지 않은 빈 주제 탐색

## 16. MCP 확장 정책

MCP는 MVP에서 제외한다.

외부 AI 도구가 프로젝트 데이터를 조회하거나 초안 생성을 도와야 할 때 도입한다.

도입 후보 tool:

```text
searchArticles
getArticle
findRelatedTopics
findMissingTopics
suggestNextTopics
createDraft
```

MCP 도입 시 주의할 점:

- 권한 검증
- 공개/비공개 글 구분
- prompt injection 방어
- tool 입력 검증
- 외부 AI 도구가 수행 가능한 작업 제한

## 17. 문서 관리 정책

비즈니스 흐름이 변경되면 다음 문서를 수정한다.

```text
docs/business.md
```

기술 구조, 모듈 경계, 스택, 확장 정책이 변경되면 다음 문서를 수정한다.

```text
docs/convention.md
```

Issue, PR, branch, commit, review, AI Agent 협업 방식이 변경되면 다음 문서를 수정한다.

```text
docs/collaboration.md
```

장기적으로 유지할 중요한 의사결정은 ADR로 남긴다.

```text
docs/decisions
```
