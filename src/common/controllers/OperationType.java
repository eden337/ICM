package common.controllers;

public enum OperationType {
	Logout,
	InsertRequirement,
	getRequirementData,
	LoginAsStudent,
	LoginAsEmployee,
	InsertEvaluation,
	getViewRequestData,
	InsertStartStage,
	UpdateStage,
	// Results:

	LoginResult, ChangeRequest_File,
	Allocate_GetITUsers,
	Allocate_SetRoles,
	User_SetStageRole,
	User_getStageRoleObject,
	User_getOrgRole,
	updateRequestStatus,
	EVAL_GetAllReportsByRID,
	DECISION_GetAllReportsByRID, 

	;

}
