package DAO;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.YeuCauDoiLop;
import Model.TrangThaiDoiLop;

public class YeuCauDoiLopDAO {

    public boolean themYeuCau(Connection conn, YeuCauDoiLop yc) throws SQLException {
        String sql = "INSERT INTO YeuCauDoiLop (MaSV, LopCu, LopMoi, LyDo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yc.getMaSV());
            ps.setString(2, yc.getLopCu());
            ps.setString(3, yc.getLopMoi());
            ps.setString(4, yc.getLyDo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThaiDuyet(Connection conn, int maYeuCau, String maSVNguoiChot, String trangThai) throws SQLException {
        String sql = "UPDATE YeuCauDoiLop yc "
                + "JOIN SinhVien sv ON sv.MaSV = ? "
                + "SET yc.TrangThai = ?, yc.NguoiXuLy = sv.UserID, yc.NgayXuLy = NOW() "
                + "WHERE yc.MaYeuCau = ? AND yc.TrangThai = 'CHO_XAC_NHAN'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSVNguoiChot);
            ps.setString(2, trangThai);
            ps.setInt(3, maYeuCau);
            return ps.executeUpdate() > 0;
        }
    }

    public YeuCauDoiLop getYeuCauById(Connection conn, int maYeuCau) throws SQLException {
        return docYeuCau(conn, maYeuCau, false);
    }

    /** Chỉ gọi trong transaction; khóa yêu cầu đến lúc commit/rollback. */
    public YeuCauDoiLop getYeuCauByIdForUpdate(Connection conn, int maYeuCau) throws SQLException {
        return docYeuCau(conn, maYeuCau, true);
    }

    private YeuCauDoiLop docYeuCau(Connection conn, int maYeuCau, boolean khoa) throws SQLException {
        String sql = "SELECT * FROM YeuCauDoiLop WHERE MaYeuCau = ?" + (khoa ? " FOR UPDATE" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maYeuCau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    YeuCauDoiLop yc = new YeuCauDoiLop();
                    yc.setMaYeuCau(rs.getInt("MaYeuCau"));
                    yc.setMaSV(rs.getString("MaSV"));
                    yc.setLopCu(rs.getString("LopCu"));
                    yc.setLopMoi(rs.getString("LopMoi"));
                    yc.setLyDo(rs.getString("LyDo"));
                    yc.setTrangThai(TrangThaiDoiLop.valueOf(rs.getString("TrangThai")));
                    return yc;
                }
            }
        }
        return null;
    }

    public List<YeuCauDoiLop> laySanGiaoDich() throws SQLException {
        List<YeuCauDoiLop> list = new ArrayList<>();
        String sql = "SELECT * FROM YeuCauDoiLop WHERE TrangThai = 'CHO_XAC_NHAN'";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                YeuCauDoiLop yc = new YeuCauDoiLop();
                yc.setMaYeuCau(rs.getInt("MaYeuCau"));
                yc.setMaSV(rs.getString("MaSV"));
                yc.setLopCu(rs.getString("LopCu"));
                yc.setLopMoi(rs.getString("LopMoi"));
                yc.setLyDo(rs.getString("LyDo"));
                list.add(yc);
            }
        }
        return list;
    }
}