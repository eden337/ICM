package client.controllers;


import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.OrganizationRole;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

public class homepageController extends AppController implements Initializable {
    public static homepageController instance;
    @FXML
    private ResourceBundle resources;

    @FXML
    private Text greeting;

    @FXML
    private URL location;

    @FXML
    private Text userRequestsInTreatment;

    @FXML
    private Text userTotalRequest;

    @FXML
    private Text UserNeedToTreat;

    @FXML
    private TableView<?> table5last;

    @FXML
    private Pane paneForIT;

    @FXML
    void gotoMyRequests(MouseEvent event) {
        mainController.instance.goToViewRequest(null);
    }

    @FXML
    void gotoRequestTreatment(MouseEvent event) {
        mainController.instance.goToViewRequest(null);
    }

    @FXML
    void initialize() {
        assert userRequestsInTreatment != null : "fx:id=\"userRequestsInTreatment\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert userTotalRequest != null : "fx:id=\"userTotalRequest\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert UserNeedToTreat != null : "fx:id=\"UserNeedToTreat\" was not injected: check your FXML file 'Homepage.fxml'.";
        assert table5last != null : "fx:id=\"table5last\" was not injected: check your FXML file 'Homepage.fxml'.";

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        paneForIT.setVisible(false);
        setGreeting();
        initData_Request();

        if (App.user.isEngineer())
            paneForIT.setVisible(true);

    }

    private void setGreeting() {
        String greetingText = "";
        GregorianCalendar time = new GregorianCalendar();
        int hour = time.get(Calendar.HOUR_OF_DAY);
//        int min = time.get(Calendar.MINUTE);
        int day = time.get(Calendar.DAY_OF_MONTH);
//        int month = time.get(Calendar.MONTH) + 1;
//        int year = time.get(Calendar.YEAR);

        if (hour < 4)
            greetingText = "Good Night";
        else if (hour < 12)
            greetingText = "Good Morning";
        else if (hour < 17 && !(hour == 12))
            greetingText = "Good Afternoon";
        else if (hour == 12)
            greetingText = "Good Noon";
        else
            greetingText = "Good Evening";


        greeting.setText(greetingText + ", " + App.user.getFirstName());
    }


    private void initData_Request() {
        App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyTotalRequests,
                "SELECT COUNT(*) FROM Requests WHERE USERNAME = '" + App.user.getUserName() + "';"));
        App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyActiveRequests,
                "SELECT COUNT(*) FROM Requests WHERE Status = 'ACTIVE' AND USERNAME = '" + App.user.getUserName() + "';"));
        App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyRequestTreatment, setTableByUser()));
    }

    // Main_getMyTotalRequests
    public void Main_getMyTotalRequests_Response(Object res) {
        if (res == null) return;
        userTotalRequest.setText(res.toString());
    }

    public void Main_getMyActiveRequests_Response(Object res) {
        if (res == null) return;
        userRequestsInTreatment.setText(res.toString());
    }

    public void Main_getMyRequestTreatment_Response(Object res) {
        if (res == null) return;
        UserNeedToTreat.setText(res.toString());
    }


    /**
     * Get Request Treatment query by permission
     *
     * @return
     */
    private String setTableByUser() {
        String query = "Select COUNT(*) FROM Requests ";
        if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))
            return query;
        if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR)) {
            query = "SELECT * FROM Requests WHERE `Status` = 'SUSPENDED'";
            return query;
        }
        query = "SELECT r.`RequestID`, `USERNAME`, `Position`, `Email`, `Existing_Cond`, `Wanted_Change`,"
                + " `Treatment_Phase`, `Status`, `Reason`, `Curr_Responsible`, `SystemID`, `Comments`, `Date`,"
                + " `Due_Date`, `FILE` FROM `Requests` as r , `Stage` as s " + "WHERE r.`RequestID` = s.`RequestID`"
                + "AND r.`Treatment_Phase` = s.`StageName`" + // active stage
                " AND `incharge` = '" + App.user.getUserName() + "'";

        if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER1)
                || App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER2)
                || App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {
            // add option to see active decision stages in addition to other permission of
            // these user
            query += " OR r.`RequestID` = s.`RequestID` AND r.`Treatment_Phase` = 'DECISION' AND s.`StageName` = 'DECISION'";

            if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN))
                query += " OR r.`RequestID` = s.`RequestID` AND r.`Treatment_Phase` = 'VALIDATION' AND s.`StageName` = 'VALIDATION' AND 'init_confirmed' = 0";

        }
        // general:
        return query;
    }
}
