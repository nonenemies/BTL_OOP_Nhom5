package Services;

import java.sql.Connection;
import java.sql.SQLException;
import DAO.YeuCauDoiLopDAO;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonTienQuyetDAO;
import Model.YeuCauDoiLop;
import Model.MonHoc;
import Util.DBConnection;
import java.util.*;

public class YeuCauDoiLopService {

    private final YeuCauDoiLopDAO yeuCauDAO = new YeuCauDoiLopDAO();
    private final DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private final LopHocDAO lopHocDAO = new LopHocDAO();

    private String checkTienQuyet(Connection conn, String maSV, String maLopMuonVao) throws SQLException {
        var lopHoc = lopHocDAO.layLopTheoMa(conn, maLopMuonVao);
        if (lopHoc == null) return "Lớp học không tồn tại!";
        String maMonMuonVao = lopHoc.getMaMon();

        // Đọc môn tiên quyết trên cùng kết nối transaction.
        java.util.List<MonHoc> listTienQuyet = MonTienQuyetDAO.layDanhSachMonTienQuyet(conn, maMonMuonVao);

        // 3. Quét từng môn
        for (MonHoc monTQ : listTienQuyet) {
            String maTienQuyet = monTQ.getMaMon();

            // 4. GỌI HÀM CỦA BẠN: Có conn
            if (!dangKyDAO.kiemTraDaQuaMon(conn, maSV, maTienQuyet)) {
                return "Chưa qua môn tiên quyết: " + monTQ.getTenMon();
            }
        }
        return "OK";
    }

    public java.util.List<YeuCauDoiLop> moSanGiaoDich() {
        try {
            return yeuCauDAO.laySanGiaoDich();
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải sàn giao dịch đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải sàn giao dịch đổi lớp.", e);
        }
    }

    public String dangBaiDoiLop(String maSVA, String lopCuA, String lopMoiA, String lyDo) {
        try (Connection conn = DBConnection.getConnection()) {

            if (!dangKyDAO.kiemTraDaDangKy(conn, maSVA, lopCuA)) {
                return "❌ Bạn không học lớp " + lopCuA + "!";
            }
            if (dangKyDAO.kiemTraDaDangKy(conn, maSVA, lopMoiA)) {
                return "❌ Bạn đã có trong lớp " + lopMoiA + " rồi!";
            }

            String ketQuaTQ = checkTienQuyet(conn, maSVA, lopMoiA);
            if (!ketQuaTQ.equals("OK")) {
                return "❌ Bạn không đủ điều kiện sang lớp " + lopMoiA + ". Lý do: " + ketQuaTQ;
            }

            YeuCauDoiLop yc = new YeuCauDoiLop(maSVA, lopCuA, lopMoiA, lyDo);
            if (yeuCauDAO.themYeuCau(conn, yc)) {
                return "✅ Đã đăng lên sàn giao dịch thành công!";
            }
            return "❌ Lỗi lưu dữ liệu!";
        } catch (SQLException e) {
            throw new ServiceException("Không thể đăng yêu cầu đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể đăng yêu cầu đổi lớp.", e);
        }
    }

    public String sinhVienBChotDon(int maYeuCau, String maSV_B) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try {
                YeuCauDoiLop baiDang = yeuCauDAO.getYeuCauByIdForUpdate(conn, maYeuCau);
                if (baiDang == null || !baiDang.getTrangThai().name().equals("CHO_XAC_NHAN")) {
                    return rollbackAndReturn(conn, "❌ Đơn không tồn tại hoặc đã bị chốt!");
                }

                String maSV_A = baiDang.getMaSV();
                String lopA_DangHoc = baiDang.getLopCu();
                String lopA_MuonSang = baiDang.getLopMoi();

                if (Objects.equals(maSV_A, maSV_B)) {
                    return rollbackAndReturn(conn, "❌ Không thể tự chốt yêu cầu của mình!");
                }
                if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_DangHoc)
                        || dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, "❌ Đăng ký của người đăng yêu cầu đã thay đổi!");
                }
                String ketQuaTQ_ChoA = checkTienQuyet(conn, maSV_A, lopA_MuonSang);
                if (!ketQuaTQ_ChoA.equals("OK")) {
                    return rollbackAndReturn(conn,
                            "❌ Người đăng yêu cầu không còn đủ điều kiện. Lý do: " + ketQuaTQ_ChoA);
                }

                if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, "❌ Lỗi: Bạn không học lớp " + lopA_MuonSang + "!");
                }
                if (dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_DangHoc)) {
                    return rollbackAndReturn(conn, "❌ Lỗi: Bạn đã học lớp " + lopA_DangHoc + " rồi!");
                }

                String ketQuaTQ_ChoB = checkTienQuyet(conn, maSV_B, lopA_DangHoc);
                if (!ketQuaTQ_ChoB.equals("OK")) {
                    return rollbackAndReturn(conn, "❌ Không đủ điều kiện chốt đơn. Lý do: " + ketQuaTQ_ChoB);
                }

                // GIAO DỊCH HOÁN ĐỔI LỚP
                boolean doiA = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_A, lopA_DangHoc, lopA_MuonSang);
                boolean doiB = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_B, lopA_MuonSang, lopA_DangHoc);
                boolean chotDon = yeuCauDAO.capNhatTrangThaiDuyet(conn, maYeuCau, maSV_B, "DA_DUYET");

                if (doiA && doiB && chotDon) {
                    conn.commit();
                    return "🎉 KHỚP LỆNH THÀNH CÔNG! Bạn đã sang lớp " + lopA_DangHoc;
                } else {
                    return rollbackAndReturn(conn, "❌ Lỗi hệ thống trong quá trình hoán đổi!");
                }
            } catch (SQLException | RuntimeException e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        } catch (SQLException e) {
            throw new ServiceException("Không thể xử lý yêu cầu đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể xử lý yêu cầu đổi lớp.", e);
        }
    }

    private String rollbackAndReturn(Connection conn, String result) throws SQLException {
        conn.rollback();
        return result;
    }

}