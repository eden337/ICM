package client.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class ViewReportsController extends AppController implements Initializable {

    private static File file;
    public static Report selectedReport;
    public static ViewReportsController instance;
    ObservableList<Report> allReports;
    ObservableList<Report> reportsActivty;
    ObservableList<Report> reportsPerformences;
    ObservableList<Report> reportsDelays;
    private String path = System.getProperty("user.dir") + "\\ReportsFiles\\";
    @FXML
    private Button showAllBtn;

    @FXML
    private Button activitybtn;

    @FXML
    private Button performencesbtn;

    @FXML
    private Button delaysBtn;

    @FXML
    private ListView<Report> reportList;

    @FXML
    private Button csvBtn;

    @FXML
    private Button viewReportBtn;

    @FXML
    private BarChart<?, ?> frequencyChart;

    @FXML
    private Button generateReportBtn;

    @FXML
    private Text sdtxt;

    @FXML
    private Text medianTxt;

    @FXML
    private Text reporttxt1;

    @FXML
    void openAsFile(ActionEvent event) {
        selectedReport = reportList.getSelectionModel().getSelectedItem();
        file = new File(path + selectedReport.toString() + ".csv");
        if (selectedReport.isPeriodReport())
            App.client.handleMessageFromClientUI(new Message(OperationType.OpenReport, "Select * From Reports WHERE ReportType='" + selectedReport.getType() + "' And Since ='" + selectedReport.getFrom().toString() + "' AND Till ='" + selectedReport.getTo().toString() + "'"));
        else
            App.client.handleMessageFromClientUI(new Message(OperationType.OpenReport, "Select * From Reports WHERE ReportType='" + selectedReport.getType() + "' And Created ='" + selectedReport.getCreated().toString() + "'"));


    }

    @FXML
    void screenGenerateReport(ActionEvent event) {
        loadPage("Reports");
    }

    @FXML
    void showActivity(ActionEvent event) {
        reportList.setItems(reportsActivty);
    }

    @FXML
    void showAllReports(ActionEvent event) {
        reportList.setItems(allReports);
    }

    @FXML
    void showDelays(ActionEvent event) {
        reportList.setItems(reportsDelays);
    }

    @FXML
    void showPerformences(ActionEvent event) {
        reportList.setItems(reportsPerformences);
    }

    @FXML
    void showReport(ActionEvent event) {
        selectedReport = reportList.getSelectionModel().getSelectedItem();


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        sdtxt.setVisible(false);
        medianTxt.setVisible(false);
        reporttxt1.setVisible(false);
        frequencyChart.setVisible(false);
        getReports();

        //getData
    }

    public void getReports() {
        App.client.handleMessageFromClientUI(new Message(OperationType.GetReports, "Select * From Reports"));

    }


    public void setReportsToList(Object obj) {
        ArrayList<Report> info = (ArrayList<Report>) obj;
        allReports = FXCollections.observableArrayList();
        reportsActivty = FXCollections.observableArrayList();
        reportsPerformences = FXCollections.observableArrayList();
        reportsDelays = FXCollections.observableArrayList();

        for (Report repo : info) {
            allReports.add(repo);
            if (repo.getType().equals("Activity"))
                reportsActivty.add(repo);
            if (repo.getType().equals("Performences"))
                reportsPerformences.add(repo);
            if (repo.getType().equals("Delays"))
                reportsDelays.add(repo);
        }

        reportList.setItems(allReports);

    }

    public void createInPC(Object obj) throws FileNotFoundException {

        PrintWriter csvFile;
        Report report = (Report) obj;


        new File(path).mkdirs();

        csvFile = new PrintWriter(file);
        csvFile.write(report.getData());
        csvFile.close();


    }


}
