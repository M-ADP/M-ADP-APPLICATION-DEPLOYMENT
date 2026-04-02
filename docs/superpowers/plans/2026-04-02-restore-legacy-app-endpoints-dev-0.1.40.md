# Restore Legacy App Endpoints On dev-0.1.40 Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore the legacy app list and summary endpoints on top of `origin/dev-0.1.39` and publish them on `dev-0.1.40`.

**Architecture:** Start from the latest remote branch, reintroduce the controller mappings, service methods, DTOs, and compatibility test for the legacy contract, then validate with focused Spring MVC tests before pushing. Keep the change set limited to the legacy compatibility surface so the branch stays easy to review and merge.

**Tech Stack:** Java, Spring Boot, JUnit, MockMvc, Git

---

### Task 1: Isolate Branch And Apply Restore

**Files:**
- Create: `docs/superpowers/plans/2026-04-02-restore-legacy-app-endpoints-dev-0.1.40.md`
- Modify: `src/main/java/madp/appdeployment/domain/presentation/controller/AppDeploymentController.java`
- Modify: `src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/request/GetAppDeploymentSummaryRequestDto.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentListResponseDto.java`
- Create: `src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentSummaryResponseDto.java`
- Test: `src/test/java/madp/appdeployment/domain/presentation/controller/AppDeploymentControllerCompatibilityTest.java`

- [ ] **Step 1: Create an isolated worktree from `origin/dev-0.1.39`**

Run: `git worktree add ~/.config/superpowers/worktrees/M-ADP-APPLICATION-DEPLOYMENT/dev-0.1.40 -b dev-0.1.40 origin/dev-0.1.39`
Expected: new worktree created on branch `dev-0.1.40`

- [ ] **Step 2: Apply the prepared restore patch**

Run: `git apply /tmp/dev-0.1.40-restore.patch`
Expected: controller, service, DTOs, and compatibility test appear in the worktree diff

- [ ] **Step 3: Review the resulting diff**

Run: `git diff --stat && git diff -- src/main/java/madp/appdeployment/domain/presentation/controller/AppDeploymentController.java src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java`
Expected: only legacy compatibility changes are present

### Task 2: Verify Restored Contract

**Files:**
- Test: `src/test/java/madp/appdeployment/domain/presentation/controller/AppDeploymentControllerCompatibilityTest.java`

- [ ] **Step 1: Run the focused compatibility test suite**

Run: `./gradlew test --tests 'madp.appdeployment.domain.presentation.controller.AppDeploymentControllerCompatibilityTest'`
Expected: PASS

- [ ] **Step 2: If needed, run a broader related test slice**

Run: `./gradlew test --tests 'madp.appdeployment.domain.presentation.controller.*'`
Expected: PASS, or capture any pre-existing unrelated failure clearly

### Task 3: Publish Branch

**Files:**
- Modify: Git history on branch `dev-0.1.40`

- [ ] **Step 1: Commit the restore**

Run: `git add docs/superpowers/plans/2026-04-02-restore-legacy-app-endpoints-dev-0.1.40.md src/main/java/madp/appdeployment/domain/presentation/controller/AppDeploymentController.java src/main/java/madp/appdeployment/domain/application/service/AppDeploymentService.java src/main/java/madp/appdeployment/domain/presentation/dto/request/GetAppDeploymentSummaryRequestDto.java src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentListResponseDto.java src/main/java/madp/appdeployment/domain/presentation/dto/response/AppDeploymentSummaryResponseDto.java src/test/java/madp/appdeployment/domain/presentation/controller/AppDeploymentControllerCompatibilityTest.java`

Run: `git commit -m "fix(app-deployment): restore legacy app endpoints"`
Expected: one commit on `dev-0.1.40`

- [ ] **Step 2: Push the branch**

Run: `git push -u origin dev-0.1.40`
Expected: remote branch `origin/dev-0.1.40` created and tracking configured
