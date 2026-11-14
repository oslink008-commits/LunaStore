#!/bin/bash

echo "========================================"
echo "LunaStore APK Builder (Lightweight)"
echo "========================================"
echo ""

echo "[1/3] Kiểm tra Java..."
if ! command -v java &> /dev/null; then
    echo "ERROR: Chưa cài Java (JDK)"
    echo "Tải tại: https://adoptium.net/temurin/releases/"
    exit 1
fi
echo "OK: Java đã cài"
echo ""

echo "[2/3] Đang build APK Release (khoảng 3-5MB)..."
echo ""

./gradlew assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Build thất bại"
    exit 1
fi

echo ""
echo "[3/3] Kiểm tra file APK..."
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo ""
    echo "========================================"
    echo "SUCCESS! APK đã tạo thành công"
    echo "========================================"
    echo ""
    echo "File: app/build/outputs/apk/release/app-release.apk"
    ls -lh "app/build/outputs/apk/release/app-release.apk"
    echo ""
    echo "Copy file này vào điện thoại và cài đặt"
    echo ""
else
    echo "ERROR: Không tìm thấy file APK"
fi
