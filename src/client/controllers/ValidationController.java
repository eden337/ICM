package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import common.entity.Stage;
import common.entity.StageRole;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ValidationController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static ValidationController instance;
	private Stage thisStage;
	protected ChangeRequest thisRequest;

	
	@FXML
	private Text requestNumberTXT;
	  
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
	private Pane pane_msg;

	@FXML
	private Text textInMsgPane;

	@FXML
	private AnchorPane pane_form;

	@FXML
	private Button validateBtn;

	@FXML
	private Button failureReportBtn;

	@FXML
	private TextArea failReportTextArea;

	@FXML
	private Text idText11;

	@FXML
	private Button noBtn;

	@FXML
	private Text failReportLabel;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Text titledPane_Text;
	private boolean responseChairman = false;

	@FXML
	private Button btnRequestExtension;

	@FXML
	private Button btnAnswerStageExtensionRequest;
	private String reportResult;
	private Stage prevStage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reportResult = null;
		pane_msg.setVisible(false);
		pane_form.setVisible(false);
		instance = this;
		long estimatedTime = 0;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		this.requestNumberTXT.setText("Request Number "+thisRequest.getRequestID());
		btnAnswerStageExtensionRequest.setVisible(false);
		thisStage = thisRequest.getCurrentStageObject();

		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);

		checkPreConditions();

		if (!thisRequest.getCurrentStage().equals("VALIDATION")) {
			pane_msg.setVisible(true);
			return;
		}

		if (!App.user.isStageRole(thisRequest.getRequestID(), StageRole.TESTER)) {
			textInMsgPane.setFill(Color.BLUE);
			textInMsgPane.setText("Stage in progress");
			pane_msg.setVisible(true);
			inchargeTF.setText("Tester");
			if (thisStage.getExtension_reason() != null)
				btnAnswerStageExtensionRequest.setVisible(true);
			return;
		}

		// Otherwise: this is the Tester in his stage
		pane_form.setVisible(true);
		setExtensionVisability();

		estimatedTime = Duration.between(ZonedDateTime.now(), thisRequest.getCurrentStageObject().getDeadline())
				.toDays();
		estimatedTime += 1;
		Tools.setTitlePane(estimatedTime, titledPane, titledPane_Text);

		checkReport();
		inchargeTF.setText(thisRequest.getCurrentStageObject().getIncharge() + "");

		titledPane.setCollapsible(false);
		titledPane.setText("Waiting for your action");
		failReportLabel.setVisible(false);
		failReportTextArea.setVisible(false);
		failureReportBtn.setDisable(true);
		failureReportBtn.setVisible(false);
	}

	private void checkPreConditions() {
		OperationType ot = OperationType.VAL_GetInitData;
		String query = "SELECT `init`,`init_confirmed` FROM `Stage` WHERE `RequestID` = '" + thisRequest.getRequestID()
				+ "' AND `StageName` = 'VALIDATION' LIMIT 1";
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void checkPreConditions_ServerResponse(Object object) {
		List<Boolean> init_res = (List<Boolean>) object;
		boolean init = init_res.get(0);
		boolean init_confirmed = init_res.get(1);

		if (init_confirmed && init) {
			init();
			// rightPane.setVisible(true);
			return;
		}
		// else
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				loadPage("PreValidation");
			}
		});
	}

	private void init() {

		if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {

			if (responseChairman) {
				titledPane.getStyleClass().remove("danger");
				titledPane.getStyleClass().add("success");
				titledPane.setCollapsible(false);
				titledPane.setText("This stage is done.");
				titledPane_Text.setText("You have only a viewing permission.");
				titledPane_Text.setFill(Color.FORESTGREEN);
				titledPane_Text.setVisible(true);

				if (!thisRequest.getCurrentStage().equals("VALIDATION")) { // Watching only
					titledPane.getStyleClass().remove("danger");
					titledPane.getStyleClass().add("success");
					titledPane.setCollapsible(false);
					titledPane.setText("This stage is done.");
					titledPane_Text.setText("You have only a viewing permission.");
					titledPane_Text.setFill(Color.FORESTGREEN);
					titledPane_Text.setVisible(true);
				}
			}
		}
	}

	@FXML
	void failureReportBtnClicked(ActionEvent event) {
		OperationType ot = OperationType.VALID_GetPrevStage;
		String query = "SELECT * FROM `Stage`  WHERE `RequestID` = " + thisRequest.getRequestID()
				+ " AND `StageName` = 'EXECUTION' LIMIT 1";
		App.client.handleMessageFromClientUI(new Message(ot, query));
		setValidationTable();
	}

	void setStageTable() {
		c2 = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		Calendar c = Calendar.getInstance();
		Date today = new Date(System.currentTimeMillis());
		c.setTime(today);
		c.add(Calendar.DATE, 7);
		Date deadlineDate = c.getTime();
		String query1 = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		String query2 = " UPDATE `Stage` SET init = 0, init_confirmed = 0, `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'VALIDATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query3 = "INSERT INTO Repeted (RequestID, StageName, StartTime, EndTime, Deadline, Incharge) VALUES ('"
				+ prevStage.getRequestID() + "', '" + prevStage.getStageName() + "', '"
				+ prevStage.getStartTime().format(formatter) + "', '" + prevStage.getEndTime().format(formatter)
				+ "', '" + prevStage.getDeadline().format(formatter) + "', '" + prevStage.getIncharge() + "');";
		OperationType ot = OperationType.VALID_UpdateRepeated;
		App.client.handleMessageFromClientUI(new Message(ot, query1));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
		App.client.handleMessageFromClientUI(new Message(ot, query3));
	}

	public void appendPrevStageObject_ServerResponse(Object object) {
		this.prevStage = (common.entity.Stage) object;
		if (prevStage == null) {
			showAlert(AlertType.ERROR, "Error", "Could not find prev stage", null);
		} else { // all good
			setStageTable();
		}
	}

	private static int c2 = 0;

	public void queryResult2(Object object) {
		c2++;
		boolean res = (boolean) object;
		if (c2 == 3) {
			if (res) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						showAlert(AlertType.INFORMATION, "Return to Executer",
								"The report was sent to the execution stage", null);
						loadPage("requestTreatment");
					}
				});
			} else
				showAlert(AlertType.ERROR, "Error!", "Cannot re-execute", null);
		}
	}

	void checkReport() {
		OperationType ot = OperationType.VALID_GetReport;
		String query = "SELECT * FROM `Execution Failure Report` WHERE RequestID = " + thisRequest.getRequestID();
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void setValidationTable() {
		String query3 = " INSERT INTO `Execution Failure Report` (RequestID, Report) VALUES ( '"
				+ thisRequest.getRequestID() + "', '" + failReportTextArea.getText() + "')";
		OperationType ot = OperationType.VALID_updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query3));
	}

	public void setValidationTable_ServerResponse(Object object) {
		boolean res = (boolean) object;
		if (res) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					showAlert(AlertType.INFORMATION, "", "", null);
					loadPage("requestTreatment");
				}
			});
		}
	}

	public void getReport_ServerResponse(Object object) {
		this.reportResult = (String) object;
		System.out.println(reportResult);
		if (reportResult != null) { // if the returned result was back
			showAlert(AlertType.INFORMATION, "Report already exists", "The report already been sent to the executer",
					null);
			noBtn.setVisible(false);
		}
	}

	@FXML
	void noBtnClick(ActionEvent event) {
		failReportLabel.setVisible(true);
		failReportTextArea.setVisible(true);
		failureReportBtn.setVisible(true);
		failureReportBtn.setDisable(false);
		validateBtn.setDisable(true);
		noBtn.setDisable(true);
	}

	@FXML
	void validateBtnClicked(ActionEvent event) {
		c = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
		String tomorrowFormat = tomorrow.getYear() + "/" + tomorrow.getMonthValue() + "/" + tomorrow.getDayOfMonth();
		String query = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'VALIDATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query3 = " UPDATE `Stage` SET  `StartTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query4 = " UPDATE `Stage` SET  `Deadline` = '" + tomorrowFormat
				+ "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		String query5 = " UPDATE `Stage` SET  `PrevStage` = 'VALIDATION' where  `StageName` = 'CLOSURE' AND `RequestID` = '"
				+ thisRequest.getRequestID() + "';";
		OperationType ot = OperationType.VALID_UpdateDB;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
		App.client.handleMessageFromClientUI(new Message(ot, query3));
		App.client.handleMessageFromClientUI(new Message(ot, query4));
		App.client.handleMessageFromClientUI(new Message(ot, query5));
		thisRequest.setPrevStage("VALIDATION");

	}

	private static int c = 0;

	public void queryResult(Object object) {
		c++;
		boolean res = (boolean) object;
		if (c == 5) {
			if (res) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						showAlert(AlertType.INFORMATION, "Validation Complete",
								"The request moved to "+thisRequest.getInitiator()+" for closure.", null);
						loadPage("requestTreatment");
					}
				});
			} else
				showAlert(AlertType.ERROR, "Error!", "Could not update the closure update", null);
		}
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
