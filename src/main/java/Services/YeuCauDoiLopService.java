package Services;

import DAO.YeuCauDoiLopDAO;
import DAO.DangKyHocDAO;
import DAO.LopHocDAO;
import DAO.MonHocDAO;
import Model.YeuCauDoiLop;
import Util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class YeuCauDoiLopService {

    private YeuCauDoiLopDAO yeuCauDAO = new YeuCauDoiLopDAO();
    private DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    private LopHocDAO lopHocDAO = new LopHocDAO();
    private MonHocDAO monHocDAO = new MonHocDAO();

    // ===============================================
    // HÀM TRỢ THỦ: KIỂM TRA MÔN TIÊN QUYẾT
    // ===============================================
    private String checkTienQuyet(int maSV, String maLopMuonVao) {
        String maMonMuonVao = lopHocDAO.getMaMonTuLop(maLopMuonVao);
        if (maMonMuonVao == null) return "Lớp học không tồn tại!";

        // Lấy danh sách các môn bắt buộc phải học trước
        List<String> listTienQuyet = monHocDAO.layDanhSachMonTienQuyet(maMonMuonVao);

        // Quét từng môn, nếu phát hiện môn nào chưa qua -> Báo lỗi ngay
        for (String monTQ : listTienQuyet) {
            if (!dangKyDAO.kiemTraDaQuaMon(maSV, monTQ)) {
                return "Chưa qua môn tiên quyết: " + monTQ;
            }
        }
        return "OK"; // Xanh chín!
    }

    // ===============================================
    // LUỒNG 1: SINH VIÊN A ĐĂNG BÀI
    // ===============================================
    public String dangBaiDoiLop(int maSVA, String lopCuA, String lopMoiA, String lyDo) {

        if (!dangKyDAO.kiemTraDaDangKy(maSVA, lopCuA)) {
            return "❌ Bạn không học lớp " + lopCuA + "!";
        }
        if (dangKyDAO.kiemTraDaDangKy(maSVA, lopMoiA)) {
            return "❌ Bạn đã có trong lớp " + lopMoiA + " rồi!";
        }

        // FLEX TIME: Kiểm tra tiên quyết cho SV A đối với lớp muốn vào
        String ketQuaTQ = checkTienQuyet(maSVA, lopMoiA);
        if (!ketQuaTQ.equals("OK")) {
            return "❌ Bạn không đủ điều kiện đổi sang lớp " + lopMoiA + ". Lý do: " + ketQuaTQ;
        }

        YeuCauDoiLop yc = new YeuCauDoiLop(maSVA, lopCuA, lopMoiA, lyDo);
        if (yeuCauDAO.themYeuCau(yc)) {
            return "✅ Đã đăng lên sàn giao dịch thành công!";
        }
        return "❌ Lỗi hệ thống!";
    }

    // ===============================================
    // LUỒNG 2: SINH VIÊN B CHỐT ĐƠN
    // ===============================================
    public String sinhVienBChotDon(int maYeuCau, int maSV_B) {
        YeuCauDoiLop baiDang = yeuCauDAO.getYeuCauById(maYeuCau);
        if (baiDang == null || !baiDang.getTrangThai().name().equals("CHO_XAC_NHAN")) {
            return "❌ Đơn không tồn tại hoặc đã bị chốt!";
        }

        int maSV_A = baiDang.getMaSV();
        String lopA_DangHoc = baiDang.getLopCu();
        String lopA_MuonSang = baiDang.getLopMoi();

        if (!dangKyDAO.kiemTraDaDangKy(maSV_B, lopA_MuonSang)) {
            return "❌ Lỗi: Bạn không học lớp " + lopA_MuonSang + " nên không thể đổi!";
        }
        if (dangKyDAO.kiemTraDaDangKy(maSV_B, lopA_DangHoc)) {
            return "❌ Lỗi: Bạn đã học lớp " + lopA_DangHoc + " rồi, đổi chéo sẽ bị trùng!";
        }

        // FLEX TIME: Kiểm tra tiên quyết cho SV B đối với lớp của thằng A (vì B sẽ sang lớp của A)
        String ketQuaTQ_ChoB = checkTienQuyet(maSV_B, lopA_DangHoc);
        if (!ketQuaTQ_ChoB.equals("OK")) {
            return "❌ Bạn không đủ điều kiện chốt đơn này. Lý do: " + ketQuaTQ_ChoB;
        }

        // Nếu tất cả cửa ải đều qua -> KHỚP LỆNH
        dangKyDAO.capNhatLopChoSinhVien(maSV_A, lopA_DangHoc, lopA_MuonSang);
        dangKyDAO.capNhatLopChoSinhVien(maSV_B, lopA_MuonSang, lopA_DangHoc);
        yeuCauDAO.capNhatTrangThaiDuyet(maYeuCau, maSV_B, "DA_DUYET");

        return "🎉 KHỚP LỆNH THÀNH CÔNG! Bạn đã đổi sang lớp " + lopA_DangHoc;
    }

    // =========================================================
    // HÀM TEST "FLEX" SỨC MẠNH LOGIC (BẢN HOÀN CHỈNH)
    // =========================================================
    public static void main(String[] args) {
        Services.DangKyHocService dkService = new Services.DangKyHocService();
        Services.YeuCauDoiLopService ycService = new Services.YeuCauDoiLopService();

        int idSinh = 3;  // SV A: Đang ở OOP, muốn sang DB
        int idVien = 4;  // SV B (Kém): Đang ở DB, muốn sang OOP
        int idBinh = 5;  // SV C (Giỏi): Đang ở DB, muốn sang OOP

        System.out.println("========== 1. KHỞI TẠO ĐĂNG KÝ HỌC LÚC ĐẦU ==========");
        System.out.println("SV Sinh (3): " + dkService.xuLyDangKyHoc(idSinh, "OOP_L01"));
        System.out.println("SV Viên (4): " + dkService.xuLyDangKyHoc(idVien, "DB_L01"));
        System.out.println("SV Bình (5): " + dkService.xuLyDangKyHoc(idBinh, "DB_L01"));

        System.out.println("\n========== 2. SV SINH (3) ĐĂNG BÀI LÊN SÀN ==========");
        System.out.println("Kết quả: " + ycService.dangBaiDoiLop(idSinh, "OOP_L01", "DB_L01", "Cần tìm người đổi gấp"));

        // ----- ĐOẠN NÀY LÀ MẸO: Tự động lấy cái Mã Yêu Cầu vừa đăng lên -----
        // ----- ĐOẠN NÀY LÀ MẸO (ĐÃ FIX CHO MYSQL) -----
        int maYeuCauMoiNhat = 0;
        try (java.sql.Connection conn = Util.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT MaYeuCau FROM YeuCauDoiLop ORDER BY MaYeuCau DESC LIMIT 1")) {

            if (rs.next()) {
                maYeuCauMoiNhat = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("LỖI LẤY MÃ YÊU CẦU LÊN: ");
            e.printStackTrace();
        }
        System.out.println("-> Hệ thống ghi nhận bài đăng có mã số: " + maYeuCauMoiNhat);

        System.out.println("\n========== 3. SV VIÊN (4 - HỌC KÉM) VÀO CHỐT ĐƠN ==========");
        System.out.println("Hệ thống phán quyết: " + ycService.sinhVienBChotDon(maYeuCauMoiNhat, idVien));

        System.out.println("\n========== 4. SV BÌNH (5 - HỌC GIỎI) VÀO CHỐT ĐƠN ==========");
        System.out.println("Hệ thống phán quyết: " + ycService.sinhVienBChotDon(maYeuCauMoiNhat, idBinh));

        System.out.println("\n========== 5. KIỂM TRA DATABASE (CHỨNG MINH KẾT QUẢ) ==========");
        // Bắn câu SQL trực tiếp để xem 2 người đã thực sự đổi chỗ dưới DB chưa
        try (java.sql.Connection conn = Util.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT MaSV, MaLop FROM DangKyHoc WHERE MaSV IN (3, 5)")) {

            System.out.println("Danh sách lớp hiện tại trong Database:");
            while (rs.next()) {
                System.out.println("- SV " + rs.getInt("MaSV") + " đang học lớp: " + rs.getString("MaLop"));
            }
        } catch (Exception e) {}
    }
}