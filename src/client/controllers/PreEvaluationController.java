package client.controllers;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.OrganizationRole;
import common.entity.StageRole;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class PreEvaluationController extends AppController implements Initializable {

    public static PreEvaluationController instance;
    private ChangeRequest thisRequest;

    @FXML
    private Text idText;

    @FXML
    private TextField tfDays;

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
    private Button btnSubmit;

    @FXML
    private Button btnAccept;

    @FXML
    private Button btnDeny;

    @FXML
    private Text txtMsg;

    @FXML
    void AcceptPreEval(ActionEvent event) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        c.setTime(today);
        c.add(Calendar.DATE, Integer.parseInt(tfDays.getText()));
        Date dedlineDate = c.getTime();
        System.out.println(dateFormat.format(dedlineDate));

        OperationType ot = OperationType.PreEVAL_SetConfirmationStatus;

        String query = "UPDATE `Stage` SET" +
                " `init_confirmed` = true ," +
                " `StartTime` = '" + dateFormat.format(today) + "'," +
                " `Deadline` = '" + dateFormat.format(dedlineDate) + "'" +
                " where  `StageName` = 'EVALUATION' AND `RequestID` = '" + thisRequest.getRequestID() + "';";

        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    @FXML
    void DenyPreEval(ActionEvent event) {
        OperationType ot = OperationType.PreEVAL_SetConfirmationStatus;
        String query = "UPDATE `Stage` SET `init` = false where  `StageName` = 'EVALUATION' AND `RequestID` = '" + thisRequest.getRequestID() + "'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    @FXML
    void SubmitDaysRequest(ActionEvent event) {
        int days = 0;
        boolean flag = false;
        try {
            days = Integer.parseInt(tfDays.getText());
            if (!(days > 0 && days < 20))
                showAlert(Alert.AlertType.WARNING, "Error", "Number must be greater then zero.", null);
            flag = true;
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Error", "Data must be a number", null);
        }

        if (flag) {
            OperationType ot = OperationType.PreEVAL_SetInitStat;
            String query = "UPDATE `Stage` SET `init` = true , `requestedDays` = '" + tfDays.getText() + "' where  `StageName` = 'EVALUATION' AND `RequestID` = '" + thisRequest.getRequestID() + "'";
            App.client.handleMessageFromClientUI(new Message(ot, query));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        thisRequest = requestTreatmentController.Instance.getCurrentRequest();

        getCurrentReqestedDays();

        // GUI Init
        btnAccept.setVisible(false);
        btnDeny.setVisible(false);
        btnSubmit.setVisible(false);
        txtMsg.setVisible(false);



    }

    private void getCurrentReqestedDays() {
        OperationType ot = OperationType.PreEVAL_getData;
        String query = "SELECT `requestedDays`,`init`,`init_confirmed` FROM `Stage` WHERE  `StageName` = 'EVALUATION' AND `RequestID`= '" + thisRequest.getRequestID() + "'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void getCurrentReqestedDays_ServerResponse(Object object) {
        List<Integer> res = (List<Integer>) object;
        tfDays.setText(res.get(0) + ""); // requestedDays
        // GUI Init by Permission
        if (App.user.isOrganizationRole(OrganizationRole.SUPERVISOR) && res.get(1) == 1) {
            btnAccept.setVisible(true);
            btnDeny.setVisible(true);
            tfDays.setEditable(false);
            return;
        }

        if(App.user.isOrganizationRole(OrganizationRole.SUPERVISOR) && res.get(0) != 0){
            txtMsg.setText("Waiting for new days evaluation from the Evaluator.");
            txtMsg.setVisible(true);
            tfDays.setVisible(false);
        }

        if (res.get(1) == 1) {
            txtMsg.setVisible(true);
            tfDays.setEditable(false);
        }
        else{
            if(App.user.isStageRole(thisRequest.getRequestID(), StageRole.EVALUATOR))
                btnSubmit.setVisible(true);
        }


    }

    public void updateStatus_serverResponse(Object object) {
        boolean res = (boolean) object;
        if (res) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    loadPage("requestTreatment");
                }
            });
        } else
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Update Failed", null);
    }

}


