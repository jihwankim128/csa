# Collaboration Convention

## 1. 문서 목적

이 문서는 CS Study Platform의 이슈, 브랜치, 커밋, PR, 리뷰, AI Agent 협업 규칙을 정의한다.

목표는 다음과 같다.

- 작업 단위를 작게 유지한다.
- GitHub Issue와 PR을 작업 기록으로 사용한다.
- AI Agent가 이해하기 쉬운 입력을 제공한다.
- 사람이 최종 의사결정권을 가진다.
- 구현, 리뷰, 문서화 흐름을 단순하게 유지한다.

## 2. 기본 원칙

### 2.1 Issue First

모든 의미 있는 작업은 Issue에서 시작한다.

예외적으로 오타 수정, 주석 수정, 사소한 문서 수정은 Issue 없이 PR을 만들 수 있다.

### 2.2 Small PR

PR은 작게 만든다.

권장 기준:

- 하나의 PR은 하나의 Issue만 해결한다.
- PR은 가능하면 300~500 lines changed 이하로 유지한다.
- 기능 구현과 대규모 리팩토링을 같은 PR에 섞지 않는다.
- API 변경과 UI 변경이 크면 분리한다.

### 2.3 Human Merge

AI Agent는 브랜치 생성, 코드 작성, 테스트, PR 생성, 리뷰 코멘트 작성까지 수행할 수 있다.

main 브랜치 merge는 사람이 수행한다.

### 2.4 Docs as Source of Context

AI Agent는 작업 전 다음 문서를 우선 확인한다.

```text
docs/business.md
docs/convention.md
docs/collaboration.md
AGENTS.md
CLAUDE.md
```

아키텍처 결정이 필요한 경우 다음 문서를 확인한다.

```text
docs/decisions
```

## 3. Issue Convention

Issue는 작업 단위를 정의한다.

Issue는 AI Agent가 바로 구현할 수 있을 정도로 구체적이어야 한다.

### 3.1 Issue Title

형식:

```text
[type] 작업 요약
```

예시:

```text
[setup] 백엔드 멀티모듈 초기 구조를 설정한다
[backend] Article 상태 전이 모델을 구현한다
[frontend] Markdown 작성/미리보기 화면을 구현한다
[docs] 협업 컨벤션 문서를 추가한다
```

### 3.2 Issue Type

사용 가능한 type:

```text
setup
backend
frontend
docs
test
refactor
bug
infra
```

### 3.3 Issue Body

Issue에는 다음 내용을 포함한다.

- 배경
- 작업 내용
- 완료 조건
- 참고 문서
- 제외할 범위
- Agent 지시사항

### 3.4 Good Issue Criteria

좋은 Issue의 기준:

- 작업 범위가 작다.
- 완료 조건이 명확하다.
- 수정해야 할 파일 또는 영역이 힌트로 주어진다.
- 하지 말아야 할 범위가 명시되어 있다.
- 테스트 기준이 있다.
- AI Agent가 임의로 큰 결정을 하지 않아도 된다.

## 4. Branch Convention

브랜치 이름 형식:

```text
<type>/<issue-number>-<short-description>
```

예시:

```text
setup/1-backend-multimodule
backend/4-article-status
frontend/7-markdown-editor
docs/12-collaboration-convention
```

허용 type:

```text
setup
backend
frontend
docs
test
refactor
bug
infra
```

## 5. Commit Convention

커밋 메시지는 다음 형식을 사용한다.

```text
type: summary
```

예시:

```text
setup: initialize backend multi-module project
feat: add article status transition
test: add article status transition tests
docs: add collaboration convention
fix: prevent invalid article publish transition
refactor: simplify review comment creation
```

허용 type:

```text
feat
fix
docs
style
refactor
test
chore
setup
infra
```

커밋은 가능한 의미 있는 단위로 나눈다.

단, AI Agent가 만든 지나치게 잘게 쪼갠 커밋은 PR 전에 squash해도 된다.

