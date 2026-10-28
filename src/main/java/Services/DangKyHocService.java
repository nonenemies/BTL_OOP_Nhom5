package Services;

import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonTienQuyetDAO;
import Model.MonHoc;
import Model.DangKyHoc;
import Model.LopHocDangKy;
import Model.LopHocThoiKhoaBieu;
import Model.TrangThaiDangKy;
import Util.DBConnection;

public class DangKyHocService {
    private final DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private final LopHocDAO lopHocDAO = new LopHocDAO();

    public String xuLyDangKyHoc(String maSV, String maLop) {
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()) {
            return "❌ Mã sinh viên hoặc mã lớp không hợp lệ!";
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try {
                if (!dangKyDAO.khoaSinhVienChoDangKy(conn, maSV)) {
                    return rollbackAndReturn(conn, "❌ Không tìm thấy sinh viên!");
                }
                // Khóa trước khi đọc để lượt đăng ký tiếp theo thấy dữ liệu mới nhất.
                var lopHoc = lopHocDAO.layLopTheoMaForUpdate(conn, maLop);
                if (lopHoc == null) {
                    return rollbackAndReturn(conn, "❌ Lớp học không tồn tại!");
                }
                if (dangKyDAO.kiemTraDaDangKy(conn, maSV, maLop)) {
                    return rollbackAndReturn(conn, "❌ Bạn đã đăng ký lớp " + maLop + " rồi!");
                }
                if (!dangKyDAO.coLichHoc(conn, maLop)) {
                    return rollbackAndReturn(conn, "❌ Lớp học chưa được xếp lịch.");
                }
                String lopTrungLich = dangKyDAO.timLopBiTrungLich(conn, maSV, maLop, null);
                if (lopTrungLich != null) {
                    return rollbackAndReturn(conn, "❌ Không thể đăng ký: " + lopTrungLich);
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
            } catch (RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể xử lý đăng ký học.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể xử lý đăng ký học.", e);
        }
    }

    public String huyDangKyHoc(String maSV, String maLop) {
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()) {
            return "❌ Mã sinh viên hoặc mã lớp không hợp lệ!";
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!dangKyDAO.khoaSinhVienChoDangKy(conn, maSV)) {
                    return rollbackAndReturn(conn, "❌ Không tìm thấy sinh viên!");
                }
                if (lopHocDAO.layLopTheoMaForUpdate(conn, maLop) == null) {
                    return rollbackAndReturn(conn, "❌ Không tìm thấy lớp học!");
                }

                TrangThaiDangKy trangThai = dangKyDAO.layTrangThaiDangKyForUpdate(conn, maSV, maLop);
                if (trangThai == null || trangThai == TrangThaiDangKy.HUY) {
                    return rollbackAndReturn(conn, "❌ Lớp này không còn trong danh sách đăng ký.");
                }
                if (trangThai == TrangThaiDangKy.DA_HOAN_THANH) {
                    return rollbackAndReturn(conn, "❌ Không thể hủy lớp đã hoàn thành.");
                }
                if (!dangKyDAO.huyDangKy(conn, maSV, maLop)) {
                    return rollbackAndReturn(conn, "❌ Đăng ký đã thay đổi, vui lòng tải lại danh sách.");
                }
                if (!lopHocDAO.giamSiSo(conn, maLop)) {
                    return rollbackAndReturn(conn,
                            "❌ Không thể cập nhật sĩ số lớp; thao tác hủy đã được hoàn tác.");
                }

                conn.commit();
                return "✅ Đã hủy đăng ký lớp " + maLop + ".";
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể hủy đăng ký lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể hủy đăng ký lớp.", e);
        }
    }

    public List<DangKyHoc> xemThoiKhoaBieu(String maSV) {
        try {
            return dangKyDAO.layDanhSachLopCuaSinhVien(maSV);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải thời khóa biểu.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải thời khóa biểu.", e);
        }
    }

    public List<LopHocThoiKhoaBieu> xemLichHocTheoTuan(String maSV) {
        if (maSV == null || maSV.isBlank()) {
            throw new IllegalArgumentException("Mã sinh viên không hợp lệ.");
        }
        try {
            return dangKyDAO.layLichHocTheoTuan(maSV);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải thời khóa biểu.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải thời khóa biểu.", e);
        }
    }

    public List<LopHocDangKy> xemDanhSachLopDangKy(String maSV) {
        try {
            return lopHocDAO.layDanhSachLopDangKy(maSV);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách lớp đăng ký.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải danh sách lớp đăng ký.", e);
        }
    }

    private String rollbackAndReturn(Connection conn, String result) throws SQLException {
        conn.rollback();
        return result;
    }
}