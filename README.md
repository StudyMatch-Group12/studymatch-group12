# studymatch-group12
Adaptive Student Group Formation


## Git Workflow

All relevant development work must be associated with a GitHub Issue.

### Branch Naming Convention

Branches must be created from `main` and follow this convention:

- `feature/<issue-number>-short-description`
- `fix/<issue-number>-short-description`
- `docs/<issue-number>-short-description`
- `refactor/<issue-number>-short-description`
- `test/<issue-number>-short-description`

Examples:

- `feature/12-student-profile`
- `fix/18-api-validation`
- `docs/1-git-workflow`

Direct development on `main` should be avoided.

### Development Workflow

The team follows this development flow:

`Issue → Branch → Implementation → Pull Request → Review → Merge`

1. Create or select a GitHub Issue.
2. Assign the Issue to the team member responsible for the work.
3. Create a branch associated with the Issue.
4. Implement the required changes.
5. Commit and push the branch.
6. Open a Pull Request to `main`.
7. Link the Pull Request to the corresponding Issue.
8. Another team member reviews the Pull Request.
9. Resolve any requested changes or review comments.
10. Merge the Pull Request after approval.
11. Delete the branch after the merge.

### Pull Request Rules

- Every relevant Pull Request must be linked to a GitHub Issue.
- At least one other team member must review the Pull Request.
- Authors must not approve their own Pull Requests.
- Requested changes must be resolved before merging.
- Direct commits to `main` should be avoided.
- The branch should be deleted after a successful merge.
- At least one Pull Request during Sprint 1 must contain a meaningful review discussion.

### Commit Convention

Commits should be small and descriptive.

Recommended format:

`<type>: <short description>`

Examples:

- `feat: add health endpoint`
- `fix: correct student validation`
- `docs: document git workflow`
- `test: add backend health endpoint test`

### Issue Assignment

Before development begins:

- the Issue should have a clear objective;
- it should be assigned to the responsible team member;
- appropriate labels should be added;
- it should be associated with the current Sprint milestone when applicable.

### Branch Cleanup

After a Pull Request is merged:

- close the associated Issue when the work is complete;
- delete the merged branch;
- verify that `main` remains stable.