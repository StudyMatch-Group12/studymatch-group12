# StudyMatch - Coding Conventions and Definition of Done

## 1. Coding Conventions

To keep the project consistent and easy to maintain, all team members should follow the same coding conventions.

### Classes
- Class names must use PascalCase.
- Class names should clearly describe their responsibility.

Examples:
- `Student`
- `StudyGroup`
- `MatchingService`

### Methods
- Method names must use camelCase.
- Method names should describe the action being performed.

Examples:
- `createStudyGroup()`
- `findAvailableGroups()`
- `addStudent()`

### Variables
- Variable names must use camelCase.
- Names should clearly describe the stored value.
- Avoid unclear abbreviations.

Examples:
- `studentName`
- `studyGroup`
- `maximumStudents`

### Constants
Constants must use UPPER_SNAKE_CASE.

Examples:
- `MAX_GROUP_SIZE`
- `DEFAULT_TIMEOUT`

### Packages
Package names must use lowercase letters.

Base package:

`pt.upt.studymatch`

Suggested packages:
- `pt.upt.studymatch.model`
- `pt.upt.studymatch.service`
- `pt.upt.studymatch.repository`
- `pt.upt.studymatch.controller`

### Code Style
- Use 4 spaces for indentation.
- Opening braces should be placed on the same line.
- Remove unused imports.
- Avoid duplicated code.
- Use meaningful variable and method names.
- Keep methods small and focused.

---

## 2. Initial Project Structure

The initial project structure should be:

```text
studymatch/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── pt/upt/studymatch/
│   │   │       ├── model/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       └── controller/
│   │   └── resources/
│   └── test/
│       └── java/
│           └── pt/upt/studymatch/
├── docs/
├── pom.xml
├── .gitignore
└── README.md