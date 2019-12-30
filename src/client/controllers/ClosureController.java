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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueDateLabel.setVisible(true);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		requestID.setText(thisRequest.getRequestID()+"");
		departmentID.setText(thisRequest.getInfoSystem());
		requestNameLabel.setText(thisRequest.getInitiator());
		existingCondition.setText(thisRequest.getExistingCondition());
		descripitionsTextArea.setText(thisRequest.getRemarks());
		inchargeTF.setText("");
		dueDateLabel.setText(thisRequest.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

	}
	
	/**
	 * @apiNote
	 * 	need to check if the process succeed or not and send an appropriate message:
	 * use finishedStatusTF
	 * @param event
	 */
    @FXML
    void closeProcessBtnClicked(ActionEvent event) {
    	String query = "UPDATE Requests SET STATUS = 'DONE' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		showAlert(AlertType.INFORMATION, "Process Finished!", "Notification has been sent to user's email and SMS", null);
		loadPage("requestTreatment");
    }


}
