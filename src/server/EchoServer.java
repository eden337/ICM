package server;
// This file contains material supporting section 3.7 of the textbook:

import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.*;
import common.ocsf.server.AbstractServer;
import common.ocsf.server.ConnectionToClient;
import server.controllers.EmailSender;

import java.io.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import client.App;

// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
    // Class variables *************************************************

    /**
     * The default port to lisen on.
     */
    final public static int DEFAULT_PORT = 5555;
    public static int portNumber = 0;
    mysqlConnection mysql = new mysqlConnection();

    // Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
    }

    // Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message m = (Message) msg;
        //ServerController.instance.startDBService();
        boolean res;
        ResultSet rs;
        String query;
        System.out.println("Message received: " + msg + " from " + client);
        try {
            switch ((m.getOperationtype())) {
                case getRequirementData:
                    rs = mysql.getQuery(m.getObject().toString());
                    // Map<Object, List<Object>> ma = Tools.resultSetToMap(rs);
                    ArrayList<ChangeRequest> requestsData = getRequsets(rs);
                    sendToClient(new Message(OperationType.getRequirementData, requestsData), client);
                    rs.close();
                    break;
                case getEmployeeData:
                    rs = mysql.getQuery(m.getObject().toString());
                    // Map<Object, List<Object>> ma = Tools.resultSetToMap(rs);
                    ArrayList<EmployeeUser> EmployeeData = getEmployees(rs);
                    sendToClient(new Message(OperationType.getEmployeeData, EmployeeData), client);
                    rs.close();
                    break;
                case updateRoleInOrg:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.updateRoleInOrg, res), client);
                    break;
                case getSystemData:
                    rs = mysql.getQuery(m.getObject().toString());
                    ArrayList<InfoSystem> infoSystem = getSystemData(rs);
                    sendToClient(new Message(OperationType.getSystemData, infoSystem), client);
                    rs.close();
                    break;
                case getViewRequestData:
                    rs = mysql.getQuery(m.getObject().toString());
                    // Map<Object, List<Object>> ma1 = Tools.resultSetToMap(rs);
                    ArrayList<ChangeRequest> requestsData1 = getRequsets(rs);
                    sendToClient(new Message(OperationType.getViewRequestData, requestsData1), client);
                    rs.close();
                    break;

                case InsertRequirement:
                    int current_rid = -1;
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    rs = mysql.getQuery("SELECT `RequestID` FROM `Requests` WHERE 1 ORDER BY RequestID DESC LIMIT 1");
                    while (rs.next()) {
                        current_rid = rs.getInt(1);
                    }
                    if (!(res && current_rid > 0))
                        current_rid = -1;
                    sendToClient(new Message(OperationType.InsertRequirement, current_rid), client);
                    break;

                case InsertEvaluation:
                    boolean resEvaluation = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.InsertEvaluation, resEvaluation), client);
                    break;

                case LoginAsEmployee:
                    rs = mysql.getQuery(m.getObject().toString());
                    EmployeeUser employeeUser = null;
                    if (rs != null) {
                        while (rs.next()) {
                            employeeUser = new EmployeeUser(rs.getString("Name"), rs.getString("Surename"),
                                    rs.getString("EMAIL"), rs.getString("username"), rs.getString("password"),

                                    rs.getString("WorkerID"), rs.getString("Department"), rs.getString("Type"), null);

                        }
                        rs.close();
                        // EmailSender.sendEmail("idanabr@gmail.com",employeeUser.getUserName() + " has
                        // just logged in. Yoooho","That's really exciting moment.");

                        sendToClient(new Message(OperationType.LoginResult, employeeUser), client);
                    } else
                        sendToClient(new Message(OperationType.LoginResult, null), client);

                    break;

                case ChangeRequest_File:
                    boolean resultFile;
                    System.out.println("Message received: " + ((MyFile) m.getObject()).getFileName() + " from " + client);
                    resultFile = FileAttacedInServer((MyFile) m.getObject(), client);
                    sendToClient(new Message(OperationType.ChangeRequest_File, resultFile), client);
                    break;
                case PreValidation_GetCOMMITEE_MEMBERS:
                case Allocate_GetITUsers:
                    List<String> listOfUsers = new ArrayList<>();
                    rs = mysql.getQuery(m.getObject().toString());
                    while (rs.next()) {
                        listOfUsers.add(rs.getString("USERNAME"));
                    }
                    sendToClient(new Message(m.getOperationtype(), listOfUsers), client);
                    rs.close();
                    break;
                case PreValidation_SetRole:
                case Allocate_SetRoles:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;

                case Allocate_UpdateRoles:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;
                case User_getStageRoleObject:
                    User u1 = (User) m.getObject();
                    // stages permission:
                    query = "SELECT * FROM Stage WHERE Incharge = '" + u1.getUserName() + "'";
                    int r_id = 0;
                    rs = mysql.getQuery(query);
                    StageRole role;
                    Map<Integer, List<StageRole>> stagesRoles = new HashMap<>();
                    if (rs != null) {
                        while (rs.next()) {
                            r_id = rs.getInt("RequestID");
                            switch (rs.getString("StageName")) {
                                case "EXECUTION":
                                    role = StageRole.EXECUTER;
                                    break;
                                case "VALIDATION":
                                    role = StageRole.TESTER;
                                    break;
                                case "EVALUATION":
                                    role = StageRole.EVALUATOR;
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + rs.getString("ROLE"));
                            } // switch

                            if (stagesRoles.get(r_id) == null)
                                stagesRoles.put(r_id, new ArrayList<>());
                            stagesRoles.get(r_id).add(role);
                        }
                    }
                    System.out.println(stagesRoles);
                    sendToClient(new Message(OperationType.User_getStageRoleObject, stagesRoles), client);
                    break;
                case User_getOrgRole:
                    User u2 = (User) m.getObject();
                    query = "SELECT RoleInOrg FROM Employees WHERE USERNAME = '" + u2.getUserName() + "'";
                    rs = mysql.getQuery(query);
                    String resOrgRole = null;
                    if (rs != null) {
                        while (rs.next()) {
                            resOrgRole = rs.getString(1);
                        }
                    }
                    System.out.println(resOrgRole);
                    sendToClient(new Message(OperationType.User_getOrgRole, resOrgRole), client);
                    break;
                case SUPERVISOR_REMARKS:
                case updateRequestStatus:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.updateRequestStatus, res), client);
                    break;

                case DECISION_GetAllReportsByRID:
                case VAL_GetAllReportsByRID:
                case EVAL_GetAllReportsByRID:
                    rs = mysql.getQuery(m.getObject().toString());
                    ArrayList<EvaluationReport> reportsToReturn = new ArrayList<EvaluationReport>();

                    while (rs.next()) {

                        EvaluationReport IndividualReport = new EvaluationReport(rs.getInt("Report_ID"),
                                rs.getString("RequestID"), rs.getString("System_ID"), rs.getString("Required_Change"),
                                rs.getString("Expected_Result"), rs.getString("Expected_Risks"),
                                rs.getDate("Estimated_Time"), rs.getTimestamp("TIMESTAMP"));

                        reportsToReturn.add(IndividualReport);
                    } // while
                    sendToClient(new Message(m.getOperationtype(), reportsToReturn), client);
                    rs.close();
                    break;
                // need for considerations...
                case VALID_updateRequestStatus:
                case DECI_UpdateDB:
                case EVAL_UpdateDB:
                case VALID_UpdateDB:
                case EXE_UpdateDB:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;
                case VAL_GetInitData:
                case EXE_GetInitData:
                case EVAL_GetInitData:
                    List<Boolean> init_res = new ArrayList<Boolean>();
                    rs = mysql.getQuery(m.getObject().toString());
                    if (rs != null) {
                        while (rs.next()) {
                            init_res.add(rs.getBoolean("init"));
                            init_res.add(rs.getBoolean("init_confirmed"));
                        }
                    }
                    init_res.add(false);
                    init_res.add(false);

                    sendToClient(new Message(m.getOperationtype(), init_res), client);
                    rs.close();
                    break;
                case PreEXE_SetInitStat:
                case PreEVAL_SetInitStat:
                case PreEXE_SetConfirmationStatus:
                case PreEVAL_SetConfirmationStatus:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;
                case PreEXE_getData:
                case PreEVAL_getData:
                    List<Integer> res3 = new ArrayList<Integer>();
                    rs = mysql.getQuery(m.getObject().toString());
                    if (rs != null) {

                        while (rs.next()) {
                            res3.add(rs.getInt("requestedDays"));
                            res3.add(rs.getInt("init"));
                            res3.add(rs.getInt("init_confirmed"));
                        }
                    }
                    res3.add(0);
                    res3.add(0);
                    res3.add(0);

                    sendToClient(new Message(m.getOperationtype(), res3), client);
                    rs.close();
                    break;
                case ChangeRequest_DownloadFile:
                    String fileToSend = "Request_" + m.getObject() + ".zip";
                    String LocalfilePath = new StringBuilder().append(System.getProperty("user.dir"))
                            .append("\\serverFiles\\").append(fileToSend).toString();
                    try {
                        File newFile = new File(LocalfilePath);
                        MyFile msgFile = new MyFile(LocalfilePath, fileToSend);
                        byte[] mybytearray = new byte[(int) newFile.length()];
                        FileInputStream fis = new FileInputStream(newFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        msgFile.initArray(mybytearray.length);
                        msgFile.setSize(mybytearray.length);

                        bis.read(msgFile.getMybytearray(), 0, mybytearray.length);
                        sendToClient(new Message(m.getOperationtype(), msgFile), client);
                    } catch (FileNotFoundException e) {
                        sendToClient(new Message(m.getOperationtype(), null), client);

                        e.printStackTrace();
                    }

                    break;
                case deleteMember:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.deleteMember, res), client);
                    break;

                case Extension_submit:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;
                case VALID_GetPrevStage:
                case DECISION_GetPrevStage:
                case ChangeRequest_getStageObject:
                    rs = mysql.getQuery(m.getObject().toString());
                    Stage cStage = null;
                    while (rs.next()) {
                        cStage = new Stage(rs.getInt("RequestID"), rs.getString("StageName"),
                                Tools.convertDateSQLToZoned(rs.getDate("StartTime")),
                                Tools.convertDateSQLToZoned(rs.getDate("EndTime")),
                                Tools.convertDateSQLToZoned(rs.getDate("Deadline")), rs.getString("Incharge")
                                , rs.getInt("init"), rs.getInt("init_confirmed"), rs.getInt("extension_days"), rs.getString("extension_reason"), rs.getString("extension_decision"), rs.getString("PrevStage"));
                    }

                    sendToClient(new Message(m.getOperationtype(), cStage), client);
                    break;
                case updateSystems:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.updateSystems, res), client);
                    break;
                case DECISION_SendEmailToUser:
                    try {
                        rs = mysql.getQuery(m.getObject().toString());
                        while (rs.next()) {
                            EmailSender.sendEmail(rs.getString("EMAIL"),"ICM Notification", "Please view your progress");
                        }
                        break;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        break;
                    }
                case VALID_UpdateRepeated:
                case DECISION_updateRequestStatus:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(m.getOperationtype(), res), client);
                    break;
                case VALID_GetReport:
                case EXECUTION_GetFailReport:
                    rs =mysql.getQuery(m.getObject().toString());
                    String s = null;
                    while(rs.next()) {
                        s = rs.getString("Report");
                    }
                    sendToClient(new Message(m.getOperationtype(), s), client);
                    break;

                case InsertReport:
                	Report report=(Report)m.getObject();

                	StringBuilder fileData=new StringBuilder();
                	if(report.getType().equals("Activity"))
                	{
                		ResultSet data=mysql.getQuery("SELECT * FROM `Reports` WHERE (ReportType IN('"+report.getType()+"') AND Since IN ('"+report.getFrom().toString()+"') AND Till IN ('"+report.getTo().toString()+"') )");
                		if(data.next())
                			break;
                		boolean period= Duration.between(Tools.convertDateSQLToZoned(report.getFrom()), Tools.convertDateSQLToZoned(report.getTo())).toDays()<12?true:false;
                		TreeMap<String, ArrayList<Integer>> frequency =Tools.frequncyDistrbution(report);
                		Set <String> keys=frequency.keySet();
                		for (String s2 : keys)
                		{

                			String from;
                			String to;
                			if(period)
                			{
                				from=s2;
                				to=s2;
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Stage` as s1, `Stage` as s2	WHERE(((s1.StageName ='CLOSURE' AND (s1.EndTime IS NULL OR s1.EndTime>'"+to+"'))AND (s1.RequestID=s2.RequestID)AND (s2.StageName='EVALUATION' AND s2.StartTime <='"+from+"') ))", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='CANCELED') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+to+"')", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Frozen`  WHERE FreezeTime<='"+from+"' AND (UnFreezeTime IS NULL OR UnFreezeTime>'"+to+"')", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='DONE') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+to+"')", frequency,s2);
                    			ArrayList<Integer> temp=frequency.get(s2);
                    			int active=temp.get(0);
                				int freezed=temp.get(2);
                				int realActive=active-freezed;
                				frequency.get(s2).set(0, realActive);
                				frequency.get(s2).add(realActive);
                			}
                			else
                			{
                				from=s2.substring(0, 12);
                				to=s2.substring(13);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Stage` as s1, `Stage` as s2	WHERE(((s1.StageName ='CLOSURE' AND (s1.EndTime IS NULL OR s1.EndTime>'"+to+"'))AND (s1.RequestID=s2.RequestID)AND (s2.StageName='EVALUATION' AND s2.StartTime <='"+from+"') ))", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='CANCELED') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+to+"')", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Frozen`  WHERE FreezeTime<='"+from+"' AND (UnFreezeTime IS NULL OR UnFreezeTime>'"+to+"')", frequency,s2);
                    			calcPeriodQuery("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='DONE') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+to+"')", frequency,s2);
                    			frequency.get(s2).add(calcActiveDaysPerPeriod(from, to));
                    			ArrayList<Integer> temp=frequency.get(s2);
                    			int active=temp.get(0);
                				int freezed=temp.get(2);
                				frequency.get(s2).set(0, active-freezed);
                			}



                		}

                		ArrayList<String> columnsActivity=new ArrayList<String>();
                		columnsActivity.add("Active");
                		columnsActivity.add("Canceled");
                		columnsActivity.add("Frozen");
                		columnsActivity.add("Done");
                		columnsActivity.add("WorkDays");


                		//ArrayList<String> totalsActivty=new ArrayList<String>();
                		/*totalsActivty.add("SELECT COUNT(*) 	FROM  `Stage` as s1, `Stage` as s2	WHERE(((s1.StageName ='CLOSURE' AND (s1.EndTime IS NULL OR s1.EndTime<'"+report.getTo().toString()+"'))AND (s1.RequestID=s2.RequestID)AND (s2.StageName='EVALUATION' AND s2.StartTime<='"+report.getFrom().toString()+"') ))");
                		totalsActivty.add("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='CANCELED') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+report.getTo().toString()+"')");
                		totalsActivty.add("SELECT COUNT(*) FROM  `Frozen`  WHERE FreezeTime<='"+report.getFrom().toString()+"' AND (UnFreezeTime IS NULL OR UnFreezeTime>'"+report.getTo().toString()+"')");
                		totalsActivty.add("SELECT COUNT(*) FROM  `Requests` as r, `Stage`  as s WHERE (r.RequestID=s.RequestID) AND (r.Status='DONE') AND (s.StageName ='CLOSURE' AND s.EndTime<= '"+report.getTo().toString()+"')");*/
                		//String activtyReportData=reportBuilder(frequency, columnsActivity, totalsActivty);
                		String activtyReportData=reportBuilder(frequency, columnsActivity);




                		mysql.insertOrUpdate("INSERT INTO `Reports`(`ReportType`, `Created`,`Since` ,`Till`,`Data`)"
            					+ "VALUES" + "('" + report.getType() +  "','"
            					+ report.getCreated().toString() + "','" +report.getFrom().toString()+"','"+report.getTo().toString()+"','" + activtyReportData  + "');");


                	}
                    if(report.getType().equals("Performences"))

                	{

                    	/**
                    	 * if there is a row we need to update so insertOrUpdeate=true
                    	 * else we need to insert so insertOrUpdeate= false
                    	 */
                    	boolean insertOrUpdeate=isReportExist("SELECT * FROM `Reports` WHERE (ReportType IN('"+report.getType()+"') AND Created IN ('"+report.getCreated().toString()+"') )");



                    	HashMap<String, ArrayList<Integer>> frequency=new HashMap<String, ArrayList<Integer>>();

                    	frequency.put("Moodle", new ArrayList<Integer>());
                    	frequency.put("Labs", new ArrayList<Integer>());
                    	frequency.put("Information Station", new ArrayList<Integer>());
                    	frequency.put("Computer Farm", new ArrayList<Integer>());
                    	frequency.put("Collage website", new ArrayList<Integer>());
                    	frequency.put("Class Computers", new ArrayList<Integer>());
                    	frequency.put("Library", new ArrayList<Integer>());


                		for(String system:frequency.keySet())
                		{

                			//logic missing: check if an extnasion was given in repeated stage
                			ArrayList<Integer> values=frequency.get(system);
                			ResultSet performData=mysql.getQuery("SELECT SUM(`extension_days`) FROM `Requests` as r ,`Stage` as s WHERE r.RequestID=s.RequestID AND r.SystemID='"+system+"'");
                			performData.next();
                			Integer val= performData.getInt(1);
                			if(val!=null)
                				values.add((Integer)val);
                			else
                				values.add(0);

                    		performData=mysql.getQuery("SELECT rep.`StartTime`, rep.`EndTime` FROM `Repeted` as rep, `Requests` as r WHERE rep.RequestID=r.RequestID AND r.SystemID='"+system + "'");
                    		int repeated=0;
                    		while(performData.next())
                    			repeated=(int)Duration.between(Tools.convertDateSQLToZoned(performData.getDate(1)), Tools.convertDateSQLToZoned(performData.getDate(2))).toDays();
                    		values.add(repeated);
                			frequency.put(system, values);
                		}


                		ArrayList<String> columnsPerformence=new ArrayList<String>();
                		columnsPerformence.add("Extensions");
                		columnsPerformence.add("Repeated Days");
                		String reportPerformancesData=reportBuilder(frequency, columnsPerformence);
                		if(insertOrUpdeate)
                			mysql.insertOrUpdate("UPDATE `Reports` SET `Data`='"+reportPerformancesData+"'WHERE `ReportType`='"+report.getType()+"' AND `Created`='"+report.getCreated()+"'  ");
                		else
                			mysql.insertOrUpdate("INSERT INTO `Reports`(`ReportType`, `Created` ,`Data`)"
                					+ "VALUES" + "('" + report.getType() +  "','"
                					+ report.getCreated().toString() + "','" + reportPerformancesData  + "');");

                		/*
                		 * here i need to check amount of time spent
                		 * calculate median
                		 * calculate SD
                		 * Calaulate Frequency distribution
                		 */



                	}
                	if(report.getType().equals("Delays"))
                	{
                    	/**
                    	 * if there is a row we need to update so insertOrUpdeate=true
                    	 * else we need to insert so insertOrUpdeate= false
                    	 */
                    	boolean insertOrUpdeate=isReportExist("SELECT * FROM `Reports` WHERE (ReportType IN('"+report.getType()+"') AND Created IN ('"+report.getCreated().toString()+"') )");


                    	HashMap<String, Integer> frequency=new HashMap<String, Integer>();

                    	frequency.put("Moodle", 0);
                    	frequency.put("Labs", 0);
                    	frequency.put("Information Station", 0);
                    	frequency.put("Computer Farm", 0);
                    	frequency.put("Collage website",0);
                    	frequency.put("Class Computers", 0);
                    	frequency.put("Library", 0);
                		calcDelayedDaysPer(frequency);
                		String reportDelayData=reportBuilderOnePram(frequency, "Delayed Days");
                		if(insertOrUpdeate)
                			mysql.insertOrUpdate("UPDATE `Reports` SET `Data`='"+reportDelayData+"'WHERE `ReportType`='"+report.getType()+"' AND `Created`='"+report.getCreated()+"'  ");
                		else
                			mysql.insertOrUpdate("INSERT INTO `Reports`(`ReportType`, `Created` ,`Data`)"
                					+ "VALUES" + "('" + report.getType() +  "','"
                					+ report.getCreated().toString() + "','" + reportDelayData  + "');");
                	}
                    break;
                case GenreateReport:
                	ResultSet reportData= mysql.getQuery(m.getObject().toString());
                	reportData.next();
                	Report csvReport=new Report(reportData.getString("ReportType"), reportData.getDate("Created"));
                	if(csvReport.isPeriodReport())
                	{
                		csvReport.setFrom(reportData.getDate("Since"));
                		csvReport.setTo(reportData.getDate("Till"));
                	}
                	csvReport.setData(reportData.getString("Data"));
                	sendToClient(new Message(OperationType.InsertReport,csvReport), client);

                	break;
                default:
                    break;
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isReportExist(String query)
    {
    	ResultSet data=mysql.getQuery(query);
    	boolean res = false;
    	try {
			res= data.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return res;
    }



    /**
     * The object MyFile get from the client to the server and need to be read
     */
    private boolean FileAttacedInServer(MyFile file, ConnectionToClient client) {
        int fileSize = ((MyFile) file).getSize();
        MyFile newMsg = (MyFile) file;
        String path = System.getProperty("user.dir") + "\\serverFiles\\";
        new File(path).mkdirs();
        try {
            File newFile = new File(path + "" + newMsg.getFileName());
            byte[] mybytearray = newMsg.getMybytearray();
            FileOutputStream fos = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(mybytearray, 0, newMsg.getSize());
            bos.flush();
            fos.flush();
            bos.close();
            fos.close();
            String fileName = newMsg.getFileName();
            String[] parts = fileName.split("Request_");

            parts = parts[1].split(".zip");
            String requestID = parts[0]; // #.zip

            String qry = "UPDATE `Requests` SET `FILE`= '" + fileName + "' WHERE RequestID = " + requestID;
            boolean res = mysql.insertOrUpdate(qry);
            if (!res) {
                System.out.println(requestID + "Update Error");
                return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error file in Server: ");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @throws SQLException
     */
    public ArrayList<ChangeRequest> getRequsets(ResultSet requestData) throws SQLException {
        ArrayList<ChangeRequest> ret = new ArrayList<ChangeRequest>();
        while (requestData.next()) {
            ChangeRequest request;
            int requestID = requestData.getInt("RequestID");
            String currentStage = requestData.getString("Treatment_Phase");
            String initiator = requestData.getString("USERNAME");
            String intiatorType = requestData.getString("Position");
            String infoSystem = requestData.getString("SystemID");
            String status = requestData.getString("Status");
            String existingCondition = requestData.getString("Existing_Cond");
            String suggestedChange = requestData.getString("Wanted_Change");
            String reasonForChange = requestData.getString("Reason");
            String remarks = requestData.getString("Comments");
            String filespaths = requestData.getString("FILE");
            String currResponsible = requestData.getString("Curr_Responsible");

            ZonedDateTime submitTime = Tools.convertDateSQLToZoned(requestData.getDate("Date"));
            ZonedDateTime dueDate = Tools.convertDateSQLToZoned(requestData.getDate("Due_Date"));

            // TODO: now, the currResponsible is 'none'. we need to get all users
            // participated in this request right now according to Stage table.
            request = new ChangeRequest(initiator, intiatorType, status, requestID, infoSystem, existingCondition,
                    suggestedChange, reasonForChange, remarks, dueDate, submitTime, currentStage, filespaths,
                    currResponsible, null);
            ret.add(request);
        }
        return ret;
    }

    public ArrayList<InfoSystem> getSystemData(ResultSet systemData) throws SQLException {
        ArrayList<InfoSystem> ret = new ArrayList<>();
        Stage cStage = null;

        while (systemData.next()) {
            InfoSystem infoSystem;
            String SystemID = systemData.getString("SystemID");
            String username = systemData.getString("username");
            infoSystem = new InfoSystem(SystemID, username);
            ret.add(infoSystem);
        }
        return ret;
    }

    /**
     * @throws SQLException
     */
    public ArrayList<EmployeeUser> getEmployees(ResultSet EmployeeData) throws SQLException {
        ArrayList<EmployeeUser> ret = new ArrayList<>();
        Stage cStage = null;

        while (EmployeeData.next()) {
            EmployeeUser employee;
            String workerID = EmployeeData.getString("WorkerID");
            String username = EmployeeData.getString("username");
            String password = EmployeeData.getString("password");
            String name = EmployeeData.getString("Name");
            String surename = EmployeeData.getString("Surename");
            String email = EmployeeData.getString("EMAIL");
            String department = EmployeeData.getString("Department");
            String type = EmployeeData.getString("Type");
            String roleInOrg = EmployeeData.getString("RoleInOrg");
//            String systemID = EmployeeData.getString("SystemID");
//            String query5 = "SELECT * FROM `Stage` WHERE RequestID = '" + requestID
//                    + "' AND `StageName` = '"+currentStage+"' LIMIT 1";
//            ResultSet rs = mysql.getQuery(query5);
//            while (rs.next()) {
//                 cStage = new Stage(
//                        rs.getInt("RequestID"),
//                        rs.getString("StageName"),
//                        Tools.convertDateSQLToZoned(rs.getDate("StartTime")),
//                        Tools.convertDateSQLToZoned(rs.getDate("EndTime")),
//                        Tools.convertDateSQLToZoned(rs.getDate("Deadline")),
//                        rs.getString("Incharge"),
//                        rs.getBoolean("Extend"),
//                        rs.getInt("init"),
//                        rs.getInt("init_confirmed")
//                );
//            }
            employee = new EmployeeUser(name, surename, email, username, password, workerID, department, type,
                    roleInOrg);
            ret.add(employee);
        }
        return ret;
    }

    public String[] requestIncharge(ResultSet inCharge) throws SQLException {
        String[] requestIncharge = new String[4];
        inCharge.next();
        requestIncharge[0] = inCharge.getString("Eval_Incharge");
        requestIncharge[1] = inCharge.getString("Decision_Incharge");
        requestIncharge[2] = inCharge.getString("Exec_Incharge");
        requestIncharge[3] = inCharge.getString("Valid_InCharge");
        return requestIncharge;

    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server stops
     * listening for connections.
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    // Class methods ***************************************************

//    /**
//     * This method is responsible for the creation of the server instance (there is
//     * no UI in this phase).
//     *
//     * @param args[0] The port number to listen on. Defaults to 5555 if no argument
//     *                is entered.
//     */
    // public static void main(String[] args) {
//		int port = 0; // Port to listen on
//
//		try {
//			port = portNumber; // Get port from command line
//		} catch (Throwable t) {
//			port = DEFAULT_PORT; // Set port to 5555
//		}
//
//		EchoServer sv = new EchoServer(port);

    //
//		try {
//			sv.listen(); // Start listening for connections
//		} catch (Exception ex) {
//			System.out.println("ERROR - Could not listen for clients!");
//		}
//		AppServer.echoserver = sv;
//		AppServer.mainAppServer(args);
//	}
    public boolean getDBStatus() {
        if (mysqlConnection.con != null)
            return true;
        return false;
    }

    public HashMap<Integer, Long> calcActiveDaysPerRequest(Report report)
    {
    	ResultSet requestId=mysql.getQuery("SELECT `RequestID` FROM `Stage` WHERE ( `EndTime` >'"+report.getFrom().toString() +"'AND `StartTime` < '"+report.getTo().toString()+ "')");
    	ResultSet current=mysql.getQuery("SELECT `RequestID`,`StageName`,`StartTime`,`EndTime` FROM `Stage` WHERE ( `EndTime` >'"+report.getFrom().toString() +"'AND `StartTime` < '"+report.getTo().toString()+ "')");
    	System.out.println("SELECT `RequestID`,`StageName`,`StartTime`,`EndTime` FROM `Stage` WHERE ( `EndTime` >'"+report.getFrom().toString() +"'AND `StartTime` < '"+report.getTo().toString()+ "')");
    	ResultSet frozen=mysql.getQuery("SELECT * FROM Frozen WHERE ( UnFreezeTime >'"+report.getFrom().toString() +"'AND FreezeTime < '"+report.getTo().toString()+ "')");
    	ResultSet repeated=mysql.getQuery("SELECT RequestID,StageName,StartTime,EndTime FROM Repeted WHERE ( EndTime >'"+report.getFrom().toString() +"'AND StartTime < '"+report.getTo().toString()+ "')");
    	HashMap<Integer, Long> hm=new HashMap<Integer, Long>();
    	long res=0;
    	ZonedDateTime d1,d2;
    	ZonedDateTime from=Tools.convertDateSQLToZoned(report.getFrom());
    	ZonedDateTime to=Tools.convertDateSQLToZoned(report.getTo());
    	//List<Integer> list=Arrays.asList(avner.getArray("RequestID"));
    	/**
    	 * zero out map
    	 */
    	try {
			while(requestId.next())
			{
				int id = 0;
				try {
					id = requestId.getInt("RequestID");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(id);
				hm.put(id, (long) 0);
			}
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

    	/**
    	 * calculate all days spent on a request
    	 *
    	 * for each request Variables:
    	 * current=time spent on request in current stages (in period)
    	 * repeated= time spent on request in repeated stages (in period)
    	 * frozen = time in period when request was frozen
    	 * formula:(current +repeated)-frozen
    	 */
    	try {

    		while(current.next())
			{
				res=0;
				int temp=current.getInt("RequestID");
				d1=Tools.convertDateSQLToZoned(current.getDate("StartTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(current.getDate("EndTime"));
				if(d2.isAfter(to))
					d2=to;
				res+=(Duration.between(d1, d2).toDays());
				hm.replace(temp, hm.get(temp)+res);

			}
			while(repeated.next())
			{
				res=0;
				int temp=current.getInt("RequestID");
				d1=Tools.convertDateSQLToZoned(repeated.getDate("StartTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(repeated.getDate("EndTime"));
				if(d2.isAfter(to))
					d2=to;
				res+=(Duration.between(d1, d2).toDays());
				hm.replace(temp, hm.get(temp)+res);
			}
			while(frozen.next())
			{
				res=0;
				int temp=current.getInt("RequestID");
				d1=Tools.convertDateSQLToZoned(frozen.getDate("FreezeTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(frozen.getDate("UnFreezeTime"));
				if(d2.isAfter(to))
					d2=to;
				res+=(Duration.between(d1, d2).toDays());
				hm.replace(temp, hm.get(temp)-res);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



    	try {
			current.close();
			frozen.close();
			repeated.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return hm;
    }

    public int calcActiveDaysPerPeriod(String since,String till)
    {

    	ResultSet current=mysql.getQuery("SELECT `StageName`,`StartTime`,`EndTime` FROM `Stage` WHERE ( `EndTime` >'"+since +"'AND `StartTime` < '"+till+ "')");
    	//System.out.println("SELECT `RequestID`,`StageName`,`StartTime`,`EndTime` FROM `Stage` WHERE ( `EndTime` >'"+since +"'AND `StartTime` < '"+till+ "')");
    	ResultSet frozen=mysql.getQuery("SELECT * FROM Frozen WHERE ( UnFreezeTime >'"+since +"'AND FreezeTime < '"+till+ "')");
    	ResultSet repeated=mysql.getQuery("SELECT RequestID,StageName,StartTime,EndTime FROM Repeted WHERE ( EndTime >'"+since+"'AND StartTime < '"+till+ "')");
    	long res=0;
    	ZonedDateTime d1,d2;
    	ZonedDateTime from=ZonedDateTime.of(Integer.parseInt(since.substring(0, 4)), Integer.parseInt(since.substring(6, 8)), Integer.parseInt(since.substring(10, 12)), 0, 0, 0, 0, ZoneId.systemDefault());
    	ZonedDateTime to=ZonedDateTime.of(Integer.parseInt(till.substring(0, 4)), Integer.parseInt(till.substring(6, 8)), Integer.parseInt(till.substring(10, 12)), 0, 0, 0, 0,ZoneId.systemDefault());
    	//List<Integer> list=Arrays.asList(avner.getArray("RequestID"));
    	long mainStages=0;
    	long repeatedStages=0;
    	long freezeStages=0;


    	/**
    	 * calculate all days spent on a request
    	 *
    	 * for each request Variables:
    	 * current=time spent on request in current stages (in period)
    	 * repeated= time spent on request in repeated stages (in period)
    	 * frozen = time in period when request was frozen
    	 * formula:(current +repeated)-frozen
    	 */
    	try {

    		while(current.next())
			{
				d1=Tools.convertDateSQLToZoned(current.getDate("StartTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(current.getDate("EndTime"));
				if(d2.isAfter(to))
					d2=to;
				mainStages+=(Duration.between(d1, d2).toDays());


			}
			while(repeated.next())
			{
				d1=Tools.convertDateSQLToZoned(repeated.getDate("StartTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(repeated.getDate("EndTime"));
				if(d2.isAfter(to))
					d2=to;
				repeatedStages+=(Duration.between(d1, d2).toDays());

			}
			while(frozen.next())
			{
				d1=Tools.convertDateSQLToZoned(frozen.getDate("FreezeTime"));
				if(d1.isBefore(from))
					d1=from;
				d2=Tools.convertDateSQLToZoned(frozen.getDate("UnFreezeTime"));
				if(d2.isAfter(to))
					d2=to;
				freezeStages+=(Duration.between(d1, d2).toDays());

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



    	try {
			current.close();
			frozen.close();
			repeated.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return (int)((mainStages+freezeStages)-freezeStages);
    }
    /*
    public HashMap<String, Long> calcDelayedDaysPerRequest(HashMap<String, Long> mapDelays)
    {
    	ResultSet delayData=mysql.getQuery("SELECT `SystemID`,EndTime,Deadline FROM `Requests` as r, `Stage` as s WHERE r.RequestID=s.RequestID AND Deadline IS NOT NULL");
    	ZonedDateTime deadline;
    	ZonedDateTime compareTo;
    	try {
			while(delayData.next())
			{
				deadline=Tools.convertDateSQLToZoned(delayData.getDate("Deadline"));
				Date temp=delayData.getDate("Endtime");
				compareTo= temp!=null?Tools.convertDateSQLToZoned(temp):ZonedDateTime.now();
				long res=Duration.between(deadline, compareTo).toDays();
				res=res>0?res:0;
				String system=delayData.getString("SystemID");
				mapDelays.replace(system, mapDelays.get(system)+res);
			}
			delayData=mysql.getQuery("SELECT `SystemID`,EndTime,Deadline FROM `Requests` as r, `Repeted` as s WHERE r.RequestID=s.RequestID AND Deadline IS NOT NULL");
			while(delayData.next())
			{
				deadline=Tools.convertDateSQLToZoned(delayData.getDate("Deadline"));
				Date temp=delayData.getDate("Endtime");
				compareTo= temp!=null?Tools.convertDateSQLToZoned(temp):ZonedDateTime.now();
				long res=Duration.between(deadline, compareTo).toDays();
				res=res>0?res:0;
				String system=delayData.getString("SystemID");
				mapDelays.replace(system, mapDelays.get(system)+res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mapDelays;

    }
    */
    public void calcDelayedDaysPer(HashMap<String, Integer> mapDelays)
    {
    	ResultSet delayData=mysql.getQuery("SELECT `SystemID`,EndTime,Deadline FROM `Requests` as r, `Stage` as s WHERE r.RequestID=s.RequestID AND Deadline IS NOT NULL");
    	ZonedDateTime deadline;
    	ZonedDateTime compareTo;
    	try {
			while(delayData.next())
			{
				deadline=Tools.convertDateSQLToZoned(delayData.getDate("Deadline"));
				Date temp=delayData.getDate("Endtime");
				compareTo= temp!=null?Tools.convertDateSQLToZoned(temp):ZonedDateTime.now();
				int res=(int)Duration.between(deadline, compareTo).toDays();
				res=res>0?res:0;
				String system=delayData.getString("SystemID");
				mapDelays.replace(system, mapDelays.get(system)+res);
			}
			delayData=mysql.getQuery("SELECT `SystemID`,EndTime,Deadline FROM `Requests` as r, `Repeted` as s WHERE r.RequestID=s.RequestID AND Deadline IS NOT NULL");
			while(delayData.next())
			{
				deadline=Tools.convertDateSQLToZoned(delayData.getDate("Deadline"));
				Date temp=delayData.getDate("Endtime");
				compareTo= temp!=null?Tools.convertDateSQLToZoned(temp):ZonedDateTime.now();
				int res=(int)Duration.between(deadline, compareTo).toDays();
				res=res>0?res:0;
				String system=delayData.getString("SystemID");
				mapDelays.replace(system, mapDelays.get(system)+res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    public void calcPeriodQuery(String query,TreeMap<String, ArrayList<Integer>> frequency,String s)
    {
		try {

				ResultSet data=mysql.getQuery(query);
				data.next();
				frequency.get(s).add(data.getInt(1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //old signature with Totals
   // public String reportBuilder (TreeMap<String, ArrayList<Integer>> frequency,ArrayList<String> columns,ArrayList<String> totals)

    public String reportBuilder (Map<String, ArrayList<Integer>> frequency,ArrayList<String> columns)
    {
    	StringBuilder ret=new StringBuilder();
    	ret.append("type/divioisn and Data");
    	ret.append(',');

    	for(String s: frequency.keySet())
    	{
    		ret.append(s);
    		ret.append(',');

    	}
    	ret.append("Median");
    	ret.append(',');
    	ret.append("Standard Deviation");
    	ret.append(',');
    	//ret.append("Total");
    	ret.append("\r\n");
    	int i=0;
    	for(String s:columns)
    	{
    		ret.append(s);
    		ret.append(',');

    		ArrayList<Integer> values=new ArrayList<Integer>();
    		for(String period:frequency.keySet())
    		{
    			int val=frequency.get(period).get(i);
    			values.add(val);
    			ret.append(val);
    			ret.append(',');





    		}
    		int median=Tools.calcMedian(values);
    		ret.append(median);
			ret.append(',');
			ret.append(Tools.calculateSD(values));
			//extra to calculate total not that important
			/*ret.append(',');
			try {
				ResultSet total=mysql.getQuery(totals.get(i));
				total.next();
				ret.append(total.getInt(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			i++;


    		ret.append("\r\n");
    	}
    	return ret.toString();
    }

    public String reportBuilderOnePram (Map<String, Integer> frequency,String column)
    {
    	StringBuilder ret=new StringBuilder();
    	ret.append("type/Division and Data");
    	ret.append(',');

    	for(String s: frequency.keySet())
    	{
    		ret.append(s);
    		ret.append(',');

    	}
    	ret.append("Median");
    	ret.append(',');
    	ret.append("Standard Deviation");
    	ret.append(',');
    	ret.append("Total");
    	ret.append("\r\n");


    	ret.append(column);
    	ret.append(',');

    	ArrayList<Integer> values=new ArrayList<Integer>();
    		for(String period:frequency.keySet())
    		{
    			int val=frequency.get(period);
    			values.add(val);
    			ret.append(val);
    			ret.append(',');





    		}
    		int median=Tools.calcMedian(values);
    		ret.append(median);
			ret.append(',');
			ret.append(Tools.calculateSD(values));
			//extra to calculate total not that important
			/*ret.append(',');
			try {
				ResultSet total=mysql.getQuery(totals.get(i));
				total.next();
				ret.append(total.getInt(1));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/



    	ret.append("\r\n");

    	return ret.toString();
    }

}










//End of EchoServer class
