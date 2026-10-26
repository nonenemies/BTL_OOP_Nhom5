package Controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashBoardController {
    @FXML private Label lblWelcome;

    public void setWelcomeName(String name) {
        lblWelcome.setText("Xin chào, " + name);
    }

    @FXML public void onBtnDangKyClick(ActionEvent event) { System.out.println("Mở màn Đăng Ký!"); }
    @FXML public void onBtnDoiLopClick(ActionEvent event) { System.out.println("Mở màn Đổi Lớp!"); }
    @FXML public void onBtnDangXuatClick(ActionEvent event) { System.out.println("Đăng xuất!"); }
}