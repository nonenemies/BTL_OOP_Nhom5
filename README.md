# BTL OOP - Quản lý sinh viên

Ứng dụng JavaFX quản lý các nghiệp vụ học vụ gồm đăng nhập, đăng ký/hủy lớp, đổi lớp, thời khóa biểu và quản lý điểm theo vai trò sinh viên/giảng viên.

## Công nghệ và yêu cầu

- Java 17
- JavaFX 21.0.13
- AtlantaFX 2.0.1
- Maven
- MySQL
- MySQL Connector/J 9.5.0
- JDBC với `PreparedStatement`

JavaFX 21.0.13 được chọn để chạy cùng JDK 17. AtlantaFX 2.0.1 được build với Java 17 và JavaFX 20, nên dùng cùng JavaFX 21 là lựa chọn tương thích gần với phiên bản nền của thư viện. Cả nhóm nên dùng JDK 17 và giữ các phiên bản dependency theo `pom.xml`.

Yêu cầu tải thêm SceneBuilder để sau này dùng

## Cấu trúc dự án

```text
Database/
└── SQL/
    └── test.sql
docs/
└── thanh-phan-danh-gia.md
src/
└── main/
    ├── java/
    │   ├── Controllers/
    │   │   ├── GiangVienController.java
    │   │   └── LoginController.java
    │   ├── DAO/
    │   │   ├── AdminDAO.java
    │   │   ├── DangKyHocDAO.java
    │   │   ├── DiemDAO.java
    │   │   ├── GiangVienDAO.java
    │   │   ├── LichHocDAO.java
    │   │   ├── LopHocDAO.java
    │   │   ├── MonHocDAO.java
    │   │   ├── MonTienQuyetDAO.java
    │   │   ├── SinhVienDAO.java
    │   │   ├── ThanhPhanDanhGiaDAO.java
    │   │   ├── UsersDAO.java
    │   │   └── YeuCauDoiLopDAO.java
    │   ├── Model/
    │   │   ├── Admin.java
    │   │   ├── BangDiemSinhVien.java
    │   │   ├── DangKyHoc.java
    │   │   ├── DiemThanhPhan.java
    │   │   ├── GiangVien.java
    │   │   ├── LichHoc.java
    │   │   ├── LopHoc.java
    │   │   ├── MonHoc.java
    │   │   ├── SinhVien.java
    │   │   ├── ThanhPhanDanhGia.java
    │   │   ├── ThongTinQuyen.java
    │   │   ├── TrangThaiDangKy.java
    │   │   ├── TrangThaiDoiLop.java
    │   │   ├── TrangThaiHoatDong.java
    │   │   ├── Users.java
    │   │   └── YeuCauDoiLop.java
    │   ├── Services/
    │   │   ├── DangKyHocService.java
    │   │   ├── DiemService.java
    │   │   ├── ServiceException.java
    │   │   ├── ThanhPhanDanhGiaService.java
    │   │   └── YeuCauDoiLopService.java
    │   ├── Util/
    │   │   └── DBConnection.java
    │   ├── Launcher.java
    │   └── MainApp.java
    └── resources/
        ├── css/
        │   └── app.css
        └── views/
            ├── DashBoardView.fxml
            ├── GiangVienView.fxml
            └── LoginView.fxml
pom.xml
README.md
```

`MainApp` khởi tạo JavaFX, `Launcher` là điểm vào để chạy ứng dụng, còn Controller xử lý sự kiện giao diện và gọi Service. Các DAO thực hiện truy vấn; Model chứa dữ liệu và enum; Service xử lý nghiệp vụ và transaction. Thư mục `src/main/resources/css/` hiện được dành sẵn cho stylesheet nhưng chưa có file CSS. `target/` và `.idea/` là dữ liệu sinh ra/cấu hình IDE, không phải mã nguồn nghiệp vụ.

Các DAO chính gồm `DangKyHocDAO`, `DiemDAO`, `LopHocDAO`, `MonHocDAO`, `MonTienQuyetDAO`, `ThanhPhanDanhGiaDAO` và `YeuCauDoiLopDAO`. Các Service chính gồm `DangKyHocService`, `DiemService`, `ThanhPhanDanhGiaService` và `YeuCauDoiLopService`.

## Cấu hình database

Chạy `Database/SQL/test.sql` trên một MySQL test để tạo schema và dữ liệu mẫu. Kết nối đọc cấu hình từ biến môi trường:

| Biến | Bắt buộc | Giá trị mặc định |
| --- | --- | --- |
| `DB_URL` | Không | `jdbc:mysql://localhost:3306/QuanLyTinChi` |
| `DB_USER` | Có | Không có |
| `DB_PASSWORD` | Có | Không có |

