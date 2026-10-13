package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Model.YeuCauDoiLop;
import Model.TrangThaiDoiLop;

public class YeuCauDoiLopDAO {

    public boolean themYeuCau(YeuCauDoiLop yc) {
        String sql = "INSERT INTO YeuCauDoiLop (MaSV, LopCu, LopMoi, LyDo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Util.DBConnection.getConnection();
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

    public boolean capNhatTrangThaiDuyet(int maYeuCau, int nguoiChotDonId, String trangThai) {
        String sql = "UPDATE YeuCauDoiLop SET TrangThai = ?, NguoiXuLy = ?, NgayXuLy = NOW() WHERE MaYeuCau = ?";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, nguoiChotDonId);
            ps.setInt(3, maYeuCau);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
                yc.setTrangThai(TrangThaiDoiLop.valueOf(rs.getString("TrangThai")));
                return yc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}