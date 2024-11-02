#!/bin/bash

BRANCH=${1:-master}

cd ..

REPO_URL="https://github.com/Wurst-Imperium/Wurst7.git"
REPO_DIR="Wurst7"
MODS_DIR="run/mods"

if [ -d "$MODS_DIR" ]; then
    echo "Checking for existing JAR files in the binaries directory..."

    for f in "$MODS_DIR"/*.jar; do
        if [ -f "$f" ]; then
            echo "Found JAR file: $(basename "$f")"

            if [[ $(basename "$f") == *Wurst* ]]; then
                echo "Deleting JAR file: $(basename "$f")"
                rm "$f"
            fi
        fi
    done
fi

if [ ! -d "$REPO_DIR" ]; then
    echo "Cloning the repository..."
    git clone "$REPO_URL"
else
    echo "Repository already exists. Pulling latest changes..."
    cd "$REPO_DIR"
    git checkout "$BRANCH"
    git pull origin "$BRANCH"
    cd ..
fi

MC_VERSION=$(grep "^minecraft_version=" gradle.properties | cut -d'=' -f2)

cd "$REPO_DIR"

echo "Building the project..."
./gradlew build
if [ $? -ne 0 ]; then
    echo "Build failed. Exiting..."
    exit 1
fi

mkdir -p "../$MODS_DIR"

echo "Copying the JAR file..."
JAR_COPIED=0
for f in build/libs/*.jar; do
    if [[ $(basename "$f") != *-sources.jar ]]; then
        echo "Found non-sources JAR: $(basename "$f")"
        cp "$f" "../$MODS_DIR"
        JAR_COPIED=1
    fi
done

cd ..
echo "Deleting the cloned repository..."
rm -rf "$REPO_DIR"

echo "Done."
