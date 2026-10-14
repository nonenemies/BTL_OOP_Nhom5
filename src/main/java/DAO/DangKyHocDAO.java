package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.DangKyHoc;

public class DangKyHocDAO {

    // Ném SQLException, dùng Connection truyền vào
    public boolean kiemTraDaDangKy(Connection conn, int maSV, String maLop) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc WHERE MaSV = ? AND MaLop = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSV);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean kiemTraDaQuaMon(Connection conn, int maSV, String maMonHoc) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc d " +
                "JOIN LopHoc l ON d.MaLop = l.MaLop " +
                "WHERE d.MaSV = ? AND l.MaMon = ? " +
                "AND d.DiemTongKet >= 4.0 AND d.TrangThai = 'DA_HOAN_THANH'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maSV);
            ps.setString(2, maMonHoc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean themDangKyMoi(Connection conn, DangKyHoc dk) throws SQLException {
        String sql = "INSERT INTO DangKyHoc (MaLop, MaSV) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dk.getMaLop());
            ps.setInt(2, dk.getMaSV());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatLopChoSinhVien(Connection conn, int maSV, String lopCu, String lopMoi) throws SQLException {
        String sql = "UPDATE DangKyHoc SET MaLop = ? WHERE MaSV = ? AND MaLop = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lopMoi);
            ps.setInt(2, maSV);
            ps.setString(3, lopCu);
            return ps.executeUpdate() > 0;
        }
    }
}