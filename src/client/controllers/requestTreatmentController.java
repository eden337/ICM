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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class requestTreatmentController extends AppController implements Initializable {

    public static requestTreatmentController Instance;

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
    private AnchorPane rightPane_msg;

    @FXML
    private AnchorPane rightPane_Freezed;

    @FXML
    private Text custom_msg;

    @FXML
    private Button btnAllocateUsers;

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

    protected ChangeRequest getCurrentRequest() {
        return selectedRequestInstance;
    }

    private void getDatafromServer() {
        App.client.handleMessageFromClientUI(new Message(OperationType.getRequirementData, "SELECT * FROM Requests"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Instance = this;
        rightPane_requestTreatment.setVisible(false);
        rightPane_msg.setVisible(true);
        btnAllocateUsers.setVisible(false);
        submitBtn.setDisable(true);
        msg.setVisible(false);
        btnUnfreeze.setVisible(false);
        rightPane_Freezed.setVisible(false);

        /*
         * status.getItems().addAll( "Active", "Done", "Frozen" );
         */

        // request data from server
        getDatafromServer();
        // event when user click on row
        table.setRowFactory(tv -> {
            TableRow<ModelTable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    // submitBtn.setDisable(false);
                    rightPane_msg.setVisible(false);
                    rightPane_requestTreatment.setVisible(true);


                    selectedRequestInstance = new ChangeRequest(row.getItem().getId(), row.getItem().getName(),
                            row.getItem().getSystemID(), row.getItem().getExistChange(), row.getItem().getComments(),
                            row.getItem().getDueDate(), row.getItem().getCurrResponsible(), row.getItem().getStatus());

                    requestID.setText(selectedRequestInstance.getRequestID());
                    existingCondition.setText(selectedRequestInstance.getExistingCondition());
                    descripitionsTextArea.setText(selectedRequestInstance.getRemarks());
                    dueDateLabel.setText(selectedRequestInstance.getDueDate());
                    requestNameLabel.setText(selectedRequestInstance.getRequestName());
                    // status.setValue(row.getItem().getStatus());
                    inchargeTF.setText(selectedRequestInstance.getCurrInCharge());
                    departmentID.setText(selectedRequestInstance.getInfoSystem());


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
            });

            return row;
        });
//        // event on button press:
//        submitBtn.setOnAction(event -> {
//            // if(statusTextField.getText() !=
//            // m.get(statusTextField.getText()).get(Integer.parseInt(requestID.getText())))
//            // {
//            App.client.handleMessageFromClientUI(
//                    new Message(OperationType.InsertRequirement, "update requirement SET STATUS = '" + "Active"
//                            + "' where idRequirement = '" + requestID.getText() + "';"));
//
//            // }
//        });
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

    @FXML
    void allocatePersonelButtonClick(ActionEvent event) {
        //AllocateController alloControl = new AllocateController();
        //alloControl.start(new Stage());
        mainController.instance.loadPage("Allocate", "Request #" + selectedRequestInstance.getRequestID() + " Treatment | Roles Allocation");

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
        evaReport.start(new Stage());
    }

    @FXML
    void exeButtonClick(ActionEvent event) {
        ExecutionController exeControl = new ExecutionController();
        exeControl.start(new Stage());
    }


    @FXML
    void unfreezeButtonClick(ActionEvent event) {
    }

    @FXML
    void validButtonClick(ActionEvent event) {
        ValidationController valControl = new ValidationController();
        valControl.start(new Stage());
    }


    /* Freezing methods*/


    @FXML
    void freezeButtonClick(ActionEvent event) {
        String query = "UPDATE Requests SET Status = 'FREEZED' WHERE RequestID = '" + getCurrentRequest().getRequestID() + "'";
        System.out.println(query);
        App.client.handleMessageFromClientUI(new Message(OperationType.FreezeRequest, query));
    }

    @FXML
    void unfreeze(ActionEvent event) {
        String query = "UPDATE Requests SET Status = 'ACTIVE' WHERE RequestID = '" + getCurrentRequest().getRequestID() + "'";
        App.client.handleMessageFromClientUI(new Message(OperationType.FreezeRequest, query));
    }

    public void freezeServerResponse(Object object) {
        refrshBtn(null);
        initialize(null, null);

    }


}
