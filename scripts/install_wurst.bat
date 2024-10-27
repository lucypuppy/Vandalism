@echo off
setlocal

cd ..

set REPO_URL=https://github.com/Wurst-Imperium/Wurst7.git
set REPO_DIR=Wurst7
set MODS_DIR=run\mods

if exist %MODS_DIR% (
    echo Checking for existing JAR files in the binaries directory...

    for %%f in (%MODS_DIR%\*.jar) do (
        echo Found JAR file: %%~nxf

        echo %%~nxf | findstr /i /c:"Wurst" >nul
        if not errorlevel 1 (
            echo Deleting JAR file: %%~nxf
            del "%%f"
        )
    )
)

if not exist %REPO_DIR% (
    echo Cloning the repository...
    git clone %REPO_URL%
) else (
    echo Repository already exists. Pulling latest changes...
    cd %REPO_DIR%
    git pull
    cd ..
)

:: Read the minecraft_version from gradle.properties
for /f "tokens=2 delims==" %%a in ('findstr /r /c:"^minecraft_version=" gradle.properties') do set MC_VERSION=%%a

cd %REPO_DIR%

:: Switch to the branch corresponding to the minecraft_version
echo Switching to branch %MC_VERSION%...
git checkout %MC_VERSION%

echo Building the project...
call gradlew.bat build

if errorlevel 1 (
    echo Build failed. Exiting...
    exit /b 1
)

if not exist ..\%MODS_DIR% (
    mkdir ..\%MODS_DIR%
)

echo Copying the JAR file...
for %%f in (build\libs\*.jar) do (
    echo Checking %%~nxf
    echo %%~nxf | findstr /i /c:"-sources" >nul
    if errorlevel 1 (
        echo Found non-sources JAR: %%~nxf
        copy "%%f" "..\%MODS_DIR%"
        set JAR_COPIED=1
    )
)

cd ..

echo Deleting the cloned repository...
rmdir /s /q %REPO_DIR%

echo.
echo When you see an error above this message than say thank you to Gradle,
echo taskkill java and delete the Wurst7 directory inside the root directory by yourself.
echo.

echo Done.