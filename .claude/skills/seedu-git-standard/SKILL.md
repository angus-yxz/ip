---
name: seedu-git-standard
description: The SE-EDU Git conventions for this project (commit subject/body, branch names, tags). Load before drafting a commit message, branch name, or tag for this repository.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html
(plus the project's tag convention from the course's standards and conventions page)

## Commit message: subject

- Aim for no more than 50 characters; 72 characters is the hard limit.
- Use the imperative mood: good `Add README.md`; bad `Added README.md`, `Adding README.md`.
- Capitalize the first letter: good `Move index.html file to root`; bad
  `move index.html file to root`.
- Do not end with a period: good `Update sample data`; bad `Update sample data.`.
- An optional scope/category prefix is allowed, e.g. `Person class: Remove static
  imports`, `Parser: Handle invalid dates`, `bug fix: Add space after name`,
  `chore: Update release date`.

## Commit message: body

- Give non-trivial commits a body explaining the details.
- Separate the subject from the body with a blank line; wrap the body at 72 characters;
  use blank lines between paragraphs.
- Explain WHAT changed and WHY, not HOW — the diff already shows the implementation.
- Avoid repeating information already clear from code comments.
- Use bullet points where that is clearer than prose.
- A useful structure: current situation (present tense) -> why a change is needed ->
  what is being done (imperative mood) -> why done that way -> other relevant info.

## Branch names

- Use a meaningful, kebab-case name made of relevant keywords (e.g. `refactor-ui-tests`).
- If the branch relates to an issue, use `issueNumber-some-keywords-from-issue-title`
  (e.g. `1234-ui-freeze-error`).

## Tags

- Use lightweight tags unless an annotated tag is explicitly requested.
- Tag the exact commit ID specified for a course increment (e.g. the merge commit
  completing that increment), using the increment's exact ID as the tag name.

## Applying this in the project

- Before proposing a commit message, check it against the subject/body rules above.
- Before creating a branch, name it per the branch-name rule.
- Prefer several small, well-scoped commits (e.g. one per standalone change) over one
  large commit that mixes unrelated concerns.
