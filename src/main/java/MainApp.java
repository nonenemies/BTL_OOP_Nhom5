import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1. Áp dụng Giao diện siêu đẹp của AtlantaFX (Chế độ Ban Đêm)
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        // 2. Load cái file giao diện FXML lúc nãy lên
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/views/DangKyView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Rộng 800, cao 600

        // 3. Hiển thị cửa sổ
        stage.setTitle("Phần mềm Quản lý Tín chỉ - Nhóm 5");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Lệnh bắt buộc để chạy JavaFX
    }
}