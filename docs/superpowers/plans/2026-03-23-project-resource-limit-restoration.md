# Project Resource Limit Restoration Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore project-level CPU, memory, and disk limit validation for app creation and resource updates, protected by a per-project Redis lock.

**Architecture:** `AppDeploymentService` regains responsibility for enforcing project aggregate resource limits before writes. It uses `ProjectClient` for limit lookup, `AppDeploymentRepository` for aggregate usage, and `ProjectResourceLockManager` for project-scoped serialization around validation and persistence.

**Tech Stack:** Spring Boot, Spring Data JPA, OpenFeign, Redis, JUnit 5, Mockito

---

## Chunk 1: Failing Service Tests

### Task 1: Add create/update limit validation tests

**Files:**
- Create: `src/test/java/madp/appdeployment/domain/application/service/AppDeploymentServiceTest.java`
- Modify: `build.gradle` only if extra test dependency is required
- Reference: `src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java`

- [ ] **Step 1: Write the failing test**

Add tests covering:
- create succeeds when proposed totals are within the project limit
- create fails with `InvalidResourceInfoException` when CPU limit is exceeded
- update succeeds when replacing the app's own resources still fits inside the project limit
- update fails with `InvalidResourceInfoException` when memory or disk limit is exceeded

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests madp.appdeployment.domain.application.service.AppDeploymentServiceTest`
Expected: FAIL because repository sum queries, project limit lookup, and lock manager behavior are not available in the current branch.

## Chunk 2: Restore Validation Plumbing

### Task 2: Restore project limit API and repository aggregation

**Files:**
- Modify: `src/main/java/madp/appdeployment/domain/infrastructure/client/ProjectClient.java`
- Modify: `src/main/java/madp/appdeployment/domain/infrastructure/client/fallback/ProjectClientFallback.java`
- Create: `src/main/java/madp/appdeployment/domain/infrastructure/client/response/ProjectResourceLimitResponseDto.java`
- Modify: `src/main/java/madp/appdeployment/domain/domain/repository/AppDeploymentRepository.java`
- Create: `src/main/java/madp/appdeployment/domain/domain/repository/dto/ProjectResourceUsageSumDto.java`

- [ ] **Step 3: Write minimal implementation**

Reintroduce:
- `ProjectClient.getProjectResourceLimit(projectId)`
- fallback method that throws `ProjectServiceUnavailableException`
- DTO carrying `maxCpu`, `maxMemory`, and `maxDisk`
- repository queries for total resource usage by project and by project excluding one app id
- helper repository query for project id by app id if needed for lock acquisition before loading the entity

- [ ] **Step 4: Run targeted test to verify it still fails for service logic**

Run: `./gradlew test --tests madp.appdeployment.domain.application.service.AppDeploymentServiceTest`
Expected: FAIL in service assertions until validation logic and locking are restored.

## Chunk 3: Restore Service Validation and Locking

### Task 3: Reintroduce project lock manager and service validation

**Files:**
- Create: `src/main/java/madp/appdeployment/domain/application/support/ProjectResourceLockManager.java`
- Create: `src/main/java/madp/appdeployment/domain/exception/ProjectResourceLockConflictException.java`
- Modify: `src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java`

- [ ] **Step 5: Write minimal implementation**

Implement:
- Redis-backed project resource lock manager with timeout and safe unlock
- `createAppDeployment` wrapped in project lock, with owner check, resource build, aggregate validation, and save
- `updateAppDeploymentResourceInfo` wrapped in project lock, with aggregate validation that excludes the target app's current resources
- limit comparison helpers that raise `InvalidResourceInfoException`

- [ ] **Step 6: Run targeted test to verify it passes**

Run: `./gradlew test --tests madp.appdeployment.domain.application.service.AppDeploymentServiceTest`
Expected: PASS

## Chunk 4: Full Verification

### Task 4: Run full verification

**Files:**
- No additional source changes expected

- [ ] **Step 7: Run full test suite**

Run: `./gradlew test`
Expected: PASS

- [ ] **Step 8: Run build**

Run: `./gradlew build`
Expected: PASS
