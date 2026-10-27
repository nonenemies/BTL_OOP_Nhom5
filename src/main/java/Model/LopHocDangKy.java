package Model;

/** Thông tin lớp học và môn học dùng trong màn hình đăng ký tín chỉ. */
public class LopHocDangKy {
    private final String maLop;
    private final String maMon;
    private final String tenMon;
    private final String giangVien;
    private final String hocKy;
    private final Integer soTinChi;
    private final Integer siSoHienTai;
    private final Integer siSoToiDa;
    private final boolean daDangKy;

    public LopHocDangKy(String maLop, String maMon, String tenMon, String giangVien,
                        String hocKy, Integer soTinChi, Integer siSoHienTai,
                        Integer siSoToiDa, boolean daDangKy) {
        this.maLop = maLop;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.giangVien = giangVien;
        this.hocKy = hocKy;
        this.soTinChi = soTinChi;
        this.siSoHienTai = siSoHienTai;
        this.siSoToiDa = siSoToiDa;
        this.daDangKy = daDangKy;
    }

    public String getMaLop() { return maLop; }
    public String getMaMon() { return maMon; }
    public String getTenMon() { return tenMon; }
    public String getGiangVien() { return giangVien; }
    public String getHocKy() { return hocKy; }
    public Integer getSoTinChi() { return soTinChi; }
    public Integer getSiSoHienTai() { return siSoHienTai; }
    public Integer getSiSoToiDa() { return siSoToiDa; }
    public boolean isDaDangKy() { return daDangKy; }
    public String getSiSo() { return siSoHienTai + " / " + siSoToiDa; }
    public String getTrangThai() {
        if (daDangKy) return "Đã đăng ký";
        if (siSoHienTai == null || siSoToiDa == null) return "Không rõ sĩ số";
        return siSoHienTai < siSoToiDa ? "Còn chỗ" : "Đã đầy";
    }
}
