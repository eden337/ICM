package client.controllers;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class ValidationController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static ValidationController instance;

	protected ChangeRequest thisRequest;

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
	private Button validateBtn;

	@FXML
	private Button failureReportBtn;

	@FXML
	private Text idText11;

	@FXML
	private Button noBtn;

	@FXML
	private Text failReportLabel;

	@FXML
	private TextArea failReportTextArea;

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
		dueDateLabel.setText(thisRequest.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		failReportLabel.setVisible(false);
		failReportTextArea.setVisible(false);
		failureReportBtn.setDisable(true);
	}
	//in this case we need to color validation back to red  because it is incomplete
	@FXML
	void failureReportBtnClicked(ActionEvent event) {
		String query = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.ERROR, "Execution Failed!", "Please notify the execution leader for re-execution", null);
		loadPage("requestTreatment");
	}

	@FXML
	void noBtnClick(ActionEvent event) {
		failReportLabel.setVisible(true);
		failReportTextArea.setVisible(true);
		failureReportBtn.setDisable(false);
		validateBtn.setDisable(true);
		noBtn.setDisable(true);
	}

	@FXML
	void validateBtnClicked(ActionEvent event) {
		String query = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Execution Validated!", "The Request moved to closure phase...", null);
		loadPage("requestTreatment");
	}

}
