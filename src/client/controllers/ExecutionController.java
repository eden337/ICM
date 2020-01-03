package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ExecutionController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static ExecutionController instance;

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
	private Button SbmtExecBtn;

	@FXML
	private DatePicker DeadlinetimeExec;

	@FXML
	private TextField daysTxt;

	@FXML
	private TitledPane titledPane;

	@FXML
	private Text msgFix;

	@FXML
	private Button submitAgree;

	@FXML
	private Button submitDisagree;

	@FXML
	private Button workDone;
	@FXML
	private AnchorPane SupervisorPane;

	private boolean responseSupervisor = false;//this provide if the supervisor agree or not.
	private static int save;
	static LocalDate  saveAfterResponse;

	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/client/views/Execution.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Execution");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load execution prompt");
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println(save);
		msgFix.setVisible(false);
		titledPane.setVisible(false);
		dueDateLabel.setVisible(true);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);
		if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
			SupervisorPane.setVisible(true);
			SbmtExecBtn.setVisible(true);// Change to false once you deal with permissions
			submitAgree.setVisible(true);
			submitDisagree.setVisible(true);
			workDone.setVisible(true);// Change to false once you deal with permissions

			if (responseSupervisor) {
				titledPane.getStyleClass().remove("danger");
				titledPane.getStyleClass().add("success");
				titledPane.setCollapsible(false);
				titledPane.setText("This stage is done.");
				msgFix.setText("You have only a viewing permission.");
				msgFix.setFill(Color.FORESTGREEN);
				msgFix.setVisible(true);
				daysTxt.setDisable(true);
				SupervisorPane.setVisible(false);
				SbmtExecBtn.setVisible(false);
				submitAgree.setVisible(false);
				submitDisagree.setVisible(false);
				workDone.setVisible(false);
				DeadlinetimeExec.setValue(addDays(save));
				daysTxt.setText(String.valueOf(save));

			} else {
				submitAgree.setVisible(true);
				submitDisagree.setVisible(true);
				submitAgree.setDisable(false);
				submitDisagree.setDisable(false);
				DeadlinetimeExec.setValue(addDays(save));
				daysTxt.setText(String.valueOf(save));
			}
			
			if (!thisRequest.getCurrentStage().equals("EXECUTION")) { // Watching only
				titledPane.getStyleClass().remove("danger");
				titledPane.getStyleClass().add("success");
				titledPane.setCollapsible(false);
				titledPane.setText("This stage is done.");
				msgFix.setText("You have only a viewing permission.");
				msgFix.setFill(Color.FORESTGREEN);
				msgFix.setVisible(true);
				daysTxt.setVisible(true);
				daysTxt.setText(String.valueOf(save));
				daysTxt.setDisable(true);
				DeadlinetimeExec.setVisible(true);
				DeadlinetimeExec.setValue(addDays(save));
				SupervisorPane.setVisible(false);
				SbmtExecBtn.setVisible(false);
				submitAgree.setVisible(false);
				submitDisagree.setVisible(false);
				workDone.setVisible(false);
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
//first click on this button to send to supervisor
	@FXML
	void submitExecution(ActionEvent event) {
		System.out.println(daysTxt.getText().toString());
		save=Integer.parseInt(daysTxt.getText().toString());
		System.out.println(save);
		daysTxt.setText(String.valueOf(save));
		daysTxt.setDisable(true);
		DeadlinetimeExec.setValue(addDays(save));
		loadPage("requestTreatment");
	}
	//if supervisor said yes so return to executer
	@FXML
	void submitAgree(ActionEvent event) {
		responseSupervisor = true;
		saveAfterResponse = addDays(save);
		loadPage("requestTreatment");
	}
	//if supervisor said no so return the executer
	@FXML
	void submitDisagree(ActionEvent event) {
		responseSupervisor = false;
		loadPage("requestTreatment");
	}
//submit of the executer after the supervisor click agree.
	@FXML
	void submitWorkDone(ActionEvent event) {
		String query1 = "UPDATE Requests SET Treatment_Phase = 'VALIDATION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "'";
		OperationType ot1 = OperationType.updateRequestStatus;
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		loadPage("requestTreatment");
	}

	public void queryResult(Object object) {
		boolean res = (boolean) object;
		if (res)
			showAlert(AlertType.INFORMATION, "Execution Success", "Execution updated", null);
		else
			showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
	}

	private LocalDate addDays(int days) {
		return LocalDate.now().plusDays(days);
	}

}

//	
//	try {
//	java.util.Date date = Date.from(DeadlinetimeExec.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
//	java.sql.Date deadLine = new java.sql.Date(date.getTime());
//	
//	 Calendar currenttime = Calendar.getInstance();               //creates the Calendar object of the current time
//	 Date endtime = new Date((currenttime.getTime()).getTime());  //creates the sql Date of the above created object
//	 
//	  
//	String query="UPDATE `Stage` SET `EndTime` = '"+endtime+"', `Deadline` = '"+deadLine+"', `Handlers` = 'handler', `Incharge` = 'incharge' WHERE `Stage`.`RequestID` = "+thisRequest.getRequestID()+" AND `Stage`.`StageName` = '"+StageName.EXECUTION+"'";
//	OperationType ot = OperationType.UpdateStage;
//	App.client.handleMessageFromClientUI(new Message(ot, query));
//	
//	}catch(Exception e) {
//		System.out.println("error here");
//		e.printStackTrace();
//	}
