package DAO;

import Model.LopHoc;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Các truy vấn dữ liệu và thao tác sĩ số cho bảng LopHoc. */
public class LopHocDAO {

    /** Tạo lớp mới; sĩ số hiện tại bắt đầu bằng 0 theo DEFAULT của database. */
    public boolean themLopHoc(LopHoc lopHoc) throws SQLException {
        Objects.requireNonNull(lopHoc, "lopHoc không được null");
        String sql = "INSERT INTO LopHoc (MaLop, MaMon, MaGV, HocKy, SiSoToiDa) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lopHoc.getMaLop());
            ps.setString(2, lopHoc.getMaMon());
            ps.setObject(3, lopHoc.getMaGV());
            ps.setString(4, lopHoc.getHocKy());
            ps.setObject(5, lopHoc.getSiSoToiDa());
            return ps.executeUpdate() == 1;
        }
    }

    /** Sửa thông tin lớp; không ghi đè sĩ số hiện tại do dữ liệu có thể đã thay đổi. */
    public boolean suaLopHoc(LopHoc lopHoc) throws SQLException {
        Objects.requireNonNull(lopHoc, "lopHoc không được null");
        String sql = "UPDATE LopHoc SET MaMon = ?, MaGV = ?, HocKy = ?, SiSoToiDa = ? "
                + "WHERE MaLop = ? AND SiSoToiDa >= SiSoHienTai";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lopHoc.getMaMon());
            ps.setObject(2, lopHoc.getMaGV());
            ps.setString(3, lopHoc.getHocKy());
            ps.setObject(4, lopHoc.getSiSoToiDa());
            ps.setString(5, lopHoc.getMaLop());
            return ps.executeUpdate() == 1;
        }
    }

    /** Ngừng kích hoạt lớp để giữ lại dữ liệu đăng ký và điểm đã phát sinh. */
    public boolean xoaLopHoc(String maLop) throws SQLException {
        String sql = "UPDATE LopHoc SET TrangThai = 0 WHERE MaLop = ? AND TrangThai <> 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() == 1;
        }
    }

    /** Lấy các lớp đang hoạt động. */
    public List<LopHoc> layDanhSachLop() throws SQLException {
        return truyVanDanhSach("SELECT MaLop, MaMon, MaGV, HocKy, SiSoToiDa, SiSoHienTai, TrangThai "
                + "FROM LopHoc WHERE TrangThai = 1 ORDER BY MaLop", null);
    }

    /** Lấy mọi lớp, gồm cả lớp đã ngừng hoạt động, cho màn hình quản trị. */
    public List<LopHoc> layTatCaLopHoc() throws SQLException {
        return truyVanDanhSach("SELECT MaLop, MaMon, MaGV, HocKy, SiSoToiDa, SiSoHienTai, TrangThai "
                + "FROM LopHoc ORDER BY MaLop", null);
    }

    /** Tìm lớp theo mã, gồm cả lớp đã ngừng hoạt động. */
    public LopHoc layLopTheoMa(String maLop) throws SQLException {
        String sql = "SELECT MaLop, MaMon, MaGV, HocKy, SiSoToiDa, SiSoHienTai, TrangThai "
                + "FROM LopHoc WHERE MaLop = ?";
        List<LopHoc> ketQua = truyVanDanhSach(sql, maLop);
        return ketQua.isEmpty() ? null : ketQua.get(0);
    }

    /** Lấy các lớp đang hoạt động của một môn. */
    public List<LopHoc> layLopTheoMon(String maMon) throws SQLException {
        String sql = "SELECT MaLop, MaMon, MaGV, HocKy, SiSoToiDa, SiSoHienTai, TrangThai "
                + "FROM LopHoc WHERE MaMon = ? AND TrangThai = 1 ORDER BY MaLop";
        return truyVanDanhSach(sql, maMon);
    }

    /** Trả về true nếu lớp đang hoạt động và còn chỗ. */
    public boolean kiemTraConCho(String maLop) throws SQLException {
        String sql = "SELECT 1 FROM LopHoc WHERE MaLop = ? AND TrangThai = 1 "
                + "AND SiSoHienTai < SiSoToiDa";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Tăng sĩ số nguyên tử nếu lớp còn hoạt động và chưa đầy. */
    public boolean tangSiSo(String maLop) throws SQLException {
        String sql = "UPDATE LopHoc SET SiSoHienTai = SiSoHienTai + 1 "
                + "WHERE MaLop = ? AND TrangThai = 1 AND SiSoHienTai < SiSoToiDa";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() == 1;
        }
    }

    /** Giảm sĩ số nguyên tử nếu sĩ số hiện tại lớn hơn 0. */
    public boolean giamSiSo(String maLop) throws SQLException {
        String sql = "UPDATE LopHoc SET SiSoHienTai = SiSoHienTai - 1 "
                + "WHERE MaLop = ? AND SiSoHienTai > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() == 1;
        }
    }

    private List<LopHoc> truyVanDanhSach(String sql, String thamSoMaMonHoacLop) throws SQLException {
        List<LopHoc> danhSach = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (thamSoMaMonHoacLop != null) {
                ps.setString(1, thamSoMaMonHoacLop);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(new LopHoc(
                            rs.getString("MaLop"),
                            rs.getString("MaMon"),
                            rs.getObject("MaGV", Integer.class),
                            rs.getString("HocKy"),
                            rs.getObject("SiSoToiDa", Integer.class),
                            rs.getObject("SiSoHienTai", Integer.class),
                            rs.getObject("TrangThai", Integer.class)
                    ));
                }
            }
        }
        return danhSach;
    }

    /** Chạy thử truy vấn danh sách lớp theo yêu cầu kiểm tra console ở tuần 1. */
    public static void main(String[] args) {
        try {
            for (LopHoc lopHoc : new LopHocDAO().layDanhSachLop()) {
                System.out.println(lopHoc.getMaLop() + " | " + lopHoc.getMaMon()
                        + " | " + lopHoc.getSiSoHienTai() + "/" + lopHoc.getSiSoToiDa());
            }
        } catch (SQLException e) {
            System.err.println("Không đọc được danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
