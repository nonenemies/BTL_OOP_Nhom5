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
# BTL OOP - Quản lý sinh viên

Ứng dụng Java quản lý dữ liệu học vụ, gồm môn học, lớp học, đăng ký học, điểm, thành phần đánh giá và yêu cầu đổi lớp. Dự án tổ chức theo các lớp Model, DAO, Service và kết nối MySQL qua JDBC.

> **Phạm vi hiện tại:** repository có phần mô hình dữ liệu, truy vấn, nghiệp vụ và script database test. Chưa thấy phần giao diện người dùng trong mã nguồn hiện tại.

## Công nghệ

- Java 17
- Maven
- MySQL
- MySQL Connector/J 9.5.0
- JDBC (`PreparedStatement`)

## Cấu trúc thư mục
### `Database/SQL`

- `test.sql`: tạo database và các bảng mẫu, đồng thời thêm dữ liệu test cho tài khoản, sinh viên, giảng viên, môn học, thành phần đánh giá và lớp học.
- Script này phục vụ môi trường test; `CREATE TABLE IF NOT EXISTS` không tự sửa cấu trúc của database đã tồn tại. Nếu schema cũ chưa tương thích, cần tạo lại database test hoặc tự viết migration.

### `docs/`

- `thanh-phan-danh-gia.md`: hướng dẫn sử dụng nghiệp vụ thêm/sửa thành phần đánh giá, các ràng buộc dữ liệu và ví dụ gọi Service.

### `src/main/java/DAO/`

Các lớp DAO thực hiện truy vấn SQL và ánh xạ dữ liệu từ database sang Model:

- `DangKyHocDAO`: kiểm tra đăng ký, kiểm tra đã qua môn tiên quyết, thêm đăng ký, đổi lớp trong bản ghi đăng ký và lấy thời khóa biểu.
- `DiemDAO`: tính điểm tổng kết từ các thành phần đánh giá và lưu điểm thành phần.
- `LopHocDAO`: thêm/sửa/ngừng hoạt động lớp, đọc danh sách lớp và cập nhật sĩ số.
- `MonHocDAO`: thêm/sửa/ngừng hoạt động môn học và truy vấn môn.
- `MonTienQuyetDAO`: thêm/xóa quan hệ môn tiên quyết và truy vấn các môn cần học trước.
- `ThanhPhanDanhGiaDAO`: đọc/thêm/sửa thành phần đánh giá, truy vấn quyền tài khoản và khóa bản ghi liên quan trong transaction.
- `YeuCauDoiLopDAO`: tạo, đọc và cập nhật trạng thái yêu cầu đổi lớp.

Các DAO thao tác trong một transaction nhận `Connection` từ bên gọi nên có thể dùng chung kết nối. Những phương thức DAO truy vấn độc lập tự mở và đóng connection.

### `src/main/java/Model/`

Các lớp dữ liệu của ứng dụng:

- `Admin`, `GiangVien`, `SinhVien`, `Users`: thông tin tài khoản và các nhóm người dùng.
- `MonHoc`, `LopHoc`, `DangKyHoc`: môn học, lớp học và đăng ký học.
- `ThanhPhanDanhGia`, `DiemThanhPhan`: cấu hình thành phần đánh giá và điểm.
- `YeuCauDoiLop`: yêu cầu trao đổi lớp.
- `ThongTinQuyen`: thông tin quyền/trạng thái được dùng khi kiểm tra phân quyền.
- `TrangThaiDangKy`, `TrangThaiDoiLop`: các trạng thái đăng ký và yêu cầu đổi lớp.

Mã nghiệp vụ sinh viên và giảng viên được biểu diễn bằng `String`, ví dụ `SV001`, `GV01`. Các khóa nội bộ như `UserID`, `MaDangKy`, `MaYeuCau`, `MaTP` vẫn là số.

### `src/main/java/Services/`

Các lớp xử lý nghiệp vụ và điều phối transaction:

- `DangKyHocService`: xử lý đăng ký lớp trong transaction, kiểm tra lớp, đăng ký trùng, môn tiên quyết và sĩ số.
- `DiemService`: gọi chức năng tính điểm tổng kết.
- `ThanhPhanDanhGiaService`: kiểm tra quyền admin và dữ liệu đánh giá trước khi ghi; thao tác danh sách thành phần được thực hiện trong một transaction.
- `YeuCauDoiLopService`: đăng yêu cầu và xử lý việc hai sinh viên đổi lớp.

Một số phương thức Service khai báo `throws SQLException`; phía gọi cần xử lý lỗi database thay vì xem lỗi truy vấn như trường hợp “không có dữ liệu”.

### `src/main/java/Util/`

- `DBConnection`: tạo kết nối JDBC đến MySQL. Thông tin kết nối hiện được khai báo trong mã nguồn; khi dùng ở máy khác cần cấu hình phù hợp và không nên đưa mật khẩu thật lên repository.
- `TestConnection`, `TestSelect`: chương trình nhỏ để kiểm tra kết nối và truy vấn database.

