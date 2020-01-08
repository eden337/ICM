package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

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
	private TitledPane titledPane;

	@FXML
	private Text msgFix;

	@FXML
	private Button workDone;

	@FXML
	private AnchorPane rightPane;

	@FXML
	private Pane pane_form;

	@FXML
	private Pane pane_msg;

	@FXML
	private Text textInMsgPane;
	@FXML
	private Text deadlineText;

	@FXML
	private AnchorPane returnedNoteAP;

    @FXML
    private TextArea returnedNotes;
	
	private boolean responseSupervisor = false; // this provide if the supervisor agree or not.
	
	static LocalDate saveAfterResponse;

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
		instance = this;
		long estimatedTime = 0;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();

		pane_msg.setVisible(false);
		pane_form.setVisible(false);

		if (!thisRequest.getCurrentStage().equals("EXECUTION")) {
			pane_msg.setVisible(true);
			inchargeTF.setText("Executer");
			return;
		}

		if (!App.user.isStageRole(thisRequest.getRequestID(), StageRole.EXECUTER)) {
			textInMsgPane.setFill(Color.BLUE);
			textInMsgPane.setText("Stage in progress");
			pane_msg.setVisible(true);
			return;
		}

		// Otherwise: this is the Executer in his stage
		if(thisRequest.isReturned()) {
			returnedNoteAP.setVisible(true);
			returnedNotes.setText(thisRequest.getReturnedNote());
		}
		pane_form.setVisible(true);
		msgFix.setVisible(false);
		titledPane.setVisible(false);
		// dueDateLabel.setVisible(true);
		// rightPane.setVisible(false);
		//TRY TO PLAY WITH THE ESTIMATED TIME IN TITLEPANE
		estimatedTime = Duration.between(ZonedDateTime.now(),thisRequest.getCurrentStageObject().getDeadline())
				.toDays();
		deadlineText.setText(String.valueOf(estimatedTime));
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
				dueDateLabel, requestNameLabel, thisRequest);

		checkPreConditions();
		inchargeTF.setText(thisRequest.getCurrentStageObject().getIncharge()+"");

	}

	// first click on this button to send to supervisor
	private void checkPreConditions() {
		OperationType ot = OperationType.EXE_GetInitData;
		String query = "SELECT `init`,`init_confirmed` FROM `Stage` WHERE `RequestID` = '" + thisRequest.getRequestID()
				+ "' AND `StageName` = 'EXECUTION' LIMIT 1";
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}

	public void checkPreConditions_ServerResponse(Object object) {
		List<Boolean> init_res = (List<Boolean>) object;
		boolean init = init_res.get(0);
		boolean init_confirmed = init_res.get(1);

		if (init_confirmed && init) {
			init();
			rightPane.setVisible(true);
			return;
		}
		// else
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				loadPage("PreExecution");
			}
		});
	}

	private void init() {

		if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
			workDone.setVisible(true);// Change to false once you deal with permissions

			if (responseSupervisor) {
				titledPane.getStyleClass().remove("danger");
				titledPane.getStyleClass().add("success");
				titledPane.setCollapsible(false);
				titledPane.setText("This stage is done.");
				msgFix.setText("You have only a viewing permission.");
				msgFix.setFill(Color.FORESTGREEN);
				msgFix.setVisible(true);
				workDone.setVisible(false);

				if (!thisRequest.getCurrentStage().equals("EXECUTION")) { // Watching only
					titledPane.getStyleClass().remove("danger");
					titledPane.getStyleClass().add("success");
					titledPane.setCollapsible(false);
					titledPane.setText("This stage is done.");
					msgFix.setText("You have only a viewing permission.");
					msgFix.setFill(Color.FORESTGREEN);
					msgFix.setVisible(true);
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
	}

	// submit of the executer after the supervisor click agree.
	@FXML
	void submitWorkDone(ActionEvent event) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar c = Calendar.getInstance();
		Date today = new Date(System.currentTimeMillis());
		c.setTime(today);
		c.add(Calendar.DATE, 7);
		Date deadlineDate = c.getTime();

		String query1 = "UPDATE Requests SET Treatment_Phase = 'VALIDATION' WHERE RequestID = '"
				+ thisRequest.getRequestID() + "';";
		String query2 = " UPDATE `Stage` SET init = 0, init_confirmed = 0, `EndTime` = '" + dateFormat.format(today)
				+ "' where  `StageName` = 'EXECUTION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

		OperationType ot1 = OperationType.EXE_UpdateDB;
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		App.client.handleMessageFromClientUI(new Message(ot1, query2));
		showAlert(AlertType.INFORMATION, "Request #"+thisRequest.getRequestID()+" Excution complete!", "The request move forward to Tester.", null);
		loadPage("requestTreatment");
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
