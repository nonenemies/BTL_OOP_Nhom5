package Services;

import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonTienQuyetDAO;
import Model.MonHoc;
import Model.DangKyHoc;
import Util.DBConnection;

public class DangKyHocService {
    private final DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private final LopHocDAO lopHocDAO = new LopHocDAO();

    public String xuLyDangKyHoc(String maSV, String maLop) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try {
                // Khóa trước khi đọc để lượt đăng ký tiếp theo thấy dữ liệu mới nhất.
                var lopHoc = lopHocDAO.layLopTheoMaForUpdate(conn, maLop);
                if (lopHoc == null) {
                    return rollbackAndReturn(conn, "❌ Lớp học không tồn tại!");
                }
                if (dangKyDAO.kiemTraDaDangKy(conn, maSV, maLop)) {
                    return rollbackAndReturn(conn, "❌ Bạn đã đăng ký lớp " + maLop + " rồi!");
                }
                for (MonHoc mon : MonTienQuyetDAO.layDanhSachMonTienQuyet(conn, lopHoc.getMaMon())) {
                    if (!dangKyDAO.kiemTraDaQuaMon(conn, maSV, mon.getMaMon())) {
                        return rollbackAndReturn(conn, "❌ Chưa qua môn tiên quyết: " + mon.getTenMon());
                    }
                }

                // Kiểm tra sức chứa và tăng sĩ số trong cùng transaction.
                if (!lopHocDAO.tangSiSo(conn, maLop)) {
                    return rollbackAndReturn(conn, "❌ Đăng ký thất bại: Lớp đã đầy hoặc đã bị khóa!");
                }

                DangKyHoc dk = new DangKyHoc(maLop, maSV);
                if (!dangKyDAO.themDangKyMoi(conn, dk)) {
                    return rollbackAndReturn(conn, "❌ Lỗi hệ thống khi lưu đăng ký!");
                }

                conn.commit(); // Thành công 100% mới lưu
                return "✅ Đăng ký thành công lớp " + maLop;

            } catch (SQLException e) {
                boolean rollbackFailed = false;
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                    rollbackFailed = true;
                }
                if (!rollbackFailed && e.getErrorCode() == 1062) {
                    return "❌ Bạn đã đăng ký lớp " + maLop + " rồi!";
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể xử lý đăng ký học.", e);
        }
    }

    public List<DangKyHoc> xemThoiKhoaBieu(String maSV) {
        try {
            return dangKyDAO.layDanhSachLopCuaSinhVien(maSV);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải thời khóa biểu.", e);
        }
    }

    private String rollbackAndReturn(Connection conn, String result) throws SQLException {
        conn.rollback();
        return result;
    }
}