
package Model.dev1;

public class GiangVien extends Users {

    private String maGiangVien;
    private String chuyenNganh;
    private String hocVi;

    public GiangVien() {
        super();
    }

    // Constructor dùng khi thêm giảng viên
    public GiangVien(Integer userID, String maGiangVien,
                     String chuyenNganh, String hocVi) {
        super(userID);
        this.maGiangVien = maGiangVien;
        this.chuyenNganh = chuyenNganh;
        this.hocVi = hocVi;
    }

    // Constructor dùng khi SELECT JOIN Users + GiaoVien
    public GiangVien(Integer userID, String hoTen,
                     String email, String khoa,
                     String maGiangVien,
                     String chuyenNganh, String hocVi) {

        super(userID, hoTen, email, khoa);

        this.maGiangVien = maGiangVien;
        this.chuyenNganh = chuyenNganh;
        this.hocVi = hocVi;
    }


    public String getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(String maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public String getChuyenNganh() {
        return chuyenNganh;
    }

    public void setChuyenNganh(String chuyenNganh) {
        this.chuyenNganh = chuyenNganh;
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }

    @Override
    public String toString() {
        return getUserID() + " " +
                getHoTen() + " " +
                getEmail() + " " +
                getKhoa() + " " +
                maGiangVien + " " +
                chuyenNganh + " " +
                hocVi;
    }
}
