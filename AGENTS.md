# Project context

This repository is a starter template for a greenfield Java project used in an
introductory software engineering course in an undergraduate computer science program.
Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a
project in this repository. If the user identifies themselves as an instructor or
another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Fluent in Python with medium-sized codebases
  (10 years, >50k LOCs), beginner in Java (<1 year, ~100 LOCs)
* IDE and level of expertise: IntelliJ, competent

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use
  of AI. For example:

    * When suggesting a Git command, briefly explain what it does.
    * Add explanatory Javadoc comments to all classes and public methods, subject to
      the exceptions in the Java coding standard below. Add comments to nontrivial
      fields when their purpose or behavior is not obvious.
    * Make generated code as self-explanatory as possible, and include explanatory
      comments where they improve understanding.
    * When faced with a design choice, choose the simplest option that is sufficient for
      the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use
`sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Required Java coding standard

**Mandatory:** before writing or reviewing any Java code in this repository, load the
`seedu-java-coding-standard` skill and follow it exactly. It is the complete,
authoritative statement of the basic and intermediate rules from the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
(the advanced rules on that page remain optional). This applies to new code and to any
existing code you touch.

## Testing

Tests live under `[project root]/src/test/java`, mirroring the package structure of the
class under test, and run via Gradle's `test` task (`./gradlew test`), which uses the
JUnit Platform.

* Test coverage target: JUnit tests should cover the top ~50% highest-value methods
  overall (prioritizing complex, core, or critical business logic) rather than every
  getter/setter or thin delegation method.
* After each code change, review whether the coverage target still holds and update or
  add JUnit tests as needed so it continues to be met.

## Git

**Mandatory:** before proposing or creating a commit message, branch name, or tag,
load the `seedu-git-standard` skill and follow it exactly. It is the complete,
authoritative statement of the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) plus
this project's tag convention. This applies to all future commits.

Do not commit or push unless explicitly asked.

## Individual project setup and workflow

Follow the
[Week 2 project instructions](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week2/project.html)
and the
[course standards and conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html).

* Keep the repository name as `ip`, the default branch as `master`, and source code
  under `[project root]/src` so the grading scripts can find the project.
* The fork must contain the full repository, and GitHub Issues must be enabled.
* Do not place the repository in a cloud-synced directory such as OneDrive or Dropbox.
* Verify the setup by running `Mona.java`.
* Complete the Week 2 increments in order: `Level-0`, `Level-1`, `Level-2`, `Level-3`,
  `Level-4`, `Level-5`, `Level-6`, and `A-Enums`.
* Commit at meaningful points and at least after every increment. Tag the completing
  commit using the increment's exact ID.
* Do not commit `.class` files or other generated files.
* When explicitly asked to push completed work, push both commits and tags because a
  normal `git push` does not automatically push tags.
* Spread work across the assigned weeks; do not work more than one week ahead.

The linked Markdown and Google documentation style guides are optional.