Ví dụ PowerShell (chỉ áp dụng cho cửa sổ hiện tại):

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/QuanLyTinChi"
$env:DB_USER = "your_mysql_user"
$env:DB_PASSWORD = "your_mysql_password"
mvn test
```

Trong IntelliJ IDEA, cấu hình các biến này trong **Run → Edit Configurations → Environment variables** cho cấu hình chạy. Ứng dụng không có tài khoản/mật khẩu mặc định và không tự đọc `.env` hay `*.local.properties`; không ghi credentials thật vào mã nguồn hoặc commit lên Git.

`test.sql` chỉ dành cho database test. Script tạo database tên `QuanLyTinChi`; giữ `DB_URL` cùng tên này để kết nối đúng schema. Nếu database cũ dùng schema khác với `test.sql`, hãy sao lưu trước và tạo lại database test theo schema hiện tại hoặc thực hiện migration phù hợp.

## Mô hình dữ liệu và trạng thái

Các bảng chính: `Users`, `SinhVien`, `GiaoVien`, `Admin`, `MonHoc`, `MonTienQuyet`, `LopHoc`, `LichHoc`, `ThanhPhanDanhGia`, `DangKyHoc`, `DiemThanhPhan` và `YeuCauDoiLop`.

- `MaSV` và `MaGV` là mã nghiệp vụ kiểu chuỗi; khóa ngoại tới sinh viên/giảng viên dùng các mã này. `YeuCauDoiLop.NguoiXuLy` vẫn tham chiếu `Users.UserID`, vì đây là ID tài khoản người xử lý.
- Các mã tự tăng như `UserID`, `MaDangKy`, `MaYeuCau`, `MaTP` và `MaDiemTP` là ID số do database quản lý.
- `LichHoc` lưu lịch lặp theo tuần của lớp bằng thứ, giờ bắt đầu và giờ kết thúc.
- `Users`, `MonHoc` và `LopHoc` sử dụng `Model.TrangThaiHoatDong` (`HOAT_DONG`, `NGUNG_HOAT_DONG`); các cột tương ứng trong MySQL dùng `ENUM`. `ThanhPhanDanhGia.TrangThai` là cờ bật/tắt riêng, không phải enum này.
- Các trạng thái đăng ký và yêu cầu đổi lớp được biểu diễn bằng enum model tương ứng.

## Nghiệp vụ chính

### Đăng ký học

`DangKyHocService` kiểm tra lớp, đăng ký trùng, môn tiên quyết và lịch trùng trong transaction. Lịch được đối chiếu theo thứ và khoảng giờ trong cùng học kỳ; hai buổi giao nhau khi giờ bắt đầu của buổi này nhỏ hơn giờ kết thúc buổi kia và ngược lại. Dòng sinh viên được khóa để các yêu cầu đăng ký đồng thời của cùng một sinh viên không vượt qua kiểm tra lịch. Sĩ số được cập nhật cùng thao tác đăng ký/hủy trong transaction. Sinh viên có thể hủy lớp đang học; lớp đã hoàn thành không thể hủy. Lớp đã hủy có thể đăng ký lại nếu còn chỗ và vẫn đạt điều kiện. Lỗi database làm rollback transaction.

Màn hình `TimetableView.fxml` luôn hiển thị 17 hàng ca và các cột từ thứ 2 đến chủ nhật, kể cả khi chưa có lớp đăng ký. Ca 1 bắt đầu lúc 07:00, mỗi ca dài một giờ; ca 6 (12:00–13:00) được dành cho nghỉ trưa. Các lớp được đặt vào đúng khung giờ dựa trên lịch thực tế và kéo dài qua các hàng ca tương ứng. Cột cuối hiển thị khoảng giờ cố định của từng ca. Bảng cuộn dọc để xem hết các ca.

### Điểm

Giảng viên đăng nhập được chuyển đến `GiangVienView.fxml`. Màn hình chỉ liệt kê các lớp được phân công, tải danh sách sinh viên đã đăng ký và bảng điểm theo các thành phần đánh giá đang hoạt động. Chức năng lưu điểm hỗ trợ cả nhập mới và cập nhật; Service kiểm tra quyền phụ trách lớp, lưu điểm và tính lại điểm tổng kết trong cùng transaction. Điểm tổng kết chỉ được cập nhật khi đã đủ điểm cho các thành phần đang hoạt động.

### Thành phần đánh giá

`ThanhPhanDanhGiaService` kiểm tra tài khoản admin đang hoạt động, dữ liệu tên/trọng số/thứ tự và tổng trọng số bằng `1`. Các thay đổi được ghi trong một transaction; lỗi sẽ rollback. Hướng dẫn chi tiết tại `docs/thanh-phan-danh-gia.md`.

### Đổi lớp

`YeuCauDoiLopService` đăng yêu cầu sau khi kiểm tra đăng ký và môn tiên quyết. Khi một sinh viên chốt yêu cầu, hệ thống kiểm tra lịch của cả hai sinh viên (bỏ qua lớp mỗi người đang rời khỏi), rồi mới hoán đổi hai đăng ký và cập nhật trạng thái yêu cầu trong cùng transaction.

## Xử lý lỗi

DAO sử dụng `SQLException` cho lỗi JDBC. Các Service chuyển lỗi ra ngoài dưới dạng `ServiceException`; lỗi kỹ thuật được giữ nguyên nguyên nhân để ghi log, còn UI nên hiển thị thông báo thân thiện thay vì chi tiết JDBC/stack trace. Một số nghiệp vụ trả về `String` hoặc `boolean` cho kết quả nghiệp vụ dự kiến (ví dụ điều kiện đăng ký không đạt); cần giữ cách xử lý kết quả này ở caller.
