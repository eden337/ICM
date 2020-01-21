package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Controller for Allocate page
 * @version 1.0 - 01/2020
 * @author Group-10: Idan Abergel, Eden Schwartz, Ira Goor, Hen Hess, Yuda Hatam
 */

public class AllocateController extends AppController implements Initializable {
    public static AllocateController instance;
    private String evaluator; 
    protected ChangeRequest thisRequest;
    @FXML
    private ResourceBundle resources;

    @FXML
    private Text requestNumberTXT;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> cbEvaluator;

    @FXML
    private ComboBox<String> cbExecuter;


    @FXML
    private Text txtWarning;

    @FXML
    private TextField inchargeTF;

    @FXML
    private Text departmentID;

    @FXML
    private Text requestID;

    @FXML
    private TextArea existingCondition;

    @FXML
    private TextArea descripitionsTextArea;


    @FXML
    private Text requestNameLabel;

    @FXML
    private Spinner<Integer> evaluationTime;

    @FXML
    private Spinner<Integer> decisionTime;

    @FXML
    private Spinner<Integer> executionTime;

    @FXML
    private Spinner<Integer> ValidationTime;

    private static int c = 0;

    /**
     *
     * @param event
     * @apiNote This function responsible of submitting of the initialization of the INIT phase.
     */

    @FXML
    void submitForm(ActionEvent event) {
        c = 0;
        boolean init = thisRequest.getCurrentStage().equals("INIT");
        String query;
        if (cbEvaluator.getValue() == null || cbExecuter.getValue() == null) {
            txtWarning.setVisible(true);
            return;
        }
        OperationType ot2 = OperationType.Allocate_UpdateRoles;

        if (init) {
            OperationType ot = OperationType.Allocate_SetRoles;
            query = "INSERT INTO Stage (RequestID,StageName,Incharge) VALUES " +
                    "('" + thisRequest.getRequestID() + "','EVALUATION','" + cbEvaluator.getValue() + "')," +
                    "('" + thisRequest.getRequestID() + "','DECISION','" + "" + "')," +
                    "('" + thisRequest.getRequestID() + "','EXECUTION','" + cbExecuter.getValue() + "')," +
                    "('" + thisRequest.getRequestID() + "','VALIDATION','" + "" + "')," +
                    "('" + thisRequest.getRequestID() + "','CLOSURE','" + "" + "')";
            App.client.handleMessageFromClientUI(new Message(ot, query));

            c = 1;
            String query2 = "UPDATE Requests SET Treatment_Phase = 'EVALUATION', Status ='ACTIVE' WHERE RequestID = '"
                    + thisRequest.getRequestID() + "'";
            App.client.handleMessageFromClientUI(new Message(ot2, query2));
            App.ForceAuthorizeAllUsers();
        } else {
            c = 2;
            query = "UPDATE Stage SET Incharge = '" + cbEvaluator.getValue() + "' WHERE StageName = 'EVALUATION' AND RequestID = '" + thisRequest.getRequestID() + "';";
            App.client.handleMessageFromClientUI(new Message(ot2, query));
            query = "UPDATE Stage SET Incharge = '" + cbExecuter.getValue() + "' WHERE StageName = 'EXECUTION' AND RequestID = '" + thisRequest.getRequestID() + "';";
            App.client.handleMessageFromClientUI(new Message(ot2, query));

        }
        loadPage("requestTreatment");
    }

    /**
     *
     * @param object
     * @apiNote this function is the response from the server  of the query updates from submitForm function
     */

    public void allocQueryResult(Object object) {
        c--;
        boolean res = (boolean) object;
        if (c == 0 || !res) {
            if (res) {
                showAlert(AlertType.INFORMATION, "Allocation Approved", "The in-charges of the request were assigned", null);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadPage("requestTreatment", "Request Treatment and Management");
                    }
                });
            } else
                showAlert(AlertType.ERROR, "Error!", "Could not assign employees", null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	txtWarning.setVisible(false);
        instance = this;
        thisRequest = requestTreatmentController.Instance.getCurrentRequest();
        Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID, null, requestNameLabel, thisRequest);
        getUsersFromServer();
        this.requestNumberTXT.setText("Request Number "+thisRequest.getRequestID());
    }

    /**
     * @apiNote
     * getUsersFromServer gets all the engineers that available on the SQL database, this function is called via the initialize function.
     */
    private void getUsersFromServer() {
        OperationType ot = OperationType.Allocate_GetITUsers;
        String query = "SELECT * FROM Employees WHERE Type = 'Engineer' AND USERNAME != '" + App.user.getUserName() + "'";
        String query2 = "SELECT `username` FROM `Systems Techncian` WHERE SystemID='"+thisRequest.getInfoSystem()+"'";

        OperationType ot2 = OperationType.Allocate_System_Incharge;
        App.client.handleMessageFromClientUI(new Message(ot2, query2));
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    /**
     *
     * @param object
     * client response from server via Allocate_GetITUsers, the Object is converted to "List<String>" which is loaded on the Allocate Incharges page.
     * mainly, the function loads the data of listOfUsers into the combobox on the screens.
     */
    public void setComboBoxesData(Object object) {
        List<String> listOfUsers = (List<String>) object;
        ObservableList<String> oblist = FXCollections.observableArrayList(listOfUsers);
        int size = oblist.size();
        Random r = new Random();

        cbEvaluator.setItems(oblist);
        cbExecuter.setItems(oblist);
        new AutoCompleteBox<String>(cbEvaluator);
        new AutoCompleteBox<String>(cbExecuter);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cbEvaluator.getSelectionModel().select(evaluator);
            }
        });
    }
    /**
     * 
     * @author Ira Goor 
     * method purpose:set from DB evaluator to be the one in charge of the system
     *
     * @param object
     */
    public void setEvaluuator(Object object)
    {
    	evaluator=(String)object;
    }

    /**
     *
     * @param object
     * response from the server in order to alert the user if the the roll appointment is either done or not, it is a response from Allocate_SetRoles
     */

    public void showResult(Object object) {
        boolean res = (boolean) object;
        showAlert(Alert.AlertType.INFORMATION, "Role Appointment", res ? "Done!" : "Failed", null);
    }
}

