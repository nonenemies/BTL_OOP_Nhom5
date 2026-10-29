package Controllers;

import Model.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class DashBoardController {
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
            showNavigationError("Không thể mở màn hình đăng ký tín chỉ. Vui lòng thử lại.");
        }
    }

    @FXML
    public void onBtnThoiKhoaBieuClick(ActionEvent event) {
        if (loggedInUser == null || !"SINHVIEN".equalsIgnoreCase(loggedInUser.getRole())
                || loggedInUser.getMaSo() == null || loggedInUser.getMaSo().isBlank()) {
            showNavigationError("Chức năng này dành cho sinh viên. Vui lòng đăng nhập bằng tài khoản sinh viên.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TimetableView.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 760);
            TimetableController controller = loader.getController();
            controller.setStudent(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Thời khóa biểu - " + loggedInUser.getHoTen());
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            showNavigationError("Không thể mở thời khóa biểu. Vui lòng thử lại.");
        }
    }

    @FXML
    public void onBtnDoiLopClick(ActionEvent event) {
        if (loggedInUser == null || !"SINHVIEN".equalsIgnoreCase(loggedInUser.getRole())
                || loggedInUser.getMaSo() == null || loggedInUser.getMaSo().isBlank()) {
            showNavigationError("Chức năng này dành cho sinh viên. Vui lòng đăng nhập bằng tài khoản sinh viên.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ExchangeView.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 760);
            ExchangeController controller = loader.getController();
            controller.setStudent(loggedInUser);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sàn giao dịch đổi lớp - " + loggedInUser.getHoTen());
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            showNavigationError("Không thể mở sàn giao dịch đổi lớp. Vui lòng thử lại.");
        }
    }
    @FXML
    public void onBtnDangXuatClick(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận đăng xuất");
        confirmation.setHeaderText("Bạn muốn đăng xuất khỏi tài khoản?");
        confirmation.setContentText("Bạn sẽ được đưa về màn hình đăng nhập.");
        Optional<ButtonType> response = confirmation.showAndWait();
        if (response.isEmpty() || response.get() != ButtonType.OK) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Scene loginScene = new Scene(loader.load(), 1080, 680);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Đăng nhập - Credit Hub");
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.centerOnScreen();
            loggedInUser = null;
        } catch (IOException | RuntimeException e) {
            showNavigationError("Không thể đăng xuất lúc này. Vui lòng thử lại.");
        }
    }

    private void showNavigationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("Không thể tiếp tục");
        alert.showAndWait();
    }
}