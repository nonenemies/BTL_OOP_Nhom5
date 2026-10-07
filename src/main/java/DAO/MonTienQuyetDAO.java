package DAO;

import Model.MonHoc;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Các truy vấn cho quan hệ môn học - môn tiên quyết. */
public class MonTienQuyetDAO {

    /** Ghi nhận môn tiên quyết của một môn học. */
    public boolean themMonTienQuyet(String maMon, String maMonTienQuyet) throws SQLException {
        if (maMon == null || maMonTienQuyet == null) {
            throw new IllegalArgumentException("Mã môn không được null");
        }
        if (maMon.equals(maMonTienQuyet)) {
            throw new IllegalArgumentException("Một môn không thể là tiên quyết của chính nó");
        }

        String sql = "INSERT INTO MonTienQuyet (MaMon, MaMonTienQuyet) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            ps.setString(2, maMonTienQuyet);
            return ps.executeUpdate() == 1;
        }
    }

    /** Xóa một quan hệ tiên quyết cụ thể. */
    public boolean xoaMonTienQuyet(String maMon, String maMonTienQuyet) throws SQLException {
        String sql = "DELETE FROM MonTienQuyet WHERE MaMon = ? AND MaMonTienQuyet = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            ps.setString(2, maMonTienQuyet);
            return ps.executeUpdate() == 1;
        }
    }

    /** Lấy danh sách môn phải học trước một môn. */
    public List<MonHoc> layDanhSachMonTienQuyet(String maMon) throws SQLException {
        String sql = "SELECT mh.MaMon, mh.TenMon, mh.SoTinChi, mh.LoaiMon, mh.MoTa, mh.TrangThai "
                + "FROM MonTienQuyet mtq JOIN MonHoc mh ON mh.MaMon = mtq.MaMonTienQuyet "
                + "WHERE mtq.MaMon = ? ORDER BY mh.MaMon";
        List<MonHoc> danhSach = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maMon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new MonHoc(
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getObject("SoTinChi", Integer.class),
                            rs.getString("LoaiMon"),
                            rs.getString("MoTa"),
                            rs.getObject("TrangThai", Integer.class)
                    ));
                }
            }
        }
        return danhSach;
    }

    /** Truy vấn mặc định môn IT01; truyền mã môn khác qua tham số chương trình. */
    public static void main(String[] args) {
        String maMon = args.length > 0 ? args[0] : "IT01";
        try {
            List<MonHoc> danhSach = new MonTienQuyetDAO().layDanhSachMonTienQuyet(maMon);
            System.out.println("Môn tiên quyết của " + maMon + ":");
            for (MonHoc monHoc : danhSach) {
                System.out.println(monHoc.getMaMon() + " | " + monHoc.getTenMon());
            }
        } catch (SQLException e) {
            System.err.println("Không đọc được môn tiên quyết: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
