package client.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ViewRequestController extends AppController implements Initializable {

	public static ViewRequestController Instance;

	protected ChangeRequest selectedRequestInstance;

	// private Map<String, List<Object>> m;
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
	private HBox stageHBox;

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
	private Text progressViewLabel;

	@FXML
	private TextField searchBoxTF;

	ObservableList<ChangeRequest> o;

	protected ChangeRequest getCurrentRequest() {
		return selectedRequestInstance;
	}

	// needs to add specific details about the user
	private void getDatafromServer() {
		App.client.handleMessageFromClientUI(new Message(OperationType.getViewRequestData,
				"SELECT * FROM Requests WHERE USERNAME = '" + App.user.getUserName() + "';"));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Instance = this;
		// request data from server
		getDatafromServer();
		searchBoxTF.setVisible(true);
		// event when user click on a row
		table.setRowFactory(tv -> {
			TableRow<ChangeRequest> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					stageHBox.setVisible(true);
					progressViewLabel.setVisible(false);
					selectedRequestInstance = row.getItem();
					Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF,
							departmentID, dueDateLabel, requestNameLabel, selectedRequestInstance);
				}
				resetStageImgStyleClass();
				Tools.highlightProgressBar(stage1, stage2, stage3, stage4, stage5, selectedRequestInstance);
			});
			return row;
		});
	}

	private void resetStageImgStyleClass() {
		stage1.getStyleClass().remove("img_stage_blocked");
		stage1.getStyleClass().remove("img_stage_passed");
		stage1.getStyleClass().remove("img_stage_current");
		// stage1.setOnMouseClicked(this::evalButtonClick);

		stage2.getStyleClass().remove("img_stage_blocked");
		stage2.getStyleClass().remove("img_stage_passed");
		stage2.getStyleClass().remove("img_stage_current");
		// stage3.setOnMouseClicked(this::decButtonClick);

		stage3.getStyleClass().remove("img_stage_blocked");
		stage3.getStyleClass().remove("img_stage_passed");
		stage3.getStyleClass().remove("img_stage_current");
		// stage3.setOnMouseClicked(this::exeButtonClick);

		stage4.getStyleClass().remove("img_stage_blocked");
		stage4.getStyleClass().remove("img_stage_passed");
		stage4.getStyleClass().remove("img_stage_current");
		// stage4.setOnMouseClicked(this::evalButtonClick);

		stage5.getStyleClass().remove("img_stage_blocked");
		stage5.getStyleClass().remove("img_stage_passed");
		stage5.getStyleClass().remove("img_stage_current");
		// stage5.setOnMouseClicked(this::closureButtonClick);

	}

	@SuppressWarnings("unchecked")
	public void setDataTable(Object object) {
		// System.out.println("--> setDataTable");

		ArrayList<ChangeRequest> info = ((ArrayList<ChangeRequest>) object);
		o = FXCollections.observableArrayList(info);

		colId.setCellValueFactory(new PropertyValueFactory<>("requestID"));
		colExisitCond.setCellValueFactory(new PropertyValueFactory<>("existingCondition"));
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

				if (String.valueOf(request.getRequestID()).toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches first name.
				} else if (request.getExistingCondition().toLowerCase().indexOf(lowerCaseFilter) != -1) {
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

}
