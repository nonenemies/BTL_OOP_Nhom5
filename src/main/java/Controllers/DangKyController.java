package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class DangKyController {

    // Hàm này sẽ chạy khi bấm cái nút bên file FXML
    @FXML
    protected void onBtnTestClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Kết nối thành công!");
        alert.setContentText("Giao diện đã nói chuyện được với Controller!");
        alert.showAndWait();
    }
}