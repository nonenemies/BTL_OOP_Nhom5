package Util;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {

            System.out.println("Ket noi MySQL thanh cong!");

        } catch (Exception e) {

            System.out.println("Ket noi MySQL that bai!");

            e.printStackTrace();
        }
    }
}