@echo off

git rev-parse --abbrev-ref HEAD > current-branch.txt
set /p branch=<current-branch.txt
del current-branch.txt

if not "%branch%"=="main" (
    echo You are not on the main branch. Switching to main...
    git checkout main
)

echo Fetching latest changes...
git fetch

echo Resetting local changes and updating to the latest main branch...
git reset --hard origin/main

echo Update complete.

pause
