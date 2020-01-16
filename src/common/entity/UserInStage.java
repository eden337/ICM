/**
 *
 */
package common.entity;

/**
 * @author eden_
 * @apiNote
 * User in stage object is responsible for the IT engineers users 
 * on a certain stage in treatment process, also this object contains an
 * enumerator for the current Stage Role for every UserInStage instance 
 */
public class UserInStage {
    private ITEngineer engineer;
    private Stage currentStage;
    private StageRole currentStageRole;

    /**
     * @param engineer
     * @param currentStage
     * @param currentStageRole
     */
    public UserInStage(ITEngineer engineer, Stage currentStage, StageRole currentStageRole) {
        this.engineer = engineer;
        this.currentStage = currentStage;
        this.currentStageRole = currentStageRole;
    }

    /**
     * @return the engineer
     */
    public ITEngineer getEngineer() {
        return engineer;
    }

    /**
     * @param engineer the engineer to set
     */
    public void setEngineer(ITEngineer engineer) {
        this.engineer = engineer;
    }

    /**
     * @return the currentStage
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * @param currentStage the currentStage to set
     */
    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    /**
     * @return the currentStageRole
     */
    public StageRole getCurrentStageRole() {
        return currentStageRole;
    }

    /**
     * @param currentStageRole the currentStageRole to set
     */
    public void setCurrentStageRole(StageRole currentStageRole) {
        this.currentStageRole = currentStageRole;
    }


}
