package server;
// This file contains material supporting section 3.7 of the textbook:

import common.Tools;
import common.controllers.Message;
import common.controllers.OperationType;
import common.entity.*;
import common.ocsf.server.AbstractServer;
import common.ocsf.server.ConnectionToClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                case getViewRequestData:
                    rs = mysql.getQuery(m.getObject().toString());
                    Map<Object, List<Object>> ma1 = Tools.resultSetToMap(rs);
                    sendToClient(new Message(OperationType.getViewRequestData, ma1), client);
                    rs.close();
                    break;

                case InsertRequirement:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.InsertRequirement, res), client);
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
                                    rs.getString("WorkerID"), rs.getString("Department"), rs.getString("Type"));
                        }
                        rs.close();
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
                case Allocate_GetITUsers:
                    List<String> listOfUsers = new ArrayList<>();
                    rs = mysql.getQuery(m.getObject().toString());
                    while (rs.next()) {
                        listOfUsers.add(rs.getString("USERNAME"));
                    }
                    sendToClient(new Message(OperationType.Allocate_GetITUsers, listOfUsers), client);
                    rs.close();
                    break;
                case Allocate_SetRoles:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.Allocate_SetRoles, res), client);
                    break;
                case User_getStageRoleObject:
                    User u1 = (User) m.getObject();
                    // stages permission:
                    query = "SELECT * FROM UserRoleInStage WHERE USERNAME = '" + u1.getUserName() + "'";
                    int r_id = 0;
                    rs = mysql.getQuery(query);
                    StageRole role;
                    Map<Integer, List<StageRole>> stagesRoles = new HashMap<>();
                    if (rs != null) {
                        while (rs.next()) {
                            r_id = rs.getInt("REQUEST_ID");
                            switch (rs.getString("ROLE")) {
                                case "EXECUTER":
                                    role = StageRole.EXECUTER;
                                    break;
                                case "TESTER":
                                    role = StageRole.TESTER;
                                    break;
                                case "EVALUATOR":
                                    role = StageRole.EVALUATOR;
                                    break;
                                case "INCHARGE":
                                    role = StageRole.INCHARGE;
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
                case FreezeRequest:
                    res = mysql.insertOrUpdate(m.getObject().toString());
                    sendToClient(new Message(OperationType.FreezeRequest, res), client);
                    break;

                case EVAL_GetAllReportsByRID:
                    rs = mysql.getQuery(m.getObject().toString());
                    ArrayList<EvaluationReport> reportsToReturn = new ArrayList<EvaluationReport>();
                    while (rs.next()) {
                        EvaluationReport IndividualReport = new EvaluationReport(
                                rs.getInt("Report_ID"),
                                rs.getString("RequestID"),
                                rs.getString("System_ID"),
                                rs.getString("Required_Change"),
                                rs.getString("Expected_Result"),
                                rs.getString("Expected_Risks"),
                                rs.getDate("Estimated_Time"),
                                rs.getDate("TIMESTAMP")
                        );
						reportsToReturn.add(IndividualReport);
                    } // while
					System.out.println(reportsToReturn);
                    sendToClient(new Message(OperationType.EVAL_GetAllReportsByRID, reportsToReturn), client);
                    rs.close();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

        // this.sendToAllClients(msg);
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
                    currResponsible);
            ret.add(request);
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

    public Stage[] getRequestActiveStages(ResultSet stageData, int requestID, int index) throws SQLException {
        Stage[] requestStages = new Stage[5];
        int i = 0;
        while (stageData.next()) {

            String temp = stageData.getString("StageName");
            ZonedDateTime start = Tools.convertDateSQLToZoned(stageData.getDate("StartTime"));
            ZonedDateTime deadline = Tools.convertDateSQLToZoned(stageData.getDate("Deadline"));
            String handler = stageData.getString("Handlers");
            String incharge = stageData.getString("Incharge");
            boolean extend = stageData.getBoolean("Extend");
            boolean delay = stageData.getBoolean("Delay");

            switch (Tools.convertStringToStageName(temp)) {
                case EVALUATION:
                    requestStages[0] = new Stage(requestID, StageName.EVALUATION, start, deadline, handler, incharge,
                            extend, delay);
                    /*
                     * need to update sql after delay update
                     */

                    break;
                case DECISION:
                    requestStages[1] = new Stage(requestID, StageName.DECISION, start, deadline, handler, incharge, extend,
                            delay);
                    /*
                     * need to update sql after delay update
                     */

                    break;

                case EXECUTION:
                    requestStages[2] = new Stage(requestID, StageName.EXECUTION, start, deadline, handler, incharge, extend,
                            delay);
                    /*
                     * need to update sql after delay update
                     */

                    break;
                case VALIDATION:
                    requestStages[3] = new Stage(requestID, StageName.VALIDATION, start, deadline, handler, incharge,
                            extend, delay);

                    /*
                     * need to update sql after delay update
                     */
                    break;

                case CLOUSRE:
                    requestStages[4] = new Stage(requestID, StageName.CLOUSRE, start, deadline, handler, incharge, extend,
                            delay);

                    /*
                     * need to update sql after delay update
                     */
                    break;
            }
            /*
             * if(i<index)
             * requestStages[i].setEndTime(Tools.convertDateSQLToZoned(stageData.getDate(
             * "EndTime"))); i++;
             */

        }
        return requestStages;

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
}
//End of EchoServer class
