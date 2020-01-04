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
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class AllocateController extends AppController implements Initializable {
    public static AllocateController instance;
    protected ChangeRequest thisRequest;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> cbEvaluator;

    @FXML
    private ComboBox<String> cbExecuter;

    @FXML
    private ComboBox<String> cbValidator;

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
    private Text dueDateLabel;


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


    @FXML
    void submitForm(ActionEvent event) {
        if (cbEvaluator.getValue() == null || cbExecuter.getValue() == null ||
                cbValidator.getValue() == null) {
            txtWarning.setVisible(true);
            return;
        }

        OperationType ot = OperationType.Allocate_SetRoles;
        String query;
        query = "INSERT INTO Stage (RequestID,StageName,Incharge) VALUES " +
                "('" + thisRequest.getRequestID() + "','EVALUATION','" + cbEvaluator.getValue() + "')," +
                "('" + thisRequest.getRequestID() + "','DECISION','" + "" + "')," +
                "('" + thisRequest.getRequestID() + "','EXECUTION','" + cbExecuter.getValue() + "')," +
                "('" + thisRequest.getRequestID() + "','VALIDATION','" + cbValidator.getValue() + "')," +
                "('" + thisRequest.getRequestID() + "','CLOSURE','" + "" + "')";
        App.client.handleMessageFromClientUI(new Message(ot, query));

        String query2 = "UPDATE Requests SET Treatment_Phase = 'EVALUATION' WHERE RequestID = '"
                + thisRequest.getRequestID() + "'";

        OperationType ot2 = OperationType.updateRequestStatus;
        App.client.handleMessageFromClientUI(new Message(ot2, query2));
        loadPage("requestTreatment");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtWarning.setVisible(false);
        instance = this;
        thisRequest = requestTreatmentController.Instance.getCurrentRequest();
        Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID, dueDateLabel, requestNameLabel, thisRequest);
        getUsersFromServer();

    }

    private void getUsersFromServer() {
        OperationType ot = OperationType.Allocate_GetITUsers;
        String query = "SELECT * FROM Employees WHERE Type = 'Engineer' AND USERNAME != '" + App.user.getUserName() + "'";
        //String query = "SELECT * FROM ITEngineer ";

        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void setComboBoxesData(Object object) {
        List<String> listOfUsers = (List<String>) object;
        ObservableList<String> oblist = FXCollections.observableArrayList(listOfUsers);
        int size = oblist.size();
        Random r = new Random(size);

        cbEvaluator.setItems(oblist);
        cbExecuter.setItems(oblist);
        cbValidator.setItems(oblist);
        new AutoCompleteBox<String>(cbEvaluator);
        new AutoCompleteBox<String>(cbExecuter);
        new AutoCompleteBox<String>(cbValidator);
        int rand = r.nextInt(size);
        //System.out.println(rand);
       // oblist.get(rand);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cbEvaluator.getSelectionModel().select(2);
            }
        });
    }

    public void showResult(Object object) {
        boolean res = (boolean) object;
        showAlert(Alert.AlertType.INFORMATION, "Role Appointment", res ? "Done!" : "Failed", null);
    }
}

