#!/bin/bash

# Get the current branch name and store it in a variable
branch=$(git rev-parse --abbrev-ref HEAD)

# Check if the current branch is not 'main'
if [ "$branch" != "main" ]; then
    echo "You are not on the main branch. Switching to main..."
    git checkout main
fi

echo "Fetching latest changes..."
git fetch

echo "Resetting local changes and updating to the latest main branch..."
git reset --hard origin/main

echo "Update complete."
