package client.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.TreeMap;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.Report;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ViewReportsController extends AppController implements Initializable {

	private static File file;
	public static Report selectedReport;
	public static ViewReportsController instance;
	public static TreeMap<String, ArrayList<Integer>> datesAndData;
	public static TreeMap<String, ArrayList<Double>> columns;
	public static TreeMap<Integer,Integer> frequency;
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
    private Button generateReportBtn;

    @FXML
    private VBox createGraphicsPane;

    @FXML
    private Label lblReportName;



    @FXML
    private TextArea reportDescArea;

    @FXML
    private Button btnViewGraphics;

    
	
    @FXML
    private AnchorPane paneList;
    @FXML
    private ImageView buffer;
    
    
    


    @FXML
    void openAsFile(ActionEvent event) {
    	selectedReport= reportList.getSelectionModel().getSelectedItem();
    	if(selectedReport==null)
    	{
    		showAlert(AlertType.ERROR, "No report was chosen", "you must choose a report to view it", null);
    		return;
    	}
    	file=new File(path+selectedReport.toString()+".csv");
    	if(!file.exists())
    		if(selectedReport.isPeriodReport())
    			App.client.handleMessageFromClientUI(new Message(OperationType.OpenReport, "Select * From Reports WHERE ReportType='"+selectedReport.getType()+"' And Since ='"+selectedReport.getFrom().toString()+"' AND Till ='"+selectedReport.getTo().toString() +"'"));
    		else
    			App.client.handleMessageFromClientUI(new Message(OperationType.OpenReport, "Select * From Reports WHERE ReportType='"+selectedReport.getType()+"' And Created ='"+selectedReport.getCreated().toString()+"'"));
    	else
    	    try {
                Desktop.getDesktop().open(file);
            }
            catch(IOException e ){}


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
    void openGraphView(ActionEvent event) {

 
    		loadPage("ReportGraphics");
    	
    }
    
    

    @FXML
    void showReport(ActionEvent event) {
    	selectedReport= reportList.getSelectionModel().getSelectedItem();
    	if(selectedReport==null)
    	{
    		showAlert(AlertType.ERROR, "No report was chosen", "you must choose a report to view it", null);
    		return;
    	}
    	breakReportData();
    	Report report=selectedReport;
    	disablePane(createGraphicsPane, 0.1);
    	if(selectedReport.isPeriodReport())
    	{
    		currentReport(1);
    		
    	}
    	else
    	{
        	if(selectedReport.getType().equals("Delays"))
        		currentReport(3);
        	else
        		currentReport(2);
        	
    	}
    	
    }
    
    private void currentReport(int i)
    {
    	String info="";
    	switch(i)
    	{
    	case 1:
    		info="activity desc";
    		break;
    	case 2:
    		info="performences desc";
    		break;
    	case 3:
    		info="delay desc";
    		break;
    	}
		openPane(createGraphicsPane);
		lblReportName.setText(labelFormat());
		reportDescArea.setText(info);
    }
    
    
    private String labelFormat()
    {
    	if(!selectedReport.isPeriodReport())
    		return "Report type:"+selectedReport.getType()+"\r\n"
    		+ "Created:"+selectedReport.flipDateformat(selectedReport.getCreated().toString())+"\r\n";
    	else
    		return "Report type: "+selectedReport.getType()+"\r\n"
    		+ "Created: "+selectedReport.flipDateformat(selectedReport.getFrom().toString())+"\r\n"
    				+ "From: "+selectedReport.flipDateformat(selectedReport.getFrom().toString())+"\r\n"
    						+ "To: "+selectedReport.flipDateformat(selectedReport.getTo().toString());
    				
    }
    void openPane(VBox pane) {
        pane.setOpacity(1);
        for (Node n : pane.getChildren())
            n.setDisable(false);

    }
    
    void disablePane(VBox pane, double opacity) {
        pane.setOpacity(opacity);
        for (Node n : pane.getChildren())
            n.setDisable(true);
    }
    
    

    
    public void periodFrequencyCalac(Object obj)
    {

    	frequency=(TreeMap<Integer, Integer>)obj;
		buffer.setVisible(false);
		//buffer.setDisable(true);
		printFrequency();
		currentReport(1);
		
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance=this;
		buffer.setVisible(true);
		disablePane(createGraphicsPane, 0);
		getReports();
	
		//getData
	}
	
	public void getReports()
	{
		App.client.handleMessageFromClientUI(new Message(OperationType.GetReports,"Select * From Reports"));
		
	}
	
	
	public void setReportsToList(Object obj)
	{
		ArrayList<Report> info =(ArrayList<Report>) obj;
		allReports=FXCollections.observableArrayList();
		reportsActivty=FXCollections.observableArrayList();
		reportsPerformences=FXCollections.observableArrayList();
		reportsDelays=FXCollections.observableArrayList();

		for(Report repo: info)
		{
			allReports.add(repo);
			if(repo.getType().equals("Activity"))
				reportsActivty.add(repo);
			if(repo.getType().equals("Performences"))
				reportsPerformences.add(repo);
			if(repo.getType().equals("Delays"))
				reportsDelays.add(repo);
		}

		buffer.setVisible(false);
		reportList.setItems(allReports);
		
	}
	
	public void createInPC(Object obj) throws FileNotFoundException
	{

		PrintWriter csvFile;
    	Report report=(Report)obj;
   	 

   	 	new File(path).mkdirs();   
   	 	
		 csvFile=new PrintWriter(file);
         csvFile.write(report.getData());
         csvFile.close();
         try {
             Desktop.getDesktop().open(file);
         }
         catch(IOException e ){}
		
		
	}
	
	
	private void breakReportData()
	{
		breakColumns();
		printCheckColumns();
		breakDatesAndData();
		calcAvg();
		printCheckDates();
	}
	
	
	private void breakDatesAndData()
	{
		datesAndData=new TreeMap<String, ArrayList<Integer>>();
		String[][]rows=breakString();


		for(int i=1;i<rows[0].length-2;i++)
		{
			String key=rows[0][i];
			ArrayList<Integer> temp=new ArrayList<Integer>();
			for(int j=1;j<rows.length;j++)
			{
				temp.add(Integer.valueOf(rows[j][i]));
			}
			datesAndData.put(key, temp);
		}
		
	}
	private void breakColumns()
	{
		columns=new TreeMap<String, ArrayList<Double>>();
		
	
		String[][]rows=breakString();

		int medianSpot=rows[0].length-2;
		int sdSpot=rows[0].length-1;
		for(int i=1;i<rows.length;i++)
		{
			ArrayList<Double> temp=new ArrayList<Double>();
			temp.add(Double.valueOf(rows[i][medianSpot]));
			temp.add(Double.valueOf(rows[i][sdSpot]));
			columns.put(rows[i][0],temp);
		}
	}
	
	private void calcAvg()
	{
		int sum=0;
		int i=0;
		for(String c:columns.keySet())
		{
			for(String s: datesAndData.keySet())
				sum+=datesAndData.get(s).get(i);
			double avg=sum;
			
			System.out.println("sum=" +sum);
			avg=avg/datesAndData.keySet().size();
			System.out.println("avg="+avg);
			columns.get(c).add(avg);
			i++;
			sum=0;
		}
		
	}
	private String[][] breakString()
	{
		String[] firstSplit=selectedReport.getData().split("\r\n");
		String[][]rows=new String[firstSplit.length][];
		for(int i=0;i<rows.length;i++)
			rows[i]=firstSplit[i].split(",");
		return rows;
	}
	
	private void printCheckDates()
	{
		System.out.println("dates or Data: "+datesAndData);
	}
	private void printCheckColumns()
	{
		System.out.println("columns: "+columns);
	}
	private void printFrequency()
	{
		System.out.println("frequency: "+frequency);
	}
	
	
	

}
