package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Model.DangKyHoc;
import Util.DBConnection;

public class DangKyHocDAO {

    // 1. HÀM KIỂM TRA TRÙNG LẶP
    // Trả về true nếu sinh viên ĐÃ CÓ TÊN trong danh sách lớp, false nếu chưa có
    public boolean kiemTraDaDangKy(int maSV, String maLop) {
        String sql = "SELECT * FROM DangKyHoc WHERE MaSV = ? AND MaLop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maSV);
            ps.setString(2, maLop);

            ResultSet rs = ps.executeQuery();
            return rs.next(); // Nếu rs.next() có dữ liệu nghĩa là đã đăng ký rồi

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. HÀM THỰC HIỆN ĐĂNG KÝ (INSERT)
    public boolean themDangKyMoi(DangKyHoc dk) {
        // Chú ý: Cột NgayDangKy và TrangThai đã được SQL thiết lập DEFAULT nên không cần INSERT
        String sql = "INSERT INTO DangKyHoc (MaLop, MaSV) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Lấy dữ liệu từ Object nhét vào 2 dấu chấm hỏi (?)
            ps.setString(1, dk.getMaLop());
            ps.setInt(2, dk.getMaSV());

            int ketQua = ps.executeUpdate();
            return ketQua > 0; // Nếu insert thành công, ketQua sẽ = 1 (trả về true)

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ĐÓNG THẾ ĐỢI DEV 4 LÀM XONG ĐIỂM SỐ
    public boolean kiemTraDaQuaMon(int maSV, String maMonHoc) {
        // Giả lập: Sinh viên 3 học cực giỏi, môn nào cũng qua (trả về true)
        if (maSV == 3) {
            return true;
        }
        // Giả lập: Sinh viên 4 học kém, tạch môn Nhập môn C (trả về false)
        if (maSV == 4 && maMonHoc.equals("IT00")) {
            return false;
        }
        return true;
    }

    // HÀM DÙNG ĐỂ HOÁN ĐỔI LỚP (Cập nhật thẳng vào DB)
    public boolean capNhatLopChoSinhVien(int maSV, String lopCu, String lopMoi) {
        String sql = "UPDATE DangKyHoc SET MaLop = ? WHERE MaSV = ? AND MaLop = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lopMoi); // Set giá trị mới
            ps.setInt(2, maSV);
            ps.setString(3, lopCu);  // Tìm đúng cái cũ để đè lên

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}