# Vandalism

Vandalism is a mod for Minecraft designed to train server administrators.

## Setup the project

1. Clone the repository using ``git clone <url>``.
2. Run ``gradle genSources`` to setup the project.
3. Compile and install addons using ``gradle build`` and ``gradle installCompileAddon``.

## Terminology

Shared build script properties are exposed to `build-logic` and are defined as convention patterns per Gradle documentation.

## How to use Addons

Vandalism has add-ons for various mods to integrate better with the main client, they are exposed as submodules and dynamically loaded
depending on if the corresponding binary file is present. This is done via `settings.gradle` and the BinaryExt in `addon-conventions`.

1. Download the latest binary file of the mod and put it in ``run/mods`` (create the folder if it doesn't exist).
2. Reload the Gradle project and the addon will be loaded.

### Addon Wurst
Alternatively you can run ``scripts/install_wurst.bat`` or ``scripts/install_wurst.sh`` to download the latest Wurst
client binary automatically.

## Guidelines

### How to name commits

- Prevent spamming commits, if you make mistakes inside a commit, and it's still latest up, rebase:
  - ``git reset HEAD~1``
  - ``Make your fixed changes``
  - ``git add *``
  - ```git commit -m "Your message"```
  - ``git push -f``
- Commits should have proper messages indicating what was changed, prevent things like ```Fixed stuff``` or ```Updated things```.
- Write commit messages in English and present tense.

### Force pushing

In case someone force pushes, you can run ``scripts/force_pull.bat``.

### Update gradle binary
Run ``gradlew wrapper --gradle-version <version>`` to update the Gradle binary.

Ex: ``gradlew wrapper --gradle-version 8.8``

### How to name branches for pull requests

- New features: `feat/<addition>`
- Bug fixes: `fix/<addition>`

### Info
Ist ja licensed unter der GNU General Public License, kann ich ja redistributen. In jeder file steht ja sowieso "This program is free software: you can redistribute it and/or modify".