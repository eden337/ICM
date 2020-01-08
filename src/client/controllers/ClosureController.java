package client.controllers;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EmployeeUser;
import common.entity.OrganizationRole;
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
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class ClosureController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static ClosureController instance;

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
	private Text finishedStatus;

	@FXML
	private Button closeProcessBtn;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Text msgFix;

	@FXML
	private Text deadlineText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueDateLabel.setVisible(true);
		instance = this;
		long estimatedTime = 0;
		pane_msg.setVisible(false);
		pane_form.setVisible(false);

		inchargeTF.setText("Supervisor");

		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);

		if (!thisRequest.getCurrentStage().equals("CLOSURE")) {
			pane_msg.setVisible(true);
			return;
		}
		
		if (!App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
			System.out.println("2");
			textInMsgPane.setFill(Color.BLUE);
			textInMsgPane.setText("Stage in progress");
			pane_msg.setVisible(true);

			return;
		}

		// Otherwise: this is the Supervisor in his stage

		pane_form.setVisible(true);
		estimatedTime = Duration.between(ZonedDateTime.now(), thisRequest.getCurrentStageObject().getDeadline())
				.toDays();
		deadlineText.setText(String.valueOf(estimatedTime));

		if (thisRequest.getPrevStage().equals("DECISION")) {
			finishedStatus.setFill(Color.DARKRED);
			finishedStatus.setText("FAILED");
		} else // else if prevStage == Validation{
		{
			finishedStatus.setFill(Color.FORESTGREEN);
			finishedStatus.setText("Request Processed Correctly");
		}

		//inchargeTF.setText(thisRequest.getCurrentStageObject().getIncharge()+"");

	}

	/**
	 * @apiNote need to check if the process succeed or not and send an appropriate
	 *          message: use finishedStatusTF
	 * @param event
	 */

	@FXML
	void closeProcessBtnClicked(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		String query;
		if (thisRequest.getPrevStage().equals("DECISION")) {
			query = "UPDATE Requests SET Treatment_Phase = 'CANCELED' , STATUS = 'CANCELED' WHERE RequestID = '"
					+ thisRequest.getRequestID() + "'";

		} else {
			query = "UPDATE Requests SET Treatment_Phase = 'DONE' , STATUS = 'DONE' WHERE RequestID = '"
					+ thisRequest.getRequestID() + "'";
		}
		// send email
		String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		App.client.handleMessageFromClientUI(new Message(ot, query2));
	}

	private static int c = 0;

	public void queryResult(Object object) {
		c++;
		boolean res = (boolean) object;
		if (c == 1) { // TODO : Add EMAIL REQUEST.
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
