package DAO.dev1;

import Util.DBConnection.DBConnection;
import Model.dev1.GiangVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GiangVienDAOImpl implements GiangVienDAO {

    public static GiangVienDAOImpl getInstance() {
        return new GiangVienDAOImpl();
    }

    // 1. Lấy danh sách tất cả giảng viên
    @Override
    public ArrayList<GiangVien> selectAll() {
        ArrayList<GiangVien> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "gv.MaGV, gv.ChuyenNganh, gv.HocVi " +
                            "FROM Users u " +
                            "JOIN GiaoVien gv ON u.UserID = gv.UserID";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maGV = rs.getString("MaGV");
                String chuyenNganh = rs.getString("ChuyenNganh");
                String hocVi = rs.getString("HocVi");

                GiangVien gv = new GiangVien(
                        userID, hoTen, email, khoa,
                        maGV, chuyenNganh, hocVi
                );

                result.add(gv);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 2. Tìm giảng viên theo UserID
    @Override
    public GiangVien selectByID(int id) {
        GiangVien giangVien = null;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "gv.MaGV, gv.ChuyenNganh, gv.HocVi " +
                            "FROM Users u " +
                            "JOIN GiaoVien gv ON u.UserID = gv.UserID " +
                            "WHERE gv.UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maGV = rs.getString("MaGV");
                String chuyenNganh = rs.getString("ChuyenNganh");
                String hocVi = rs.getString("HocVi");

                giangVien = new GiangVien(
                        userID, hoTen, email, khoa,
                        maGV, chuyenNganh, hocVi
                );
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return giangVien;
    }


    @Override
    public void insert(GiangVien giangVien) {
        try (Connection con = DBConnection.getConnection()) {
            insert(con, giangVien);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them GiaoVien", e);
        }
    }

    // INSERT dùng chung Connection cho Transaction
    public void insert(Connection con, GiangVien giangVien)
            throws SQLException {

        String sql =
                "INSERT INTO GiaoVien " +
                        "(UserID, MaGV, ChuyenNganh, HocVi) " +
                        "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, giangVien.getUserID());
            ps.setString(2, giangVien.getMaGiangVien());
            ps.setString(3, giangVien.getChuyenNganh());
            ps.setString(4, giangVien.getHocVi());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Them GiaoVien that bai");
            }
        }
    }


    // 4. Xóa thông tin giảng viên
    @Override
    public void delete(GiangVien giangVien) {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "DELETE FROM GiaoVien " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, giangVien.getUserID());

            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. Cập nhật thông tin giảng viên
    @Override
    public void update(GiangVien giangVien) {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "UPDATE GiaoVien SET " +
                            "MaGV = ?, " +
                            "ChuyenNganh = ?, " +
                            "HocVi = ? " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, giangVien.getMaGiangVien());
            ps.setString(2, giangVien.getChuyenNganh());
            ps.setString(3, giangVien.getHocVi());
            ps.setInt(4, giangVien.getUserID());

            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 6. Tìm giảng viên theo tên
    @Override
    public ArrayList<GiangVien> selectByName(String name) {
        ArrayList<GiangVien> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "gv.MaGV, gv.ChuyenNganh, gv.HocVi " +
                            "FROM Users u " +
                            "JOIN GiaoVien gv ON u.UserID = gv.UserID " +
                            "WHERE u.HoTen LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maGV = rs.getString("MaGV");
                String chuyenNganh = rs.getString("ChuyenNganh");
                String hocVi = rs.getString("HocVi");

                GiangVien gv = new GiangVien(
                        userID, hoTen, email, khoa,
                        maGV, chuyenNganh, hocVi
                );

                result.add(gv);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 7. Tìm giảng viên theo khoa
    @Override
    public ArrayList<GiangVien> selectByKhoa(String khoa) {
        ArrayList<GiangVien> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "gv.MaGV, gv.ChuyenNganh, gv.HocVi " +
                            "FROM Users u " +
                            "JOIN GiaoVien gv ON u.UserID = gv.UserID " +
                            "WHERE u.Khoa = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, khoa);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoaGV = rs.getString("Khoa");
                String maGV = rs.getString("MaGV");
                String chuyenNganh = rs.getString("ChuyenNganh");
                String hocVi = rs.getString("HocVi");

                GiangVien gv = new GiangVien(
                        userID, hoTen, email, khoaGV,
                        maGV, chuyenNganh, hocVi
                );

                result.add(gv);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // 8. Đếm tổng số giảng viên
    @Override
    public int countGiangVien() {
        int count = 0;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT COUNT(*) AS TongGV " +
                            "FROM GiaoVien";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("TongGV");
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    // 9. Kiểm tra giảng viên tồn tại theo UserID
    @Override
    public boolean existsByID(int id) {
        boolean exists = false;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT UserID FROM GiaoVien " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exists = true;
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }
}
