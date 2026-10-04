package Services;

import DAO.DangKyHocDAO;
import Model.DangKyHoc;

public class DangKyHocService {

    // Gọi anh công nhân DAO ra đây để sai vặt
    private DangKyHocDAO dangKyDAO = new DangKyHocDAO();

    // Hàm này sẽ được gọi khi Sinh viên bấm nút "ĐĂNG KÝ" trên màn hình
    // Nó sẽ trả về một câu thông báo (String) để hiện lên màn hình cho SV đọc
    public String xuLyDangKyHoc(int maSV, String maLop) {

        // LUẬT 1: Kiểm tra xem đã đăng ký lớp này chưa?
        boolean daDangKy = dangKyDAO.kiemTraDaDangKy(maSV, maLop);
        if (daDangKy) {
            return "❌ Bạn đã đăng ký lớp này rồi, không thể đăng ký lại!";
        }

        // LUẬT 2: Kiểm tra sĩ số lớp (Sẽ làm sau khi Dev 2 code xong LopHocDAO)
        // Hiện tại cứ cho qua. Sau này bạn sẽ viết thêm:
        // if (lopHocDAO.kiemTraDaySiSo(maLop)) return "❌ Lớp đã đầy!";

        // VƯỢT QUA CÁC LUẬT -> Tiến hành đăng ký
        // Dùng Constructor số 2 mà ta đã tạo lúc nãy
        DangKyHoc dkMoi = new DangKyHoc(maLop, maSV);

        boolean thanhCong = dangKyDAO.themDangKyMoi(dkMoi);

        if (thanhCong) {
            // Sau này nhớ gọi LopHocDAO để TĂNG SĨ SỐ HỆN TẠI LÊN 1 nhé
            return "✅ Đăng ký thành công lớp " + maLop;
        } else {
            return "❌ Có lỗi hệ thống xảy ra, vui lòng thử lại!";
        }
    }

    public static void main(String[] args) {
        DangKyHocService service = new DangKyHocService();

        // Giả sử Sinh viên Nguyễn Văn Sinh (có UserID = 3 trong SQL)
        // Bấm đăng ký môn OOP (có MaLop = 'OOP_L01' trong SQL)
        int idSinhVien = 3;
        String maLopHoc = "OOP_L01";

        System.out.println("⏳ Đang xử lý đăng ký...");
        String ketQua = service.xuLyDangKyHoc(idSinhVien, maLopHoc);
        System.out.println("Kết quả: " + ketQua);

        System.out.println("---------------------------------");
        System.out.println("⏳ SV Cố tình bấm đăng ký lần 2...");
        String ketQuaLan2 = service.xuLyDangKyHoc(idSinhVien, maLopHoc);
        System.out.println("Kết quả: " + ketQuaLan2);
    }
}