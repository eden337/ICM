package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private ComboBox<String> cbTester;

    @FXML
    private ComboBox<String> cbIncharge;

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
        if (cbEvaluator.getValue() == null  || cbExecuter.getValue() == null ||
                cbTester.getValue() == null || cbIncharge.getValue() == null)
        {
            txtWarning.setVisible(true);
            return;
        }

        OperationType ot = OperationType.Allocate_SetRoles;
        String query;
        query = "INSERT INTO UserRoleInStage (REQUEST_ID,USERNAME, ROLE) VALUES " +
                "('" + thisRequest.getRequestID() + "','" + cbEvaluator.getValue() + "','EVALUATOR')," +
                "('" + thisRequest.getRequestID() + "','" + cbExecuter.getValue() + "','EXECUTER')," +
                "('" + thisRequest.getRequestID() + "','" + cbTester.getValue() + "','TESTER')," +
                "('" + thisRequest.getRequestID() + "','" + cbIncharge.getValue() + "','INCHARGE')";
        App.client.handleMessageFromClientUI(new Message(ot, query));

        String query2 = "UPDATE Requests SET Treatment_Phase = 'EVALUATION' WHERE RequestID = '"
                + thisRequest.getRequestID() + "'";
        OperationType ot2 = OperationType.updateRequestStatus;
        App.client.handleMessageFromClientUI(new Message(ot2, query2));
        loadPage("requestTreatment");
    }

    @FXML
    void initialize() {
        assert cbEvaluator != null : "fx:id=\"cbEvaluator\" was not injected: check your FXML file 'Allocate.fxml'.";
        assert cbExecuter != null : "fx:id=\"cbExecuter\" was not injected: check your FXML file 'Allocate.fxml'.";
        assert cbTester != null : "fx:id=\"cbTester\" was not injected: check your FXML file 'Allocate.fxml'.";
        assert cbIncharge != null : "fx:id=\"cbIncharge\" was not injected: check your FXML file 'Allocate.fxml'.";
        //We have to do something with the spinners!!!!
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
        cbEvaluator.setItems(oblist);
        cbExecuter.setItems(oblist);
        cbTester.setItems(oblist);
        cbIncharge.setItems(oblist);
        new AutoCompleteBox<String>(cbEvaluator);
        new AutoCompleteBox<String>(cbExecuter);
        new AutoCompleteBox<String>(cbTester);
        new AutoCompleteBox<String>(cbIncharge);
    }
    
    public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/client/views/Allocate.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Allocate Responsible Team");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load allocate prompt");
			e.printStackTrace();
		}
	}
    
    public void showResult(Object object) {
        boolean res = (boolean) object;
        showAlert(Alert.AlertType.INFORMATION, "Role Appointment", res ? "Done!" : "Failed", null);
    }
}
