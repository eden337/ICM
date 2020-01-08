package common.controllers;

public enum OperationType {
	Logout,
	InsertRequirement,
	getRequirementData,
	LoginAsStudent,
	LoginAsEmployee,
	InsertEvaluation,
	getViewRequestData,
	// Results:
//
	LoginResult, ChangeRequest_File,
	Allocate_GetITUsers,
	Allocate_SetRoles,
	User_SetStageRole,
	User_getStageRoleObject,
	User_getOrgRole,
	updateRequestStatus,
	EVAL_GetAllReportsByRID,
	DECISION_GetAllReportsByRID,
	VAL_GetAllReportsByRID,
	EVAL_GetInitData,
	EXE_GetInitData,
	PreEVAL_SetInitStat,
	PreEVAL_SetConfirmationStatus,
	PreEVAL_getData,
	PreEXE_SetConfirmationStatus,
	PreEXE_SetInitStat,
	PreEXE_getData,
	EXE_UpdateDB,
	VALID_UpdateDB,
	EVAL_UpdateDB,
	DECI_UpdateDB,
	ChangeRequest_DownloadFile,
	SUPERVISOR_REMARKS,
	Extension_getData,
	Extension_submit,
	ChangeRequest_getStageObject, getEmployeeData, updateRoleInOrg, updateSystems, deleteMember, PreValidation_SetRole, PreValidation_GetCOMMITEE_MEMBERS, getSystemData, VAL_GetInitData



	;

}
