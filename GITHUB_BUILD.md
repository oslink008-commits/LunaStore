# Build APK bằng GitHub Actions (Không cần cài gì cả)

## Cách sử dụng

### Bước 1: Push code lên GitHub

Nếu chưa có repository:

1. Vào https://github.com/new
2. Tạo repository mới (ví dụ: `LunaStore`)
3. Mở terminal trong thư mục LunaStore:

```bash
git init
git add .
git commit -m "Initial commit - LunaStore app"
git branch -M main
git remote add origin https://github.com/USERNAME/LunaStore.git
git push -u origin main
```

Thay `USERNAME` bằng GitHub username của bạn.

### Bước 2: Trigger build APK

Có 2 cách:

**Cách 1: Tự động khi push**
- Sau khi push, GitHub Actions sẽ tự động chạy build
- Vào tab "Actions" trong repository để xem progress

**Cách 2: Chạy thủ công**
1. Vào repository trên GitHub
2. Chọn tab "Actions"
3. Chọn workflow "Build APK"
4. Nhấn "Run workflow" > "Run workflow"

### Bước 3: Tải APK

Sau khi build thành công (~3-5 phút):

1. Vào tab "Actions"
2. Chọn workflow vừa chạy
3. Kéo xuống phần "Artifacts"
4. Nhấn vào "LunaStore-APK"
5. Tải file `app-release.apk`

File APK sẽ được lưu trong 30 ngày.

## Ưu điểm

✅ **Không cần cài Android SDK** (~300MB)
✅ **Không cần cài JDK** trên máy
✅ **Không cần Android Studio**
✅ **Build nhanh trên cloud**
✅ **APK chỉ 3-5MB**

## Re-build

Muốn build lại:
- Push code mới
- Hoặc chạy thủ công từ Actions tab

## Lưu ý

- APK được sign với debug key (cho phát triển)
- Nếu muốn release key, cần cấu hình signing riêng
- File APK có thể cài trực tiếp trên Android
