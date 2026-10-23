package Services;

import java.sql.Connection;
import java.sql.SQLException;
import DAO.YeuCauDoiLopDAO;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonHocDAO;
import DAO.MonTienQuyetDAO;
import Model.YeuCauDoiLop;
import Model.MonHoc;
import Util.DBConnection;
import java.util.*;

public class YeuCauDoiLopService {

    private YeuCauDoiLopDAO yeuCauDAO = new YeuCauDoiLopDAO();
    private DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private LopHocDAO lopHocDAO = new LopHocDAO();

    private String checkTienQuyet(Connection conn, int maSV, String maLopMuonVao) throws SQLException {

        // 1. GỌI DEV 2: Đã thêm chữ 'conn' vào tham số đầu tiên
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

    public String dangBaiDoiLop(int maSVA, String lopCuA, String lopMoiA, String lyDo) {
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
            e.printStackTrace();
            return "❌ Lỗi cơ sở dữ liệu!";
        }
    }

    public String sinhVienBChotDon(int maYeuCau, int maSV_B) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            YeuCauDoiLop baiDang = yeuCauDAO.getYeuCauByIdForUpdate(conn, maYeuCau);
            if (baiDang == null || !baiDang.getTrangThai().name().equals("CHO_XAC_NHAN")) {
                return "❌ Đơn không tồn tại hoặc đã bị chốt!";
            }

            int maSV_A = baiDang.getMaSV();
            String lopA_DangHoc = baiDang.getLopCu();
            String lopA_MuonSang = baiDang.getLopMoi();

            if (maSV_A == maSV_B) {
                return "❌ Không thể tự chốt yêu cầu của mình!";
            }
            if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_DangHoc)
                    || dangKyDAO.kiemTraDaDangKy(conn, maSV_A, lopA_MuonSang)) {
                return "❌ Đăng ký của người đăng yêu cầu đã thay đổi!";
            }
            String ketQuaTQ_ChoA = checkTienQuyet(conn, maSV_A, lopA_MuonSang);
            if (!ketQuaTQ_ChoA.equals("OK")) {
                return "❌ Người đăng yêu cầu không còn đủ điều kiện. Lý do: " + ketQuaTQ_ChoA;
            }

            if (!dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_MuonSang)) {
                return "❌ Lỗi: Bạn không học lớp " + lopA_MuonSang + "!";
            }
            if (dangKyDAO.kiemTraDaDangKy(conn, maSV_B, lopA_DangHoc)) {
                return "❌ Lỗi: Bạn đã học lớp " + lopA_DangHoc + " rồi!";
            }

            String ketQuaTQ_ChoB = checkTienQuyet(conn, maSV_B, lopA_DangHoc);
            if (!ketQuaTQ_ChoB.equals("OK")) {
                return "❌ Không đủ điều kiện chốt đơn. Lý do: " + ketQuaTQ_ChoB;
            }

            // GIAO DỊCH HOÁN ĐỔI LỚP
            boolean doiA = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_A, lopA_DangHoc, lopA_MuonSang);
            boolean doiB = dangKyDAO.capNhatLopChoSinhVien(conn, maSV_B, lopA_MuonSang, lopA_DangHoc);
            boolean chotDon = yeuCauDAO.capNhatTrangThaiDuyet(conn, maYeuCau, maSV_B, "DA_DUYET");

            if (doiA && doiB && chotDon) {
                conn.commit();
                return "🎉 KHỚP LỆNH THÀNH CÔNG! Bạn đã sang lớp " + lopA_DangHoc;
            } else {
                conn.rollback();
                return "❌ Lỗi hệ thống trong quá trình hoán đổi!";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return "❌ Lỗi cơ sở dữ liệu!";
        } finally {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) { ex.printStackTrace(); }
                try {
                    conn.close();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public static void main(String[] args) {
        DangKyHocService dkService = new DangKyHocService();
        YeuCauDoiLopService ycService = new YeuCauDoiLopService();
        System.out.println("BƯỚC 1: " + dkService.xuLyDangKyHoc(3, "OOP_L01"));
        System.out.println("BƯỚC 1: " + dkService.xuLyDangKyHoc(4, "DB_L01"));
        System.out.println("BƯỚC 1: " + dkService.xuLyDangKyHoc(5, "DB_L01"));

        System.out.println("BƯỚC 2 (SV3 ĐĂNG BÀI): " + ycService.dangBaiDoiLop(3, "OOP_L01", "DB_L01", "Doi lop"));

        int idDonMoi = 0;
        try (java.sql.Connection c = Util.DBConnection.getConnection();
             java.sql.ResultSet rs = c.createStatement().executeQuery("SELECT MaYeuCau FROM YeuCauDoiLop ORDER BY MaYeuCau DESC LIMIT 1")) {
            if (rs.next()) idDonMoi = rs.getInt(1);
        } catch (Exception e) {}

        System.out.println("BƯỚC 3 (SV4 KÉM): " + ycService.sinhVienBChotDon(idDonMoi, 4));
        System.out.println("BƯỚC 4 (SV5 GIỎI): " + ycService.sinhVienBChotDon(idDonMoi, 5));
    }
}