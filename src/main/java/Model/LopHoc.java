package Model;

/**
 * Model ánh xạ tới bảng LopHoc trong database.
 */
public class LopHoc {
    private String maLop;
    private String maMon;
    private Integer maGV;
    private String hocKy;
    private Integer siSoToiDa;
    private Integer siSoHienTai;
    private Integer trangThai;

    public LopHoc() {
    }

    public LopHoc(String maLop, String maMon, Integer maGV, String hocKy,
                  Integer siSoToiDa, Integer siSoHienTai, Integer trangThai) {
        this.maLop = maLop;
        this.maMon = maMon;
        this.maGV = maGV;
        this.hocKy = hocKy;
        this.siSoToiDa = siSoToiDa;
        this.siSoHienTai = siSoHienTai;
        this.trangThai = trangThai;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public Integer getMaGV() {
        return maGV;
    }

    public void setMaGV(Integer maGV) {
        this.maGV = maGV;
    }

    public String getHocKy() {
        return hocKy;
    }

    public void setHocKy(String hocKy) {
        this.hocKy = hocKy;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(Integer siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    public Integer getSiSoHienTai() {
        return siSoHienTai;
    }

    public void setSiSoHienTai(Integer siSoHienTai) {
        this.siSoHienTai = siSoHienTai;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }
}
