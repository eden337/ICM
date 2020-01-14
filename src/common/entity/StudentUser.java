/**
 * 
 */
package common.entity;

/**
 * @author Yuda Hatam
 *	This class represents the students  of our system 
 */
public class StudentUser extends User {

	private int userID;
	private String faculty;
	
	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param userName
	 * @param password
	 */
	public StudentUser(String firstName, String lastName, String email, String userName, String password,int userID, String faculty) {
		super(firstName, lastName, email, userName, password,"Student");
		this.faculty=faculty;
		this.userID=userID;
	}
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	/**
	 * @return the faculty
	 */
	public String getFaculty() {
		return faculty;
	}
	/**
	 * @param faculty the faculty to set
	 */
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
}
