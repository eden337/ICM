package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.StageName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private TextField reqChngTXT;

	@FXML
	private TextField expResTXT;

	@FXML
	private TextField cnstrntTXT;

	@FXML
	private Button SbmtExecBtn;

	@FXML
	private DatePicker DeadlinetimeExec;

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
		dueDateLabel.setVisible(true);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID, dueDateLabel, requestNameLabel, thisRequest);
	}
	
	@FXML
    void submitExecution(ActionEvent event) {
    	
    	try {
    	java.util.Date date = Date.from(DeadlinetimeExec.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    	java.sql.Date deadLine = new java.sql.Date(date.getTime());
    	
    	 Calendar currenttime = Calendar.getInstance();               //creates the Calendar object of the current time
    	 Date endtime = new Date((currenttime.getTime()).getTime());  //creates the sql Date of the above created object
    	 
    	  
    	String query="UPDATE `Stage` SET `EndTime` = '"+endtime+"', `Deadline` = '"+deadLine+"', `Handlers` = 'handler', `Incharge` = 'incharge' WHERE `Stage`.`RequestID` = "+thisRequest.getRequestID()+" AND `Stage`.`StageName` = '"+StageName.EXECUTION+"'";
		OperationType ot = OperationType.UpdateStage;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		
    	}catch(Exception e) {
    		System.out.println("error here");
    		e.printStackTrace();
    	}
	}

	public void queryResult(Object object) {
		boolean res = (boolean) object;
		if (res) 
				System.out.println("good");
			else
				System.out.println("no");
	}

}
