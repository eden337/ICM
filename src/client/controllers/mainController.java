package client.controllers;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.OrganizationRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <code>Main controller</code>
 * class controls the template: 'main'
 * The main screen of the application. all the others pages will be open in this 'center' pane
 * menu actions,
 * log out,
 * Notifications
 * @version 1.0 - 01/2020
 * @author Group-10: Idan Abergel, Eden Schwartz, Ira Goor, Hen Hess, Yuda Hatam
 */
public class mainController extends AppController implements Initializable {
    public static mainController instance;
    public static Stage primaryStage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn1;

    @FXML
    private Button btn2;

    @FXML
    private Button btn3;

    @FXML
    private Text pageTitle;


    @FXML
    private Text text_hello;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Pane pHome;

    @FXML
    private Pane p2;

    @FXML
    private Pane p3;

    @FXML
    private Pane p4;

    @FXML
    private Pane p5;

    @FXML
    private Pane p6;
    // Menu Links


    /**
     * link to page 'home'
     * show 'Home' page in the template
     *
     * @param event
     */
    @FXML
    void gotoHome(MouseEvent event) {
        markPage(pHome);
        loadPage("Homepage", "Dashboard");
    }

    /**
     * link to page request
     * show 'New Request' page in the template
     *
     * @param event
     */
    @FXML
    void gotoNewRequest(MouseEvent event) {
        markPage(p2);
        loadPage("ChangeRequestForm", "Change Request");
    }

    @FXML
    void goToViewRequest(MouseEvent event) {
        markPage(p3);
        loadPage("watchRequest", "My Requests");
    }


    @FXML
    void gotoRequestTreatment(MouseEvent event) {
        markPage(p4);
        loadPage("requestTreatment", "Request Treatment and Management");
    }

    @FXML
    void goToStats(MouseEvent event) {
        markPage(p5);
        loadPage("Reports", "Reports Generator");
    }

    @FXML
    void goToManager(MouseEvent event) {
        markPage(p6);
        loadPage("ManagerPage", "Manager view");
    }

    /**
     * select a pain to be colored as current page
     *
     * @param p : Pane to be colored as selected.
     */
    void markPage(Pane p) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (p == null) return;
                p.getStyleClass().add("bg_currentPage");
                if (p != pHome)
                    pHome.getStyleClass().remove("bg_currentPage");
                if (p != p2)
                    p2.getStyleClass().remove("bg_currentPage");
                if (p != p3)
                    p3.getStyleClass().remove("bg_currentPage");
                if (p != p4)
                    p4.getStyleClass().remove("bg_currentPage");
                if (p != p5)
                    p5.getStyleClass().remove("bg_currentPage");
                if (p != p6)
                    p6.getStyleClass().remove("bg_currentPage");
            }
        });
    }


    @FXML
    void initialize() {
        assert btn1 != null : "fx:id=\"btn1\" was not injected: check your FXML file 'Main.fxml'.";
        assert btn2 != null : "fx:id=\"btn2\" was not injected: check your FXML file 'Main.fxml'.";
        assert btn3 != null : "fx:id=\"btn3\" was not injected: check your FXML file 'Main.fxml'.";

    }


    /**
     * Load FXML page file as a child of main window. Loaded page resolution
     * should be 1000*710.
     *
     * @param fxmlname  : String (Without file type name)
     * @param pageTitle : String
     */
    protected void loadPage(String fxmlname, String pageTitle) {
        setTitle(pageTitle);
        loadPage(fxmlname);
    }

    /**
     * Change current view Title
     *
     * @param pageTitle : String
     */
    protected void setTitle(String pageTitle) {
        this.pageTitle.setText(pageTitle);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        primaryStage = App.stage;
        setUser();
        pHome.setVisible(false);
        p2.setVisible(false);
        p3.setVisible(false);
        p4.setVisible(false);
        p5.setVisible(false);
        p6.setVisible(false);

        loadPage("Homepage");
    }

    public void initialize_afterUserUpdate() {
        if (!App.appInitialized) {
            gotoHome(null);
            App.appInitialized = true;
        }

        // DEFAULT
        pHome.setVisible(true);
        p2.setVisible(true);
        p3.setVisible(true);


        p6.setVisible(false);

        // Menu Permissions:
        if (App.user.isEngineer()) {
            pHome.setVisible(true);
            p2.setVisible(true);
            p3.setVisible(true);
            p4.setVisible(true);
        }


        if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR)) {
            p5.setVisible(true);
            p6.setVisible(true);
        }

        String showRole= !App.user.getOrgRole().equals("") ? " ( " + App.user.getOrgRole() + " ) " :"";
        text_hello.setText("Hello, " + App.user.getFirstName() + showRole);

    }

    public void showAlertAtMainController(AlertType at, String title, String content, String header) {
        showAlert(at, title, content, header);
    }

    /**
     * Take object <code>User</code> from <code>Login</code> class and set it on <code>App</code> class.
     */
    public void setUser() {
        App.user = LoginController.instance.user;
        App.user.updatePermissions();

        if (App.user != null)
            text_hello.setText("Hello, " + App.user.getFirstName());
        else
            text_hello.setText("Hello, ");
    }

    /**
     * Logout Action:
     * go to login page and erase current user object.
     *
     * @param event
     */
    @FXML
    void logout(MouseEvent event) {

        App.client.handleMessageFromClientUI(new Message(OperationType.Logout, App.user.getUserName()));
        App.user = null;
        //BypassedApp.main(null);
        changeWindow((Stage) ((Node) event.getSource()).getScene().getWindow(), "/client/views/Login.fxml");
    }

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }


}
