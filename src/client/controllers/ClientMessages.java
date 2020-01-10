package client.controllers;

import client.App;
import common.controllers.Message;
//

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
            case getEmployeeData:
                ManagerViewPage.Instance.setDataTable(m.getObject());
                break;
            case getSystemData:
                ManagerViewPage.Instance.setSystemData(m.getObject());
                break;
            case InsertRequirement:
                ChangeRequestController.instance.queryResult(m.getObject());
                break;
            case InsertEvaluation:
                EvaluationReportController.instance.insertNewRequestResult(m.getObject());
                break;

            case ChangeRequest_File:
                ChangeRequestController.instance.uploadFileResult(m.getObject());
                break;
            case Allocate_GetITUsers:
                AllocateController.instance.setComboBoxesData(m.getObject());
                break;
            case Allocate_UpdateRoles:
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
            case SUPERVISOR_REMARKS:
                requestTreatmentController.Instance.freezeServerResponse(m.getObject());
                break;
            case updateRoleInOrg:
            case deleteMember:

                break;
            case EVAL_GetAllReportsByRID:
                EvaluationReportController.instance.setFieldsData_ServerResponse(m.getObject());
                break;
            case DECISION_GetAllReportsByRID:
                decisionController.instance.setFieldsData_ServerResponse(m.getObject());
                break;
            
            case VALID_updateRequestStatus:
            	ValidationController.instance.queryResult2(m.getObject());
            	break;
                
            case VAL_GetAllReportsByRID:
                ValidationController.instance.queryResult(m.getObject());
                break;

            case EXE_UpdateDB:
                ExecutionController.instance.queryResult(m.getObject());
                break;

            // pre Eval
            case PreEVAL_SetInitStat:
            case PreEVAL_SetConfirmationStatus:
                PreEvaluationController.instance.updateStatus_serverResponse(m.getObject());
                break;
            case PreEVAL_getData:
                PreEvaluationController.instance.getCurrentReqestedDays_ServerResponse(m.getObject());
                break;
            case EXE_GetInitData:
                ExecutionController.instance.checkPreConditions_ServerResponse(m.getObject());
                break;
            case VAL_GetInitData:
                ValidationController.instance.checkPreConditions_ServerResponse(m.getObject());
                break;
            // pre EXE
            case PreEXE_SetInitStat:
            case PreEXE_SetConfirmationStatus:
                PreExecutionController.instance.updateStatus_serverResponse(m.getObject());
                break;
            case PreEXE_getData:
                PreExecutionController.instance.getCurrentReqestedDays_ServerResponse(m.getObject());
                break;
            case VALID_UpdateDB:
                ValidationController.instance.queryResult(m.getObject());
                break;
            case EVAL_UpdateDB:
                EvaluationReportController.instance.queryResult(m.getObject());
                break;
            case ChangeRequest_DownloadFile:
                requestTreatmentController.Instance.DownloadFiles_ServerResponse(m.getObject());
                break;

            case Extension_submit:
                ExtensionController.instance.InsertOrUpdate_ServerResponse(m.getObject());
                break;
            case ChangeRequest_getStageObject:
                requestTreatmentController.Instance.appendStageObject_ServerResponse(m.getObject());
                break;
            case updateSystems:
                ManagerViewPage.Instance.getquery(m.getObject());
                break;
            case PreValidation_GetCOMMITEE_MEMBERS:
                PreValidationController.instance.setComboBoxesData(m.getObject());
                break;
            case PreValidation_SetRole:
                PreValidationController.instance.queryResult(m.getObject());
            case DECISION_GetPrevStage:
                decisionController.instance.appendPrevStageObject_ServerResponse(m.getObject());
                break;
            case DECISION_updateRequestStatus:
                decisionController.instance.queryResult2(m.getObject());
                break;
            case VALID_CheckReport:
            	ValidationController.instance.getReport_ServerResponse(m.getObject());
            	break;
            case VALID_GetPrevStage:
            	ValidationController.instance.appendPrevStageObject_ServerResponse(m.getObject());
            	break;
            case EXECUTION_GetFailReport:
            	ExecutionController.instance.getReport_ServerResponse(m.getObject());
            	break;
            default:
                break;
        }
    }
}// class