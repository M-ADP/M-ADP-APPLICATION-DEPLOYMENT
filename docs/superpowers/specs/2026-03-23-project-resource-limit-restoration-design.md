# Project Resource Limit Restoration Design

**Goal:** Restore project-level resource limit validation for app creation and resource updates in `dev-0.1.34`, including concurrency protection for per-project limit checks.

## Scope

- Re-add project resource limit lookup through `ProjectClient`.
- Re-add project usage aggregation queries in `AppDeploymentRepository`.
- Re-add service-layer validation before create and resource update.
- Re-add project-scoped Redis locking to serialize limit checks and writes.
- Keep existing request DTO validation and existing owner checks intact.

## Design

`AppDeploymentService` will validate per-project aggregate CPU, memory, and disk totals before persisting a new app deployment or updating an existing deployment's resources. The service will fetch the project's resource limits from the project service, fetch current aggregated usage from the local repository, and compare the proposed totals against the limit.

To avoid races between concurrent create/update requests for the same project, validation and write operations will execute under a Redis lock managed by `ProjectResourceLockManager`. Lock acquisition failures will raise a business exception instead of silently allowing conflicting writes.

## Error Handling

- Non-owner requests continue to raise `ProjectAccessDeniedException`.
- Project service fallback on limit lookup continues to raise `ProjectServiceUnavailableException`.
- Resource totals exceeding project limits raise `InvalidResourceInfoException` with a specific message.
- Lock timeout or interruption raises `ProjectResourceLockConflictException`.

## Testing

- Add service tests for create within limits, create over limit, update within limits, and update over limit.
- Mock repository sums, project limit responses, and lock execution so the tests stay fast and deterministic.
- Run targeted service tests first, then full Gradle test suite.
