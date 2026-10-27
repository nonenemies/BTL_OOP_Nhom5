package Controllers;

import Model.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashBoardController {
    private static final Logger LOGGER = Logger.getLogger(DashBoardController.class.getName());
    @FXML private Label lblWelcome;
    private Users loggedInUser;

    public void setLoggedInUser(Users user) {
        loggedInUser = user;
        if (user != null) setWelcomeName(user.getHoTen());
    }

    public void setWelcomeName(String name) {
        lblWelcome.setText("Xin chào, " + name);
    }

    @FXML
    public void onBtnDangKyClick(ActionEvent event) {
        if (loggedInUser == null || !"SINHVIEN".equalsIgnoreCase(loggedInUser.getRole())
                || loggedInUser.getMaSo() == null || loggedInUser.getMaSo().isBlank()) {
            showNavigationError("Chức năng này dành cho sinh viên. Vui lòng đăng nhập bằng tài khoản sinh viên.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegistrationView.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 760);
            RegistrationController controller = loader.getController();
            controller.setStudent(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Đăng ký tín chỉ - " + loggedInUser.getHoTen());
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Không thể mở màn hình đăng ký tín chỉ.", e);
            showNavigationError("Không thể mở màn hình đăng ký tín chỉ. Vui lòng thử lại.");
        }
    }

    @FXML public void onBtnDoiLopClick(ActionEvent event) { System.out.println("Mở màn Đổi Lớp!"); }
    @FXML public void onBtnDangXuatClick(ActionEvent event) { System.out.println("Đăng xuất!"); }

    private void showNavigationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("Không thể tiếp tục");
        alert.showAndWait();
    }
}
