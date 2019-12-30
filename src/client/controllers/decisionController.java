package client.controllers;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class decisionController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static decisionController instance;

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
	private TextArea requiredChangeText;

	@FXML
	private TextArea expectedResultText;

	@FXML
	private TextArea constraintText;

	@FXML
	private Text msgFix;

	@FXML
	private Text timeEvaluationText;

	@FXML
	private Button reEvaluateBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private Button declineBtn;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		msgFix.setVisible(false);
		dueDateLabel.setVisible(false);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();

		requestID.setText(thisRequest.getRequestID()+"");
		departmentID.setText(thisRequest.getInfoSystem());
		requestNameLabel.setText(thisRequest.getInitiator());
		existingCondition.setText(thisRequest.getExistingCondition());
		descripitionsTextArea.setText(thisRequest.getRemarks());
		inchargeTF.setText("");
		dueDateLabel.setText(thisRequest.getDueDate().toString());
		setFieldsData();
	}

	private void setFieldsData(){
		OperationType ot = OperationType.DECISION_GetAllReportsByRID;
		String query= "SELECT * FROM `EvaluationReports` WHERE REQUESTID = " + thisRequest.getRequestID() + " ORDER BY Report_ID DESC LIMIT 1;";
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void setFieldsData_ServerResponse(Object object){
		ArrayList<EvaluationReport> reports = (ArrayList<EvaluationReport>) object;
		if(reports.size() > 0)
		{
			if(!thisRequest.getCurrentStage().equals("DECISION"))
				msgFix.setVisible(true);
			EvaluationReport individualReport = reports.get(0);

			requiredChangeText.setText(individualReport.getRequired_change());
			expectedResultText.setText(individualReport.getExpected_result());
			constraintText.setText(individualReport.getExpected_risks());
			timeEvaluationText.setText(individualReport.getEstimated_time().toLocalDate().toString());
		}
	}

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
		loadPage("requestTreatment");
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
		loadPage("requestTreatment");
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
		loadPage("requestTreatment");
	}

	public void queryResult(Object object) {
		boolean res = (boolean) object;

		if (res)
			showAlert(AlertType.INFORMATION, "Evaluation Success", "Report updated", null);
		else
			showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
	}
}
