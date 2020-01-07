package client.controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.EmployeeUser;
import javafx.animation.FadeTransition;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
/**
 * 
 * @author Hen Eden Yuda
 *
 */
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
	private Button commitee1Btn;

	@FXML
	private Button commitee2Btn;

	@FXML
	private Button SupervisorBtn;

	@FXML
	private Button removeCommiteeBtn;

	@FXML
	private TextField SurenameTf;

	@FXML
	private Button chairmanBtn;

	@FXML
	private TextField nameTf;

	@FXML
	private TextField PositionTf;
    @FXML
    private CheckBox libraryCheckbox;

    @FXML
    private CheckBox labsCheckbox;

    @FXML
    private CheckBox classComputersCheckbox;

    @FXML
    private CheckBox collageCheckbox;

    @FXML
    private CheckBox computerFarmCheckbox;

    @FXML
    private CheckBox moodleCheckbox;

    @FXML
    private CheckBox informationStationCheckbox;
    
    @FXML
    private Button systemSelectionSubmit;

    @FXML
    private Button deleteMember;

	

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
					commitee1Btn.setDisable(false);
					commitee2Btn.setDisable(false);
					Tools.fillEmployeesPanes(WorkerID, nameTf, SurenameTf, EmailTf, PositionTf, null,
							selectedEmployeeInstance);
					
					
					
					
				}
			});
			return row;
		});

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
 

	void appointment(String roleInOrg) {
		if (selectedEmployeeInstance.getRoleInOrg().equals(roleInOrg))
			return;
		if (!selectedEmployeeInstance.getRoleInOrg().equals("")) {
			showAlert(AlertType.WARNING, "Appointment failure",
					selectedEmployeeInstance.getRoleInOrg() + " Cannot fill two roles", null);
			return;
		}

		String query1 = "UPDATE Employees SET RoleInOrg = '' WHERE RoleInOrg = '" + roleInOrg + "';";
		OperationType ot1 = OperationType.updateRoleInOrg;
		for (EmployeeUser e : o) {
			if (e.getRoleInOrg().equals(roleInOrg))
				e.setOrgRoleServerResponse("");
		}
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		query1 = "UPDATE Employees SET RoleInOrg = '" + roleInOrg + "' WHERE WorkerID = '"
				+ selectedEmployeeInstance.getWorkerID() + "';";
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		selectedEmployeeInstance.setOrgRoleServerResponse(roleInOrg);
		PositionTf.setText(roleInOrg);
		table.refresh();
	}

	@FXML
	void SupervisorBtnClicked(ActionEvent event) {
		appointment("SUPERVISOR");
	}

	@FXML
	void chairmanBtnClicked(ActionEvent event) {
		appointment("COMMITEE_CHAIRMAN");
	}

	@FXML
	void commitee1BtnClicked(ActionEvent event) {
		appointment("COMMITEE_MEMBER1");
	}

	@FXML
	void commitee2BtnClicked(ActionEvent event) {
		appointment("COMMITEE_MEMBER2");
	}

	@SuppressWarnings("unchecked")
	public void setDataTable(Object object) {
		// System.out.println("--> setDataTable");
		ArrayList<EmployeeUser> info = ((ArrayList<EmployeeUser>) object);
		o = FXCollections.observableArrayList(info);

		colId.setCellValueFactory(new PropertyValueFactory<>("workerID"));
		colfullname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		colPostion.setCellValueFactory(new PropertyValueFactory<>("roleInOrg"));
		

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
				}
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
    @FXML
    void deleteMembr(ActionEvent event) {
    	String query="UPDATE `Employees` SET `RoleInOrg` = '' WHERE `Employees`.`WorkerID` = '"+selectedEmployeeInstance.getWorkerID()+"';";
		OperationType ot=OperationType.deleteMember;
		App.client.handleMessageFromClientUI(new Message(ot, query));
		
		table.refresh();//HANDLE LIVE REFRESH
    }
    @FXML
    void systemSelectionClicked(ActionEvent event) {
    	ArrayList<String> selectedSystems = new ArrayList<>();
    	String query;
    	if(libraryCheckbox.isSelected())
    		selectedSystems.add("Library");
    	if(labsCheckbox.isSelected())
    		selectedSystems.add("Labs");
    	if(moodleCheckbox.isSelected())
    		selectedSystems.add("Moodle");
    	if(computerFarmCheckbox.isSelected())
    		selectedSystems.add("Computer Farm");
    	if(classComputersCheckbox.isSelected())
    		selectedSystems.add("Class Computers");
    	if(collageCheckbox.isSelected())
    		selectedSystems.add("Collage Website");
    	if(informationStationCheckbox.isSelected())
    		selectedSystems.add("information Station");
    	for(String s: selectedSystems)
    	{
    		query="UPDATE `Systems Techncian` SET `username` = '"+selectedEmployeeInstance.getUserName()+"' WHERE `Systems Techncian`.`SystemID` = '"+s+"';";
    		OperationType ot=OperationType.updateSystems;
    		App.client.handleMessageFromClientUI(new Message(ot, query));
    	}
    }
    public void getquery(Object object)
    {
    	boolean res=(boolean)object;
    	if(res)
    		showAlert(AlertType.INFORMATION, "selected succsses", "The selected are in the DB", null);
    	else
    		showAlert(AlertType.ERROR, "selected Fail", "Eror in the DB check again", null);
    }	
}
