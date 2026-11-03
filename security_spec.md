# Pathfinder AI Security Specification

## 1. Data Invariants
1. **User Profile Ownership (`/users/{userId}`)**: A user profile document can only be created, read, updated, or deleted by the authenticated user whose `request.auth.uid` strictly matches `{userId}` and `data.userId`.
2. **Goal / Milestone / Session Ownership (`/users/{userId}/goals/{goalId}`)**: A goal, milestone, custom task, project, certification, or interview session record can only be created, read, listed, updated, or deleted by the authenticated owner (`request.auth.uid == userId` and `data.userId == request.auth.uid`).
3. **Immutable Audit Metadata**: `userId` and `createdAt` cannot be modified after creation on any document.
4. **Strict Key & Size Enforcement**: Every string and array is strictly bounded in length, and unexpected ghost keys are rejected via `hasOnly()`.

## 2. The Dirty Dozen Payloads
1. **Unauthenticated Profile Read**: `GET /users/alice_123` with `auth == null` -> REJECT.
2. **Cross-User Profile Read (PII Leak)**: `GET /users/bob_456` as `alice_123` -> REJECT.
3. **Identity Spoofing on Create**: `CREATE /users/alice_123` with `userId: "bob_456"` -> REJECT.
4. **Ghost Field Injection (Shadow Update)**: `UPDATE /users/alice_123` with `isAdmin: true` -> REJECT.
5. **Immutable Field Tampering**: `UPDATE /users/alice_123` changing `createdAt` or `userId` -> REJECT.
6. **Value Poisoning (Oversized String)**: `CREATE /users/alice_123` with `fullName` of 5000 chars -> REJECT.
7. **Out-of-Bounds Readiness Score**: `UPDATE /users/alice_123` with `readinessScore: 999` -> REJECT.
8. **Array Overflow**: `UPDATE /users/alice_123` with 100 items in `technicalSkills` -> REJECT.
9. **Unauthenticated Goal List**: `LIST /users/alice_123/goals` with `auth == null` -> REJECT.
10. **Cross-User Goal Access**: `GET /users/bob_456/goals/goal_1` as `alice_123` -> REJECT.
11. **Invalid Goal Category Enum**: `CREATE /users/alice_123/goals/goal_1` with `category: "hacked"` -> REJECT.
12. **Future Timestamp Spoofing**: `CREATE /users/alice_123/goals/goal_1` with `createdAt > request.time` -> REJECT.
