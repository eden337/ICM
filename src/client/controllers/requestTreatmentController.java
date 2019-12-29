package client.controllers;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class requestTreatmentController extends AppController implements Initializable {

    public static requestTreatmentController Instance;

    protected ChangeRequest selectedRequestInstance;

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
    private Text idText1;

    @FXML
    private Text requestNameLabel;

    @FXML
    private Text idText2;

    @FXML
    private Text dueDateLabel;

    @FXML
    private Button evaluationBtn;

    @FXML
    private Button decisionBtn;

    @FXML
    private Button executionBtn;

    @FXML
    private Button validationBtn;

    @FXML
    private Button closureBtn;

    @FXML
    private Button freezeBtn;

    @FXML
    private Button refrshBtn;

    @FXML
    private Button personalAllocationBtn;

    @FXML
    private Button btnUnfreeze;

    @FXML
    private AnchorPane rightPane_Init;

    @FXML
    private Text custom_msg1;

    @FXML
    private Button btnInit;

    ObservableList<ChangeRequest> o;

    protected ChangeRequest getCurrentRequest() {
        return selectedRequestInstance;
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

        //init buttons:
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


        // event when user click on a row
        table.setRowFactory(tv -> {
            TableRow<ChangeRequest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectedRequestInstance = row.getItem();

                    initPanes();
                    rightPane_selectRequest.setVisible(false);
                    if (row.getItem().getCurrentStage().equals("INIT")) {
                        rightPane_Init.setVisible(true);
                        if(App.user.isOrganizationRole(OrganizationRole.SUPERVISOR))
							btnInit.setVisible(true);
                    } else { // ACTIVE request
                        rightPane_requestTreatment.setVisible(true);

                        requestID.setText("" + selectedRequestInstance.getRequestID());
                        existingCondition.setText(selectedRequestInstance.getExistingCondition());
                        descripitionsTextArea.setText(selectedRequestInstance.getRemarks());
                        departmentID.setText(selectedRequestInstance.getInfoSystem());
                        requestNameLabel.setText(selectedRequestInstance.getInitiator());

                        if (selectedRequestInstance.getStatus().equals("FREEZED")) {
                            rightPane_Freezed.setVisible(true);
                            rightPane_requestTreatment.setDisable(true);
                            if (App.user.isOrganizationRole(OrganizationRole.DIRECTOR))
                                btnUnfreeze.setVisible(true);
                        } else {
                            rightPane_Freezed.setVisible(false);
                            rightPane_requestTreatment.setDisable(false);
                        }
                    }
                }
            });

            return row;
        });
    }


    public void setDataTable(Object object) {
        ArrayList<ChangeRequest> info = ((ArrayList<ChangeRequest>) object);
        o = FXCollections.observableArrayList(info);

        colId.setCellValueFactory(new PropertyValueFactory<>("requestID"));
        colIntitator.setCellValueFactory(new PropertyValueFactory<>("initiator"));
        colStage.setCellValueFactory(new PropertyValueFactory<>("currentStage"));
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

    @FXML
    void allocatePersonelButtonClick(ActionEvent event) {
        // AllocateController alloControl = new AllocateController();
        // alloControl.start(new Stage());
        mainController.instance.loadPage("Allocate",
                "Request #" + selectedRequestInstance.getRequestID() + " Treatment | Roles Allocation");

    }

    @FXML
    void closureButtonClick(ActionEvent event) {
        ClosureController closureControl = new ClosureController();
        closureControl.start(new Stage());

    }

    @FXML
    void decButtonClick(ActionEvent event) {
        DecisionController decControl = new DecisionController();
        decControl.start(new Stage());
    }

    @FXML
    void evalButtonClick(ActionEvent event) {
        EvaluationReportController evaReport = new EvaluationReportController();
        loadPage("EvaluationForm");
    }

    @FXML
    void exeButtonClick(ActionEvent event) {
        ExecutionController exeControl = new ExecutionController();
        loadPage("EvaluationForm");
    }

    @FXML
    void unfreezeButtonClick(ActionEvent event) {
    }

    @FXML
    void validButtonClick(ActionEvent event) {
        ValidationController valControl = new ValidationController();
        valControl.start(new Stage());
    }

    /* Freezing methods */

    @FXML
    void freezeButtonClick(ActionEvent event) {
        String query = "UPDATE Requests SET Status = 'FREEZED' WHERE RequestID = '" + getCurrentRequest().getRequestID()
                + "'";
        System.out.println(query);
        App.client.handleMessageFromClientUI(new Message(OperationType.FreezeRequest, query));
    }

    @FXML
    void unfreeze(ActionEvent event) {
        String query = "UPDATE Requests SET Status = 'ACTIVE' WHERE RequestID = '" + getCurrentRequest().getRequestID()
                + "'";
        App.client.handleMessageFromClientUI(new Message(OperationType.FreezeRequest, query));
    }

    public void freezeServerResponse(Object object) {
        refrshBtn(null);
        initialize(null, null);

    }

}
