package Services;

import DAO.DiemDAO;
import Util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DiemService {
    private final DiemDAO diemDAO = new DiemDAO();

    /**
     * Lưu tổng(trọng số * điểm đạt được) vào DangKyHoc.DiemTongKet.
     * Không cập nhật khi chưa đủ điểm, trùng điểm hoặc môn chưa có thành phần hoạt động.
     * Kết quả làm tròn đến hai chữ số thập phân theo cột DiemTongKet.
     * @return số đăng ký được cập nhật (có thể là 0 nếu giá trị không đổi tùy cấu hình JDBC)
     */
    public int tinhDiemTongKet(String maSV, String maLop) {
        if ((maSV == null || maSV.isBlank()) || maLop == null || maLop.isBlank()) {
            throw new ServiceException("Mã sinh viên và mã lớp không hợp lệ.");
        }
        // Một câu UPDATE kiểm tra đủ điểm và lưu kết quả trong cùng thao tác SQL.
        try (Connection conn = DBConnection.getConnection()) {
            return diemDAO.capNhatDiemTongKet(conn, maSV, maLop);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tính điểm tổng kết.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tính điểm tổng kết.", e);
        }
    }

    /**
     * Thêm điểm thành phần và tính lại điểm tổng kết trong cùng transaction.
     *
     * @return false nếu không tìm thấy duy nhất đăng ký/thành phần phù hợp
     */
    public boolean nhapDiemVaTinhTongKet(String maSV, String maLop, int thuTu, float diemDatDuoc) {
        if (maSV == null || maSV.isBlank() || maLop == null || maLop.isBlank()) {
            throw new ServiceException("Mã sinh viên và mã lớp không hợp lệ.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!diemDAO.nhapDiem(conn, maSV, maLop, thuTu, diemDatDuoc)) {
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
            throw new ServiceException("Không thể nhập điểm và cập nhật điểm tổng kết.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể nhập điểm và cập nhật điểm tổng kết.", e);
        }
    }
}
