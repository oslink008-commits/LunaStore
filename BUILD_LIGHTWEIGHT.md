# Hướng dẫn Build APK Nhẹ (Cho máy yếu 1.5GB trống)

## Cách 1: Build tự động (Khuyên dùng)

### Windows:
Chạy file `build.bat` - nó sẽ tự động kiểm tra và build

```bash
build.bat
```

### Linux/Mac:
```bash
chmod +x build.sh
./build.sh
```

## Cách 2: Build thủ công

### Bước 1: Cài JDK nhẹ (~100MB)

Tải tại: https://adoptium.net/temurin/releases/

Chọn:
- Version: 8 hoặc 11
- Architecture: x86 (máy 32-bit) hoặc x64 (máy 64-bit)
- Package: JRE (nhẹ hơn JDK) hoặc JDK (nếu cần dev)

Cài xong kiểm tra:
```bash
java -version
```

### Bước 2: Build APK

Mở terminal/cmd trong thư mục LunaStore, chạy:

```bash
# Windows
gradlew.bat assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"

# Linux/Mac
./gradlew assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"
```

Lần đầu chạy sẽ tải Gradle và dependencies (~200MB, chỉ tải 1 lần)

### Bước 3: Lấy APK

File APK tại: `app/build/outputs/apk/release/app-release.apk`

Kích thước dự kiến: **3-5MB** (sau khi ProGuard shrink)

## Tối ưu cho máy rất yếu

Nếu máy quá yếu (RAM < 2GB), giảm memory limit:

```bash
gradlew.bat assembleRelease -Dorg.gradle.jvmargs="-Xmx512m"
```

Hoặc build debug (nhanh hơn nhưng APK lớn hơn ~10MB):

```bash
gradlew.bat assembleDebug
```

## Xóa cache để thu hồi dung lượng

Sau khi build xong, có thể xóa cache để thu hồi ~200MB:

```bash
# Windows
rmdir /s /q .gradle

# Linux/Mac
rm -rf .gradle
```

## Lưu ý

- **Không cần Android Studio** - chỉ cần JDK và Gradle
- **Lần đầu build lâu** (~5-10 phút) vì tải dependencies
- **Lần sau build nhanh** (~1-2 phút)
- **APK chỉ 3-5MB** - phù hợp cho máy yếu
- **Máy cần ít nhất 500MB trống** để build, sau đó có thể xóa cache
