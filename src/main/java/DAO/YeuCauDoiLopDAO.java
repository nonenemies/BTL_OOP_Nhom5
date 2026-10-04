package DAO;

import java.util.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import Util.DBConnection;
import Model.YeuCauDoiLop;

public class YeuCauDoiLopDAO {

    // 1. HÀM CHO SINH VIÊN (Gửi yêu cầu)
    public boolean themYeuCau(YeuCauDoiLop yc) {
        String sql = "INSERT INTO YeuCauDoiLop (MaSV, LopCu, LopMoi, LyDo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, yc.getMaSV());
            ps.setString(2, yc.getLopCu());
            ps.setString(3, yc.getLopMoi());
            ps.setString(4, yc.getLyDo());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Dùng để Services lấy thông tin thằng A ra để hoán đổi với thằng B
    public YeuCauDoiLop getYeuCauById(int maYeuCau) {
        String sql = "SELECT * FROM YeuCauDoiLop WHERE MaYeuCau = ?";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maYeuCau);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                YeuCauDoiLop yc = new YeuCauDoiLop();
                yc.setMaYeuCau(rs.getInt("MaYeuCau"));
                yc.setMaSV(rs.getInt("MaSV"));
                yc.setLopCu(rs.getString("LopCu"));
                yc.setLopMoi(rs.getString("LopMoi"));
                yc.setLyDo(rs.getString("LyDo"));
                // Chuyển String từ SQL thành Enum trong Java
                yc.setTrangThai(Model.TrangThaiDoiLop.valueOf(rs.getString("TrangThai")));
                return yc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy
    }

    // Lấy tất cả các bài đăng có trạng thái "CHO_XAC_NHAN" đẩy lên UI
    public java.util.List<YeuCauDoiLop> layDanhSachChoXacNhan() {
        java.util.List<YeuCauDoiLop> danhSach = new java.util.ArrayList<>();
        String sql = "SELECT * FROM YeuCauDoiLop WHERE TrangThai = 'CHO_XAC_NHAN'";

        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                YeuCauDoiLop yc = new YeuCauDoiLop();
                yc.setMaYeuCau(rs.getInt("MaYeuCau"));
                yc.setMaSV(rs.getInt("MaSV"));
                yc.setLopCu(rs.getString("LopCu"));
                yc.setLopMoi(rs.getString("LopMoi"));
                yc.setLyDo(rs.getString("LyDo"));
                yc.setTrangThai(Model.TrangThaiDoiLop.valueOf(rs.getString("TrangThai")));
                danhSach.add(yc); // Nhét vào mảng
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}