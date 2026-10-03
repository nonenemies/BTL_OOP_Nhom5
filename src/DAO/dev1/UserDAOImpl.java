package dao;

import database.DBConnection;
import model.Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;

public class UserDAOImpl implements UserDAO {

    public static UserDAOImpl getInstance() {
        return new UserDAOImpl();
    }

    @Override
    public Users checkLogin(String email, String passwordHash) {

        Users user = null;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT * FROM Users " +
                            "WHERE Email = ? " +
                            "AND PasswordHash = ? " +
                            "AND TrangThai = 1";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Integer id = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String userEmail = rs.getString("Email");
                String password = rs.getString("PasswordHash");
                String role = rs.getString("Role");
                String maSo = rs.getString("MaSo");
                String khoa = rs.getString("Khoa");
                Date ngayTao = rs.getTimestamp("NgayTao");
                Integer trangThai = rs.getInt("TrangThai");

                user = new Users(id, hoTen, userEmail, password, role, maSo, khoa, ngayTao, trangThai);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public ArrayList<Users> selectAll() {

        ArrayList<Users> result = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM Users";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Integer id = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String passwordHash = rs.getString("PasswordHash");
                String role = rs.getString("Role");
                String maSo = rs.getString("MaSo");
                String khoa = rs.getString("Khoa");
                Date ngayTao = rs.getTimestamp("NgayTao");
                Integer trangThai = rs.getInt("TrangThai");

                Users user = new Users(id, hoTen, email, passwordHash, role, maSo, khoa, ngayTao, trangThai);

                result.add(user);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Users selectByID(int id) {

        Users user = null;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT * FROM Users " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Integer userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String passwordHash = rs.getString("PasswordHash");
                String role = rs.getString("Role");
                String maSo = rs.getString("MaSo");
                String khoa = rs.getString("Khoa");
                Date ngayTao = rs.getTimestamp("NgayTao");
                Integer trangThai = rs.getInt("TrangThai");

                user = new Users(userID, hoTen, email, passwordHash, role, maSo, khoa, ngayTao, trangThai);
            }

            rs.close();
            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void insert(Users t) {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "INSERT INTO Users " +
                            "(HoTen, Email, PasswordHash, Role, MaSo, Khoa, TrangThai) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, t.getHoTen());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getPasswordHash());
            ps.setString(4, t.getRole());
            ps.setString(5, t.getMaSo());
            ps.setString(6, t.getKhoa());

            if (t.getKhoa() == null || t.getKhoa().trim().isEmpty()) {
                ps.setNull(6, java.sql.Types.VARCHAR);
            } else {
                ps.setString(6, t.getKhoa());
            }

            if (t.getTrangThai() != null) {
                ps.setInt(7, t.getTrangThai());
            } else {
                ps.setInt(7, 1);
            }

            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(Users t) {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "DELETE FROM Users " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, t.getUserID());

            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Users t) {

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "UPDATE Users SET " +
                            "HoTen = ?, " +
                            "Email = ?, " +
                            "PasswordHash = ?, " +
                            "Role = ?, " +
                            "MaSo = ?, " +
                            "Khoa = ?, " +
                            "TrangThai = ? " +
                            "WHERE UserID = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, t.getHoTen());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getPasswordHash());
            ps.setString(4, t.getRole());
            ps.setString(5, t.getMaSo());
            ps.setString(6, t.getKhoa());
            ps.setInt(7, t.getTrangThai());
            ps.setInt(8, t.getUserID());

            ps.executeUpdate();

            ps.close();
            DBConnection.closeConnection(con);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}