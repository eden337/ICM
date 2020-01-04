package client.controllers;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
	private Button closeProcessBtn;

	@FXML
	private TextField finishedStatusTF;
	
	//public static String previousStage = "";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueDateLabel.setVisible(true);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);
		if (thisRequest.getPrevStage().equals("DECISION")) {
			finishedStatusTF.setText("FAILED");
		} else // else if prevStage == Validation
			finishedStatusTF.setText("Request Processed Correctly");
	}

	/**
	 * @apiNote need to check if the process succeed or not and send an appropriate
	 *          message: use finishedStatusTF
	 * @param event
	 */

	@FXML
	void closeProcessBtnClicked(ActionEvent event) {
		String query;
		if (thisRequest.getPrevStage().equals("DECISION")) {
			query = "UPDATE Requests SET Treatment_Phase = 'CANCELED' , STATUS = 'CANCELED' WHERE RequestID = '"
					+ thisRequest.getRequestID() + "'";

		} else {
			query = "UPDATE Requests SET Treatment_Phase = 'DONE' , STATUS = 'DONE' WHERE RequestID = '"
					+ thisRequest.getRequestID() + "'";
		}
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));

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
