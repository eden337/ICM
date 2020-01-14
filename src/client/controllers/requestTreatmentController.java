package client.controllers;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.MyFile;
import common.entity.OrganizationRole;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;

public class requestTreatmentController extends AppController implements Initializable {

	public static requestTreatmentController Instance;
	private static ActionEvent tempEvent;
	protected ChangeRequest selectedRequested;

	@FXML
	private TextArea supervisorRemarks;
	// @FXML
	// private Pane Footer_defualt;

	private static long days;

	@FXML
	private Button btnIncharges;

	@FXML
	private Button updateRemarksBtn;

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
	private DatePicker dueDateLabel;

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

	@FXML
	private TextArea wantedChangeText;

	@FXML
	private TextArea reasonText;

	ObservableList<ChangeRequest> o;

	protected ChangeRequest getCurrentRequest() {
		return selectedRequested;
	}

	private void getDatafromServer() {
		App.client.handleMessageFromClientUI(new Message(OperationType.getRequirementData, setTableByUser()));
	}

	/**
	 * Query by Permission
	 *
	 * @return
	 */
	private String setTableByUser() {
		String query = "Select * FROM Requests ";
		if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))
			return query;
		if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR)) {
			query = "SELECT * FROM Requests WHERE `Status` = 'SUSPENDED'";
			return query;
		}
		query = "SELECT r.`RequestID`, `USERNAME`, `Position`, `Email`, `Existing_Cond`, `Wanted_Change`,"
				+ " `Treatment_Phase`, `Status`, `Reason`, `Curr_Responsible`, `SystemID`, `Comments`, `Date`,"
				+ " `Due_Date`, `FILE` FROM `Requests` as r , `Stage` as s " + "WHERE r.`RequestID` = s.`RequestID`"
				+ "AND r.`Treatment_Phase` = s.`StageName`" + // active stage
				" AND `incharge` = '" + App.user.getUserName() + "'";

		if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER1)
				|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER2)
				|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {
			// add option to see active decision stages in addition to other permission of
			// these user
			query += " OR r.`RequestID` = s.`RequestID` AND r.`Treatment_Phase` = 'DECISION' AND s.`StageName` = 'DECISION'";

			if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN))
				query += " OR r.`RequestID` = s.`RequestID` AND r.`Treatment_Phase` = 'VALIDATION' AND s.`StageName` = 'VALIDATION' AND 'init_confirmed' = 0";

		}
		// general:
		return query;
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

		// panes defualt:
		// Footer_defualt.setVisible(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Instance = this;
		// request data from server
		getDatafromServer();
		rightPane_selectRequest.setVisible(true);
		initPanes();
		btnIncharges.setVisible(false);
		searchBoxTF.setVisible(true);
		updateRemarksBtn.setVisible(false);
		freezeBtn.setVisible(false);

		// event when user click on a row
		table.setRowFactory(tv -> {
			TableRow<ChangeRequest> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					btnDownloadFiles.setDisable(false);
					selectedRequested = row.getItem();
					appendStageObject();
					initPanes();
					String filename = "Request_" + selectedRequested.getRequestID() + ".zip";
					if (selectedRequested.getFilesPaths() != null)
						if (selectedRequested.getFilesPaths().equals(filename))
							btnDownloadFiles.setVisible(true);

					rightPane_selectRequest.setVisible(false);
					if (row.getItem().getCurrentStage().equals("INIT")) {
						rightPane_Init.setVisible(true);
						if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
							btnInit.setVisible(true);
						}

					} else { // ACTIVE request
						if ((App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))) {

							wantedChangeText.setEditable(true);
							reasonText.setEditable(true);
							existingCondition.setEditable(true);
							descripitionsTextArea.setEditable(true);
							dueDateLabel.setDisable(false);
							updateRemarksBtn.setDisable(false);
							freezeBtn.setVisible(true);
							freezeBtn.setDisable(false);
							if (selectedRequested.getStatus().equals("DONE")
									|| selectedRequested.getStatus().equals("CANCELED")) {
								freezeBtn.setDisable(true);
								updateRemarksBtn.setDisable(true);
							}
						}

						rightPane_requestTreatment.setVisible(true);
						Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF,
								departmentID, null, requestNameLabel, selectedRequested);
						wantedChangeText.setText(selectedRequested.getSuggestedChange());
						reasonText.setText(selectedRequested.getReasonForChange());
						dueDateLabel.setValue(selectedRequested.getDueDate().toLocalDate());

						if (selectedRequested.getStatus().equals("SUSPENDED")
								&& !(selectedRequested.getStatus().equals("DONE")
										|| selectedRequested.getStatus().equals("CANCELED"))) {

							rightPane_Freezed.setVisible(true);
							btnDownloadFiles.setDisable(true);
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
					if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
						btnIncharges.setVisible(true);
						updateRemarksBtn.setVisible(true);
						submitBtn.setVisible(false);
						supervisorRemarks.setPromptText("Please write your remarks");
						supervisorRemarks.setVisible(false);

					}
					resetStageImgStyleClass();
					Tools.highlightProgressBar(stage1, stage2, stage3, stage4, stage5, selectedRequested);

				} // row selected
			});
			return row;
		});

		// if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))

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
					return true; // Filter matches initiator name.
				} else if (request.getCurrentStage().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches current stage.
				} else if (request.getStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true;
				} else if (String.valueOf(request.getRequestID()).toLowerCase().indexOf(lowerCaseFilter) != -1)
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

		// table.setItems(o);

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
		// InsertStartStage(StageName.EXECUTION.toString());
		loadPage("Execution");
	}

	@FXML
	void validButtonClick(MouseEvent event) {
		loadPage("Validation");
	}

	/* Freezing methods */

	@FXML
	void freezeButtonClick(ActionEvent event) {
		c = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		String query = "UPDATE Requests SET Status = 'SUSPENDED' WHERE RequestID = '"
				+ getCurrentRequest().getRequestID() + "'";
		String query2 = "INSERT INTO Frozen (RequestID, StageName, FreezeTime) VALUES ('"
				+ selectedRequested.getRequestID() + "', '" + getCurrentRequest().getCurrentStage() + "', '"
				+ dateFormat.format(today) + "'); ";
		System.out.println(query);
		App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query));
		App.client.handleMessageFromClientUI(new Message(OperationType.insertFreezedRequest, query2));

	}

	@FXML
	void unfreeze(ActionEvent event) {
		c = 0;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		String query = "UPDATE Requests SET Status = 'ACTIVE' WHERE RequestID = '" + getCurrentRequest().getRequestID()
				+ "'";
		String query2 = "UPDATE Frozen SET UnFreezeTime = '" + dateFormat.format(today) + "' WHERE RequestID = '"
				+ getCurrentRequest().getRequestID() + "'";

		String query3 = "SELECT FreezeTime,UnFreezeTime FROM Frozen  WHERE RequestID = '"
				+ getCurrentRequest().getRequestID() + "'";
		App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query));
		App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query2));
		App.client.handleMessageFromClientUI(new Message(OperationType.getTimeFromFrozen, query3));

	}

	public void unFreezeSelectFrozenResponse(Object object) {
		ArrayList<ZonedDateTime> frozenTimes = (ArrayList<ZonedDateTime>) object;
		System.out.println(frozenTimes);
		days = Period.between(frozenTimes.get(0).toLocalDate(), ZonedDateTime.now().toLocalDate()).getDays();
		selectedRequested.setDueDate(selectedRequested.getDueDate().plusDays(days));
		System.out.println(days);
		String query = "UPDATE Stage SET Deadline =DATE_ADD(Deadline,INTERVAL " + days + " DAY) WHERE RequestID = '"
				+ selectedRequested.getRequestID() + "' AND StageName ='" + selectedRequested.getCurrentStage() + "'";
		App.client.handleMessageFromClientUI(new Message(OperationType.updateUnfrozenStage, query));
	}

	public void freezeUpdateResponse(Object object) {
		boolean res = (boolean) object;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date today = new Date(System.currentTimeMillis());
		if (!res) {
			String query2 = "UPDATE Frozen SET FreezeTime = '" + dateFormat.format(today)
					+ "', UnFreezeTime = NULL WHERE RequestID = '" + getCurrentRequest().getRequestID() + "'";
			App.client.handleMessageFromClientUI(new Message(OperationType.updateRequestStatus, query2));
		}
		showAlert(AlertType.INFORMATION, "Request Suspended!", "Please notify your director", null);
	}

	public void unFreezeUpdateResponse(Object object) {
		boolean res = (boolean) object;
		if (!res) {
			showAlert(AlertType.ERROR, "Error trying to unfreeze", "Failed to unfreeze : query failed at DB", null);
		} else
			showAlert(AlertType.INFORMATION, "Request Resumed", "Please notify your Supervisor", null);
	}

	public void freezeServerResponse(Object object) {
		refrshBtn(null);
		initialize(null, null);

	}

	@FXML
	void submitBtnClicked(ActionEvent event) {
		c = 0;
		updateRemarksBtn.setVisible(true);
		updateRemarksBtn.setDisable(false);
		submitBtn.setVisible(false);
		submitBtn.setDisable(true);
		supervisorRemarks.setVisible(false);
		String query1 = "UPDATE Requests SET Existing_Cond = '" + existingCondition.getText() + "'" + ",Comments = '"
				+ descripitionsTextArea.getText() + "', Wanted_Change ='" + wantedChangeText.getText() + "', Reason = '"
				+ reasonText.getText() + "', " + " Due_Date = '" + Date.valueOf(dueDateLabel.getValue()) + "'"
				+ " WHERE RequestID = " + requestID.getText() + ";";
		String query2 = "INSERT INTO `Supervisor Update History` (`RequestID`, `Updater_Name`, `update_remarks`) VALUES ("
				+ selectedRequested.getRequestID() + ", '" + App.user.getFirstName() + " " + App.user.getLastName()
				+ "', '" + supervisorRemarks.getText() + "');";
		OperationType ot1 = OperationType.updateRequestStatus;
		OperationType ot2 = OperationType.SUPERVISOR_REMARKS;
		App.client.handleMessageFromClientUI(new Message(ot1, query1));
		App.client.handleMessageFromClientUI(new Message(ot2, query2));

		stageProgressHBox.setVisible(true);
	}

	private static int c = 0;

	public void queryResult(Object object) {
		c++;
		boolean res = (boolean) object;
		if (c == 2) {
			if (res) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						showAlert(AlertType.INFORMATION, "Request Updated!", "The request details were changed", null);
					}
				});
			} else
				showAlert(AlertType.ERROR, "Error!", "Cannot send update!", null);
		}
	}

	@FXML
	void updateRemarksBtnClicked(ActionEvent event) {
		updateRemarksBtn.setVisible(false);
		updateRemarksBtn.setDisable(true);
		submitBtn.setVisible(true);
		submitBtn.setDisable(false);
		supervisorRemarks.setVisible(true);
		supervisorRemarks.setText("");
		supervisorRemarks.setPromptText("Please write your remarks");
		stageProgressHBox.setVisible(false);
	}

	/* DOWNLOAD FILES */

	@FXML
	void DownloadFiles(ActionEvent event) {
		tempEvent = event;
		App.client.handleMessageFromClientUI(
				new Message(OperationType.ChangeRequest_DownloadFile, selectedRequested.getRequestID()));
	}

	public void DownloadFiles_ServerResponse(Object object) {
		if (object == null) {
			showAlert(AlertType.ERROR, "File not Found", "The requested files not found.", null);
			return;
		}

		MyFile msgFile = (MyFile) object;
		Node source = (Node) tempEvent.getSource();
		Window theStage = source.getScene().getWindow();
		final String[] userPath = { "" };
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DirectoryChooser dirChooser = new DirectoryChooser();
				File chosenDir = dirChooser.showDialog(mainController.primaryStage);
				try {
					if (chosenDir == null) // user aborted
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

					// open the downloaded file using operation system
					Desktop desktop = Desktop.getDesktop();
					desktop.open(new File(chosenDir.getPath()));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}
	// get Stage Object to Request:

	void appendStageObject() {
		if (selectedRequested.getCurrentStage().equals("INIT"))
			return;
		String query5 = "SELECT * FROM `Stage` WHERE `RequestID` = '" + selectedRequested.getRequestID()
				+ "' AND `StageName` = '" + selectedRequested.getCurrentStage() + "' LIMIT 1";
		App.client.handleMessageFromClientUI(new Message(OperationType.ChangeRequest_getStageObject, query5));
		rightPane_requestTreatment.setDisable(true);
	}

	void appendStageObject_ServerResponse(Object object) {
		common.entity.Stage currentStage = (common.entity.Stage) object;
		selectedRequested.setCurrentStageObject(currentStage);
		rightPane_requestTreatment.setDisable(false);

	}

}
