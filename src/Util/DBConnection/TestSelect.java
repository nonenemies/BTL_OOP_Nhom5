package testjdbc;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestSelect {

    public static void main(String[] args) {

        String sql = "SELECT * FROM Users";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                int userID = rs.getInt("UserID");
                String hoTen = rs.getString("HoTen");
                String email = rs.getString("Email");
                String role = rs.getString("Role");

                System.out.println(
                        userID + " | "
                                + hoTen + " | "
                                + email + " | "
                                + role
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}