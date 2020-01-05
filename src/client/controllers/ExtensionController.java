package client.controllers;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.ChangeRequest;
import common.entity.Extension;
import common.entity.OrganizationRole;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ExtensionController extends AppController implements Initializable {
    public static ExtensionController instance;
    protected ChangeRequest thisRequest;

    @FXML
    private Pane defualtPane;

    @FXML
    private TextArea taReason;

    @FXML
    private Button btnAccept;

    @FXML
    private Button btnDeny;

    @FXML
    private Button btnSubmit;

    @FXML
    private TextField tfDays;

    @FXML
    private Pane msgPane;


    @FXML
    private Text warning;
    @FXML
    void submitForm(ActionEvent event) {
        this.event = event;
        warning.setVisible(false);

        if(tfDays.getText().isEmpty() || taReason.getText().isEmpty()){
            warning.setVisible(true);
            return;
        }
        OperationType ot = OperationType.Extension_submit;
        String query = "UPDATE `Stage` SET " +
                "`extension_days`= '"+tfDays.getText()+"', " +
                "`extension_reason`= '"+taReason.getText()+"' " +
                "WHERE`RequestID`= '"+thisRequest.getRequestID()+"' AND `StageName` = '"+thisRequest.getCurrentStage()+"'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    @FXML
    void accept(ActionEvent event) {
        this.event = event;
        OperationType ot = OperationType.Extension_submit;
        String query = "UPDATE `Stage` SET " +
                "`extension_decision`= 'ACCEPT' " +
                "WHERE`RequestID`= '"+thisRequest.getRequestID()+"' AND `StageName` = '"+thisRequest.getCurrentStage()+"'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    @FXML
    void deny(ActionEvent event) {
        this.event = event;
        OperationType ot = OperationType.Extension_submit;
        String query = "UPDATE `Stage` SET " +
                "`extension_decision`= 'DENIED' " +
                "WHERE`RequestID`= '"+thisRequest.getRequestID()+"' AND `StageName` = '"+thisRequest.getCurrentStage()+"'";
        App.client.handleMessageFromClientUI(new Message(ot, query));

    }


    private Event event;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        thisRequest = requestTreatmentController.Instance.selectedRequested;
        warning.setVisible(false);
        defualtPane.setVisible(false);
        msgPane.setVisible(false);
        btnDeny.setVisible(false);
        btnAccept.setVisible(false);

        getCurrentExtension();
    }

    // TODO : Update Deadline Time in Stage !!!!!!!!!!!!!!!

    private void getCurrentExtension() {
        OperationType ot = OperationType.Extension_getData;
        String query = "SELECT `RequestID`,`StageName`,`extension_days`,`extension_reason`,`extension_decision` FROM `Stage` WHERE `StageName` = '" + thisRequest.getCurrentStage() + "' AND `RequestID`= '" + thisRequest.getRequestID() + "'";
        App.client.handleMessageFromClientUI(new Message(ot, query));
    }

    public void getCurrentExtension_ServerResponse(Object object) {
        Extension extension = (Extension) object;
        if (extension.getReason() != null ) { // if request sent
            defualtPane.setVisible(true);
            tfDays.setEditable(false);
            taReason.setEditable(false);
            btnSubmit.setVisible(false);

            taReason.setText(extension.getReason());
            tfDays.setText(extension.getDays() + "");

            warning.setText("Extension request " + extension.getDecision().toLowerCase());
            warning.setVisible(true);

            if(extension.getDecision()!=null){
                btnAccept.setVisible(false);
                btnDeny.setVisible(false);
                return;
            }

            if (!App.user.isOrganizationRole(OrganizationRole.SUPERVISOR)) {
                btnAccept.setVisible(false);
                btnDeny.setVisible(false);
                return;
            }
            // supervisor response
            btnSubmit.setVisible(false);
            btnAccept.setVisible(true);
            btnDeny.setVisible(true);

        } else { // == null -> user can send a form.

            defualtPane.setVisible(true);
            btnSubmit.setVisible(true);

        }
    }

    public void InsertOrUpdate_ServerResponse(Object object){
        Boolean res = (Boolean)object;
        if(res){
            showAlert(Alert.AlertType.INFORMATION,"Operation Done!","Operation done successfully!",null);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
                }
            });

        }
        else{
            showAlert(Alert.AlertType.ERROR,"Operation Failed!","Operation failed. Try again.",null);
        }

    }

    @FXML
    void close(ActionEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
            }
        });
    }

}
