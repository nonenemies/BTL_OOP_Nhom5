package DAO;

public class LopHocDAO {
    // ĐÓNG THẾ CHO DEV 2
    public String getMaMonTuLop(String maLop) {
        // Giả sử cứ mã lớp bắt đầu bằng OOP thì trả về môn IT01
        if (maLop.startsWith("OOP")) {
            return "IT01";
        }
        if (maLop.startsWith("DB")) {
            return "IT02";
        }
        return "IT01"; // Mặc định
    }
}