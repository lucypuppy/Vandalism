# Vandalism Test

Vandalism is a mod for Minecraft designed to train server administrators.

## Setup the project

1. Clone the repository using ``git clone --recursive <url>``.
2. Run ``gradle genSources`` to setup the project.
3. Compile and install addons using ``gradle build`` and ``gradle installCompileAddon``.

## Guidelines

### How to name commits
- Untested changes should be marked with ``ut: `` to indicate that.
- Prevent spamming commits, if you make mistakes inside a commit, and it's still latest up, rebase:
  - ``git reset HEAD~1``
  - ``Make your fixed changes``
  - ``git add *``
  - ```git commit -m "Your message"```
  - ``git push -f``
- Commits should have proper messages indicating what was changed, prevent things like ```Fixed stuff``` or ```Updated things```.
- Write commit messages in English and present tense.

### Update gradle binary
Run ``gradlew wrapper --gradle-version <version>`` to update the Gradle binary.

Ex: ``gradlew wrapper --gradle-version 8.8``

### Pull upstream commits
1. ``git submodule update --remote --merge``
2. Then commit using ``git add .`` and ``git commit -m "Update upstream (name)"``

   Ex: ``git commit -m "Update upstream (Wurst7)"``
3. Push changes using ``git push``

### General notes
Build conventions are located in the ``build-logic`` folder.

In case someone force pushes, you need to:
1. ``git fetch``
2. ``git reset --hard origin/main``
NOTE: This will remove all your local changes, so make sure to backup them.