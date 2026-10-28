package Controllers;

import Model.LopHocDangKy;
import Model.Users;
import Services.DangKyHocService;
import Services.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationController {
    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class.getName());
    private final DangKyHocService registrationService = new DangKyHocService();
    private final ObservableList<LopHocDangKy> classes = FXCollections.observableArrayList();
    private final FilteredList<LopHocDangKy> filteredClasses = new FilteredList<>(classes, item -> true);
    private Users student;

    @FXML private Label lblStudent;
    @FXML private Label lblStatus;
    @FXML private Label lblEmpty;
    @FXML private Label lblCourseName;
    @FXML private Label lblCourseCode;
    @FXML private Label lblInstructor;
    @FXML private Label lblSemester;
    @FXML private Label lblCredits;
    @FXML private Label lblSchedule;
    @FXML private Label lblCapacity;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbSemester;
    @FXML private TableView<LopHocDangKy> tblClasses;
    @FXML private TableColumn<LopHocDangKy, String> colClassCode;
    @FXML private TableColumn<LopHocDangKy, String> colCourseCode;
    @FXML private TableColumn<LopHocDangKy, String> colCourseName;
    @FXML private TableColumn<LopHocDangKy, String> colSemester;
    @FXML private TableColumn<LopHocDangKy, String> colSchedule;
    @FXML private TableColumn<LopHocDangKy, Integer> colCredits;
    @FXML private TableColumn<LopHocDangKy, String> colInstructor;
    @FXML private TableColumn<LopHocDangKy, String> colCapacity;
    @FXML private TableColumn<LopHocDangKy, String> colAvailability;
    @FXML private TableView<LopHocDangKy> tblRegistered;
    @FXML private TableColumn<LopHocDangKy, String> colRegisteredClass;
    @FXML private TableColumn<LopHocDangKy, String> colRegisteredCourse;
    @FXML private TableColumn<LopHocDangKy, String> colRegisteredSemester;
    @FXML private TableColumn<LopHocDangKy, String> colRegisteredSchedule;
    @FXML private TableColumn<LopHocDangKy, Integer> colRegisteredCredits;
    @FXML private TableColumn<LopHocDangKy, String> colRegisteredStatus;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancelRegistration;

    @FXML
    private void initialize() {
        colClassCode.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("hocKy"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("lichHoc"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("giangVien"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("siSo"));
        colAvailability.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colRegisteredClass.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        colRegisteredCourse.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colRegisteredSemester.setCellValueFactory(new PropertyValueFactory<>("hocKy"));
        colRegisteredSchedule.setCellValueFactory(new PropertyValueFactory<>("lichHoc"));
        colRegisteredCredits.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));
        colRegisteredStatus.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        tblClasses.setItems(filteredClasses);
        cmbSemester.getItems().add("Tất cả học kỳ");
        cmbSemester.getSelectionModel().selectFirst();
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cmbSemester.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        tblClasses.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) -> showSelection(selected));
        tblRegistered.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, selected) ->
                        btnCancelRegistration.setDisable(selected == null || !selected.isCoTheHuy()));
    }

    public void setStudent(Users student) {
        this.student = student;
        if (student == null || !"SINHVIEN".equalsIgnoreCase(student.getRole())
                || student.getMaSo() == null || student.getMaSo().isBlank()) {
            setStatus("Không xác định được mã sinh viên. Vui lòng quay lại và đăng nhập lại.", true);
            return;
        }
        lblStudent.setText(student.getHoTen() + "  ·  Mã sinh viên " + student.getMaSo());
        loadClasses();
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        loadClasses();
    }

    @FXML
    private void onConfirmRegistration(ActionEvent event) {
        LopHocDangKy selected = tblClasses.getSelectionModel().getSelectedItem();
        if (student == null || selected == null) return;
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận đăng ký");
        confirmation.setHeaderText("Đăng ký lớp " + selected.getMaLop() + "?");
        confirmation.setContentText(selected.getTenMon() + " · " + selected.getSoTinChi()
                + " tín chỉ · Học kỳ " + selected.getHocKy()
                + " · " + selected.getLichHoc());
        Optional<javafx.scene.control.ButtonType> response = confirmation.showAndWait();
        if (response.isEmpty() || response.get() != javafx.scene.control.ButtonType.OK) return;

        try {
            String result = registrationService.xuLyDangKyHoc(student.getMaSo(), selected.getMaLop());
            boolean success = result != null && result.startsWith("✅");
            setStatus(result == null ? "Không nhận được kết quả đăng ký." : result, !success);
            if (!success) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Đăng ký không thành công");
                alert.setHeaderText("Không thể đăng ký lớp " + selected.getMaLop());
                alert.setContentText(result == null
                        ? "Không nhận được kết quả đăng ký."
                        : result.replaceFirst("^❌\\s*", ""));
                alert.showAndWait();
            }
            loadClasses();
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Lỗi đăng ký lớp " + selected.getMaLop()
                    + " cho sinh viên " + student.getMaSo(), e);
            setStatus("Không thể xử lý đăng ký do lỗi hệ thống. Vui lòng thử lại sau.", true);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong đợi khi đăng ký tín chỉ.", e);
            setStatus("Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại sau.", true);
        }
    }

    @FXML
    private void onCancelRegistration(ActionEvent event) {
        LopHocDangKy selected = tblRegistered.getSelectionModel().getSelectedItem();
        if (student == null || selected == null || !selected.isCoTheHuy()) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận hủy đăng ký");
        confirmation.setHeaderText("Hủy lớp " + selected.getMaLop() + "?");
        confirmation.setContentText(selected.getTenMon() + " · Học kỳ " + selected.getHocKy()
                + " · " + selected.getLichHoc()
                + "\nLớp sẽ được cập nhật sĩ số và bạn có thể đăng ký lại nếu còn chỗ.");
        Optional<javafx.scene.control.ButtonType> response = confirmation.showAndWait();
        if (response.isEmpty() || response.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            String result = registrationService.huyDangKyHoc(student.getMaSo(), selected.getMaLop());
            boolean success = result != null && result.startsWith("✅");
            setStatus(result == null ? "Không nhận được kết quả hủy đăng ký." : result, !success);

            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
            alert.setTitle(success ? "Đã hủy đăng ký" : "Không thể hủy đăng ký");
            alert.setHeaderText(selected.getTenMon() + " · " + selected.getMaLop());
            alert.setContentText(result == null ? "Không nhận được kết quả hủy đăng ký."
                    : result.replaceFirst("^[✅❌]\\s*", ""));
            alert.showAndWait();
            loadClasses();
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Lỗi hủy lớp " + selected.getMaLop()
                    + " cho sinh viên " + student.getMaSo(), e);
            setStatus("Không thể hủy đăng ký do lỗi hệ thống. Vui lòng thử lại sau.", true);
            showError("Không thể hủy đăng ký do lỗi hệ thống. Vui lòng thử lại sau.");
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong đợi khi hủy đăng ký.", e);
            setStatus("Đã xảy ra lỗi khi hủy đăng ký. Vui lòng thử lại sau.", true);
            showError("Đã xảy ra lỗi khi hủy đăng ký. Vui lòng thử lại sau.");
        }
    }

    @FXML
    private void onBackToDashboard(ActionEvent event) {
        if (student == null) {
            showError("Không thể quay lại dashboard vì phiên sinh viên không còn hợp lệ.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashBoardView.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 760);
            DashBoardController controller = loader.getController();
            controller.setLoggedInUser(student);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Phần mềm Quản lý Tín chỉ - " + student.getHoTen());
            stage.centerOnScreen();
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Không thể quay lại dashboard.", e);
            showError("Không thể mở dashboard. Vui lòng thử lại.");
        }
    }

    private void loadClasses() {
        if (student == null || student.getMaSo() == null || student.getMaSo().isBlank()) return;
        try {
            List<LopHocDangKy> items = registrationService.xemDanhSachLopDangKy(student.getMaSo());
            classes.setAll(items);
            cmbSemester.getItems().setAll("Tất cả học kỳ");
            items.stream().map(LopHocDangKy::getHocKy).filter(value -> value != null && !value.isBlank())
                    .distinct().sorted().forEach(cmbSemester.getItems()::add);
            cmbSemester.getSelectionModel().selectFirst();
            applyFilters();
            tblRegistered.getSelectionModel().clearSelection();
            btnCancelRegistration.setDisable(true);
            tblRegistered.setItems(FXCollections.observableArrayList(
                    items.stream().filter(LopHocDangKy::isDaDangKy).toList()));
            lblEmpty.setText(items.isEmpty()
                    ? "Hiện chưa có lớp học nào được mở. Vui lòng kiểm tra lại sau."
                    : "Không tìm thấy lớp phù hợp với bộ lọc.");
            showSelection(tblClasses.getSelectionModel().getSelectedItem());
            if (items.isEmpty()) setStatus("Danh sách lớp đã tải nhưng hiện chưa có lớp mở.", false);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Không thể tải lớp đăng ký cho sinh viên " + student.getMaSo(), e);
            classes.clear();
            tblRegistered.setItems(FXCollections.observableArrayList());
            btnCancelRegistration.setDisable(true);
            lblEmpty.setText("Không thể tải danh sách lớp. Kiểm tra kết nối cơ sở dữ liệu rồi thử tải lại.");
            showSelection(null);
            setStatus("Lỗi tải dữ liệu lớp học. Vui lòng thử lại.", true);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Lỗi không mong đợi khi tải danh sách lớp.", e);
            classes.clear();
            tblRegistered.setItems(FXCollections.observableArrayList());
            btnCancelRegistration.setDisable(true);
            lblEmpty.setText("Đã xảy ra lỗi khi tải danh sách lớp.");
            showSelection(null);
            setStatus("Đã xảy ra lỗi khi tải dữ liệu.", true);
        }
    }

    private void applyFilters() {
        String query = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String semester = cmbSemester.getValue();
        filteredClasses.setPredicate(item -> {
            boolean semesterMatches = semester == null || semester.equals("Tất cả học kỳ")
                    || semester.equals(item.getHocKy());
            String searchable = (item.getMaLop() + " " + item.getMaMon() + " " + item.getTenMon()
                    + " " + item.getGiangVien()).toLowerCase(Locale.ROOT);
            return semesterMatches && searchable.contains(query);
        });
        boolean hasRows = !filteredClasses.isEmpty();
        lblEmpty.setVisible(!hasRows);
        lblEmpty.setManaged(!hasRows);
    }

    private void showSelection(LopHocDangKy item) {
        if (item == null) {
            lblCourseName.setText("Chọn một lớp để xem thông tin");
            lblCourseCode.setText("—");
            lblInstructor.setText("—");
            lblSemester.setText("—");
            lblCredits.setText("—");
            lblSchedule.setText("—");
            lblCapacity.setText("—");
            btnConfirm.setDisable(true);
            return;
        }
        lblCourseName.setText(item.getTenMon());
        lblCourseCode.setText(item.getMaMon() + "  ·  Mã lớp " + item.getMaLop());
        lblInstructor.setText(item.getGiangVien());
        lblSemester.setText(item.getHocKy());
        lblCredits.setText(item.getSoTinChi() + " tín chỉ");
        lblSchedule.setText(item.getLichHoc());
        lblCapacity.setText(item.getSiSo() + " sinh viên · " + item.getTrangThai());
        btnConfirm.setDisable(item.isDaDangKy()
                || !item.isCoLichHoc()
                || item.getSiSoHienTai() == null || item.getSiSoToiDa() == null
                || item.getSiSoHienTai() >= item.getSiSoToiDa());
    }

    private void setStatus(String message, boolean error) {
        lblStatus.setText(message);
        lblStatus.getStyleClass().removeAll("registration-message-success", "registration-message-error");
        lblStatus.getStyleClass().add(error ? "registration-message-error" : "registration-message-success");
        lblStatus.setVisible(true);
        lblStatus.setManaged(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText("Không thể tiếp tục");
        alert.showAndWait();
    }
}
