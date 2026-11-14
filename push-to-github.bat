@echo off
echo ========================================
echo Push LunaStore to GitHub
echo ========================================
echo.

set /p REPO_URL="Nhập GitHub repository URL (ví dụ: https://github.com/username/LunaStore.git): "

if "%REPO_URL%"=="" (
    echo ERROR: Chưa nhập URL
    pause
    exit /b 1
)

echo.
echo [1/3] Đang thêm remote...
git remote add origin %REPO_URL% 2>nul
git remote set-url origin %REPO_URL%
echo OK
echo.

echo [2/3] Đang push code lên GitHub...
git branch -M main
git push -u origin main

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Push thất bại
    echo.
    echo Có thể bạn cần:
    echo 1. Tạo repository trống trên GitHub trước
    echo 2. Đăng nhập GitHub nếu chưa
    echo 3. Sử dụng SSH key thay vì password
    echo.
    pause
    exit /b 1
)

echo.
echo [3/3] SUCCESS!
echo.
echo Code đã push lên GitHub thành công
echo Vào repository và vào tab "Actions" để build APK
echo.
pause
