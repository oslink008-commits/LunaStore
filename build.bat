@echo off
echo ========================================
echo LunaStore APK Builder (Lightweight)
echo ========================================
echo.

echo [1/3] Kiểm tra Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Chưa cài Java (JDK)
    echo Tải tại: https://adoptium.net/temurin/releases/
    pause
    exit /b 1
)
echo OK: Java đã cài
echo.

echo [2/3] Đang build APK Release (khoảng 3-5MB)...
echo.

gradlew.bat assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build thất bại
    pause
    exit /b 1
)

echo.
echo [3/3] Kiểm tra file APK...
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo.
    echo ========================================
    echo SUCCESS! APK đã tạo thành công
    echo ========================================
    echo.
    echo File: app\build\outputs\apk\release\app-release.apk
    dir "app\build\outputs\apk\release\app-release.apk"
    echo.
    echo Copy file này vào điện thoại và cài đặt
    echo.
) else (
    echo ERROR: Không tìm thấy file APK
)

pause
