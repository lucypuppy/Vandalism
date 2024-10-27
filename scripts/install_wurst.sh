#!/bin/bash

set -e

cd ..

REPO_URL="https://github.com/Wurst-Imperium/Wurst7.git"
REPO_DIR="Wurst7"
MODS_DIR="run/mods"

if [ -d "$MODS_DIR" ]; then
    echo "Checking for existing JAR files in the binaries directory..."

    for f in "$MODS_DIR"/*.jar; do
        echo "Found JAR file: $(basename "$f")"

        if [[ "$(basename "$f")" == *Wurst* ]]; then
            echo "Deleting JAR file: $(basename "$f")"
            rm "$f"
        fi
    done
fi

if [ ! -d "$REPO_DIR" ]; then
    echo "Cloning the repository..."
    git clone "$REPO_URL"
else
    echo "Repository already exists. Pulling latest changes..."
    cd "$REPO_DIR"
    git pull
    cd ..
fi

cd "$REPO_DIR"

echo "Building the project..."

./gradlew build

if [ $? -ne 0 ]; then
    echo "Build failed. Exiting..."
    exit 1
fi

if [ ! -d "../$MODS_DIR" ]; then
    mkdir "../$MODS_DIR"
fi

echo "Copying the JAR file..."

for f in build/libs/*.jar; do
    echo "Checking $(basename "$f")"

    if [[ "$(basename "$f")" != *-sources* ]]; then
        echo "Found non-sources JAR: $(basename "$f")"
        cp "$f" "../$MODS_DIR"
        JAR_COPIED=1
    fi
done

cd ..

echo "Deleting the cloned repository..."
rm -rf "$REPO_DIR"

echo "Done."
