package client.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.MyFile;
import common.entity.OrganizationRole;
import common.entity.StageName;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class requestTreatmentController extends AppController implements Initializable {

    public static requestTreatmentController Instance;
    private static ActionEvent tempEvent;
    protected ChangeRequest selectedRequested;

	@FXML
	public TableView<ChangeRequest> table;

	@FXML
	private TableColumn<ChangeRequest, Integer> colId;

	@FXML
	private TableColumn<ChangeRequest, String> colIntitator;

	@FXML
	private TableColumn<ChangeRequest, String> colStatus;

	@FXML
	private TableColumn<ChangeRequest, String> colStage;

	@FXML
	private AnchorPane rightPane_selectRequest;

	@FXML
	private AnchorPane rightPane_Freezed;

	@FXML
	private Text custom_msg;

	@FXML
	private AnchorPane rightPane_requestTreatment;

	@FXML
	private Text idText;

	@FXML
	private Text requestID;

	@FXML
	private Button submitBtn;

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
	private HBox stageProgressHBox;

	@FXML
	private Text idText1;

	@FXML
	private Text requestNameLabel;

	@FXML
	private Text idText2;

	@FXML
	private Text dueDateLabel;

	@FXML
	private Button freezeBtn;

	@FXML
	private Button btnUnfreeze;

	@FXML
	private AnchorPane rightPane_Init;

	@FXML
	private Text custom_msg1;

	@FXML
	private Button btnInit;

	@FXML
	private ImageView stage1;

	@FXML
	private ImageView stage2;

	@FXML
	private ImageView stage3;

	@FXML
	private ImageView stage4;

	@FXML
	private ImageView stage5;

	@FXML
	private TextField searchBoxTF;

    @FXML
    private Button btnDownloadFiles;

    ObservableList<ChangeRequest> o;

	protected ChangeRequest getCurrentRequest() {
		return selectedRequested;
	}


	private void getDatafromServer() {
		App.client.handleMessageFromClientUI(new Message(OperationType.getRequirementData, setTableByUser()));
	}

	private String setTableByUser() {
		/*
		 * check user premissons and return premissions by role
		 */
		// if user is supervisor
		return "Select * FROM Requests";
	}

	private void initPanes() {
		// init layers:
		rightPane_requestTreatment.setVisible(false);
		rightPane_Freezed.setVisible(false);
		rightPane_Init.setVisible(false);

		// init buttons:
		btnDownloadFiles.setVisible(false);
		btnInit.setVisible(false);
		submitBtn.setDisable(true);
		msg.setVisible(false);
		btnUnfreeze.setVisible(false);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Instance = this;
		// request data from server
		getDatafromServer();
		rightPane_selectRequest.setVisible(true);
		initPanes();
		searchBoxTF.setVisible(true);
		// event when user click on a row
		table.setRowFactory(tv -> {
			TableRow<ChangeRequest> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					selectedRequested = row.getItem();
					initPanes();

					String filename = "Request_"+ selectedRequested.getRequestID()+ ".zip";
					System.out.println(selectedRequested.getFilesPaths());

					if(selectedRequested.getFilesPaths().equals(filename))
						btnDownloadFiles.setVisible(true);

					rightPane_selectRequest.setVisible(false);
					if (row.getItem().getCurrentStage().equals("INIT")) {
						rightPane_Init.setVisible(true);
						if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
							btnInit.setVisible(true);
						}

					} else { // ACTIVE request
						if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
							existingCondition.setEditable(true);
							descripitionsTextArea.setEditable(true);
							inchargeTF.setEditable(true);
							submitBtn.setDisable(false);
						}
						rightPane_requestTreatment.setVisible(true);
						Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF,
								departmentID, dueDateLabel, requestNameLabel, selectedRequested);
						if (selectedRequested.getStatus().equals("FREEZED")) {
							rightPane_Freezed.setVisible(true);
							rightPane_requestTreatment.setDisable(true);
							stageProgressHBox.setVisible(false);
							if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR))
								btnUnfreeze.setVisible(true);
						} else {
							rightPane_Freezed.setVisible(false);
							rightPane_requestTreatment.setDisable(false);
							stageProgressHBox.setVisible(true);
						}
					}

					resetStageImgStyleClass();
					Tools.highlightProgressBar(stage1, stage2, stage3, stage4, stage5, selectedRequested);
				} // row selected
			});

			return row;
		});

	}// initialize

	private void resetStageImgStyleClass() {
		stage1.getStyleClass().remove("img_stage_blocked");
		stage1.getStyleClass().remove("img_stage_passed");
		stage1.getStyleClass().remove("img_stage_current");
		stage1.setOnMouseClicked(this::evalButtonClick);

		stage2.getStyleClass().remove("img_stage_blocked");
		stage2.getStyleClass().remove("img_stage_passed");
		stage2.getStyleClass().remove("img_stage_current");
		stage3.setOnMouseClicked(this::decButtonClick);

		stage3.getStyleClass().remove("img_stage_blocked");
		stage3.getStyleClass().remove("img_stage_passed");
		stage3.getStyleClass().remove("img_stage_current");
		stage3.setOnMouseClicked(this::exeButtonClick);

		stage4.getStyleClass().remove("img_stage_blocked");
		stage4.getStyleClass().remove("img_stage_passed");
		stage4.getStyleClass().remove("img_stage_current");
		stage4.setOnMouseClicked(this::validButtonClick);

		stage5.getStyleClass().remove("img_stage_blocked");
		stage5.getStyleClass().remove("img_stage_passed");
		stage5.getStyleClass().remove("img_stage_current");
		stage5.setOnMouseClicked(this::closureButtonClick);

	}


	public void setDataTable(Object object) {
		ArrayList<ChangeRequest> info = ((ArrayList<ChangeRequest>) object);
		o = FXCollections.observableArrayList(info);

		colId.setCellValueFactory(new PropertyValueFactory<>("requestID"));
		colIntitator.setCellValueFactory(new PropertyValueFactory<>("initiator"));
		colStage.setCellValueFactory(new PropertyValueFactory<>("currentStage"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

		FilteredList<ChangeRequest> filteredData = new FilteredList<>(o, b -> true);
		searchBoxTF.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(request -> {
				// If filter text is empty, display all persons.

				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (request.getInitiator().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches first name.
				} else if (request.getCurrentStage().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches last name.
				} else if (request.getStatus().toLowerCase().indexOf(lowerCaseFilter) != -1)
					return true;
				else
					return false; // Does not match.
			});
		});
		// 3. Wrap the FilteredList in a SortedList.
		SortedList<ChangeRequest> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(table.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		table.setItems(sortedData);

		//table.setItems(o);
		
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
	void allocatePersonelButtonClick(ActionEvent event) {
		// AllocateController alloControl = new AllocateController();
		// alloControl.start(new Stage());
		mainController.instance.loadPage("Allocate",
				"Request #" + selectedRequested.getRequestID() + " Treatment | Roles Allocation");
	}

	@FXML
	void closureButtonClick(MouseEvent event) {
		loadPage("Closure");

	}

	@FXML
	void decButtonClick(MouseEvent event) {
		loadPage("Decision");
	}

	@FXML
	void evalButtonClick(MouseEvent event) {

		loadPage("EvaluationForm");
	}

	@FXML
	void exeButtonClick(MouseEvent event) {
		//InsertStartStage(StageName.EXECUTION.toString());
		loadPage("Execution");
	}

	@FXML
	void validButtonClick(MouseEvent event) {
		loadPage("Validation");
	}

	/* Freezing methods */

	@FXML
	void freezeButtonClick(ActionEvent event) {
		String query = "UPDATE Requests SET Status = 'FREEZED' WHERE RequestID = '" + getCurrentRequest().getRequestID()
				+ "'";
		System.out.println(query);
		App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query));
	}

	@FXML
	void unfreeze(ActionEvent event) {
		String query = "UPDATE Requests SET Status = 'ACTIVE' WHERE RequestID = '" + getCurrentRequest().getRequestID()
				+ "'";
		App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query));
	}

	public void freezeServerResponse(Object object) {
		refrshBtn(null);
		initialize(null, null);

	}

	/*public void InsertStartStage(String stageName) {
		Calendar currenttime = Calendar.getInstance(); // creates the Calendar object of the current time
		Date starttime = new Date((currenttime.getTime()).getTime()); // creates the sql Date of the above created
																		// object
		LocalDate duedate = LocalDate.of(selectedRequestInstance.getDueDate().getYear(),
				selectedRequestInstance.getDueDate().getMonthValue(),
				selectedRequestInstance.getDueDate().getDayOfMonth());
		// System.out.println(Date.valueOf(duedate.toString()));
		String query = "INSERT INTO `Stages` (`RequestID`, `StageName`, `StartTime`, `EndTime`, `Deadline`, `Handlers`, `Incharge`, `Delay`, `Extend`)"
				+ "VALUES" + "('" + selectedRequestInstance.getRequestID() + "', '" + stageName + "', '" + starttime
				+ "', NULL, '" + duedate.toString() + "', '', '', '0', '1');";

		OperationType ot = OperationType.InsertStartStage;
		App.client.handleMessageFromClientUI(new Message(ot, query));
	}*/
	@FXML
	void submitBtnClicked(ActionEvent event) {
		showAlert(AlertType.INFORMATION, "Mock Button",
				"Need to import a query for updating the request tuple in the DB table", null);
	}



	/* DOWNLOAD FILES */

	@FXML
	void DownloadFiles(ActionEvent event) {
		tempEvent = event;
		App.client.handleMessageFromClientUI(new Message(OperationType.ChangeRequest_DownloadFile, selectedRequested.getRequestID()));
	}

	public void DownloadFiles_ServerResponse(Object object) {
		if(object == null){
			showAlert(AlertType.ERROR,"File not Found", "The requested files not found.",null);
			return;
		}

		MyFile msgFile = (MyFile) object;
		Node source = (Node) tempEvent.getSource();
		Window theStage = source.getScene().getWindow();
		final String[] userPath = {""};
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DirectoryChooser dirChooser = new DirectoryChooser();
				File chosenDir = dirChooser.showDialog(mainController.primaryStage);
				try {
					if(chosenDir == null) // user aborted
						return;
					File newFile = new File(chosenDir.getPath() + "\\" + msgFile.getFileName());
					byte[] mybytearray = msgFile.getMybytearray();
					FileOutputStream fos = new FileOutputStream(newFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bos.write(mybytearray, 0, msgFile.getSize());

					// release file
					bos.flush();
					fos.flush();
					bos.close();
					fos.close();
					showAlert(AlertType.CONFIRMATION,"Download succeed", "The requested files not found.",null);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		});

	}
}
