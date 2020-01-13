package client.controllers;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class PreValidationController extends AppController implements Initializable {

    public static PreValidationController instance;
    private ChangeRequest thisRequest;

    @FXML
    private Text idText;

    @FXML
    private Text requestID;

    @FXML
    private TextArea existingCondition;

    @FXML
    private TextArea descripitionsTextArea;

    @FXML
    private Text msg;

    @FXML
    private TextField inchargeTF;

    @FXML
    private Text departmentID;

    @FXML
    private Text idText1;

    @FXML
    private Text requestNameLabel;

    @FXML
    private Text idText2;

    @FXML
    private Text dueDateLabel;

    @FXML
    private Button btnAllocate;

    @FXML
    private Text txtMsg;

    @FXML
    private ComboBox<String> cbValidator;

    @FXML
    private Text txtWarning;

    @FXML
    void allocateTester(ActionEvent event) {
        if (cbValidator.getValue() == null) {
            txtWarning.setVisible(true);
            return;
        }

        OperationType ot = OperationType.PreValidation_SetRole;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        c.setTime(today);
        c.add(Calendar.DATE, 7);
        Date deadlineDate = c.getTime();

        String query1 = " UPDATE `Stage` SET  `StartTime` = '" + dateFormat.format(today) +
                "' ,`Deadline` = '" + dateFormat.format(deadlineDate) +
                "' , `Incharge` = '" +cbValidator.getValue() +
                "' , `init` = 1" +
                " , `init_confirmed` = 1" +
                " where  `StageName` = 'VALIDATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
        App.client.handleMessageFromClientUI(new Message(ot, query1));
        App.ForceAuthorizeAllUsers();


    }

    public void queryResult(Object object) {
        boolean res = (boolean) object;
        if (res) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadPage("requestTreatment");
                }
            });
        } else
            showAlert(Alert.AlertType.ERROR, "Error!", "Could not update.", null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        btnAllocate.setVisible(true);
        cbValidator.setDisable(false);
        thisRequest = requestTreatmentController.Instance.getCurrentRequest();
        Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);
        inchargeTF.setText("Tester");
        //getUsersFromServer();
        if(!App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {
        	btnAllocate.setVisible(false);
        	cbValidator.setDisable(true);
        	return;
        }
        getUsersFromServer();
    }

    private void getUsersFromServer() {
        OperationType ot = OperationType.PreValidation_GetCOMMITEE_MEMBERS;
        String query = "SELECT * FROM `Employees` WHERE `RoleInOrg` LIKE 'COMMITEE_MEMBER%'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void setComboBoxesData(Object object) {
        List<String> listOfUsers = (List<String>) object;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<String> oblist = FXCollections.observableArrayList(listOfUsers);
                int size = oblist.size();
                Random r = new Random(size);
                cbValidator.setItems(oblist);            }
        });

    }
}
