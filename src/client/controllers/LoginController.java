package client.controllers;

import client.App;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.EmployeeUser;
import common.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends AppController implements Initializable {
    public static LoginController instance;

    @FXML
    private RadioButton type_worker;

    @FXML
    private ToggleGroup userType;

    @FXML
    private RadioButton type_Student;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button btnLogin;

    @FXML
    private Text usernameEmptyWarning;

    @FXML
    private Text passwordEmptyWarning;

    @FXML
    private Text usertypeEmptyWarning;

    private ActionEvent thisScreen;

    public User user;

    /**
     * Submit button Action. check validation of form field and send login request.
     *
     * @param event
     */
    @FXML
    void submitLogin(ActionEvent event) {
        thisScreen = event;
        resetWarnings();
        if (!formChecker())
            return;
        sendLogin();
    }

    /**
     * Send login details to server in order to get successful / failed message.
     */
    private void sendLogin() {
        String tablename = type_Student.isSelected() ? "Students" : "Employees";
        OperationType ot;

        if (tablename.equals("Students"))
            ot = OperationType.LoginAsStudent;
        else
            ot = OperationType.LoginAsEmployee;
        String query = "SELECT * FROM " + tablename + " WHERE username = '" + username.getText() + "' AND password = '"
                + password.getText() + "';";
        Message msg = new Message(ot, query);
        App.client.handleMessageFromClientUI(msg);
    }

    /**
     * String tablename = type_Student.isSelected()? "Students" : "Employees";
     * OperationType ot;
     *
     * if(tablename.equals("Students")) ot = OperationType.LoginAsStudent; else ot =
     * OperationType.LoginAsEmployee;
     *
     * ArrayList<String> msgContent= new ArrayList<>(); String query = "SELECT *
     * FROM "+ tablename +" WHERE username = '"+username.getText()+"' AND password =
     * '"+password.getText()+"';"; msgContent.add(query);
     * msgContent.add(username.getText()); Message msg = new Message(ot, query);
     * App.client.handleMessageFromClientUI(msg);
     */
    /**
     * Server returns to this function with a login result.
     *
     * @param res
     */

    public void getLoginResult(Object res) {
        user = (User) res;
        // change window:
        if (user == null) // print error
            showAlert(AlertType.ERROR, "Login Failed", "Login Failed\nAlready connected", null);
        else {
            if (user.getFirstName().equals("fail"))
                showAlert(AlertType.ERROR, "Login Failed", "Login Failed!\nCheck user name and password!", null);
            else
                changeWindow((Stage) ((Node) thisScreen.getSource()).getScene().getWindow(), "/client/views/Main.fxml");
        }
    }

    /**
     * hide all form warning components.
     */
    private void resetWarnings() {
        usernameEmptyWarning.setVisible(false);
        passwordEmptyWarning.setVisible(false);
        usertypeEmptyWarning.setVisible(false);
    }

    /**
     * check form fields
     *
     * @return true : form valid. else: otherwise
     */
    private Boolean formChecker() {
        boolean flag = true;
        if (username.getText().isEmpty()) {
            usernameEmptyWarning.setVisible(true);
            flag = false;
        }
        if (password.getText().isEmpty()) {
            passwordEmptyWarning.setVisible(true);
            flag = false;
        }
        if (!type_Student.isSelected() && !type_worker.isSelected()) {
            usertypeEmptyWarning.setVisible(true);
            flag = false;
        }
        return flag;
    }

    @FXML
    void initialize() {
        assert type_worker != null : "fx:id=\"type_worker\" was not injected: check your FXML file 'Login.fxml'.";
        assert userType != null : "fx:id=\"userType\" was not injected: check your FXML file 'Login.fxml'.";
        assert type_Student != null : "fx:id=\"type_Student\" was not injected: check your FXML file 'Login.fxml'.";
        assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'Login.fxml'.";
        assert btnLogin != null : "fx:id=\"btnLogin\" was not injected: check your FXML file 'Login.fxml'.";
        assert password != null : "fx:id=\"fpassword\" was not injected: check your FXML file 'Login.fxml'.";
        assert usernameEmptyWarning != null : "fx:id=\"usernameEmptyWarning\" was not injected: check your FXML file 'Login.fxml'.";
        assert passwordEmptyWarning != null : "fx:id=\"passwordEmptyWarning\" was not injected: check your FXML file 'Login.fxml'.";
        assert usertypeEmptyWarning != null : "fx:id=\"usertypeEmptyWarning\" was not injected: check your FXML file 'Login.fxml'.";

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
    }
}
