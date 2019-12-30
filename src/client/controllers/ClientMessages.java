package client.controllers;

import client.App;
import common.controllers.Message;

/**
 * get <Code> Message </code> from server and send it to the relevant
 * controller. Usually used for transfer data from db to client.
 *
 * @author Idan Abergel
 */
public class ClientMessages {

    /***
     * messageFromServer - The next method receives 1 Object and , divides all
     * actions into cases according to Operationtype and use the Object to
     * perform the operation and after send a msg to the client with the
     * appropriate details
     */

    public static void messageFromServer(Message msg) {
        // System.out.println("--> ClientMessages.messageFromServer");
        Message m = msg;
        // System.out.println("m = " + m);

        switch (m.getOperationtype()) {
            case getRequirementData:
                requestTreatmentController.Instance.setDataTable(m.getObject());
                break;
                
            case getViewRequestData:
            	ViewRequestController.Instance.setDataTable(m.getObject());
            	break;
            // case updateRequirement:
            // PrototypeController.Instance.alertMsg(m.getObject());
            // break;

            case LoginResult:
                LoginController.instance.getLoginResult(m.getObject());
                break;

            case InsertRequirement:
                ChangeRequestController.instance.queryResult(m.getObject());
                break;
                
            case InsertEvaluation:
    			EvaluationReportController.instance. queryResult(m.getObject());
    			break;
    			
            case ChangeRequest_File:
                ChangeRequestController.instance.uploadFileAndqueryResult(m.getObject());
                break;
            case Allocate_GetITUsers:
                AllocateController.instance.setComboBoxesData(m.getObject());
                break;
            case Allocate_SetRoles:
                AllocateController.instance.showResult(m.getObject());
                break;
            case User_getStageRoleObject:
                App.user.setStageRoleServerResponse(m.getObject());
                break;
            case User_getOrgRole:
                App.user.setOrgRoleServerResponse(m.getObject());
                break;
            case updateRequestStatus:
                requestTreatmentController.Instance.freezeServerResponse(m.getObject());
                break;
            case EVAL_GetAllReportsByRID:
                EvaluationReportController.instance.setFieldsData_ServerResponse(m.getObject());
                break;
            case DECISION_GetAllReportsByRID:
                decisionController.instance.setFieldsData_ServerResponse(m.getObject());
                break;
            default:
                break;

        }
    }
}// class