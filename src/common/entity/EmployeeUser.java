/**
 *
 */
package common.entity;

import client.App;
import common.controllers.Message;
import common.controllers.OperationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yuda Hatam This class represents the employees of our system
 */
public class EmployeeUser extends User {

	private String workerID;
	private String department;
	private OrganizationRole roleInOrg;
	private Map<Integer, List<StageRole>> stagesRoles;
	private String systemID;

	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param userName
	 * @param password
	 */
	public EmployeeUser(String firstName, String lastName, String email, String userName, String password,
			String workerID, String department, String type, String roleInOrg, String systemID) {
		super(firstName, lastName, email, userName, password, type);
		this.department = department;
		this.workerID = workerID;
		// add setPermission
		stagesRoles = new HashMap<>();
		this.systemID = systemID;
		setOrgRoleServerResponse(roleInOrg);
		// updatePermissions();
	}

	@Override
	public boolean isOrganizationRole(OrganizationRole role) {
		return roleInOrg == role;
	}

	@Override
	public boolean isStageRole(int requestID, StageRole role) {
		return stagesRoles.get(requestID).contains(role);
	}

	private static int temp_requestID;
	private static StageRole temp_role;

	@Override
	public void setStageRole(int requestID, StageRole role) {
		if (App.client == null)
			return;
		if (stagesRoles.get(requestID).contains(role))
			return;

		// set temp:
		temp_requestID = requestID;
		temp_role = role;

		// update DB:
		OperationType ot = OperationType.User_SetStageRole;
		String query;
		query = "INSERT ";
		App.client.handleMessageFromClientUI(new Message(ot, query));

	}

	@Override
	public void setStageRoleServerResponse(Object object) {
		Map<Integer, List<StageRole>> res = (Map<Integer, List<StageRole>>) object;
		this.stagesRoles = res;
	}

	@Override
	public void setOrgRoleServerResponse(Object object) {
		String res = (String) object;
		if(res == null) {
			return;
		}
		switch (res) {
		case "":
			roleInOrg=null;
			break;
		case "COMMITEE_MEMBER1":
			roleInOrg = OrganizationRole.COMMITEE_MEMBER1;
			break;
		case "COMMITEE_MEMBER2":
			roleInOrg = OrganizationRole.COMMITEE_MEMBER2;
			break;
		case "COMMITEE_CHAIRMAN":
			roleInOrg = OrganizationRole.COMMITEE_CHAIRMAN;
			break;
		case "SUPERVISOR":
			roleInOrg = OrganizationRole.SUPERVISOR;
			break;
		case "DIRECTOR":
			roleInOrg = OrganizationRole.DIRECTOR;
			break;
		}
	}

	@Override
	public void updatePermissions() {
		OperationType ot = OperationType.User_getStageRoleObject;
		App.client.handleMessageFromClientUI(new Message(ot, this));
		OperationType ot2 = OperationType.User_getOrgRole;
		App.client.handleMessageFromClientUI(new Message(ot2, this));

	}

	/**
	 * @return the userID
	 */
	public String getWorkerID() {
		return workerID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.workerID = userID;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @return the SystemID
	 */
	public String getSystemID() {
		if(systemID==null)
			return "";
		return systemID;
	}

	/**
	 * @param the SystemID
	 */

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	/**
     * @return the RoleInOrg
     */

	public String  getRoleInOrg() {
			if(roleInOrg==null)
				return "";
	        switch (roleInOrg) {
	            case COMMITEE_MEMBER1:
	                return "COMMITEE_MEMBER1";
	            case COMMITEE_MEMBER2:
	                return "COMMITEE_MEMBER2";
	                
	            case COMMITEE_CHAIRMAN:
	                return "COMMITEE_CHAIRMAN";
	                
	            case SUPERVISOR:
	                return "SUPERVISOR";
	                
	            case DIRECTOR:
	                return "DIRECTOR";
	                
	              default:
	            	  return null;
	       }
	}
}
