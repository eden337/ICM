package client.controllers;

import client.App;
import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.EvaluationReport;
import common.entity.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class ValidationController extends AppController implements Initializable {

    /*
     * this static variable is supposed to hold all the data of the request chosen
     * in request treatment
     */
    // public static ChangeRequest thisRequest;
    public static ValidationController instance;
    private Stage thisStage;
    protected ChangeRequest thisRequest;

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
    private Button validateBtn;

    @FXML
    private Button failureReportBtn;

    @FXML
    private Text idText11;

    @FXML
    private Button noBtn;

    @FXML
    private Text failReportLabel;

    @FXML
    private TextArea failReportTextArea;


    @FXML
    private TitledPane titledPane;

    @FXML
    private Text titledPane_Text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dueDateLabel.setVisible(true);
        instance = this;
        thisRequest = requestTreatmentController.Instance.getCurrentRequest();
        thisStage = thisRequest.getCurrentStageObject();

        if (thisStage.getInit_confirmed() != 1 && thisStage.getInit() != 1) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    loadPage("PreValidation");
                }
            });
            return;
        }

        // Stage Initialized :)

        titledPane.setCollapsible(false);
        titledPane.setText("Waiting for your action");

        if (!thisRequest.getCurrentStage().

                equals("VALIDATION")) {
            titledPane.setText("This stage is done");
            titledPane_Text.setText("This Report has Been Approved. The stage is done.");
            titledPane.getStyleClass().remove("danger");
            titledPane.getStyleClass().add("success");
        }
        Tools.fillRequestPanes(requestID, existingCondition, descripitionsTextArea, inchargeTF, departmentID, dueDateLabel, requestNameLabel, thisRequest);
        failReportLabel.setVisible(false);
        failReportTextArea.setVisible(false);
        failureReportBtn.setDisable(true);
        failureReportBtn.setVisible(false);

        setFieldsData();

    }

    private void setFieldsData() {
        OperationType ot = OperationType.VAL_GetAllReportsByRID;
        String query = "SELECT * FROM `EvaluationReports` WHERE REQUESTID = " + thisRequest.getRequestID()
                + " ORDER BY Report_ID DESC LIMIT 1;";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void setFieldsData_ServerResponse(Object object) {
        ArrayList<EvaluationReport> reports = (ArrayList<EvaluationReport>) object;
        if (reports.size() > 0) {
            // SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
            EvaluationReport individualReport = reports.get(0);
            Date reportDate = individualReport.getTimestamp();
            Calendar reportDateCal = Calendar.getInstance();
            reportDateCal.setTime(reportDate);
            reportDateCal.add(Calendar.DATE, 7);
            reportDateCal.set(Calendar.HOUR_OF_DAY, 0);
            reportDateCal.set(Calendar.MINUTE, 0);
            reportDateCal.set(Calendar.SECOND, 0);
            reportDateCal.set(Calendar.MILLISECOND, 0);

            Date today = new Date(System.currentTimeMillis());
            Calendar todayCal = Calendar.getInstance();
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);

            long diff = reportDateCal.getTime().getTime() - todayCal.getTime().getTime();
            long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (thisRequest.getCurrentStage().equals("VALIDATION")) {
                if (daysDiff >= 0) {
                    titledPane_Text.setText(
                            TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " Days left to complete this stage");
                    titledPane.getStyleClass().removeAll();
                    titledPane.getStyleClass().add("info");
                } else {
                    titledPane_Text
                            .setText("Stage in " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " days late!");
                    titledPane.getStyleClass().removeAll();
                    titledPane.getStyleClass().add("danger");
                }

            }
        }
    }


    //in this case we need to color validation back to red  because it is incomplete
    @FXML
    void failureReportBtnClicked(ActionEvent event) {
        String query = "UPDATE Requests SET Treatment_Phase = 'EXECUTION' WHERE RequestID = '"
                + thisRequest.getRequestID() + "'";
        OperationType ot = OperationType.updateRequestStatus;
        App.client.handleMessageFromClientUI(new Message(ot, query));
        showAlert(AlertType.ERROR, "Execution Failed!", "Please notify the execution leader for re-execution", null);
        loadPage("requestTreatment");
    }

    @FXML
    void noBtnClick(ActionEvent event) {
        failReportLabel.setVisible(true);
        failReportTextArea.setVisible(true);
        failureReportBtn.setVisible(true);
        failureReportBtn.setDisable(false);
        validateBtn.setDisable(true);
        noBtn.setDisable(true);
    }

    @FXML
    void validateBtnClicked(ActionEvent event) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date today = new Date(System.currentTimeMillis());

        String query = "UPDATE Requests SET Treatment_Phase = 'CLOSURE' WHERE RequestID = '"
                + thisRequest.getRequestID() + "'";
        String query2 = " UPDATE `Stage` SET  `EndTime` = '" + dateFormat.format(today) + "' where  `StageName` = 'VALIDATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";
        String query3 = " UPDATE `Stage` SET  `StartTime` = '" + dateFormat.format(today) + "' where  `StageName` = 'CLOSURE' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

        OperationType ot = OperationType.VALID_UpdateDB;
        App.client.handleMessageFromClientUI(new Message(ot, query));
        App.client.handleMessageFromClientUI(new Message(ot, query2));
        App.client.handleMessageFromClientUI(new Message(ot, query3));
        thisRequest.setPrevStage("VALIDATION");


    }

    private static int c = 0;

    public void queryResult(Object object) {
        c++;
        boolean res = (boolean) object;
        if (c == 3) {
            if (res) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadPage("requestTreatment");
                    }
                });
            } else
                showAlert(AlertType.ERROR, "Error!", "Data Error2.", null);
        }
    }
}
