
package DAO.dev1;

import Util.DBConnection.DBConnection;
import Model.dev1.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminDAOImpl implements AdminDAO {

    public static AdminDAOImpl getInstance() {
        return new AdminDAOImpl();
    }

    // 1. Lấy danh sách tất cả Admin
    @Override
    public ArrayList<Admin> selectAll() {
        ArrayList<Admin> result = new ArrayList<>();

        String sql =
                "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                        "a.ChucVu " +
                        "FROM Users u " +
                        "JOIN Admin a ON u.UserID = a.UserID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String chucVu = rs.getString("ChucVu");

                Admin admin = new Admin(userID, chucVu);
                admin.setHoTen(hoTen);
                admin.setEmail(email);
                admin.setKhoa(khoa);

                result.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 2. Tìm Admin theo UserID
    @Override
    public Admin selectByID(int id) {
        Admin admin = null;

        String sql =
                "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                        "a.ChucVu " +
                        "FROM Users u " +
                        "JOIN Admin a ON u.UserID = a.UserID " +
                        "WHERE a.UserID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer userID = rs.getInt("UserID");
                    String hoTen = rs.getString("HoTen");
                    String email = rs.getString("Email");
                    String khoa = rs.getString("Khoa");
                    String chucVu = rs.getString("ChucVu");

                    admin = new Admin(userID, chucVu);
                    admin.setHoTen(hoTen);
                    admin.setEmail(email);
                    admin.setKhoa(khoa);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admin;
    }

    // 3. Thêm thông tin Admin
    @Override
    public void insert(Admin admin) {

        String sql =
                "INSERT INTO Admin (UserID, ChucVu) " +
                        "VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, admin.getUserID());
            ps.setString(2, admin.getChucVu());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 4. Xóa thông tin Admin
    @Override
    public void delete(Admin admin) {

        String sql =
                "DELETE FROM Admin " +
                        "WHERE UserID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, admin.getUserID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Cập nhật thông tin Admin
    @Override
    public void update(Admin admin) {

        String sql =
                "UPDATE Admin SET " +
                        "ChucVu = ? " +
                        "WHERE UserID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getChucVu());
            ps.setInt(2, admin.getUserID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
