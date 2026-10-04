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
                    "FROM Users u" +
                    "JOIN SinhVien sv" +
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
            try{
                Connection con = DBConnection.getConnection();
                String sql = "INSERT INTO SinhVien " +
                        "(MaSV, NgaySinh, GioiTinh, Lop, KhoaHoc, HeDaoTao) " +
                        "VALUES(?, ?, ?, ?, ?, ?) ";
                PreparedStatement ps = con.prepareStatement(sql);

                ps.setString(1, sinhVien.getMaSinhVien());
                ps.setDate(2, new java.sql.Date(sinhVien.getNgaySinh().getTime()));
                ps.setString(3, sinhVien.getGioiTinh());
                ps.setString(4, sinhVien.getLop());
                ps.setInt(5, sinhVien.getKhoaHoc());
                ps.setString(6, sinhVien.getHeDaoTao());

                ps.executeUpdate();
                ps.close();
                DBConnection.closeConnection(con);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
    }

    @Override
    public void delete(SinhVien sv) {
         try{
             Connection con = DBConnection.getConnection();

             String sql ="DELETE FROM SinhVien " +
                     "WHERE UserID = ?";

             PreparedStatement ps = con.prepareStatement(sql);
             ps.setInt(1, sv.getUserID());
             ps.executeUpdate();
             ps.close();
             DBConnection.closeConnection(con);
         } catch (SQLException e) {
                e.printStackTrace();
         }

    }

    @Override
    public void update(SinhVien sv) {
        try{
            Connection con = DBConnection.getConnection();

            String sql = "UPDATE SinhVien SET " +
                    "maSV = ?, " +
                    "ngaySinh = ?, " +
                    "gioiTinh = ?, " +
                    "lop = ?, " +
                    "khoaHoc = ?, " +
                    "heDaoTao = ? " +
                    "WHERE userID = ? ";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, sv.getMaSinhVien());
            ps.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getLop());
            ps.setInt(5, sv.getKhoaHoc());
            ps.setString(6, sv.getHeDaoTao());
            ps.setInt(7, sv.getUserID());
            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        }
        catch (SQLException e){
            e.printStackTrace();
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
