---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard for this project (naming, layout, statements, comments). Load before writing or reviewing any Java code in this repository, and follow it for every new or edited line.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

This project mandates the **basic and intermediate** rules below for all Java code.
The advanced rules on that page are optional and not covered here. When code in this
repo predates a rule, bring it into compliance as you touch it; when writing new code,
follow these rules from the start.

## Naming

- Package names: all lowercase.
- Class/enum names: nouns, `PascalCase` (e.g. `Line`, `AudioSystem`).
- Variable names: `camelCase` (e.g. `line`, `audioSystem`).
- Constant names: `SCREAMING_SNAKE_CASE` (e.g. `MAX_ITERATIONS`).
- Method names: verbs, `camelCase` (e.g. `getName()`, `computeTotalWidth()`).
- Test method names may use `featureUnderTest_testScenario_expectedBehavior()`
  (e.g. `sortList_emptyList_exceptionThrown()`); parts can be omitted if not relevant.
- Abbreviations/acronyms embedded in names are NOT fully capitalized:
  good `exportHtmlSource()`, `openDvdPlayer()`; bad `exportHTMLSource()`, `openDVDPlayer()`.
- All names are written in English.
- Scope drives name length: short names (`i`, `j`, `k`, `c`, `d`) are fine for small-scope
  scratch variables; give wide-scope variables descriptive names.
- Boolean variables/methods read as booleans: prefer prefixes `is`, `has`, `was`, `can`,
  `should` (e.g. `isSet`, `hasData`, `boolean hasLicense()`, `boolean canEvaluate()`).
- Collections use plural names (e.g. `Collection<Point> points;`, `int[] values;`).
- Iterator variables: `i`, `j`, `k`; use `j`, `k`, ... only for nested loops.
- Related constants share a common prefix, e.g.:
  ```java
  static final int COLOR_RED   = 1;
  static final int COLOR_GREEN = 2;
  static final int COLOR_BLUE  = 3;
  ```

## Layout

- Indent with 4 spaces, never tabs.
- Line length: soft limit 110 chars, hard limit 120 chars. Wrap longer lines at sensible
  points (after a comma, before an operator/`.`); keep the method/constructor name
  attached to its opening parenthesis.
- Indent wrapped lines by 8 spaces (twice the normal indent).
- Use K&R/Egyptian braces:
  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```
- Method definitions:
  ```java
  public void someMethod() throws SomeException {
      ...
  }
  ```
- `if`/`for`/`while`/`do-while`/`try-catch`: opening brace on the same line as the
  keyword, standard forms, always braced (see "Conditionals" and "Loops" below).
- `switch` fall-through must be marked explicitly with `// Fallthrough` on any `case`
  that has no `break`.
- Whitespace: spaces around operators and after commas/semicolons/keywords.
  Good: `a = (b + c) * d;`, `while (true) {`, `doSomething(a, b, c, d);`,
  `for (i = 0; i < 10; i++) {`.
  Bad: `a=(b+c)*d;`, `while(true){`, `doSomething(a,b,c,d);`, `for(i=0;i<10;i++){`.
- Separate logical units within a block with one blank line.

## Statements

- Every class belongs to a package.
- Import ordering must be consistent (static imports, then `java.*`, `javax.*`, `org.*`,
  `com.*`, `javafx.*`, `junit.*`); pick one order for the project and stick to it.
- Import classes explicitly; never use wildcard imports
  (good: `import java.util.List;`; bad: `import java.util.*;`).
- Array brackets attach to the type, not the variable:
  good `int[] a = new int[20];`; bad `int a[] = new int[20];`.
- Initialize variables where they are declared, and declare them in the smallest
  practical scope.
- Never expose fields publicly, except constants or fields in a genuine behavior-free
  data class. Use non-public fields with accessors otherwise.

## Loops and conditionals

- Always brace loop and conditional bodies, even one-liners:
  ```java
  for (i = 0; i < 100; i++) {
      sum += value[i];
  }
  ```
- Put the conditional and its body on separate lines — never `if (isDone) doCleanup();`.

## Comments

- All comments are written in English (American spelling).
- Every public class and public method needs a descriptive header (Javadoc) comment.
  Exceptions where it may be omitted: getters/setters, overriding methods whose
  inherited Javadoc applies exactly as-is, and test classes/methods.
- Javadoc form:
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @param zone Zone of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```
  - `/**` opens on its own line; each following line's `*` aligns with the first.
  - First sentence is a short summary; for methods, phrase it as `Returns ...`,
    `Adds ...`, `Sends ...` (not imperative mood).
  - Blank line between the description and the `@param`/`@return`/`@throws` block.
  - `@return` may be omitted for `void` methods or when the return is obvious;
    `@param` may be omitted when all parameters are self-explanatory or already
    covered in the main description.
  - No blank line between the Javadoc block and the class/method it documents.
  - Single-line field comments: `/** Number of connections to this database */`.
- Indent comments consistently with the surrounding code.

## Applying this in the project

- When writing new Java code or editing existing code, check it against this list
  before considering the change done.
- When reviewing a diff (yours, a teammate's, or an AI assistant's), flag violations
  of these rules the same way you would flag a bug.
