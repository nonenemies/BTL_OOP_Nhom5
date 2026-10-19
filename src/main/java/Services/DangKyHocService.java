package Services;

import java.sql.Connection;
import java.sql.SQLException;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
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

            if (dangKyDAO.kiemTraDaDangKy(conn, maSV, maLop)) {
                return "❌ Bạn đã đăng ký lớp " + maLop + " rồi!";
            }

            // Gọi Dev 2: Vừa kiểm tra vừa Tăng sĩ số
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
            return "❌ Lỗi cơ sở dữ liệu!";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}