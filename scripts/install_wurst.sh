#!/bin/bash

cd ..

REPO_URL=https://github.com/Wurst-Imperium/Wurst7.git
REPO_DIR=Wurst7
MODS_DIR=run/mods

if [ -d "$MODS_DIR" ]; then
    echo "Checking for existing JAR files in the binaries directory..."

    for f in "$MODS_DIR"/*.jar; do
        echo "Found JAR file: $(basename "$f")"

        if echo "$(basename "$f")" | grep -iq "Wurst"; then
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

# Read the minecraft_version from gradle.properties
MC_VERSION=$(grep "^minecraft_version=" gradle.properties | cut -d'=' -f2)

cd "$REPO_DIR"

# Switch to the branch corresponding to the minecraft_version
echo "Switching to branch $MC_VERSION..."
git checkout "$MC_VERSION"

echo "Building the project..."
./gradlew build

if [ $? -ne 0 ]; then
    echo "Build failed. Exiting..."
    exit 1
fi

if [ ! -d "../$MODS_DIR" ]; then
    mkdir -p "../$MODS_DIR"
fi

echo "Copying the JAR file..."
for f in build/libs/*.jar; do
    echo "Checking $(basename "$f")"
    if ! echo "$(basename "$f")" | grep -iq "-sources"; then
        echo "Found non-sources JAR: $(basename "$f")"
        cp "$f" "../$MODS_DIR"
        JAR_COPIED=1
    fi
done

cd ..

echo "Deleting the cloned repository..."
rm -rf "$REPO_DIR"

echo
echo "When you see an error above this message than say thank you to Gradle,"
echo "kill the java process and delete the Wurst7 directory inside the root directory by yourself."
echo

echo "Done."