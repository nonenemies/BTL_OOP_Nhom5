package Controllers;

import Model.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashBoardController {
    @FXML private Label lblWelcome;
    private Users loggedInUser;
    private AppShellController appShell;

    public void setLoggedInUser(Users user) {
        loggedInUser = user;
        if (user != null) setWelcomeName(user.getHoTen());
    }

    public void setAppShell(AppShellController appShell) {
        this.appShell = appShell;
    }

    public void setWelcomeName(String name) {
        lblWelcome.setText("Xin chào, " + name);
    }

    @FXML
    public void onBtnDangKyClick(ActionEvent event) {
        if (appShell != null) appShell.showRegistration();
    }

    @FXML
    public void onBtnThoiKhoaBieuClick(ActionEvent event) {
        if (appShell != null) appShell.showTimetable();
    }

    @FXML
    public void onBtnDoiLopClick(ActionEvent event) {
        if (appShell != null) appShell.showExchange();
    }
}