## 6. Pull Request Convention

PR 제목 형식:

```text
[type] 작업 요약
```

예시:

```text
[backend] Article 상태 전이 모델을 구현한다
[frontend] Markdown 작성 화면을 구현한다
[docs] 협업 컨벤션을 추가한다
```

PR 본문에는 다음 내용을 포함한다.

- 관련 Issue
- 변경 내용
- 테스트 결과
- 리뷰 포인트
- 제외한 범위
- 스크린샷 또는 API 예시

## 7. Review Convention

리뷰어는 다음 관점으로 리뷰한다.

### 7.1 Backend Review

- 상태 전이가 명시적인가?
- Controller에 비즈니스 규칙이 들어가지 않았는가?
- domain feature 패키지에 규칙이 응집되어 있는가?
- JPA 연관관계가 과도하지 않은가?
- 테스트가 상태 전이와 권한을 검증하는가?
- infrastructure 관심사가 domain에 들어오지 않았는가?

### 7.2 Frontend Review

- public 페이지가 SEO를 고려하는가?
- workspace 페이지가 불필요하게 복잡하지 않은가?
- Markdown 미리보기가 안전하게 렌더링되는가?
- 이미지 슬라이드 directive가 일관되게 처리되는가?
- 컴포넌트 분리가 과도하지 않은가?

### 7.3 Docs Review

- business 변경은 `docs/business.md`에 반영되었는가?
- 기술 컨벤션 변경은 `docs/convention.md`에 반영되었는가?
- 장기 의사결정은 ADR로 남겼는가?
- 문서와 실제 구현이 어긋나지 않는가?

## 8. AI Agent Workflow

### 8.1 Minimal Local Workflow

처음에는 GitHub Actions 자동화 없이 로컬에서 진행한다.

권장 흐름:

```text
1. 사람이 AI에게 Issue 초안 생성을 요청한다.
2. 사람이 Issue 내용을 확인한다.
3. AI 또는 사람이 gh issue create로 Issue를 생성한다.
4. 사람이 AI에게 특정 Issue 구현을 요청한다.
5. AI가 브랜치 생성, 구현, 테스트를 수행한다.
6. AI 또는 사람이 PR을 생성한다.
7. Claude 또는 사람이 리뷰한다.
8. 사람이 최종 merge한다.
```

### 8.2 Agent Roles

Codex:

- 구현
- 테스트 작성
- 작은 리팩토링
- 문서의 직접적인 수정

Claude:

- 설계 검토
- PR 리뷰
- 과도한 추상화 지적
- 문서/ADR 필요 여부 판단
- 구현 방향 제안

Human:

- 범위 확정
- 요구사항 결정
- 최종 승인
- merge

### 8.3 Agent Safety Rules

AI Agent는 다음을 수행하지 않는다.

- main 직접 push
- 임의의 대규모 리팩토링
- 승인 없는 기술 스택 변경
- 승인 없는 의존성 대량 추가
- production secret 접근
- 문서와 다른 구조로 구현

## 9. Label Convention

초기 라벨은 최소화한다.

### Type Labels

```text
type:setup
type:backend
type:frontend
type:docs
type:test
type:refactor
type:bug
type:infra
```

### Status Labels

```text
status:ready
status:in-progress
status:blocked
status:review-needed
```

### Agent Labels

```text
agent:codex
agent:claude
agent:human
```

### Priority Labels

```text
priority:high
priority:medium
priority:low
```

## 10. Definition of Done

작업 완료 기준:

- Issue의 Acceptance Criteria를 만족한다.
- 필요한 테스트를 작성하거나 갱신했다.
- 로컬에서 관련 테스트를 실행했다.
- 문서 변경이 필요한 경우 문서를 수정했다.
- PR 본문에 변경 내용과 테스트 결과를 기록했다.
- 리뷰 코멘트가 해결되었다.
- 사람이 최종 확인했다.