## Các bảng database

Script test định nghĩa các bảng chính:

| Bảng | Mục đích |
| --- | --- |
| `Users` | Tài khoản, vai trò, trạng thái và thông tin đăng nhập |
| `SinhVien` | Thông tin bổ sung của sinh viên; `MaSV` là mã nghiệp vụ dạng chuỗi |
| `GiaoVien` | Thông tin bổ sung của giảng viên; `MaGV` là mã nghiệp vụ dạng chuỗi |
| `Admin` | Thông tin bổ sung của admin |
| `MonHoc` | Danh mục môn học |
| `MonTienQuyet` | Quan hệ môn học và môn tiên quyết |
| `LopHoc` | Lớp mở cho môn học, giảng viên phụ trách và sĩ số |
| `ThanhPhanDanhGia` | Tên, trọng số, thứ tự và trạng thái thành phần đánh giá |
| `DangKyHoc` | Sinh viên đăng ký lớp và điểm tổng kết |
| `DiemThanhPhan` | Điểm của từng thành phần đánh giá |
| `YeuCauDoiLop` | Yêu cầu trao đổi lớp và thông tin xử lý |

Trong schema test hiện tại:

- `DangKyHoc.MaSV` và `YeuCauDoiLop.MaSV` dùng `VARCHAR(20)`, tham chiếu `SinhVien.MaSV`.
- `LopHoc.MaGV` dùng `VARCHAR(20)`, tham chiếu `GiaoVien.MaGV`.
- `YeuCauDoiLop.NguoiXuLy` vẫn là số và tham chiếu `Users.UserID`, vì đây là ID tài khoản người xử lý, không phải mã sinh viên/giảng viên.
- Các mã tự tăng như `MaDangKy`, `MaYeuCau`, `MaTP`, `MaDiemTP` vẫn do database quản lý.

## Nghiệp vụ chính

### Đăng ký học

`DangKyHocService` mở transaction để kiểm tra và xử lý đăng ký. Quy trình gồm kiểm tra lớp, kiểm tra sinh viên đã đăng ký lớp chưa, kiểm tra các môn tiên quyết và kiểm tra/tăng sĩ số lớp trước khi ghi đăng ký. Nếu một bước không thành công, transaction được rollback.

### Thành phần đánh giá

`ThanhPhanDanhGiaService`:

- Chỉ cho phép tài khoản có quyền `ADMIN` và đang hoạt động thêm/sửa cấu hình.
- Khi thêm mới, nhận toàn bộ danh sách thành phần trong một lần gọi.
- Kiểm tra tên không rỗng, độ dài tối đa 200 ký tự, trọng số thuộc `(0, 1]` và tối đa hai chữ số thập phân.
- Kiểm tra tổng trọng số các thành phần đang hoạt động bằng `1`.
- Kiểm tra thứ tự không trùng trong cùng môn.
- Ghi toàn bộ thay đổi trong một transaction; lỗi sẽ rollback toàn bộ.

Xem thêm `docs/thanh-phan-danh-gia.md` để biết cách gọi Service.

### Tính điểm tổng kết

`DiemDAO.capNhatDiemTongKet(...)` tính tổng có trọng số từ các thành phần đánh giá đang hoạt động. Chỉ cập nhật khi đã có điểm cho tất cả thành phần cần tính. Điểm được làm tròn hai chữ số thập phân; trạng thái đăng ký được cập nhật theo điểm tổng kết.

`DiemDAO.nhapDiem(...)` tìm đăng ký và thành phần đánh giá theo mã sinh viên, mã lớp và thứ tự thành phần, sau đó thêm điểm. Cột `MaDiemTP` được database tự sinh.

### Đổi lớp

`YeuCauDoiLopService` cho phép đăng yêu cầu đổi lớp sau khi kiểm tra đăng ký hiện tại và điều kiện môn tiên quyết. Khi sinh viên khác chốt yêu cầu, service khóa yêu cầu và xử lý việc đổi hai đăng ký cùng cập nhật trạng thái yêu cầu trong transaction.

## Kết nối database

1. Cài MySQL và chạy `Database/SQL/test.sql` trong môi trường test.
2. Kiểm tra URL, tên database, tài khoản và mật khẩu trong `Util/DBConnection.java` để khớp với cấu hình máy đang chạy.
3. Nếu database đã tạo từ schema cũ, cần migrate hoặc tạo lại database test để các kiểu `MaSV`/`MaGV` và khóa ngoại khớp với schema hiện tại.

> Lưu ý: tên database trong script test và URL kết nối trong `DBConnection` hiện cần được đối chiếu trước khi chạy. Tên database có thể phân biệt chữ hoa/chữ thường tùy hệ điều hành và cấu hình MySQL.

## Build và chạy

Cần cài JDK 17 và Maven.

```bash
mvn test
