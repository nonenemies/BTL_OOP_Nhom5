package DAO.dev1;

import Model.dev1.Users;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        Users u = new Users();

        u.setHoTen("Nguyễn Văn Test");
        u.setEmail("test01@truong.edu.vn");
        u.setPasswordHash("123");
        u.setRole("ADMIN");
        u.setMaSo("TEST09");

        u.setKhoa(null);
        u.setTrangThai(null);

        UserDAOImpl.getInstance().insert(u);
    }
}
