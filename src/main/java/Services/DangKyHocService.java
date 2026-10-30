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

    public boolean xuLyDangKyHoc(String maSV, String maLop) {
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()) {
            return false;
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try {
                if (!dangKyDAO.khoaSinhVienChoDangKy(conn, maSV)) {
                    return rollbackAndReturn(conn, false);
                }
                // Khóa trước khi đọc để lượt đăng ký tiếp theo thấy dữ liệu mới nhất.
                var lopHoc = lopHocDAO.layLopTheoMaForUpdate(conn, maLop);
                if (lopHoc == null) {
                    return rollbackAndReturn(conn, false);
                }
                if (dangKyDAO.kiemTraDaDangKy(conn, maSV, maLop)) {
                    return rollbackAndReturn(conn, false);
                }
                if (!dangKyDAO.coLichHoc(conn, maLop)) {
                    return rollbackAndReturn(conn, false);
                }
                if (dangKyDAO.coLopBiTrungLich(conn, maSV, maLop, null)) {
                    return rollbackAndReturn(conn, false);
                }
                for (MonHoc mon : MonTienQuyetDAO.layDanhSachMonTienQuyet(conn, lopHoc.getMaMon())) {
                    if (!dangKyDAO.kiemTraDaQuaMon(conn, maSV, mon.getMaMon())) {
                        return rollbackAndReturn(conn, false);
                    }
                }

                // Kiểm tra sức chứa và tăng sĩ số trong cùng transaction.
                if (!lopHocDAO.tangSiSo(conn, maLop)) {
                    return rollbackAndReturn(conn, false);
                }

                DangKyHoc dk = new DangKyHoc(maLop, maSV);
                if (!dangKyDAO.themDangKyMoi(conn, dk)) {
                    return rollbackAndReturn(conn, false);
                }

                conn.commit(); // Thành công 100% mới lưu
                return true;

            } catch (SQLException e) {
                boolean rollbackFailed = false;
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                    rollbackFailed = true;
                }
                if (!rollbackFailed && e.getErrorCode() == 1062) {
                    return false;
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

    public boolean huyDangKyHoc(String maSV, String maLop) {
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()) {
            return false;
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!dangKyDAO.khoaSinhVienChoDangKy(conn, maSV)) {
                    return rollbackAndReturn(conn, false);
                }
                if (lopHocDAO.layLopTheoMaForUpdate(conn, maLop) == null) {
                    return rollbackAndReturn(conn, false);
                }

                TrangThaiDangKy trangThai = dangKyDAO.layTrangThaiDangKyForUpdate(conn, maSV, maLop);
                if (trangThai == null || trangThai == TrangThaiDangKy.HUY) {
                    return rollbackAndReturn(conn, false);
                }
                if (trangThai == TrangThaiDangKy.DA_HOAN_THANH) {
                    return rollbackAndReturn(conn, false);
                }
                if (!dangKyDAO.huyDangKy(conn, maSV, maLop)) {
                    return rollbackAndReturn(conn, false);
                }
                if (!lopHocDAO.giamSiSo(conn, maLop)) {
                    return rollbackAndReturn(conn, false);
                }

                conn.commit();
                return true;
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

    private boolean rollbackAndReturn(Connection conn, boolean result) throws SQLException {
        conn.rollback();
        return result;
    }
}