# Cài Android SDK Command Line Tools (Nhẹ - ~300MB)

## Bước 1: Tải Android Command Line Tools

Vào: https://developer.android.com/studio#command-tools

Tải **Command line tools only** cho Windows:
- Chọn "Command line tools only"
- Version mới nhất (không cần bản Studio)

Giải nén vào thư mục ví dụ: `C:\Android\sdk`

## Bước 2: Cài đặt các package cần thiết

Mở Command Prompt/PowerShell, chạy:

```bash
cd C:\Android\sdk\cmdline-tools\latest\bin

# Chấp nhận license
sdkmanager --licenses

# Cài Android SDK Platform 34
sdkmanager "platforms;android-34"

# Cài Android SDK Build-Tools 34.0.0
sdkmanager "build-tools;34.0.0"

# Cài Platform Tools (ADB)
sdkmanager "platform-tools"
```

## Bước 3: Thiết lập biến môi trường

### Cách 1: Tạm thời (chỉ cho session hiện tại)
```bash
set ANDROID_HOME=C:\Android\sdk
set PATH=%PATH%;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin
```

### Cách 2: Vĩnh viễn (Cấu hình trong Windows)
1. Nhấn Win + R, gõ `sysdm.cpl`
2. Chọn tab "Advanced" > "Environment Variables"
3. Thêm biến mới:
   - Name: `ANDROID_HOME`
   - Value: `C:\Android\sdk`
4. Chỉnh sửa biến `Path`, thêm:
   - `%ANDROID_HOME%\platform-tools`
   - `%ANDROID_HOME%\cmdline-tools\latest\bin`

## Bước 4: Cấu hình cho LunaStore

Mở file `local.properties` trong thư mục LunaStore, thêm:

```properties
sdk.dir=C:\\Android\\sdk
```

Hoặc chạy lệnh:
```bash
echo sdk.dir=C:\\Android\\sdk > local.properties
```

## Bước 5: Build APK

Bây giờ có thể build rồi:

```bash
cd C:\Users\Hello\Desktop\Krypton\Krypton\LunaStore
gradlew.bat assembleRelease
```

## Tối ưu bộ nhớ

Nếu máy yếu, có thể:
- Chỉ cài 1 version platform (android-34)
- Chỉ cài 1 version build-tools (34.0.0)
- Sau khi build xong có thể xóa cache: xóa thư mục `.gradle`

## Tổng dung lượng
- Android SDK Command Line Tools: ~300MB
- Dependencies Gradle (lần đầu): ~200MB
- APK kết quả: 3-5MB

**Tổng cần: ~500MB trống** (có thể xóa cache sau khi build)
