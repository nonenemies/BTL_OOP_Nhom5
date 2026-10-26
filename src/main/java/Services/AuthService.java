package Services;

import Model.Users;
import Util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {

    // Hàm này check DB. Nếu đúng trả về Object người dùng, sai trả về null
    public Users login(String email, String password) {
        String sql = "SELECT * FROM Users WHERE Email = ? AND PasswordHash = ? AND TrangThai = 'HOAT_DONG'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Trả về Object User
                    return new Users(
                            rs.getInt("UserID"),
                            rs.getString("HoTen"),
                            rs.getString("Email"),
                            rs.getString("PasswordHash"),
                            rs.getString("Role"),
                            rs.getString("MaSo"),
                            rs.getString("Khoa"),
                            rs.getDate("NgayTao"),
                            Model.TrangThaiHoatDong.valueOf(rs.getString("TrangThai"))
                    ) {};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Đăng nhập thất bại
    }
}