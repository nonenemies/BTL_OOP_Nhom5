package DAO;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Model.DangKyHoc;
import Model.LopHocThoiKhoaBieu;
import Model.TrangThaiDangKy;

public class DangKyHocDAO {

    /** Khóa dòng sinh viên để các đăng ký đồng thời của cùng một sinh viên được tuần tự hóa. */
    public boolean khoaSinhVienChoDangKy(Connection conn, String maSV) throws SQLException {
        String sql = "SELECT MaSV FROM SinhVien WHERE MaSV = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean coLichHoc(Connection conn, String maLop) throws SQLException {
        String sql = "SELECT 1 FROM LichHoc WHERE MaLop = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Kiểm tra lịch lớp mới có trùng với một lớp đã đăng ký trong cùng học kỳ không.
     * maLopBoQua dùng khi đổi lớp để bỏ lớp mà sinh viên đang rời khỏi thời khóa biểu so sánh.
     */
    public boolean coLopBiTrungLich(Connection conn, String maSV, String maLopMoi,
                                    String maLopBoQua) throws SQLException {
        String boQuaSql = maLopBoQua == null ? "" : "AND lh.MaLop <> ? ";
        String sql = "SELECT 1 "
                + "FROM DangKyHoc dk "
                + "JOIN LopHoc lh ON lh.MaLop = dk.MaLop "
                + "JOIN LichHoc lich ON lich.MaLop = lh.MaLop "
                + "JOIN LopHoc lopMoi ON lopMoi.MaLop = ? "
                + "JOIN LichHoc lichMoi ON lichMoi.MaLop = lopMoi.MaLop "
                + "WHERE dk.MaSV = ? AND dk.TrangThai <> 'HUY' "
                + "AND lh.HocKy = lopMoi.HocKy "
                + boQuaSql
                + "AND lich.Thu = lichMoi.Thu "
                + "AND lich.GioBatDau < lichMoi.GioKetThuc "
                + "AND lich.GioKetThuc > lichMoi.GioBatDau "
                + "ORDER BY lich.Thu, lich.GioBatDau LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLopMoi);
            ps.setString(2, maSV);
            if (maLopBoQua != null) {
                ps.setString(3, maLopBoQua);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Ném SQLException, dùng Connection truyền vào
    public boolean kiemTraDaDangKy(Connection conn, String maSV, String maLop) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc "
                + "WHERE MaSV = ? AND MaLop = ? AND TrangThai <> 'HUY'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public TrangThaiDangKy layTrangThaiDangKyForUpdate(Connection conn, String maSV, String maLop)
            throws SQLException {
        String sql = "SELECT TrangThai FROM DangKyHoc WHERE MaSV = ? AND MaLop = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? TrangThaiDangKy.valueOf(rs.getString("TrangThai")) : null;
            }
        }
    }

    public boolean huyDangKy(Connection conn, String maSV, String maLop) throws SQLException {
        String sql = "UPDATE DangKyHoc SET TrangThai = 'HUY' "
                + "WHERE MaSV = ? AND MaLop = ? AND TrangThai = 'DANG_HOC'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maLop);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean kiemTraDaQuaMon(Connection conn, String maSV, String maMonHoc) throws SQLException {
        String sql = "SELECT 1 FROM DangKyHoc d " +
                "JOIN LopHoc l ON d.MaLop = l.MaLop " +
                "WHERE d.MaSV = ? AND l.MaMon = ? " +
                "AND d.DiemTongKet >= 4.0 AND d.TrangThai = 'DA_HOAN_THANH'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setString(2, maMonHoc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean themDangKyMoi(Connection conn, DangKyHoc dk) throws SQLException {
        String reactivateSql = "UPDATE DangKyHoc SET TrangThai = 'DANG_HOC' "
                + "WHERE MaSV = ? AND MaLop = ? AND TrangThai = 'HUY'";
        try (PreparedStatement ps = conn.prepareStatement(reactivateSql)) {
            ps.setString(1, dk.getMaSV());
            ps.setString(2, dk.getMaLop());
            if (ps.executeUpdate() == 1) {
                return true;
            }
        }

        String sql = "INSERT INTO DangKyHoc (MaLop, MaSV) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dk.getMaLop());
            ps.setString(2, dk.getMaSV());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean capNhatLopChoSinhVien(Connection conn, String maSV, String lopCu, String lopMoi) throws SQLException {
        String sql = "UPDATE DangKyHoc SET MaLop = ? "
                + "WHERE MaSV = ? AND MaLop = ? AND TrangThai <> 'HUY'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lopMoi);
            ps.setString(2, maSV);
            ps.setString(3, lopCu);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy danh sách lớp để in ra Thời khóa biểu cho Sinh viên xem
    public List<DangKyHoc> layDanhSachLopCuaSinhVien(String maSV) throws SQLException {
        List<DangKyHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM DangKyHoc WHERE MaSV = ? AND TrangThai != 'HUY'";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DangKyHoc dk = new DangKyHoc();
                    dk.setMaDangKy(rs.getInt("MaDangKy"));
                    dk.setMaLop(rs.getString("MaLop"));
                    dk.setDiemTongKet(rs.getFloat("DiemTongKet"));
                    dk.setTrangThai(Model.TrangThaiDangKy.valueOf(rs.getString("TrangThai")));
                    list.add(dk);
                }
            }
        }
        return list;
    }

    public List<LopHocThoiKhoaBieu> layLichHocTheoTuan(String maSV) throws SQLException {
        List<LopHocThoiKhoaBieu> lichHoc = new ArrayList<>();
        String sql = "SELECT lh.MaLop, mh.MaMon, mh.TenMon, lh.HocKy, lich.Thu, "
                + "TIME_FORMAT(lich.GioBatDau, '%H:%i') AS GioBatDau, "
                + "TIME_FORMAT(lich.GioKetThuc, '%H:%i') AS GioKetThuc "
                + "FROM DangKyHoc dk "
                + "JOIN LopHoc lh ON lh.MaLop = dk.MaLop "
                + "JOIN MonHoc mh ON mh.MaMon = lh.MaMon "
                + "JOIN LichHoc lich ON lich.MaLop = lh.MaLop "
                + "WHERE dk.MaSV = ? AND dk.TrangThai = 'DANG_HOC' "
                + "ORDER BY lich.Thu, lich.GioBatDau, lich.GioKetThuc, mh.TenMon";
        try (Connection conn = Util.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lichHoc.add(new LopHocThoiKhoaBieu(
                            rs.getString("MaLop"),
                            rs.getString("MaMon"),
                            rs.getString("TenMon"),
                            rs.getString("HocKy"),
                            rs.getInt("Thu"),
                            rs.getString("GioBatDau"),
                            rs.getString("GioKetThuc")));
                }
            }
        }
        return lichHoc;
    }
}