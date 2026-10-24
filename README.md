# BTL_OOP_Nhom5
Ứng dụng Java quản lý sinh viên và các nghiệp vụ học vụ, sử dụng MySQL qua JDBC.

## Yêu cầu

- JDK 17
- Maven
- MySQL

## Cấu hình database

Chạy script `Database/SQL/test.sql` trong MySQL để tạo dữ liệu kiểm thử. Ứng dụng đọc cấu hình kết nối từ biến môi trường:

| Biến | Bắt buộc | Mặc định / mô tả |
| --- | --- | --- |
| `DB_URL` | Không | Mặc định `jdbc:mysql://localhost:3306/test` |
| `DB_USER` | Có | Tên tài khoản MySQL |
| `DB_PASSWORD` | Có | Mật khẩu MySQL |

Ví dụ PowerShell, chỉ có hiệu lực trong cửa sổ hiện tại:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/test"
$env:DB_USER = "your_mysql_user"
$env:DB_PASSWORD = "your_mysql_password"
mvn test
```

Không ghi mật khẩu thật vào mã nguồn hoặc commit lên Git. File `.env` và `*.local.properties` được bỏ qua bởi `.gitignore`; ứng dụng không tự đọc các file này, nên hãy nạp cấu hình vào biến môi trường của tiến trình trước khi chạy.

## Xử lý lỗi

DAO sử dụng `SQLException` cho lỗi JDBC. Service chuyển lỗi database thành `ServiceException` (unchecked), giữ nguyên nguyên nhân để ghi log kỹ thuật nhưng chỉ cung cấp thông báo tổng quát cho tầng gọi. Tầng giao diện nên bắt `ServiceException` ở ranh giới xử lý sự kiện và hiển thị thông báo thân thiện; không hiển thị stack trace hoặc chi tiết JDBC cho người dùng.

## Build và kiểm tra

```bash
mvn test
```

Các nghiệp vụ hiện được tổ chức trong `src/main/java/Services`, truy vấn database trong `src/main/java/DAO`, model trong `src/main/java/Model`, và cấu hình JDBC trong `src/main/java/Util/DBConnection.java`.
