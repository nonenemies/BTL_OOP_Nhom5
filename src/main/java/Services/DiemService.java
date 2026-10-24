package Services;

import DAO.DiemDAO;
import Util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DiemService {
    private final DiemDAO diemDAO = new DiemDAO();

    /**
     * Lưu tổng(trọng số * điểm đạt được) vào DangKyHoc.DiemTongKet.
     * Không cập nhật khi chưa đủ điểm, trùng điểm hoặc môn chưa có thành phần hoạt động.
     * Kết quả làm tròn đến hai chữ số thập phân theo cột DiemTongKet.
     * @return số đăng ký được cập nhật (có thể là 0 nếu giá trị không đổi tùy cấu hình JDBC)
     */
    public int tinhDiemTongKet(String maSV, String maLop) throws SQLException {
        if ((maSV == null || maSV.isBlank()) || maLop == null || maLop.isBlank()) {
            throw new IllegalArgumentException("Mã sinh viên và mã lớp không hợp lệ.");
        }
        // Một câu UPDATE kiểm tra đủ điểm và lưu kết quả trong cùng thao tác SQL.
        try (Connection conn = DBConnection.getConnection()) {
            return diemDAO.capNhatDiemTongKet(conn, maSV, maLop);
        }
    }
}
