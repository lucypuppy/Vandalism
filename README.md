# Vandalism

Vandalism is a mod for Minecraft designed to train server administrators.

## Setup the project

1. Clone the repository using ``git clone <url>``.
2. Run ``gradle genSources`` to setup the project.
3. Compile and install addons using ``gradle build`` and ``gradle installCompileAddon``.

## How to use Addons

1. Download the latest binary file of the mod and put it in ``run/mods`` (create the folder if it doesn't exist).
2. Reload the Gradle project and the addon will be loaded.

## How to setup the Wurst Client by ease

Alternatively you can run ``scripts/install_wurst.bat`` or ``scripts/install_wurst.sh`` to download the latest Wurst
Client binary automatically.

## Guidelines

### How to name commits

- Untested changes should be marked with ``ut: `` to indicate that. <!--- Fr fr $$ -->
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

### General notes
Build conventions are located in the ``build-logic`` folder.

In case someone force pushes, you can run ``scripts/force_pull.bat``.