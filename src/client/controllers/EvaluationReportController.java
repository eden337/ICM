package client.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EvaluationReportController extends AppController implements Initializable {

	/*
	 * this static variable is supposed to hold all the data of the request chosen
	 * in request treatment
	 */
	// public static ChangeRequest thisRequest;
	public static EvaluationReportController instance;

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
	private Button SbmtEvlBtn;

	@FXML
	private DatePicker timeEvlBox;

	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/client/views/Evaluator.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Evaluation Assessment");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not load evaluation prompt");
			e.printStackTrace();
		}
	}

	@FXML
	void SbmtEvlBtnClick(ActionEvent event) {
		String reportID, systemID, requiredChange, expectedResult, expectedRisk, estimatedTime;
		reportID = "15";
		systemID = this.departmentID.getText();
		requiredChange = this.reqChngTXT.getText();
		expectedResult = this.expResTXT.getText();
		expectedRisk = this.cnstrntTXT.getText();
		LocalDate date = this.timeEvlBox.getValue();
		estimatedTime = date.toString();
		EvaluationReport rep = new EvaluationReport(systemID, requiredChange, expectedResult, expectedRisk,
				estimatedTime);

		String query= "INSERT INTO `Evaluation Reports` (`RequestID`, `System_ID`, `Required_Change`, `Expected_Result`, `Expected_Risks`, `Estimated_Time`) VALUES ("
				
				+ "'"+thisRequest.getRequestID()+"', "
				+ "'"+systemID+"', "
				+ "'"+requiredChange+"', "
				+ "'"+expectedResult+"', "
				+ "'"+expectedRisk+"', "
				+ "'"+estimatedTime+"');";
		OperationType ot = OperationType.InsertEvaluation;
		App.client.handleMessageFromClientUI(new Message(ot, query));

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueDateLabel.setVisible(false);
		instance = this;
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		thisRequest = requestTreatmentController.Instance.getCurrentRequest();
		requestID.setText(thisRequest.getRequestID()+"");
		departmentID.setText(thisRequest.getInfoSystem());
		requestNameLabel.setText(thisRequest.getInitiator());
		existingCondition.setText(thisRequest.getExistingCondition());
		descripitionsTextArea.setText(thisRequest.getRemarks());
		inchargeTF.setText("");
		dueDateLabel.setText(thisRequest.getDueDate().toString());
	}

	public void queryResult(Object object) {
		boolean res = (boolean) object;

		if (res)
			showAlert(AlertType.INFORMATION, "Evaluation Success", "Report updated", null);
		else
			showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
	}
}
