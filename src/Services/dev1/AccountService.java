
package Services.dev1;

import DAO.dev1.UserDAOImpl;
import DAO.dev1.SinhVienDAOImpl;
import DAO.dev1.GiangVienDAOImpl;

import Model.dev1.Users;
import Model.dev1.SinhVien;
import Model.dev1.GiangVien;

import Util.DBConnection.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountService {

    private final UserDAOImpl userDAO =
            UserDAOImpl.getInstance();

    private final SinhVienDAOImpl sinhVienDAO =
            SinhVienDAOImpl.getInstance();

    private final GiangVienDAOImpl giangVienDAO =
            GiangVienDAOImpl.getInstance();

    // 1. Admin tạo tài khoản sinh viên
    public void createSinhVien(Users admin, Users user, SinhVien sv) {

        checkAdmin(admin);
        if (user == null || !"SINHVIEN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Role phai la SINHVIEN");
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                userDAO.insert(con, user);

                sv.setUserID(user.getUserID());

                sinhVienDAO.insert(con, sv);

                con.commit();

                System.out.println("Them sinh vien thanh cong");

            } catch (Exception e) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }

                user.setUserID(null);
                sv.setUserID(null);

                throw new RuntimeException("Loi them sinh vien", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Loi ket noi database", e);
        }
    }

    // 2. Admin tạo tài khoản giảng viên
    public void createGiangVien(Users admin, Users user, GiangVien gv) {

        checkAdmin(admin);
        if (user == null || !"GIANGVIEN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Role phai la GIANGVIEN");
        }

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                userDAO.insert(con, user);

                gv.setUserID(user.getUserID());

                giangVienDAO.insert(con, gv);

                con.commit();

                System.out.println("Them giang vien thanh cong");

            } catch (Exception e) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }

                user.setUserID(null);
                gv.setUserID(null);

                throw new RuntimeException("Loi them giang vien", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Loi ket noi database", e);
        }
    }

    // 3. Kiểm tra quyền Admin
    private void checkAdmin(Users admin) {

        if (admin == null || admin.getUserID() == null) {
            throw new SecurityException("Chua dang nhap");
        }

        Users currentAdmin = userDAO.selectByID(admin.getUserID());

        if (currentAdmin == null ||
                !"ADMIN".equalsIgnoreCase(currentAdmin.getRole()) ||
                !Integer.valueOf(1).equals(currentAdmin.getTrangThai())) {

            throw new SecurityException("Khong co quyen Admin");
        }
    }
}
