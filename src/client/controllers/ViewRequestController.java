package client.controllers;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ViewRequestController extends AppController implements Initializable {

	public static ViewRequestController Instance;

	protected ChangeRequest selectedRequestInstance;
	
	private Map<String, List<Object>> m;
	@FXML
	private TableView<ModelTable> table;

	@FXML
	private TableColumn<ModelTable, String> colId;

	@FXML
	private TableColumn<ModelTable, String> colExisitCond;

	@FXML
	private TableColumn<ModelTable, String> colStatus;

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
	
	//suggestion: array for the progress
    
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

		//submitBtn.setDisable(true);
		msg.setVisible(false);
		
		// request data from server
		getDatafromServer();
		// event when user click on row
		table.setRowFactory(tv -> {
			TableRow<ModelTable> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					// submitBtn.setDisable(false);
					selectedRequestInstance = new ChangeRequest(row.getItem().getId(), row.getItem().getName(),
							row.getItem().getSystemID(), row.getItem().getExistChange(), row.getItem().getComments(),
							row.getItem().getDueDate(), row.getItem().getCurrResponsible(),row.getItem().getStatus());
				
					requestID.setText(selectedRequestInstance.getRequestID());
					existingCondition.setText(selectedRequestInstance.getExistingCondition());
					descripitionsTextArea.setText(selectedRequestInstance.getRemarks());
					dueDateLabel.setText(selectedRequestInstance.getDueDate());
					requestNameLabel.setText(selectedRequestInstance.getRequestName());
					// status.setValue(row.getItem().getStatus());
					inchargeTF.setText(selectedRequestInstance.getCurrInCharge());
					departmentID.setText(selectedRequestInstance.getInfoSystem());
				}
			});

			return row;
		});
	}

	private ObservableList<ModelTable> getModelTable(Map<String, List<Object>> m) {
		ObservableList<ModelTable> items = FXCollections.observableArrayList();
		try {
			for (int i = 0; i < m.get("RequestID").size(); i++) {
				items.add(new ModelTable(m.get("RequestID").get(i), m.get("USERNAME").get(i), m.get("Position").get(i),
						m.get("Email").get(i), m.get("Existing_Cond").get(i), m.get("Wanted_Change").get(i),
						m.get("Treatment_Phase").get(i), m.get("Status").get(i), m.get("Reason").get(i),
						m.get("Curr_Responsible").get(i), m.get("SystemID").get(i), m.get("Comments").get(i),
						m.get("Date").get(i), m.get("Due_Date").get(i), m.get("FILE").get(i)));
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Not found any requests");
		}
		;
//		for (ModelTable modelTable : items) {
//			System.out.println(modelTable.getId().toString()+","+modelTable.getName().toString()+","+modelTable.getStatus().toString());
//		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public void setDataTable(Object object) {
		// System.out.println("--> setDataTable");
		m = ((Map<String, List<Object>>) object);
		ObservableList<ModelTable> o = getModelTable(m);


		colId.setCellValueFactory(new PropertyValueFactory<ModelTable, String>("id"));
		colExisitCond.setCellValueFactory(new PropertyValueFactory<ModelTable, String>("name"));
		colStatus.setCellValueFactory(new PropertyValueFactory<ModelTable, String>("status"));

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
