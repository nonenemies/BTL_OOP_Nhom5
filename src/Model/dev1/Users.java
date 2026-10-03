package model;

import java.util.Date;

public class Users {

    private Integer userID;
    private String hoTen;
    private String email;
    private String passwordHash;
    private String role;
    private String maSo;
    private String khoa;
    private Date ngayTao;
    private Integer trangThai;

    public Users() {
    }

    public Users(Integer userID) {
        this.userID = userID;
    }

    public Users(String hoTen, String email, String passwordHash,
                 String role, String maSo, String khoa,
                 Integer trangThai) {

        this.hoTen = hoTen;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.maSo = maSo;
        this.khoa = khoa;
        this.trangThai = trangThai;
    }

    public Users(Integer userID, String hoTen, String email,
                 String passwordHash, String role,
                 String maSo, String khoa,
                 Date ngayTao, Integer trangThai) {

        this.userID = userID;
        this.hoTen = hoTen;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.maSo = maSo;
        this.khoa = khoa;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    public Users(Integer userID, String hoTen, String email, String khoa) {
        this.userID = userID;
        this.hoTen = hoTen;
        this.email = email;
        this.khoa = khoa;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMaSo() {
        return maSo;
    }

    public void setMaSo(String maSo) {
        this.maSo = maSo;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return userID + " "
                + hoTen + " "
                + email + " "
                + passwordHash + " "
                + role + " "
                + maSo + " "
                + khoa + " "
                + ngayTao + " "
                + trangThai;
    }
}