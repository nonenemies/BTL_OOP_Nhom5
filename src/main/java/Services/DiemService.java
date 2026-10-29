package Services;

import DAO.DiemDAO;
import DAO.LopHocDAO;
import Model.BangDiemSinhVien;
import Model.GiangVien;
import Model.LopHoc;
import Model.TrangThaiHoatDong;
import Model.ThanhPhanDanhGia;
import Util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DiemService {
    private final DiemDAO diemDAO = new DiemDAO();
    private final LopHocDAO lopHocDAO = new LopHocDAO();

    public List<LopHoc> layLopPhuTrach(GiangVien giangVien) {
        kiemTraGiangVien(giangVien);
        try {
            return lopHocDAO.layDanhSachLopCuaGiangVien(giangVien.getMaGiangVien());
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải danh sách lớp phụ trách.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải danh sách lớp phụ trách.", e);
        }
    }

    public List<BangDiemSinhVien> layBangDiem(GiangVien giangVien, String maLop) {
        kiemTraGiangVien(giangVien);
        try (Connection conn = DBConnection.getConnection()) {
            kiemTraPhuTrach(conn, giangVien, maLop, false);
            return diemDAO.layBangDiemTheoLop(conn, maLop);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải bảng điểm lớp học phần.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải bảng điểm lớp học phần.", e);
        }
    }

    /** Tính lại điểm tổng kết khi tất cả thành phần đang hoạt động đã có điểm. */
    public int tinhDiemTongKet(GiangVien giangVien, String maSV, String maLop) {
        kiemTraGiangVien(giangVien);
        if (maSV == null || maSV.isBlank()) {
            throw new ServiceException("Mã sinh viên không hợp lệ.");
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                kiemTraPhuTrach(conn, giangVien, maLop, true);
                int updated = diemDAO.capNhatDiemTongKet(conn, maSV, maLop);
                conn.commit();
                return updated;
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể tính điểm tổng kết.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tính điểm tổng kết.", e);
        }
    }

    /**
     * Lưu mới hoặc cập nhật điểm thành phần, sau đó tính lại điểm tổng kết nếu đã đủ điểm.
     * Kết quả nghiệp vụ không hợp lệ được trả về false; lỗi cơ sở dữ liệu được ném lên caller.
     */
    public boolean nhapDiemVaTinhTongKet(GiangVien giangVien, String maSV, String maLop,
                                        ThanhPhanDanhGia thanhPhan, float diemDatDuoc) {
        kiemTraGiangVien(giangVien);
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()
                || thanhPhan == null || thanhPhan.getMaTP() <= 0
                || !Float.isFinite(diemDatDuoc) || diemDatDuoc < 0 || diemDatDuoc > 10) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                kiemTraPhuTrach(conn, giangVien, maLop, true);
                if (!thanhPhan.getTrangThai()) {
                    conn.rollback();
                    return false;
                }
                if (!diemDAO.nhapDiem(conn, maSV, maLop,
                        thanhPhan.getMaTP(), diemDatDuoc)) {
                    conn.rollback();
                    return false;
                }
                diemDAO.capNhatDiemTongKet(conn, maSV, maLop);
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
            throw new ServiceException("Không thể lưu điểm và cập nhật điểm tổng kết.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể lưu điểm và cập nhật điểm tổng kết.", e);
        }
    }

    private void kiemTraPhuTrach(Connection conn, GiangVien giangVien,
                                  String maLop, boolean khoa) throws SQLException {
        if (maLop == null || maLop.isBlank()) {
            throw new ServiceException("Mã lớp không hợp lệ.");
        }
        LopHoc lopHoc = khoa ? lopHocDAO.layLopTheoMaForUpdate(conn, maLop)
                : lopHocDAO.layLopTheoMa(conn, maLop);
        if (lopHoc == null || !giangVien.getMaGiangVien().equals(lopHoc.getMaGV())) {
            throw new ServiceException("Bạn không được phân công phụ trách lớp này.");
        }
    }

    private void kiemTraGiangVien(GiangVien giangVien) {
        if (giangVien == null || !"GIAOVIEN".equalsIgnoreCase(giangVien.getRole())
                || giangVien.getMaGiangVien() == null || giangVien.getMaGiangVien().isBlank()
                || giangVien.getTrangThai() != TrangThaiHoatDong.HOAT_DONG) {
            throw new ServiceException("Tài khoản giảng viên không hợp lệ hoặc đã ngừng hoạt động.");
        }
    }
}
