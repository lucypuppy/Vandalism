# Vandalism

Vandalism is a mod for Minecraft designed to train server administrators.

## Guidelines

- Untested changes should be marked with ``ut: `` to indicate that.
- Prevent spamming commits, if you make mistakes inside a commit, and it's still latest up, rebase:
  - ``git reset HEAD~1``
  - ``Make your fixed changes``
  - ``git add *``
  - ```git commit -m "Your message"```
  - ``git push -f``
- Commits should have proper messages indicating what was changed, prevent things like ```Fixed stuff``` or ```Updated things```.
- Write commit messages in English and present tense.