package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
import common.entity.OrganizationRole;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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
	private Text timeEvaluationText;

	@FXML
	private Button reEvaluateBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private Button declineBtn;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Text titledPane_Text;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		dueDateLabel.setVisible(false);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		titledPane.setCollapsible(false);
		titledPane.setText("Waiting for your action");

		approveBtn.setVisible(false);
		declineBtn.setVisible(false);
		reEvaluateBtn.setVisible(false);

		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);


		if (!thisRequest.getCurrentStage().equals("DECISION")) {

			titledPane.setText("This stage is done");
			titledPane_Text.setText("This Report has Been Approved. The stage is done.");
			titledPane.getStyleClass().remove("danger");
			titledPane.getStyleClass().add("success");
		}
		else{
			if(App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)
					|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER1)
					|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER2)){
				approveBtn.setVisible(true);
				declineBtn.setVisible(true);
				reEvaluateBtn.setVisible(true);
			}

		}
		setFieldsData();
	}

	private void setFieldsData() {
		OperationType ot = OperationType.DECISION_GetAllReportsByRID;
		String query = "SELECT * FROM `EvaluationReports` WHERE REQUESTID = " + thisRequest.getRequestID()
				+ " ORDER BY Report_ID DESC LIMIT 1;";
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void setFieldsData_ServerResponse(Object object) {
		ArrayList<EvaluationReport> reports = (ArrayList<EvaluationReport>) object;
		if (reports.size() > 0) {
			// SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
			EvaluationReport individualReport = reports.get(0);
			Date reportDate = individualReport.getTimestamp();
			Calendar reportDateCal = Calendar.getInstance();
			reportDateCal.setTime(reportDate);
			reportDateCal.add(Calendar.DATE, 7);
			reportDateCal.set(Calendar.HOUR_OF_DAY, 0);
			reportDateCal.set(Calendar.MINUTE, 0);
			reportDateCal.set(Calendar.SECOND, 0);
			reportDateCal.set(Calendar.MILLISECOND, 0);

			Date today = new Date(System.currentTimeMillis());
			Calendar todayCal = Calendar.getInstance();
			todayCal.set(Calendar.HOUR_OF_DAY, 0);
			todayCal.set(Calendar.MINUTE, 0);
			todayCal.set(Calendar.SECOND, 0);
			todayCal.set(Calendar.MILLISECOND, 0);

			long diff = reportDateCal.getTime().getTime() - todayCal.getTime().getTime();
			long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

			if (thisRequest.getCurrentStage().equals("DECISION")) {
				if (daysDiff >= 0) {
					titledPane_Text.setText(
							TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " Days left to complete this stage");
					titledPane.getStyleClass().removeAll();
					titledPane.getStyleClass().add("info");
				} else {
					titledPane_Text
							.setText("Stage in " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days late!");
					titledPane.getStyleClass().removeAll();
					titledPane.getStyleClass().add("danger");
				}

			}
			requiredChangeText.setText(individualReport.getRequired_change());
			expectedResultText.setText(individualReport.getExpected_result());
			constraintText.setText(individualReport.getExpected_risks());
			timeEvaluationText.setText(individualReport.getEstimated_time().toLocalDate().toString());
		}
	}

	@FXML
	void approveBtnClick(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());

		String query1 = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today) + "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

		OperationType ot = OperationType.DECI_UpdateDB;
		App.client.handleMessageFromClientUI(new Message(ot, query1));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
		thisRequest.setReturned(false);
		showAlert(AlertType.INFORMATION, "Evaluation Approved", "Request moved to execution phase...", null);
	}

	private static int c = 0;
	public void queryResult(Object object) {
		c++;
		boolean res = (boolean) object;
		if (c == 2) {
			if (res) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						loadPage("requestTreatment");
					}
				});
			} else
				showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
		}
	}

	@FXML
	void declineBtnClick(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		String query1 = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today) + "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query1));
		App.client.handleMessageFromClientUI(new Message(ot, query2));

		thisRequest.setPrevStage("DECISION");
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

}
