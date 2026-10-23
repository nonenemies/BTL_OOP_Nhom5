package Services;

import java.sql.Connection;
import java.sql.SQLException;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonTienQuyetDAO;
import Model.MonHoc;
import Model.DangKyHoc;
import Util.DBConnection;

public class DangKyHocService {
    private DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private LopHocDAO lopHocDAO = new LopHocDAO();

    public String xuLyDangKyHoc(int maSV, String maLop) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // Khóa trước khi đọc để lượt đăng ký tiếp theo thấy dữ liệu mới nhất.
            var lopHoc = lopHocDAO.layLopTheoMaForUpdate(conn, maLop);
            if (lopHoc == null) {
                return "❌ Lớp học không tồn tại!";
            }
            if (dangKyDAO.kiemTraDaDangKy(conn, maSV, maLop)) {
                return "❌ Bạn đã đăng ký lớp " + maLop + " rồi!";
            }
            for (MonHoc mon : MonTienQuyetDAO.layDanhSachMonTienQuyet(conn, lopHoc.getMaMon())) {
                if (!dangKyDAO.kiemTraDaQuaMon(conn, maSV, mon.getMaMon())) {
                    return "❌ Chưa qua môn tiên quyết: " + mon.getTenMon();
                }
            }

            // Kiểm tra sức chứa và tăng sĩ số trong cùng transaction.
            if (!lopHocDAO.tangSiSo(conn, maLop)) {
                conn.rollback();
                return "❌ Đăng ký thất bại: Lớp đã đầy hoặc đã bị khóa!";
            }

            DangKyHoc dk = new DangKyHoc(maLop, maSV);
            if (!dangKyDAO.themDangKyMoi(conn, dk)) {
                conn.rollback();
                return "❌ Lỗi hệ thống khi lưu đăng ký!";
            }

            conn.commit(); // Thành công 100% mới lưu
            return "✅ Đăng ký thành công lớp " + maLop;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            if (e.getErrorCode() == 1062) {
                return "❌ Bạn đã đăng ký lớp " + maLop + " rồi!";
            }
            return "❌ Lỗi cơ sở dữ liệu!";
        } finally {
            if (conn != null) {
                try {
                    // Kết thúc cả những nhánh return sớm, không tự commit khi dọn dẹp.
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
                try {
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public java.util.List<DangKyHoc> xemThoiKhoaBieu(int maSV) {
        return dangKyDAO.layDanhSachLopCuaSinhVien(maSV);
    }
}