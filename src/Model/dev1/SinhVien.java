package model;

import java.util.Date;

public class SinhVien extends Users{
    private String maSinhVien;
    private Date ngaySinh;
    private String gioiTinh;
    private String lop;
    private Integer khoaHoc;
    private String heDaoTao;

    public SinhVien() {
        super();
    }

    public SinhVien(int userID, String maSinhVien, Date ngaySinh, String gioiTinh,
                    String lop, Integer khoaHoc, String heDaoTao) {
        super(userID);
        this.maSinhVien = maSinhVien;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.lop = lop;
        this.khoaHoc = khoaHoc;
        this.heDaoTao = heDaoTao;
    }

    public SinhVien(Integer userID, String hoTen, String email,
                    String khoa, String maSinhVien, Date ngaySinh, String gioiTinh,
                    String lop, Integer khoaHoc, String heDaoTao) {
        super(userID, hoTen, email, khoa);
        this.maSinhVien = maSinhVien;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.lop = lop;
        this.khoaHoc = khoaHoc;
        this.heDaoTao = heDaoTao;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public Integer getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(Integer khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public String getHeDaoTao() {
        return heDaoTao;
    }

    public void setHeDaoTao(String heDaoTao) {
        this.heDaoTao = heDaoTao;
    }

    @Override
    public String toString() {
        return  super.toString() +
                " " +maSinhVien +
                " " + ngaySinh +
                " " + gioiTinh +
                " " + lop +
                " " + khoaHoc +
                " " + heDaoTao;
    }
}
