package client.controllers;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ViewRequestController extends AppController implements Initializable {

	public static ViewRequestController Instance;

	protected ChangeRequest selectedRequestInstance;
	
	//private Map<String, List<Object>> m;
	@FXML
	private TableView<ChangeRequest> table;

	@FXML
	private TableColumn<ChangeRequest, Integer> colId;

	@FXML
	private TableColumn<ChangeRequest, String> colExisitCond;

	@FXML
	private TableColumn<ChangeRequest, String> colStatus;

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
    private Circle circleEvaAssesment;

    @FXML
    private Circle circleDecision;

    @FXML
    private Circle circleExecution;

    @FXML
    private Circle circleValidation;

    @FXML
    private Circle circleClosure;
    
    ObservableList<ChangeRequest> o;
	
	Circle[] circlesArray = new Circle[5];
    
	protected ChangeRequest getCurrentRequest() {
		return selectedRequestInstance;
	}
	//needs to add specific details about the user
	private void getDatafromServer() {
		App.client.handleMessageFromClientUI(new Message(OperationType.getViewRequestData, "SELECT * FROM Requests WHERE USERNAME = '"+ App.user.getUserName() +"';"));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Instance = this;
		// request data from server
				getDatafromServer();

				// event when user click on a row
				table.setRowFactory(tv -> {
					TableRow<ChangeRequest> row = new TableRow<>();
					row.setOnMouseClicked(event -> {
						if (!row.isEmpty()) {
							intitialCircleArray();
							//only temporary.... sorry for the ugly code hehe...
							if(row.getItem().getCurrentStage().equals("EVALUATION")) {
								circlesArray[0].setFill(Paint.valueOf("#74b9ff"));
							}
							
							if(row.getItem().getCurrentStage().equals("DECISION")) {
								circlesArray[0].setFill(Paint.valueOf("#16a085"));
								circlesArray[1].setFill(Paint.valueOf("#74b9ff"));
							}
							if(row.getItem().getCurrentStage().equals("EXECUTION")) {
								circlesArray[0].setFill(Paint.valueOf("#16a085"));
								circlesArray[1].setFill(Paint.valueOf("#16a085"));
								circlesArray[2].setFill(Paint.valueOf("#74b9ff"));
							}
							if(row.getItem().getCurrentStage().equals("VALIDATION")) {
								circlesArray[0].setFill(Paint.valueOf("#16a085"));
								circlesArray[1].setFill(Paint.valueOf("#16a085"));
								circlesArray[2].setFill(Paint.valueOf("#16a085"));
								circlesArray[3].setFill(Paint.valueOf("#74b9ff"));
							}
							if(row.getItem().getCurrentStage().equals("CLOSURE")) {
								circlesArray[0].setFill(Paint.valueOf("#16a085"));
								circlesArray[1].setFill(Paint.valueOf("#16a085"));
								circlesArray[2].setFill(Paint.valueOf("#16a085"));
								circlesArray[3].setFill(Paint.valueOf("#16a085"));
								circlesArray[4].setFill(Paint.valueOf("#74b9ff"));
							}

							selectedRequestInstance = row.getItem();

							requestID.setText("" + selectedRequestInstance.getRequestID());
							existingCondition.setText(selectedRequestInstance.getExistingCondition());
							descripitionsTextArea.setText(selectedRequestInstance.getRemarks());
							departmentID.setText(selectedRequestInstance.getInfoSystem());
							requestNameLabel.setText(selectedRequestInstance.getInitiator());
							dueDateLabel.setText(selectedRequestInstance.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
							if (selectedRequestInstance.getStatus().equals("FREEZED")) {

						
							}
						}
					});

					return row;
				});
	}


	private void intitialCircleArray() {
		// TODO Auto-generated method stub
		circlesArray[0] = circleEvaAssesment;
		circlesArray[1] = circleDecision;
		circlesArray[2] = circleExecution;
		circlesArray[3] = circleValidation;
		circlesArray[4] = circleClosure;
		for(int i=0;i<5;i++) {
			circlesArray[i].setFill(Paint.valueOf("#c0392b"));
		}
	}
	@SuppressWarnings("unchecked")
	public void setDataTable(Object object) {
		// System.out.println("--> setDataTable");
		
		ArrayList<ChangeRequest> info = ((ArrayList<ChangeRequest>) object);
		o = FXCollections.observableArrayList(info);
		
		colId.setCellValueFactory(new PropertyValueFactory<>("requestID"));
		colExisitCond.setCellValueFactory(new PropertyValueFactory<>("initiator"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		
		table.setItems(o);
	}

	public void alertMsg(Object object) {
		Boolean queryResult = (Boolean) object;
		FadeTransition ft = new FadeTransition(Duration.millis(1400), msg);
		msg.setText(queryResult ? "Done" : "Failed");
		msg.setFill(queryResult ? Color.BLUE : Color.RED);
		msg.setVisible(true);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setAutoReverse(false);
		ft.play();
		getDatafromServer();
	}

	@FXML
	void refrshBtn(ActionEvent event) {
		getDatafromServer();
		
		FadeTransition ft = new FadeTransition(Duration.millis(1000), msg);
		msg.setText("Refreshed...");
		msg.setFill(Color.GREEN);
		msg.setVisible(true);
		ft.setFromValue(1.0);
		ft.setToValue(0.0);
		ft.setAutoReverse(false);
		ft.play();
	}
	
	
}
