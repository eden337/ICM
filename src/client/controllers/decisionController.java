package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EmailContent;
import common.entity.EvaluationReport;
import common.entity.OrganizationRole;
import common.entity.Stage;
import javafx.application.Platform;
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
import server.controllers.EmailSender;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
	private common.entity.Stage thisStage;

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

	@FXML
	private Button btnRequestExtension;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		long estimatedTime = 0;
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		thisStage = thisRequest.getCurrentStageObject();

		dueDateLabel.setVisible(false);

		titledPane.setCollapsible(false);
		titledPane.setText("Waiting for your action");

		approveBtn.setVisible(false);
		declineBtn.setVisible(false);
		reEvaluateBtn.setVisible(false);
		inchargeTF.setText("Committee Chairman");
		btnRequestExtension.setVisible(false);

		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);

		if (!thisRequest.getCurrentStage().equals("DECISION")) {

			titledPane.setText("This stage is done");
			titledPane_Text.setText("This Report has Been Approved. The stage is done.");
			titledPane.getStyleClass().remove("danger");
			titledPane.getStyleClass().add("success");
		} else {
			if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {
				approveBtn.setVisible(true);
				declineBtn.setVisible(true);
				reEvaluateBtn.setVisible(true);
				setExtensionVisability();
			}else{
				if (thisStage.getExtension_days() != 0)
					btnRequestExtension.setVisible(true);
			}
		}

		setFieldsData();
		estimatedTime = Duration.between(ZonedDateTime.now(), thisRequest.getCurrentStageObject().getDeadline())
				.toDays();
		estimatedTime += 1;
		Tools.setTitlePane(estimatedTime, titledPane, titledPane_Text);
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
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

		OperationType ot = OperationType.DECI_UpdateDB;
		App.client.handleMessageFromClientUI(new Message(ot, query1));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
		thisRequest.setReturned(false);
		showAlert(AlertType.INFORMATION, "Evaluation Approved", "Request moved to execution phase...", null);
		loadPage("requestTreatment");
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
		thisRequest.setPrevStage("DECISION");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
		String tomorrowFormat = tomorrow.getYear() + "/" + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
		String query1 = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query3 = " UPDATE `Stage` SET  `StartTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query4 = " UPDATE `Stage` SET  `Deadline` = '" + tomorrowFormat
				+ "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query5 = " UPDATE `Stage` SET  `PrevStage` = 'DECISION' where  `StageName` = 'CLOSURE' AND `RequestID` = '"
				+ thisRequest.getRequestID() + "';";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query1));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
		App.client.handleMessageFromClientUI(new Message(ot, query3));
		App.client.handleMessageFromClientUI(new Message(ot, query4));
		App.client.handleMessageFromClientUI(new Message(ot, query5));
		showAlert(AlertType.INFORMATION, "Evaluation Declined", "Request moved to closure phase...", null);
		reEvaluateBtn.setDisable(true);
		approveBtn.setDisable(true);
		declineBtn.setDisable(true);
		loadPage("requestTreatment");
	}

	@FXML
	void reEvaluateBtnClick(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());

		String query = "UPDATE Requests SET Treatment_Phase = 'EVALUATION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Need to Re-Evaluate", "Request moved back to evaluation phase...", null);
// Select Email from Employees where username = thisrequest.getStageObject().getIncharge
//
		query = "SELECT e.EMAIL FROM Employees e, Stage s WHERE e.username = s.Incharge and s.StageName='EVALUATION' and s.RequestID = "
				+ thisRequest.getRequestID() + ";";
		OperationType ot2 = OperationType.SendGeneralEmail;
		App.client.handleMessageFromClientUI(new Message(ot2, query));

		query = " UPDATE `Stage` SET init = 0, init_confirmed = 0, `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		
		query = " UPDATE `Stage` SET `StartTime` = '',`Deadline` = '',`EndTime` = '', where  `StageName` = 'EVALUATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		Stage currentStage = thisRequest.getCurrentStageObject();
		
	/*	query = "INSERT into 'Repeted'(RequestID, StageName, StartTime, EndTime, Deadline, Incharge, Delay, Extend) VALUES ("
				+currentStage.getRequestID() +", "+ currentStage.getStageName()+", "+currentStage.getStartTime()+", "+ currentStage.getEndTime()+", "
				+currentStage.getDeadline()  +", "+ currentStage.getIncharge()+", "+ currentStage.getDe;
		*/
		reEvaluateBtn.setDisable(true);
		approveBtn.setDisable(true);
		declineBtn.setDisable(true);
		loadPage("requestTreatment");
	}


	// Extensions:
	private void setExtensionVisability() {
		btnRequestExtension.setVisible(false);
		long daysDifference = Tools.DaysDifferenceFromToday(thisRequest.getCurrentStageObject().getDeadline());
		if (daysDifference >= -3) {
			btnRequestExtension.setVisible(true);
			if (thisStage.getExtension_days() != 0)
				btnRequestExtension.setDisable(true);
		}
	}

	@FXML
	void requestExtension(ActionEvent event) {
		start(new javafx.stage.Stage());
	}

	public void start(javafx.stage.Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/client/views/Extension.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Extension");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load execution prompt");
			e.printStackTrace();
		}
	}
}
