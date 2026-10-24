package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Util.DBConnection;

// Truy cập cơ sở dữ liệu để lưu điểm thành phần của sinh viên.
public class DiemDAO {

    /** Chỉ cập nhật khi mỗi thành phần đang hoạt động có đúng một điểm. */
    public int capNhatDiemTongKet(Connection conn, String maSV, String maLop) throws SQLException {
        String sql = "UPDATE DangKyHoc dk JOIN ("
                + "SELECT dk2.MaDangKy, ROUND(SUM(tp.TrongSo * d.DiemDatDuoc), 2) AS TongDiem "
                + "FROM DangKyHoc dk2 "
                + "JOIN LopHoc lh ON lh.MaLop = dk2.MaLop "
                + "JOIN ThanhPhanDanhGia tp ON tp.MaMon = lh.MaMon AND tp.TrangThai = 1 "
                + "LEFT JOIN DiemThanhPhan d ON d.MaDangKy = dk2.MaDangKy AND d.MaTP = tp.MaTP "
                + "WHERE dk2.MaSV = ? AND dk2.MaLop = ? "
                + "GROUP BY dk2.MaDangKy "
                + "HAVING COUNT(*) = COUNT(DISTINCT tp.MaTP) "
                + "AND COUNT(d.DiemDatDuoc) = COUNT(*)"
                + ") ketQua ON ketQua.MaDangKy = dk.MaDangKy "
                + "SET dk.DiemTongKet = ketQua.TongDiem, "
                + "dk.TrangThai = IF(ketQua.TongDiem >= 4.0, 'DA_HOAN_THANH', 'DANG_HOC')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maLop);
            return ps.executeUpdate();
        }
    }

    /* @param maSV mã sinh viên trong bảng DangKyHoc (tham chiếu SinhVien.MaSV)
     * @param maLop mã lớp sinh viên đã đăng ký
     * @param thuTu thứ tự thành phần đánh giá trong ThanhPhanDanhGia
     * @param diemDatDuoc điểm đạt được cần lưu vào DiemThanhPhan
     * @return true nếu thêm điểm thành công; false nếu không tìm được kết quả duy nhất
     */
    public boolean nhapDiem(String maSV, String maLop, int thuTu, float diemDatDuoc) throws SQLException {
        String timThanhPhan = "SELECT dk.MaDangKy, tp.MaTP "
                + "FROM DangKyHoc dk "
                + "JOIN LopHoc lh ON dk.MaLop = lh.MaLop "
                + "JOIN ThanhPhanDanhGia tp ON lh.MaMon = tp.MaMon "
                + "WHERE dk.MaSV = ? AND dk.MaLop = ? "
                + "AND tp.ThuTu = ? AND tp.TrangThai = 1";
        // NgayNhap được cơ sở dữ liệu gán theo giá trị mặc định CURRENT_TIMESTAMP.
        String themDiem = "INSERT INTO DiemThanhPhan (MaDangKy, MaTP, DiemDatDuoc) "
                + "VALUES (?, ?, ?)";

        // try-with-resources tự đóng kết nối, câu lệnh và kết quả sau khi sử dụng.
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement timPS = conn.prepareStatement(timThanhPhan)) {
            // Gán tham số vào các dấu ? theo thứ tự trong câu truy vấn tìm kiếm.
            timPS.setString(1, maSV);
            timPS.setString(2, maLop);
            timPS.setInt(3, thuTu);

            int maDangKy;
            int maTP;
            try (ResultSet rs = timPS.executeQuery()) {
                // Không có đăng ký hoặc thành phần đánh giá phù hợp thì không lưu điểm.
                if (!rs.next()) {
                    return false;
                }
                maDangKy = rs.getInt("MaDangKy");
                maTP = rs.getInt("MaTP");
                // Có nhiều kết quả thì không xác định được duy nhất nơi cần nhập điểm.
                if (rs.next()) {
                    return false;
                }
            }

            // Lưu điểm với mã đăng ký và mã thành phần vừa tìm được.
            try (PreparedStatement themPS = conn.prepareStatement(themDiem)) {
                themPS.setInt(1, maDangKy);
                themPS.setInt(2, maTP);
                themPS.setFloat(3, diemDatDuoc);
                // Có ít nhất một dòng được thêm nghĩa là lưu điểm thành công.
                return themPS.executeUpdate() > 0;
            }
        }
    }
}
