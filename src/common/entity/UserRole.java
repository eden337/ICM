package common.entity;

/**
 * the following getters and setters if this class are replacing the initial constructor as in
 * Lab 8 - usage of Singleton Design pattern.
 */

public class UserRole {

    private String roleID;
    private static UserRole userRoleInstance = new UserRole();
    private OrganizationRole roleName;

    /**
     * blank constructor
     */
    private UserRole() {
    }

    /**
     * @return the roleID
     */
    public String getRoleID() {
        return roleID;
    }

    /**
     * @param roleID the roleID to set
     */
    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    /**
     * @return the userRoleInstance
     */
    public static UserRole getUserRoleInstance() {
        return userRoleInstance;
    }

    /**
     * @return the roleName
     */
    public OrganizationRole getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(OrganizationRole roleName) {
        this.roleName = roleName;
    }


}
