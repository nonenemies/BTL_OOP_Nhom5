package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Util.DBConnection;

// Truy cập cơ sở dữ liệu để lưu điểm thành phần của sinh viên.
public class DiemDAO {

    /* @param maSV mã sinh viên trong bảng DangKyHoc (tham chiếu Users.UserID)
     * @param maLop mã lớp sinh viên đã đăng ký
     * @param thuTu thứ tự thành phần đánh giá trong ThanhPhanDanhGia
     * @param diemDatDuoc điểm đạt được cần lưu vào DiemThanhPhan
     * @return true nếu thêm điểm thành công; false nếu không tìm được kết quả
     *         duy nhất hoặc xảy ra lỗi SQL
     */
    public boolean nhapDiem(int maSV, String maLop, int thuTu, float diemDatDuoc) {
        // Chỉ chọn thành phần đánh giá đang hoạt động (TrangThai = 1).
        String timThanhPhan = "SELECT dk.MaDangKy, tp.MaTP "
                + "FROM DangKyHoc dk "
                + "JOIN LopHoc lh ON dk.MaLop = lh.MaLop "
                + "JOIN ThanhPhanDanhGia tp ON lh.MaMon = tp.MaMon "
                + "WHERE dk.MaSV = ? AND dk.MaLop = ? "
                + "AND tp.ThuTu = ? AND tp.TrangThai = 1";
        // NgayNhap được cơ sở dữ liệu gán theo giá trị mặc định CURRENT_TIMESTAMP.
        String themDiem = "INSERT INTO DiemThanhPhan (MaDiemTP, MaDangKy, MaTP, DiemDatDuoc) "
                + "SELECT COALESCE(MAX(MaDiemTP), 0) + 1, ?, ?, ? FROM DiemThanhPhan";

        // try-with-resources tự đóng kết nối, câu lệnh và kết quả sau khi sử dụng.
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement timPS = conn.prepareStatement(timThanhPhan)) {
            // Gán tham số vào các dấu ? theo thứ tự trong câu truy vấn tìm kiếm.
            timPS.setInt(1, maSV);
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
                // Chi nhap diem khi xac dinh duoc duy nhat mot thanh phan.
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
        } catch (SQLException e) {
            // In thông tin lỗi để hỗ trợ kiểm tra và báo thao tác thất bại.
            e.printStackTrace();
            return false;
        }
    }
}
