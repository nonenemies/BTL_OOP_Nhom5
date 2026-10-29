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

    private boolean checkTienQuyet(Connection conn, String maSV, String maLopMuonVao) throws SQLException {
        var lopHoc = lopHocDAO.layLopTheoMa(conn, maLopMuonVao);
        if (lopHoc == null) return false;
        String maMonMuonVao = lopHoc.getMaMon();

        // Đọc môn tiên quyết trên cùng kết nối transaction.
        java.util.List<MonHoc> listTienQuyet = MonTienQuyetDAO.layDanhSachMonTienQuyet(conn, maMonMuonVao);

        // 3. Quét từng môn
        for (MonHoc monTQ : listTienQuyet) {
            String maTienQuyet = monTQ.getMaMon();

            // 4. GỌI HÀM CỦA BẠN: Có conn
            if (!dangKyDAO.kiemTraDaQuaMon(conn, maSV, maTienQuyet)) {
                return false;
            }
        }
        return true;
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

    public java.util.List<YeuCauDoiLop> xemYeuCauCuaSinhVien(String maSV) {
        if (maSV == null || maSV.isBlank()) throw new IllegalArgumentException("Mã sinh viên không hợp lệ.");
        try {
            return yeuCauDAO.layYeuCauCuaSinhVien(maSV);
        } catch (SQLException e) {
            throw new ServiceException("Không thể tải lịch sử đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể tải lịch sử đổi lớp.", e);
        }
    }

    public boolean huyYeuCau(int maYeuCau, String maSV) {
        if (maSV == null || maSV.isBlank() || maYeuCau <= 0) {
            return false;
        }
        try {
            return yeuCauDAO.huyYeuCauCuaSinhVien(maSV, maYeuCau);
        } catch (SQLException e) {
            throw new ServiceException("Không thể hủy yêu cầu đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể hủy yêu cầu đổi lớp.", e);
        }
    }

    public boolean dangBaiDoiLop(String maSVA, String lopCuA, String lopMoiA, String lyDo) {
        if (maSVA == null || maSVA.isBlank() || lopCuA == null || lopCuA.isBlank()
                || lopMoiA == null || lopMoiA.isBlank() || lopCuA.equals(lopMoiA)) {
            return false;
        }
        if (lyDo != null && lyDo.length() > 200) {
            return false;
        }
        try (Connection conn = DBConnection.getConnection()) {

            if (!dangKyDAO.kiemTraDaDangKy(conn, maSVA, lopCuA)) {
                return false;
            }
            var lopMuonVao = lopHocDAO.layLopTheoMa(conn, lopMoiA);
            if (lopMuonVao == null || lopMuonVao.getTrangThai() != Model.TrangThaiHoatDong.HOAT_DONG) {
                return false;
            }
            if (dangKyDAO.kiemTraDaDangKy(conn, maSVA, lopMoiA)) {
                return false;
            }

            if (!checkTienQuyet(conn, maSVA, lopMoiA)) {
                return false;
            }

            YeuCauDoiLop yc = new YeuCauDoiLop(maSVA, lopCuA, lopMoiA, lyDo);
            if (yeuCauDAO.themYeuCau(conn, yc)) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new ServiceException("Không thể đăng yêu cầu đổi lớp.", e);
        } catch (RuntimeException e) {
            throw ServiceException.wrap("Không thể đăng yêu cầu đổi lớp.", e);
        }
    }

    public boolean sinhVienBChotDon(int maYeuCau, String maSV_B) {
        if (maYeuCau <= 0 || maSV_B == null || maSV_B.isBlank()) {
            return false;
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            try {
                YeuCauDoiLop baiDang = yeuCauDAO.getYeuCauByIdForUpdate(conn, maYeuCau);
                if (baiDang == null || !baiDang.getTrangThai().name().equals("CHO_XAC_NHAN")) {
                    return rollbackAndReturn(conn, false);
                }

                String maSV_A = baiDang.getMaSV();
                String lopA_DangHoc = baiDang.getLopCu();
                String lopA_MuonSang = baiDang.getLopMoi();

                if (Objects.equals(maSV_A, maSV_B)) {
                    return rollbackAndReturn(conn, false);
                }
                for (String maSV : java.util.stream.Stream.of(maSV_A, maSV_B).sorted().toList()) {
                    if (!dangKyDAO.khoaSinhVienChoDangKy(conn, maSV)) {
                        return rollbackAndReturn(conn, false);
                    }
                }
                if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_DangHoc)
                        || dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, false);
                }
                if (!dangKyDAO.coLichHoc(conn, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, false);
                }
                if (dangKyDAO.coLopBiTrungLich(
                        conn, maSV_A, lopA_MuonSang, lopA_DangHoc)) {
                    return rollbackAndReturn(conn, false);
                }
                if (!checkTienQuyet(conn, maSV_A, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, false);
                }

                if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, false);
                }
                if (dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_DangHoc)) {
                    return rollbackAndReturn(conn, false);
                }

                if (!dangKyDAO.coLichHoc(conn, lopA_DangHoc)) {
                    return rollbackAndReturn(conn, false);
                }
                if (dangKyDAO.coLopBiTrungLich(
                        conn, maSV_B, lopA_DangHoc, lopA_MuonSang)) {
                    return rollbackAndReturn(conn, false);
                }
                if (!checkTienQuyet(conn, maSV_B, lopA_DangHoc)) {
                    return rollbackAndReturn(conn, false);
                }

                // GIAO DỊCH HOÁN ĐỔI LỚP
                boolean doiA = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_A, lopA_DangHoc, lopA_MuonSang);
                boolean doiB = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_B, lopA_MuonSang, lopA_DangHoc);
                boolean chotDon = yeuCauDAO.capNhatTrangThaiDuyet(conn, maYeuCau, maSV_B, "DA_DUYET");

                if (doiA && doiB && chotDon) {
                    conn.commit();
                    return true;
                } else {
                    return rollbackAndReturn(conn, false);
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

    private boolean rollbackAndReturn(Connection conn, boolean result) throws SQLException {
        conn.rollback();
        return result;
    }

}