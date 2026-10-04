package Services;

import DAO.YeuCauDoiLopDAO;
import DAO.DangKyHocDAO;
// import DAO.LopHocDAO; // Cần Dev 2 để check xem 2 lớp có cùng 1 Môn không
import Model.YeuCauDoiLop;

public class YeuCauDoiLopService {

    private YeuCauDoiLopDAO yeuCauDAO = new YeuCauDoiLopDAO();
    private DangKyHocDAO dangKyDAO = new DangKyHocDAO();
    // private LopHocDAO lopHocDAO = new LopHocDAO();

    // ===============================================
    // LUỒNG 1: SINH VIÊN A ĐĂNG BÀI LÊN "SÀN"
    // ===============================================
    public String dangBaiDoiLop(int maSVA, String lopCuA, String lopMoiA, String lyDo) {

        // LUẬT 1: Sinh viên A phải đang học LopCu
        if (!dangKyDAO.kiemTraDaDangKy(maSVA, lopCuA)) {
            return "❌ Bạn không có mặt trong lớp này, sao đòi đổi?";
        }

        /*
        // LUẬT 2: Chỉ cho phép đổi cùng 1 môn học (VD: OOP sáng đổi lấy OOP chiều)
        String monCuaLopCu = lopHocDAO.getMaMonTuLop(lopCuA);
        String monCuaLopMoi = lopHocDAO.getMaMonTuLop(lopMoiA);
        if (!monCuaLopCu.equals(monCuaLopMoi)) {
            return "❌ Bôn ba không qua thời vận, đổi lớp phải cùng môn học bạn nhé!";
        }
        */

        // Đẩy lên sàn (Lưu vào DB với trạng thái CHO_XAC_NHAN)
        YeuCauDoiLop yc = new YeuCauDoiLop(maSVA, lopCuA, lopMoiA, lyDo);
        if (yeuCauDAO.themYeuCau(yc)) {
            return "✅ Đã đăng bài lên sàn thành công! Đợi 'đối tác' vào chốt đơn.";
        }
        return "❌ Lỗi hệ thống!";
    }

    // ===============================================
    // LUỒNG 2: SINH VIÊN B BẤM NÚT "CHỐT ĐƠN" (ACCEPT)
    // ===============================================
    // UI sẽ truyền vào: Mã bài đăng (maYeuCau) và ID của Sinh viên B (người bấm accept)
    public String sinhVienBChotDon(int maYeuCau, int maSV_B) {

        // Bước 1: Lấy thông tin bài đăng từ Database lên
        YeuCauDoiLop baiDang = yeuCauDAO.getYeuCauById(maYeuCau);
        if (baiDang == null || !baiDang.getTrangThai().name().equals("CHO_XAC_NHAN")) {
            return "❌ Đơn này đã bị người khác giành mất hoặc không tồn tại!";
        }

        int maSV_A = baiDang.getMaSV();
        String lopA_DangHoc = baiDang.getLopCu();
        String lopA_MuonSang = baiDang.getLopMoi();

        // LUẬT BẢO MẬT: Sinh viên B CÓ ĐANG HỌC cái lớp mà thằng A muốn sang không?
        if (!dangKyDAO.kiemTraDaDangKy(maSV_B, lopA_MuonSang)) {
            return "❌ Bạn không học lớp " + lopA_MuonSang + ", lấy tư cách gì mà đổi?";
        }

        // =========================================================
        // THỰC THI HOÁN ĐỔI LỊCH SỬ (TRANSACTION)
        // =========================================================

        // 1. Chuyển Sinh viên A sang Lớp B
        dangKyDAO.capNhatLopChoSinhVien(maSV_A, lopA_DangHoc, lopA_MuonSang);

        // 2. Chuyển Sinh viên B sang Lớp A
        dangKyDAO.capNhatLopChoSinhVien(maSV_B, lopA_MuonSang, lopA_DangHoc);

        // 3. Đóng bài đăng trên sàn (Đổi trạng thái thành DA_DUYET)
        // Mẹo: Lưu ID của Sinh viên B vào cột NguoiXuLy luôn để biết ai là người khớp lệnh!
        //yeuCauDAO.capNhatTrangThaiDuyet(maYeuCau, maSV_B, "DA_DUYET");

        return "🎉 KHỚP LỆNH THÀNH CÔNG! Bạn và " + maSV_A + " đã đổi chỗ cho nhau.";
    }
}