package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EvaluationReportController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static EvaluationReportController instance;

	protected ChangeRequest thisRequest;
	private boolean firstReportForRequest;

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
	private Button SbmtEvlBtn;

	@FXML
	private DatePicker timeEvlBox;

	@FXML
	private TextArea reqChngTXT;

	@FXML
	private TextArea expResTXT;

	@FXML
	private TextArea cnstrntTXT;

	@FXML
	private Text msgFix;

	@FXML
	private TitledPane titledPane;

	@FXML
	void SbmtEvlBtnClick(ActionEvent event) {

		if (departmentID.getText().isEmpty() || reqChngTXT.getText().isEmpty() || expResTXT.getText().isEmpty()
				|| timeEvlBox.getValue() == null) {
			showAlert(AlertType.WARNING, "EvaluationReport", "You must fill all required fields.", null);
			return;
		}

		String systemID, requiredChange, expectedResult, expectedRisk, estimatedTime;
		systemID = this.departmentID.getText();
		requiredChange = this.reqChngTXT.getText();
		expectedResult = this.expResTXT.getText();
		expectedRisk = this.cnstrntTXT.getText();
		LocalDate date = this.timeEvlBox.getValue();
		estimatedTime = date.toString();

		String query = "INSERT INTO `EvaluationReports` (`RequestID`, `System_ID`, `Required_Change`, `Expected_Result`, `Expected_Risks`, `Estimated_Time`) VALUES ("

				+ "'" + thisRequest.getRequestID() + "', " + "'" + systemID + "', " + "'" + requiredChange + "', " + "'"
				+ expectedResult + "', " + "'" + expectedRisk + "', " + "'" + estimatedTime + "');";

		OperationType ot = OperationType.InsertEvaluation;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		String query1 = "UPDATE Requests SET Treatment_Phase = 'DECISION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot1 = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		// showAlert(AlertType.INFORMATION, "Evaluation Approved", "Request moved to
		// execution phase...", null);
		loadPage("requestTreatment");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		msgFix.setVisible(false);
		dueDateLabel.setVisible(false);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();

		if (!thisRequest.getCurrentStage().equals("EVALUATION")) { // Watching only
			titledPane.getStyleClass().remove("danger");
			titledPane.getStyleClass().add("success");
			titledPane.setCollapsible(false);
			titledPane.setText("This stage is done.");
			SbmtEvlBtn.setVisible(false);
			timeEvlBox.setDisable(true);
			msgFix.setText("You have only a viewing permission.");
			msgFix.setFill(Color.FORESTGREEN);
			msgFix.setVisible(true);
			reqChngTXT.setEditable(false);
			expResTXT.setEditable(false);
			cnstrntTXT.setEditable(false);
			timeEvlBox.setEditable(false);

		}
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);
		setFieldsData();
	}

	private void setFieldsData() {
		OperationType ot = OperationType.EVAL_GetAllReportsByRID;
		String query = "SELECT * FROM `EvaluationReports` WHERE RequestID = " + thisRequest.getRequestID()
				+ " ORDER BY Report_ID DESC LIMIT 1";
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void setFieldsData_ServerResponse(Object object) {
		ArrayList<EvaluationReport> reports = (ArrayList<EvaluationReport>) object;
		if (reports.size() > 0) {
			if (thisRequest.getCurrentStage().equals("EVALUATION"))
				msgFix.setVisible(true);
			EvaluationReport individualReport = reports.get(0);
			reqChngTXT.setText(individualReport.getRequired_change());
			expResTXT.setText(individualReport.getExpected_result());
			cnstrntTXT.setText(individualReport.getExpected_risks());
			timeEvlBox.setValue(individualReport.getEstimated_time().toLocalDate());
		}

	}

	public void queryResult(Object object) {
		boolean res = (boolean) object;

		if (res)
			showAlert(AlertType.INFORMATION, "Evaluation Success", "Report updated", null);
		else
			showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
	}
}
