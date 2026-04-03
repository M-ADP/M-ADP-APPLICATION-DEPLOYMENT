# Restore Legacy App Endpoints Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore the legacy app list and summary endpoints from `origin/dev-0.1.30` on top of the current branch.

**Architecture:** Add back the removed controller routes and the supporting request/response DTOs, then reintroduce the two read-only service methods without disturbing the newer project resource limit logic added after `dev-0.1.30`. Locking and resource-limit validation stay as they are on the current branch.

**Tech Stack:** Spring Boot, Spring MVC Test, JUnit 5, Mockito

---

## Chunk 1: Route Regression Tests

### Task 1: Add failing controller tests for the legacy routes

**Files:**
- Create: `src/test/java/madp/appdeployment/domain/presentation/controller/AppDeploymentControllerCompatibilityTest.java`
- Modify: `src/main/java/madp/appdeployment/domain/presentation/controller/AppDeploymentController.java`

- [ ] **Step 1: Write the failing tests**

Add controller tests that expect:
- `GET /apps/projects/{project_id}/apps` returns `200 OK` with the legacy response message
- `POST /apps/summary` returns `200 OK` with the legacy response message

- [ ] **Step 2: Run the targeted test to verify it fails**

Run: `./gradlew test --tests madp.appdeployment.domain.presentation.controller.AppDeploymentControllerCompatibilityTest`
Expected: FAIL with `404 Not Found` because the legacy routes do not exist on the current branch.

## Chunk 2: Restore Legacy Read APIs

### Task 2: Reintroduce controller, DTO, and service support

**Files:**
- Modify: `src/main/java/madp/appdeployment/domain/presentation/controller/AppDeploymentController.java`
- Modify: `src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/request/GetAppDeploymentSummaryRequestDto.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentListResponseDto.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentSummaryResponseDto.java`

- [ ] **Step 3: Restore the minimal implementation**

Bring back:
- `GET /apps/projects/{project_id}/apps`
- `POST /apps/summary`
- the DTOs and read-only service methods those routes depend on

- [ ] **Step 4: Run the targeted test to verify it passes**

Run: `./gradlew test --tests madp.appdeployment.domain.presentation.controller.AppDeploymentControllerCompatibilityTest`
Expected: PASS

## Chunk 3: Verification And Diff Review

### Task 3: Verify and summarize branch differences

**Files:**
- No additional source files expected

- [ ] **Step 5: Run focused verification**

Run: `./gradlew test`
Expected: PASS

- [ ] **Step 6: Summarize remaining differences between `dev-0.1.30` and `dev-0.1.34`**

Capture the non-endpoint changes so the user can decide whether more backports are needed.
