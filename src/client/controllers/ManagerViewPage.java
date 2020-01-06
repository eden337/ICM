package client.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EmployeeUser;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ManagerViewPage extends AppController implements Initializable {
	public static ManagerViewPage Instance;
	protected EmployeeUser selectedEmployeeInstance;
	ObservableList<EmployeeUser> o;
	@FXML
	private TextField searchBoxTF;
	
	@FXML
    private Text msg;

	@FXML
	private TableView<EmployeeUser> table;

	@FXML
	private TableColumn<EmployeeUser, String> colId;

	@FXML
	private TableColumn<EmployeeUser, String> colfullname;

	@FXML
	private TableColumn<EmployeeUser, String> colPostion;

	@FXML
	private TableColumn<EmployeeUser, String> colSystemID;

	@FXML
	private AnchorPane rightPane_requestTreatment;

	@FXML
	private TextField EmailTf;

	@FXML
	private Text WorkerID;

	@FXML
	private Button commiteeBtn;

	@FXML
	private Button SupervisorBtn;

	@FXML
	private TextField SurenameTf;

	@FXML
	private Button chairmanBtn;

	@FXML
	private TextField nameTf;

	@FXML
	private TextField PositionTf;

	@FXML
	private TextField expertiseTf;

	private void getDatafromServer() {
		App.client.handleMessageFromClientUI(
				new Message(OperationType.getEmployeeData, "SELECT * FROM Employees WHERE Department = 'IT';"));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Instance = this;
		// request data from server
		getDatafromServer();
		searchBoxTF.setVisible(true);
		// event when user click on a row
		table.setRowFactory(tv -> {
			TableRow<EmployeeUser> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
//					stageHBox.setVisible(true);
//					progressViewLabel.setVisible(false);
					selectedEmployeeInstance = row.getItem();
					SupervisorBtn.setDisable(false);
					chairmanBtn.setDisable(false);
					commiteeBtn.setDisable(false);
					Tools.fillEmployeesPanes(WorkerID, nameTf, SurenameTf, EmailTf, PositionTf, expertiseTf, selectedEmployeeInstance);
				}
			});
			return row;
		});

	}

	@FXML
	void SupervisorBtnClicked(ActionEvent event) {
		  String query1 = "UPDATE Employees SET RoleInOrg = '' WHERE RoleInOrg = 'SUPERVISOR';";
	        OperationType ot1 = OperationType.updateRoleInOrg;
	        App.client.handleMessageFromClientUI(new Message(ot1, query1));
	        query1 = "UPDATE Employees SET RoleInOrg = 'SUPERVISOR' WHERE WorkerID = '"
	        		+selectedEmployeeInstance.getWorkerID()+ "';";
	        App.client.handleMessageFromClientUI(new Message(ot1, query1));
	        
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
	    void refrshBtn(MouseEvent event) {
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

	@FXML
	void chairmanBtnClicked(ActionEvent event) {
		String query1 = "UPDATE Employees SET RoleInOrg = '' WHERE RoleInOrg = 'COMMITEE_CHAIRMAN';";
        OperationType ot1 = OperationType.updateRoleInOrg;
        App.client.handleMessageFromClientUI(new Message(ot1, query1));
        query1 = "UPDATE Employees SET RoleInOrg = 'COMMITEE_CHAIRMAN' WHERE WorkerID = '"
        		+selectedEmployeeInstance.getWorkerID()+ "';";
        App.client.handleMessageFromClientUI(new Message(ot1, query1));
	}

	@FXML
	void commiteeBtnClicked(ActionEvent event) {
		
		String query1 = "UPDATE Employees SET RoleInOrg = '' WHERE RoleInOrg = 'SUPERVISOR';";
        OperationType ot1 = OperationType.updateRoleInOrg;
        App.client.handleMessageFromClientUI(new Message(ot1, query1));
        query1 = "UPDATE Employees SET RoleInOrg = 'SUPERVISOR' WHERE WorkerID = '"
        		+selectedEmployeeInstance.getWorkerID()+ "';";
        App.client.handleMessageFromClientUI(new Message(ot1, query1));

	}

	@SuppressWarnings("unchecked")
	public void setDataTable(Object object) {
		// System.out.println("--> setDataTable");
		ArrayList<EmployeeUser> info = ((ArrayList<EmployeeUser>) object);
		o = FXCollections.observableArrayList(info);

		colId.setCellValueFactory(new PropertyValueFactory<>("workerID"));
		colfullname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		colPostion.setCellValueFactory(new PropertyValueFactory<>("roleInOrg"));
		colSystemID.setCellValueFactory(new PropertyValueFactory<>("systemID"));

		FilteredList<EmployeeUser> filteredData = new FilteredList<>(o, b -> true);
		searchBoxTF.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(employee -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (employee.getWorkerID().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches first name.
				} else if (employee.getFirstName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				} else if (employee.getLastName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true;
				} else if (employee.getRoleInOrg().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true;
				}else if (employee.getSystemID().toLowerCase().indexOf(lowerCaseFilter) != -1) 
				return true;
				else
					return false; // Does not match.
			});
		});
		// 3. Wrap the FilteredList in a SortedList.
		SortedList<EmployeeUser> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(table.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		table.setItems(sortedData);

		// table.setItems(o);
	}
}
