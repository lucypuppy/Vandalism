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

cd %REPO_DIR%

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

echo Done.
