package common.entity;

/**
 * @author eden_ 
 * @apiNote
 * 		   IT engineer class is extending of the EmployeeUser class This
 *         user object has special permissions on the ICM system for maintaining
 *         requests
 */
@SuppressWarnings("serial")
public class ITEngineer extends EmployeeUser {

	private UserRole userRole;

	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param userName
	 * @param password
	 * @param userID
	 * @param department
	 * @param type
	 */
	public ITEngineer(String firstName, String lastName, String email, String userName, String password, String userID,
			String department, String type) {
		super(firstName, lastName, email, userName, password, userID, department, type,null);
		this.userRole = UserRole.getUserRoleInstance();
	}

	/**
	 * @return the userRole
	 */
	public UserRole getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

}
