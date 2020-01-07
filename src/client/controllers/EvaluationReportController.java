package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
import common.entity.OrganizationRole;
import common.entity.StageRole;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class EvaluationReportController extends AppController implements Initializable {

    /*
     * this static variable is supposed to hold all the data of the request chosen
     * in request treatment
     */
    // public static ChangeRequest thisRequest;
    public static EvaluationReportController instance;
    private common.entity.Stage thisStage;
    protected ChangeRequest thisRequest;
    private boolean firstReportForRequest;

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
    private Button SbmtEvlBtn;

    @FXML
    private DatePicker timeEvlBox;

    @FXML
    private TextArea reqChngTXT;

    @FXML
    private TextArea expResTXT;

    @FXML
    private TextArea cnstrntTXT;

    @FXML
    private Text msgFix;

    @FXML
    private TitledPane titledPane;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private Button btnRequestExtension;

    @FXML
    private Pane Pane_Form;

    @FXML
    private Pane Pane_locked;

    @FXML
    private Text txt_locked;


    @FXML
    void SbmtEvlBtnClick(ActionEvent event) {

        if (departmentID.getText().isEmpty() || reqChngTXT.getText().isEmpty() || expResTXT.getText().isEmpty()
                || timeEvlBox.getValue() == null) {
            showAlert(AlertType.WARNING, "EvaluationReport", "You must fill all required fields.", null);
            return;
        }

        String systemID, requiredChange, expectedResult, expectedRisk, estimatedTime;
        systemID = this.departmentID.getText();
        requiredChange = this.reqChngTXT.getText();
        expectedResult = this.expResTXT.getText();
        expectedRisk = this.cnstrntTXT.getText();
        LocalDate date = this.timeEvlBox.getValue();
        estimatedTime = date.toString();

        String query = "INSERT INTO `EvaluationReports` (`RequestID`, `System_ID`, `Required_Change`, `Expected_Result`, `Expected_Risks`, `Estimated_Time`) VALUES ("

                + "'" + thisRequest.getRequestID() + "', " + "'" + systemID + "', " + "'" + requiredChange + "', " + "'"
                + expectedResult + "', " + "'" + expectedRisk + "', " + "'" + estimatedTime + "');";

        OperationType ot = OperationType.InsertEvaluation;
        App.client.handleMessageFromClientUI(new Message(ot, query));
        String query1 = "UPDATE Requests SET Treatment_Phase = 'DECISION' WHERE RequestID = '"
                + thisRequest.getRequestID() + "'";
        OperationType ot1 = OperationType.updateRequestStatus;
        App.client.handleMessageFromClientUI(new Message(ot1, query1));
        // showAlert(AlertType.INFORMATION, "Evaluation Approved", "Request moved to
        // execution phase...", null);
        loadPage("requestTreatment");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        thisRequest = requestTreatmentController.Instance.getCurrentRequest();
        thisStage = thisRequest.getCurrentStageObject();
        SbmtEvlBtn.setVisible(false);
        rightPane.setVisible(false);
        Pane_Form.setVisible(false);
        Pane_locked.setVisible(false);

        msgFix.setVisible(false);
        dueDateLabel.setVisible(false);
        titledPane.setCollapsible(false);
        titledPane.setText("Welcome");

        btnRequestExtension.setVisible(false);

        Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID,
                dueDateLabel, requestNameLabel, thisRequest);

        if(!thisRequest.getCurrentStage().equals("EVALUATION")){
            formInit();
            Pane_Form.setVisible(true);
            rightPane.setVisible(true);

        }
        else{ // in Eval Stage
            setExtensionVisability();
            if (thisStage.getInit_confirmed() == 1 && thisStage.getInit() == 1)
                rightPane.setVisible(true);
             else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadPage("PreEvaluation");
                    }
                });
                return;
            }

            if(App.user.isStageRole(thisRequest.getRequestID(), StageRole.EVALUATOR)) { // NOT EVALUATOR
                SbmtEvlBtn.setVisible(true);
                Pane_Form.setVisible(true);

            }
            else // EVALUATOR
                Pane_locked.setVisible(true);

        }




    }


    private void formInit() {
        if (!thisRequest.getCurrentStage().equals("EVALUATION")) { // Watching only
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    titledPane.getStyleClass().remove("danger");
                    titledPane.getStyleClass().add("success");
                    titledPane.setText("This stage is done.");
                    SbmtEvlBtn.setVisible(false);
                    msgFix.setText("You have only a viewing permission.");
                    msgFix.setFill(Color.FORESTGREEN);
                    msgFix.setVisible(true);
                    reqChngTXT.setEditable(false);
                    expResTXT.setEditable(false);
                    cnstrntTXT.setEditable(false);
                    timeEvlBox.setEditable(false);
                }
            });

        } else { // currently doing this stage
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    titledPane.getStyleClass().remove("danger");
                    titledPane.getStyleClass().add("info");

                    titledPane.setText("Fill in Evaluation report.");
                    msgFix.setText("After you submit the form, the evaluation will go to decision stage. ");
                }
            });

        }

        setFieldsData();
    }

    private void setFieldsData() {
        System.out.println("sdfsf");
        OperationType ot = OperationType.EVAL_GetAllReportsByRID;
        String query = "SELECT * FROM `EvaluationReports` WHERE RequestID = " + thisRequest.getRequestID()
                + " ORDER BY Report_ID DESC LIMIT 1";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void setFieldsData_ServerResponse(Object object) {
        ArrayList<EvaluationReport> reports = (ArrayList<EvaluationReport>) object;
        if (reports.size() > 0) {
            if (thisRequest.getCurrentStage().equals("EVALUATION"))
                msgFix.setVisible(true);
            EvaluationReport individualReport = reports.get(0);
            reqChngTXT.setText(individualReport.getRequired_change());
            expResTXT.setText(individualReport.getExpected_result());
            cnstrntTXT.setText(individualReport.getExpected_risks());
            timeEvlBox.setValue(individualReport.getEstimated_time().toLocalDate());
        }

    }

    public void insertNewRequestResult(Object object) {
        boolean res = (boolean) object;

        if (res) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c = Calendar.getInstance();
            Date today = new Date(System.currentTimeMillis());
            c.setTime(today);
            c.add(Calendar.DATE, 7);
            Date deadlineDate = c.getTime();

            String query1 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today) + "' where  `StageName` = 'EVALUATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
            String query2 = " UPDATE `Stage` SET  `StartTime` = '" + dateFormat.format(today) + "' , `Deadline` = '" + dateFormat.format(deadlineDate) + "' where  `StageName` = 'DECISION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

            OperationType ot1 = OperationType.EVAL_UpdateDB;
            App.client.handleMessageFromClientUI(new Message(ot1, query1));
            App.client.handleMessageFromClientUI(new Message(ot1, query2));
        } else
            showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
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
                        showAlert(AlertType.INFORMATION, "Evaluation Success", "Report updated", null);
                        loadPage("requestTreatment");
                    }
                });
            } else
                showAlert(AlertType.ERROR, "Error!", "Data Error.", null);
        }
    }


    // Extensions:
    private void setExtensionVisability() {
        btnRequestExtension.setVisible(false);
        long daysDifference = Tools.DaysDifferenceFromToday(thisRequest.getCurrentStageObject().getDeadline());
        if (daysDifference >= -3)
            btnRequestExtension.setVisible(true);
    }

    @FXML
    void requestExtension(ActionEvent event) {
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/views/Extension.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Extension");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Could not load execution prompt");
            e.printStackTrace();
        }
    }
}
