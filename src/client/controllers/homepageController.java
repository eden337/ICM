package client.controllers;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

/**
 * controller for homepage
 */
public class homepageController extends AppController implements Initializable {
	public static homepageController instance;
	@FXML
	private ResourceBundle resources;

	@FXML
	private Text greeting;

	@FXML
	private URL location;

	@FXML
	private Text userRequestsInTreatment;

	@FXML
	private Text userTotalRequest;

	@FXML
	private Text UserNeedToTreat;

    @FXML
    private TableView<ChangeRequest> table5last;

    @FXML
    private TableColumn<ChangeRequest,Integer> IDtable;

    @FXML
    private TableColumn<ChangeRequest, String> EXsitingConditiontable;

    @FXML
    private TableColumn<ChangeRequest, String> Statustable;

    @FXML
    private TableColumn<ChangeRequest, String> Stagetable;

	@FXML
	private Pane paneForIT;
	
	ObservableList<ChangeRequest> o;
	
	@FXML
	void gotoMyRequests(MouseEvent event) {
		mainController.instance.goToViewRequest(null);
	}

	@FXML
	void gotoRequestTreatment(MouseEvent event) {
		mainController.instance.gotoRequestTreatment(null);
	}
	/**
	 *
	 * @param location
	 * @param resources
	 * @apiNote initialization of the homepage screen
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		paneForIT.setVisible(false);
		setGreeting();
		initData_Request();
		 get10requests();
		if (App.user.isEngineer())
			paneForIT.setVisible(true);
	}

	public void get10requests() {
		String query = "SELECT * FROM `Requests` WHERE `USERNAME`='" + App.user.getUserName() + "' AND (Status='ACTIVE' OR Treatment_Phase='INIT')  ORDER BY DATE LIMIT 10";
		App.client.handleMessageFromClientUI(new Message(OperationType.TenRequest, query));
	}

	private void setGreeting() {
		String greetingText = "";
		GregorianCalendar time = new GregorianCalendar();
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int day = time.get(Calendar.DAY_OF_MONTH);

		if (hour < 4)
			greetingText = "Good Night";
		else if (hour < 12)
			greetingText = "Good Morning";
		else if (hour < 17 && !(hour == 12))
			greetingText = "Good Afternoon";
		else if (hour == 12)
			greetingText = "Good Noon";
		else
			greetingText = "Good Evening";

		greeting.setText(greetingText + ", " + App.user.getFirstName());
	}

	/**
	 * initialize the dashboard's buttons text
	 */

	private void initData_Request() {
		App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyActiveRequests, setTableByUser()));
		App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyTotalRequests,
				"SELECT COUNT(*) FROM Requests WHERE USERNAME = '" + App.user.getUserName() + "';"));
		App.client.handleMessageFromClientUI(new Message(OperationType.Main_getMyRequestTreatment,
				"SELECT COUNT(*) FROM Requests WHERE (Status = 'ACTIVE' OR Status = 'WAITING(SUPERVISOR)')  AND USERNAME = '"
						+ App.user.getUserName() + "';"));
	}

	/**
	 *
	 * @param res
	 * response from server, if not empty will change userTotalRequest text
	 */
	public void Main_getMyTotalRequests_Response(Object res) {
		if (res == null)
			return;
		userTotalRequest.setText(res.toString());
	}
	/**
	 *
	 * @param res
	 * response from server, if not empty will change userRequestsInTreatment text
	 */
	public void Main_getMyActiveRequests_Response(Object res) {
		if (res == null)
			return;
		userRequestsInTreatment.setText(res.toString());
	}

	/**
	 *
	 * @param res
	 * response from server, if not empty will change UserNeedToTreat text
	 */
	public void Main_getMyRequestTreatment_Response(Object res) {
		if (res == null)
			return;
		UserNeedToTreat.setText(res.toString());
	}

	/**
	 * Get Request Treatment query by permission
	 *
	 * @return
	 */
	private String setTableByUser() {
		String query = "Select COUNT(*) FROM Requests WHERE Status='WAITING(SUPERVISOR)'";
		if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))
			return query;
		if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR)) {
			query = "SELECT COUNT(*) FROM Requests WHERE `Status` = 'SUSPENDED'";
			return query;
		}
		query = "SELECT COUNT(*) FROM Requests R WHERE R.RequestID IN(SELECT RequestID FROM Stage S WHERE R.RequestID = S.RequestID AND R.Treatment_Phase =S.StageName AND S.Incharge = '"
				+ App.user.getUserName() + "')";

		if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER1)
				|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_MEMBER2)
				|| App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN)) {
			// add option to see active decision stages in addition to other permission of
			// these user
			query = "SELECT COUNT(*) FROM Requests R WHERE R.RequestID IN(SELECT RequestID FROM Stage S WHERE R.RequestID = S.RequestID AND R.Treatment_Phase =S.StageName AND S.Incharge = '"
					+ App.user.getUserName()
					+ "' OR R.RequestID = S.RequestID AND R.Treatment_Phase = 'DECISION' AND S.StageName = 'DECISION')";

			if (App.user.isOrganizationRole(OrganizationRole.COMMITEE_CHAIRMAN))
				query = "SELECT COUNT(*) FROM Requests R WHERE R.RequestID IN(SELECT RequestID FROM Stage S WHERE R.RequestID = S.RequestID AND R.Treatment_Phase =S.StageName AND S.Incharge = '"
						+ App.user.getUserName()
						+ "' OR R.RequestID = S.RequestID AND R.Treatment_Phase = 'DECISION' AND S.StageName = 'DECISION' OR R.RequestID = S.RequestID AND R.Treatment_Phase = 'VALIDATION' AND S.StageName = 'VALIDATION' AND S.init_confirmed = 0)";
			return query;
		}
		// general:
		return query;
	}

	/**
	 *
	 * @param object - will get all the info of last 10 requests
	 * Setting the table view of the dashboard
	 */
	public void setTable(Object object) {
		ArrayList<ChangeRequest> info = ((ArrayList<ChangeRequest>) object);
        o = FXCollections.observableArrayList(info);

        IDtable.setCellValueFactory(new PropertyValueFactory<>("requestID"));
        EXsitingConditiontable.setCellValueFactory(new PropertyValueFactory<>("existingCondition"));
        Statustable.setCellValueFactory(new PropertyValueFactory<>("status"));
        Stagetable.setCellValueFactory(new PropertyValueFactory<>("currentStage"));
        table5last.setItems(o);
		
	}
}
