package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DecisionControllerOld extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static DecisionControllerOld instance;

	protected ChangeRequest thisRequest;
	protected EvaluationReport thisEvaReport;

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
	private TextField reqChngTXT;

	@FXML
	private TextField expResTXT;

	@FXML
	private TextField cnstrntTXT;

	@FXML
	private Button SbmtEvlBtn;

	@FXML
	private DatePicker timeEvlBox;

	@FXML
	private Button reEvaluateBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private Button declineBtn;

	@FXML
	private Text requiredChangeText;

	@FXML
	private Text expectedResultText;

	@FXML
	private Text constraintText;

	@FXML
	private Text timeEvaluationText;

	@FXML
	void approveBtnClick(ActionEvent event) {
		String query = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Evaluation Approved", "Request moved to execution phase...", null);
		reEvaluateBtn.setDisable(true);
		approveBtn.setDisable(true);
		declineBtn.setDisable(true);
	}

	@FXML
	void declineBtnClick(ActionEvent event) {
		String query = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.ERROR, "Evaluation Declined", "Request moved to closure phase...", null);
		reEvaluateBtn.setDisable(true);
		approveBtn.setDisable(true);
		declineBtn.setDisable(true);
	}

	@FXML
	void reEvaluateBtnClick(ActionEvent event) {
		String query = "UPDATE Requests SET Treatment_Phase = 'EVALUATION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Need to Re-Evaluate", "Request moved back to evaluation phase...", null);
		reEvaluateBtn.setDisable(true);
		approveBtn.setDisable(true);
		declineBtn.setDisable(true);

	}

	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/client/views/DecisionOld.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Decision");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load evaluation prompt");
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueDateLabel.setVisible(true);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		requestID.setText(thisRequest.getRequestID() + "");
		departmentID.setText(thisRequest.getInfoSystem());
		requestNameLabel.setText(thisRequest.getInitiator());
		existingCondition.setText(thisRequest.getExistingCondition());
		descripitionsTextArea.setText(thisRequest.getRemarks());
		inchargeTF.setText("");
		dueDateLabel.setText(thisRequest.getDueDate().toString());
	}

	/*
	 * private void getDatafromServer() { App.client.handleMessageFromClientUI(new
	 * Message(OperationType.getRequirementData, setTableByUser())); }
	 * 
	 * private String setTableByUser() {
	 * 
	 * check user premissons and return premissions by role
	 * 
	 * // if user is supervisor return
	 * "SELECT * FROM `Evaluation Reports` WHERE RequestID = "+thisRequest.
	 * getRequestID(); }
	 */

}
