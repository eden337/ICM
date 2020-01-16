/**
 *
 */
package common.entity;

import client.App;
import client.controllers.mainController;
import common.controllers.Message;
import common.controllers.OperationType;
import javafx.application.Platform;

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
    public StudentUser(String firstName, String lastName, String email, String userName, String password, int userID, String faculty) {
        super(firstName, lastName, email, userName, password, "Student");
        this.faculty = faculty;
        this.userID = userID;
    }

    @Override
    public void updatePermissions() {
        OperationType ot2 = OperationType.User_getStudentAccess;
        App.client.handleMessageFromClientUI(new Message(ot2, this));
    }

    @Override
    public void setStudentPermission(Object object) {
        User student = (User) object;
        if (student == null)
            return;

        if (mainController.instance != null) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    mainController.instance.initialize_afterUserUpdate();
                }
            });

        }
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
