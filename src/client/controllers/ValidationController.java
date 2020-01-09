package client.controllers;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
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
import javafx.fxml.Initializable;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		pane_msg.setVisible(false);
		pane_form.setVisible(false);
		instance = this;
		long estimatedTime = 0;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
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
			return;
		}

		// Otherwise: this is the Tester in his stage
		pane_form.setVisible(true);
		// titledPane.setVisible(false);
		// dueDateLabel.setVisible(true);
		// rightPane.setVisible(false);
		// TRY TO PLAY WITH THE ESTIMATED TIME IN TITLEPANE
		estimatedTime = Duration.between(ZonedDateTime.now(), thisRequest.getCurrentStageObject().getDeadline())
				.toDays();
		estimatedTime+=1;
		Tools.setTitlePane(estimatedTime, titledPane, titledPane_Text);
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
			// workDone.setVisible(true);// Change to false once you deal with permissions

			if (responseChairman) {
				titledPane.getStyleClass().remove("danger");
				titledPane.getStyleClass().add("success");
				titledPane.setCollapsible(false);
				titledPane.setText("This stage is done.");
				titledPane_Text.setText("You have only a viewing permission.");
				titledPane_Text.setFill(Color.FORESTGREEN);
				titledPane_Text.setVisible(true);
				// workDone.setVisible(false);

				if (!thisRequest.getCurrentStage().equals("VALIDATION")) { // Watching only
					titledPane.getStyleClass().remove("danger");
					titledPane.getStyleClass().add("success");
					titledPane.setCollapsible(false);
					titledPane.setText("This stage is done.");
					titledPane_Text.setText("You have only a viewing permission.");
					titledPane_Text.setFill(Color.FORESTGREEN);
					titledPane_Text.setVisible(true);
					// workDone.setVisible(false);
				}
			}
			/*
			 * if(App.user.isStageRole(thisRequest.getRequestID(),StageRole.EXECUTER)) {
			 * if(responseSupervisor) { daysTxt.setVisible(true); daysTxt.setDisable(true);
			 * DeadlinetimeExec.setVisible(true); DeadlinetimeExec.setDisable(true);
			 * workDone.setVisible(true); workDone.setDisable(false);
			 * SbmtExecBtn.setDisable(true); DeadlinetimeExec.setValue(addDays(save)); }else
			 * { SbmtExecBtn.setVisible(true); SbmtExecBtn.setDisable(false);
			 * workDone.setVisible(true); workDone.setDisable(true);
			 * daysTxt.setVisible(true); daysTxt.setDisable(false);
			 * DeadlinetimeExec.setVisible(true); DeadlinetimeExec.setDisable(true); }
			 *
			 * }
			 */
		}
	}

	// in this case we need to color validation back to red because it is incomplete
	@FXML
	void failureReportBtnClicked(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar c = Calendar.getInstance();
		Date today = new Date(System.currentTimeMillis());
		c.setTime(today);
		c.add(Calendar.DATE, 7);
		Date deadlineDate = c.getTime();
		String query = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));

		query = " UPDATE `Stage` SET init = 0, init_confirmed = 0, `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'VALIDATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Execution Failed!", "Please notify the execution leader for re-execution",
				null);
		thisRequest.setReturnedNote(failReportTextArea.getText());
		loadPage("requestTreatment");
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
		String query5 = " UPDATE `Stage` SET  `PrevStage` = 'VALIDATION' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
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
		if (c == 3) {
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
}
