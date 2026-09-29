package src.Util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Ket noi thanh cong!");
        } catch (SQLException e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }
    }
}
