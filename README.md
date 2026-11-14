# LunaStore

LunaStore là ứng dụng Android được viết bằng Kotlin với Material Design 3, cho phép tải và chạy userscripts (Lua, Python, Java) từ GitHub repository.

## Tính năng

- **Material You Design**: Giao diện hiện đại với Material Design 3
- **Tải Scripts từ GitHub**: Tự động tải và phân tích scripts từ repository
- **Parser Tags Hỗ trợ**:
  - `//name`: Tên hiển thị của script
  - `//comment`: Mô tả ngắn
  - `//type`: Phân loại (userscript/library)
- **Hỗ trợ đa ngôn ngữ**: Lua, Python, Java
- **Background Service**: Chạy scripts ở nền với notification
- **Lock/Suspend**: Tạm dừng/tiếp tục script đang chạy
- **Shizuku Integration**: Hỗ trợ Shizuku cho quyền nâng cao (tùy chọn)

## Cấu trúc Project

```
LunaStore/
├── app/
│   ├── src/main/
│   │   ├── java/com/lunastore/app/
│   │   │   ├── MainActivity.kt          # Main UI
│   │   │   ├── ScriptViewModel.kt       # ViewModel quản lý scripts
│   │   │   ├── ShizukuManager.kt        # Quản lý Shizuku
│   │   │   └── ui/theme/                # Material Theme
│   │   ├── res/                         # Resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Yêu cầu

**Cách build nhẹ nhàng (không cần Android Studio):**
- JDK 8 trở lên (có thể dùng OpenJDK 8/11)
- Gradle 8.2 (đã bao gồm trong project)
- Internet để tải dependencies lần đầu

**Để cài JDK nhẹ:**
- Windows: Tải tại https://adoptium.net/temurin/releases/ (chọn x86 hoặc x64 tùy máy)
- Chọn bản ~100MB thay vì bản full ~200MB

## Build Project (Không cần Android Studio)

### Build APK siêu nhẹ (~3-5MB)

```bash
# Windows
gradlew.bat assembleRelease

# Linux/Mac
./gradlew assembleRelease
```

APK sẽ được tạo tại: `app/build/outputs/apk/release/app-release.apk`

### Tối ưu thêm cho máy yếu

Nếu máy rất yếu (RAM < 4GB), thêm memory limit:

```bash
# Windows - giới hạn 1GB RAM
gradlew.bat assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"

# Linux/Mac
./gradlew assembleRelease -Dorg.gradle.jvmargs="-Xmx1024m"
```

### Build Debug (nhanh hơn nhưng APK lớn hơn)

```bash
gradlew.bat assembleDebug
```

APK tại: `app/build/outputs/apk/debug/app-debug.apk`

## Cài đặt APK

```bash
# Sử dụng ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Hoặc copy file APK vào thiết bị và cài đặt thủ công
```

## Cấu hình GitHub Repository

Mặc định, app tải scripts từ: `https://github.com/oslink008-commits/UTL`

Để thay đổi repository, sửa trong `ScriptViewModel.kt`:

```kotlin
private val githubRepo = "your-username/your-repo"
```

## Định dạng Tags trong Scripts

Scripts cần có tags theo định dạng sau:

```lua
--name My Script Name
--comment This is a short description
--type userscript

-- Your script code here
print("Hello from LunaStore!")
```

Hoặc cho Python:

```python
#name My Python Script
#comment This is a Python script
#type userscript

print("Hello from LunaStore!")
```

Hoặc cho Java:

```java
//name My Java Script
//comment This is a Java script
//type userscript

public class MyScript {
    public static void main(String[] args) {
        System.out.println("Hello from LunaStore!");
    }
}
```

## Phân loại Scripts

- **userscript**: Script có thể chạy trực tiếp, hiển thị nút Download và Play
- **library**: Thư viện hỗ trợ, chỉ hiển thị nút Download (không có Play)

## Quyền Ứng Dụng

- `INTERNET`: Để tải scripts từ GitHub
- `POST_NOTIFICATIONS`: Hiển thị notification khi script chạy ở nền
- `FOREGROUND_SERVICE`: Chạy background service
- `REQUEST_INSTALL_PACKAGES`: Cài đặt packages (nếu cần)

## Shizuku Integration

Shizuku là tùy chọn cho các scripts cần quyền nâng cao:

1. Cài đặt Shizuku từ: https://shizuku.rikka.app/
2. Kết nối Shizuku trên thiết bị
3. Grant permission cho LunaStore

Nếu không có Shizuku, app vẫn hoạt động nhưng với quyền hạn chế.

## Development

### Thêm dependencies

Edit `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("your-dependency")
}
```

### Thay đổi Material Theme

Edit `app/src/main/java/com/lunastore/app/ui/theme/Color.kt` hoặc `Theme.kt`

## Troubleshooting

### Gradle sync thất bại

- Kiểm tra kết nối internet
- Xóa thư mục `.gradle` và sync lại
- Đảm bảo JDK được cài đặt đúng

### Không tải được scripts

- Kiểm tra kết nối internet
- Đảm bảo GitHub repository có thể truy cập
- Kiểm tra API rate limit của GitHub

### Notification không hiển thị

- Đảm bảo đã grant permission POST_NOTIFICATIONS
- Kiểm tra settings notification của thiết bị

## License

MIT License

## Contributing

Contributions are welcome! Vui lòng mở pull request.
