package client.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.Report;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ReportGenerateController extends AppController implements Initializable {

	public static ReportGenerateController instance;
	private ArrayList<RadioButton> types;
	
	//private Reports newReport;

	private static File file;
	
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
    
    
    private Stage stage;
    
    /*
     * all need to go into report entity
     * 
     */
    private String reportName;
    
    private LocalDate from;
    
    private LocalDate to;
    
	private String path = System.getProperty("user.dir") + "\\ReportsFiles\\";
    private static String periodReport; 
    private static String regularReport;
    

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
    	Report report=new Report(reportName, Date.valueOf(LocalDate.now()),Date.valueOf(from), Date.valueOf(to));
   	 	periodReport = report.getType()+" "+report.getFrom().toString()+" "+report.getTo().toString();
   	 	regularReport=report.getType()+report.getCreated().toString();
   	 	
    	if(report.isPeriodReport())
    	{
    		
    		if(!(file=new File(path+report.toString()+".csv")).exists())
    		{
    		App.client.handleMessageFromClientUI(new Message(OperationType.InsertReport, report));
        	App.client.handleMessageFromClientUI(new Message(OperationType.GenreateReport, "Select * From Reports WHERE ReportType='"+report.getType()+"' And Since ='"+report.getFrom().toString()+"' AND Till ='"+report.getTo().toString() +"'"));
    		}
    		else
    		{
    			showAlert(AlertType.WARNING, "Report allready exist", "this file Exist at "+path+periodReport, null);
    		}
    	}
    	else
    	{
    		//if(!(file=new File(path+regularReport+".csv")).exists())
    		file=new File(path+report.toString()+".csv");
    		App.client.handleMessageFromClientUI(new Message(OperationType.InsertReport, report));
        	App.client.handleMessageFromClientUI(new Message(OperationType.GenreateReport, "Select * From Reports WHERE ReportType='"+report.getType()+"' And Created ='"+report.getCreated().toString()+"'"));
    		

    	}
    	
    	
    	
    	//showAlert(AlertType.INFORMATION, "Success", "Report Type:"+reportName+"\nFrom:"+from.toString()+"\nTo:"+to.toString(),"Report Generated" );

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
			else if(from.isAfter(LocalDate.now())|| to.isAfter(LocalDate.now()))
			{
				showAlert(AlertType.ERROR, "Dates input failure","report period can't contain time after today' " , null);
				return false;
			}
			else
			{
				System.out.println("you chose Dates from: "+ from.toString()+ "and to:"+to.toString() );
				return true;
			}
    	
    }
    
    
   
	public void openNewReport(Object obj) throws FileNotFoundException
    {
		PrintWriter csvFile;
    	Report report=(Report)obj;
   	 

   	 	new File(path).mkdirs();   
   	 	
		 csvFile=new PrintWriter(file);
         csvFile.write(report.getData());
         csvFile.close();
         showAlert(AlertType.INFORMATION, "Success", "new file at "+path+periodReport,"Report Generated" );
         
    	/*if(reportStyle(report))
    	{
			 csvFile=new PrintWriter(file);
	         csvFile.write(report.getData());
	         csvFile.close();
	         showAlert(AlertType.INFORMATION, "Success", "new file at "+path+periodReport,"Report Generated" );
    		
    		/*if(!(file=new File(path+periodReport+".csv")).exists())
    		{
    			System.out.println(file.getPath());
    			 csvFile=new PrintWriter(file);
    	         csvFile.write(report.getData());
    	         csvFile.close();
    	     	showAlert(AlertType.INFORMATION, "Success", "new file at "+path+periodReport,"Report Generated" );

    	         
    		}
    		else
    		{
    			showAlert(AlertType.WARNING, "Report allready exist", "this file Exist at "+path+periodReport, null);
    			
    		}
    	}
    	else
    	{
    		if(!(file=new File(path+regularReport+".csv")).exists())
    		{
    			 csvFile=new PrintWriter(file);
    	         csvFile.write(report.getData());
    	         csvFile.close();
    	     	showAlert(AlertType.INFORMATION, "Success", "Report Type:"+reportName+"\nFrom:"+from.toString()+"\nTo:"+to.toString(),"Report Generated" );

    		}
    		else
    		{
    			showAlert(AlertType.WARNING, "Report allready exist", "this file Exist at "+path, null);
    			
    		}
    		
    	}*/
    	
    	/*File temp=new File(report.getType()+"_"+report.getFrom().toString()+"_"+report.getTo().toString()+".csv");
    	if(!temp.exists())
			try {
				temp.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
    /*	FileChooser fc=new FileChooser();
    	fc.setTitle("SaveDialog");
    	fc.setInitialFileName(report.getType()+"_"+report.getCreated()+"_"+".csv");
    	fc.getExtensionFilters().add(new ExtensionFilter("CSV File", "*.csv"));
    	*/
    

        
   
         /*
		try {
			File file= fc.showSaveDialog(stage);
			fc.setInitialDirectory(file.getParentFile());
			
			csvFile = new PrintWriter(file);
			csvFile.write(report.getData());
		
			csvFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
    
   
    		
    	
    
    	
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
		instance=this;
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
	
	private boolean reportStyle(Report style)
	{
		
		if(style.isPeriodReport())
			return true;
		else
			return false;
	}
	private String identifyReportType(RadioButton rb)
	{
		
		if(rb.equals(radioActivty))
			return "Activity";
		else if(rb.equals(radioDelays))
			return "Delays";
		else if(rb.equals(radioPerformences))
			return "Performences";
		else
			return null;
		
	
	}
	


}
