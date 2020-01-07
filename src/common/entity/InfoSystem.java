package common.entity;

import java.io.Serializable;

/**
 * infoSystem is all the information systems of the college.
 * @author Hen_Yehuda
 *
 */
public class InfoSystem implements Serializable{
//
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String SystemID;
	String UserName;
	


	/**
	 * @param systemID
	 * @param userName
	 */
	public InfoSystem(String systemID, String userName) {		
		SystemID = systemID;
		UserName = userName;
	}

	/**
	 * @return the systemID
	 */
	public String getSystemID() {
		return SystemID;
	}

	/**
	 * @param systemID the systemID to set
	 */
	public void setSystemID(String systemID) {
		SystemID = systemID;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return UserName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}

}
