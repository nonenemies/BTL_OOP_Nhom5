package DAO.dev1;

import Util.DBConnection.DBConnection;
import Model.dev1.SinhVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class SinhVienDAOImpl implements SinhVienDAO{

    public static SinhVienDAOImpl getInstance(){
        return new SinhVienDAOImpl();
    }

    @Override
    public ArrayList<SinhVien> selectAll() {
        ArrayList<SinhVien> result = new ArrayList<>();
        try{
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * " +
                    "FROM Users u " +
                    "JOIN SinhVien sv " +
                    "ON u.userID = sv.userID";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Integer id = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maSinhVien = rs.getString("MaSV");
                Date ngaySinh = rs.getDate("NgaySinh");
                String gioiTinh = rs.getString("GioiTinh");
                String lop = rs.getString("Lop");
                Integer khoaHoc = rs.getInt("KhoaHoc");
                String heDaoTao = rs.getString("HeDaoTao");

                SinhVien sv = new SinhVien(id, hoTen, email, khoa, maSinhVien, ngaySinh, gioiTinh, lop, khoaHoc, heDaoTao);
                result.add(sv);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SinhVien selectByID(int id) {
        SinhVien sv = null;
        try{
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * " +
                    "FROM Users u " +
                    "JOIN SinhVien sv " +
                    "ON u.userID = sv.userID " +
                    "WHERE sv.userID = ? ";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maSinhVien = rs.getString("MaSV");
                Date ngaySinh = rs.getDate("NgaySinh");
                String gioiTinh = rs.getString("GioiTinh");
                String lop = rs.getString("Lop");
                Integer khoaHoc = rs.getInt("KhoaHoc");
                String heDaoTao = rs.getString("HeDaoTao");

                sv = new SinhVien(userID, hoTen, email, khoa, maSinhVien, ngaySinh, gioiTinh, lop, khoaHoc, heDaoTao);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return sv;
    }


    @Override
    public void insert(SinhVien sinhVien) {
        try (Connection con = DBConnection.getConnection()) {
            insert(con, sinhVien);
        } catch (SQLException e) {
            throw new RuntimeException("Loi them SinhVien", e);
        }
    }

    // INSERT dùng chung Connection cho Transaction
    public void insert(Connection con, SinhVien sinhVien)
            throws SQLException {

        String sql =
                "INSERT INTO SinhVien " +
                        "(UserID, MaSV, NgaySinh, GioiTinh, Lop, KhoaHoc, HeDaoTao) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sinhVien.getUserID());
            ps.setString(2, sinhVien.getMaSinhVien());

            if (sinhVien.getNgaySinh() != null) {
                ps.setDate(3, new java.sql.Date(
                        sinhVien.getNgaySinh().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }

            ps.setString(4, sinhVien.getGioiTinh());
            ps.setString(5, sinhVien.getLop());

            if (sinhVien.getKhoaHoc() != null) {
                ps.setInt(6, sinhVien.getKhoaHoc());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            ps.setString(7, sinhVien.getHeDaoTao());

            int rows = ps.executeUpdate();

            if (rows != 1) {
                throw new SQLException("Them SinhVien that bai");
            }
        }
    }



    @Override
    public void update(SinhVien sv) {
        if (sv == null || sv.getUserID() == null) {
            throw new IllegalArgumentException("Thieu UserID");
        }

        String sql =
                "UPDATE Users u " +
                        "JOIN SinhVien s ON u.UserID = s.UserID " +
                        "SET u.HoTen = ?, u.Email = ?, u.Khoa = ?, " +
                        "s.MaSV = ?, s.NgaySinh = ?, s.GioiTinh = ?, " +
                        "s.Lop = ?, s.KhoaHoc = ?, s.HeDaoTao = ? " +
                        "WHERE u.UserID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sv.getHoTen());
            ps.setString(2, sv.getEmail());
            ps.setString(3, sv.getKhoa());
            ps.setString(4, sv.getMaSinhVien());

            if (sv.getNgaySinh() != null) {
                ps.setDate(5, new java.sql.Date(sv.getNgaySinh().getTime()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setString(6, sv.getGioiTinh());
            ps.setString(7, sv.getLop());

            if (sv.getKhoaHoc() != null) {
                ps.setInt(8, sv.getKhoaHoc());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.setString(9, sv.getHeDaoTao());
            ps.setInt(10, sv.getUserID());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat sinh vien", e);
        }
    }

    @Override
    public void delete(SinhVien sv) {
        if (sv == null || sv.getUserID() == null) {
            throw new IllegalArgumentException("Thieu UserID");
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String sql1 = "DELETE FROM SinhVien WHERE UserID = ?";

                try (PreparedStatement ps = con.prepareStatement(sql1)) {
                    ps.setInt(1, sv.getUserID());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Khong tim thay sinh vien");
                    }
                }

                String sql2 = "DELETE FROM Users WHERE UserID = ?";

                try (PreparedStatement ps = con.prepareStatement(sql2)) {
                    ps.setInt(1, sv.getUserID());

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Khong tim thay tai khoan");
                    }
                }

                con.commit();

            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa sinh vien", e);
        }
    }


    @Override
    public SinhVien selectByMaSV(String maSV) {

        SinhVien sinhVien = null;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "sv.MaSV, sv.NgaySinh, sv.GioiTinh, " +
                            "sv.Lop, sv.KhoaHoc, sv.HeDaoTao " +
                            "FROM Users u " +
                            "JOIN SinhVien sv ON u.UserID = sv.UserID " +
                            "WHERE sv.MaSV = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, maSV);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");

                String maSinhVien = rs.getString("MaSV");
                Date ngaySinh = rs.getDate("NgaySinh");

                String gioiTinh = rs.getString("GioiTinh");

                String lop = rs.getString("Lop");
                Integer khoaHoc = rs.getInt("KhoaHoc");
                String heDaoTao = rs.getString("HeDaoTao");

                sinhVien = new SinhVien(userID, hoTen, email, khoa, maSinhVien, ngaySinh, gioiTinh, lop, khoaHoc, heDaoTao);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sinhVien;
    }
    @Override
    public ArrayList<SinhVien> selectByLop(String lop) {

        ArrayList<SinhVien> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "sv.MaSV, sv.NgaySinh, sv.GioiTinh, " +
                            "sv.Lop, sv.KhoaHoc, sv.HeDaoTao " +
                            "FROM Users u " +
                            "JOIN SinhVien sv ON u.UserID = sv.UserID " +
                            "WHERE sv.Lop = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, lop);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maSinhVien = rs.getString("MaSV");
                Date ngaySinh = rs.getDate("NgaySinh");
                String gioiTinh = rs.getString("GioiTinh");
                String lopSV = rs.getString("Lop");
                Integer khoaHoc = rs.getInt("KhoaHoc");
                String heDaoTao = rs.getString("HeDaoTao");

                SinhVien sv = new SinhVien(userID, hoTen, email, khoa, maSinhVien, ngaySinh, gioiTinh, lopSV, khoaHoc, heDaoTao);

                result.add(sv);
            }
            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    @Override
    public ArrayList<SinhVien> selectByKhoaHoc(int khoaHoc) {

        ArrayList<SinhVien> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT u.UserID, u.HoTen, u.Email, u.Khoa, " +
                            "sv.MaSV, sv.NgaySinh, sv.GioiTinh, " +
                            "sv.Lop, sv.KhoaHoc, sv.HeDaoTao " +
                            "FROM Users u " +
                            "JOIN SinhVien sv ON u.UserID = sv.UserID " +
                            "WHERE sv.KhoaHoc = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, khoaHoc);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String khoa = rs.getString("Khoa");
                String maSinhVien = rs.getString("MaSV");
                Date ngaySinh = rs.getDate("NgaySinh");
                String gioiTinh = rs.getString("GioiTinh");
                String lop = rs.getString("Lop");
                Integer khoaHocSV = rs.getInt("KhoaHoc");
                String heDaoTao = rs.getString("HeDaoTao");

                SinhVien sv = new SinhVien(userID, hoTen, email, khoa, maSinhVien, ngaySinh, gioiTinh, lop, khoaHocSV, heDaoTao);

                result.add(sv);
            }
            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
