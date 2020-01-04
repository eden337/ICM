package client.controllers;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

//import common.entity.Reports;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class ReportGenerateController extends AppController implements Initializable {

	private ArrayList<RadioButton> types;
	
	//private Reports newReport;

    @FXML
    private AnchorPane reportTypePane;
    
    @FXML
    private AnchorPane datesPane;
    
    @FXML
    private AnchorPane submitPane;


    @FXML
    private RadioButton radioActivty;

    @FXML
    private ToggleGroup ReportType;

    @FXML
    private RadioButton radioPerformences;

    @FXML
    private RadioButton radioDelays;

    @FXML
    private Button btnSelectType;

    @FXML
    private DatePicker fromDatePic;

    @FXML
    private DatePicker toDatePic;

    @FXML
    private Button btnSelectDates;

    @FXML
    private Button btnChangeType1;

    @FXML
    private TextArea reportInfo;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnChangeDates;

    @FXML
    private Button btnViewReports;
    
    
    /*
     * all need to go into report entity
     * 
     */
    private String reportName;
    
    private LocalDate from;
    
    private LocalDate to;
    
    

    @FXML
    void ChooseDates(ActionEvent event) {
    	if(isDatelegal())
    	{
    		currentStep(3);
    		reportInfo.setText("report type "+reportName+"\nfrom:"+fromDatePic.getValue().toString()+"\nto:"+toDatePic.getValue().toString());
    	}
    	
    }

    @FXML
    void changeDates(ActionEvent event) {
    	currentStep(2);

    }

    @FXML
    void changeType(ActionEvent event) {
    	currentStep(1);

    }

    @FXML
    void chooseReportType(ActionEvent event) {
    	if(isReportType())
    		currentStep(2);
    }
    

    @FXML
    void submit(ActionEvent event) {
    	showAlert(AlertType.INFORMATION, "Success", "Report Type:"+reportName+"\nFrom:"+from.toString()+"\nTo:"+to.toString(),"Report Generated" );

    }
    private boolean isReportType()
    {
    	
    	for(RadioButton rb:types)
    		if(rb.isSelected())
    		{
    			/*
    			 * here we supposed to save report type
    			 */
    			reportName=identifyReportType(rb);
    			System.out.println("you chose " + reportName);
    			
    			
    			return true;
    			
    		}
    	showAlert(AlertType.ERROR, "Type input failure","didn't choose a type of report " , null);
    	return false;
    }
    
    private boolean isDatelegal()
    {
    	from=fromDatePic.getValue();
    	to=  toDatePic.getValue();
    	
    	if(from==null || to==null)
    		if(from!=null)
    		{
    			showAlert(AlertType.ERROR, "Dates input failure","didn't choose a to date " , null);
    			return false;
    		}
    		else if(to!=null)
    		{
    			showAlert(AlertType.ERROR, "Dates input failure","didn't choose a from date " , null);
    			return false;
    		}
    		else
    		{
    			showAlert(AlertType.ERROR, "Dates input failure","didn't choose any dates " , null);
    			return false;
    		}
    	else
			if(from.isAfter(to))
			{
				showAlert(AlertType.ERROR, "Dates input failure","date 'from' needs to be before 'to' " , null);
				return false;
			}
			else
			{
				System.out.println("you chose Dates from: "+ from.toString()+ "and to:"+to.toString() );
				return true;
			}
    	
    }
    
    
    
    
    /*
    private Reports.ReportType setReportType(RadioButton rb)
    {
    	if(rb.equals(radioActivty))
    		return Reports.ReportType.Activty;
    	else if(rb.equals(radioPerformences))
    		return Reports.ReportType.Performences;
    	else 
    		return Reports.ReportType.Delays;
    }*/

    @FXML
    void viewReports(ActionEvent event) {

    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setupReportType();
		currentStep(1);
	}
	
	void currentStep (int step )
	{
		switch(step)
		{
		case 1:
			openPane(reportTypePane);
			disablePane(datesPane, 0.7);
			disablePane(submitPane, 0.5);
			break;
		case 2:
			disablePane(reportTypePane, 0.8);
			openPane(datesPane);
			disablePane(reportTypePane, 0.7);
			break;
		case 3:
			disablePane(reportTypePane, 0.6);
			disablePane(datesPane, 0.8);
			openPane(submitPane);
			break;
		}
	}
	
	
	void openPane(AnchorPane pane)
	{
		pane.setOpacity(1);
		for(Node n:pane.getChildren())
			n.setDisable(false);
		
	}
	
	void disablePane(AnchorPane pane,double opacity)
	{
		pane.setOpacity(opacity);
		for(Node n:pane.getChildren())
			n.setDisable(true);
	}
	
	public void setupReportType()
	{
		types=new ArrayList<RadioButton>();
		types.add(radioActivty);
		types.add(radioPerformences);
		types.add(radioDelays);
		
	}
	
	private String identifyReportType(RadioButton rb)
	{
		if(rb.equals(radioActivty))
			return "activity";
		else if(rb.equals(radioDelays))
			return "Delays";
		else if(rb.equals(radioPerformences))
			return "Performences";
		else
			return null;
		
	
	}

}
