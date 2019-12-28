/**
 * 
 */
package common.entity;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Yuda Hatam
 *
 */
public class ChangeRequest {

	/**
	 * @apiNote
	 * initiator: user creating request 
	 * request details by user: status,existingCondition,suggestedChange,reasonForChange
	 * request optional details by user: attachedFiles,remarks
	 * request system created details: requestID,submitTime
	 * stages default details: allStages
	 * stages details given by user:incharge
	 * stages details add by system: repeated (a stage that was repeated so it's results were discarded)
	 * Auxiliary stage variable: indexOfCurrentStage   
	 */
	private User initiator;
	private String status;
	private String requestID;
	private String infoSystem;
	private String existingCondition;
	private String suggestedChange;
	private String reasonForChange;
	private String remarks;
	private String dueDate;
	private String requestName,currInCharge;
	private ZonedDateTime submitTime;
	private Stage[] allStages = new Stage[5];
	private boolean[] isExtended = new boolean[5];
	private ArrayList<Stage> repeated;
	private int indexOfCurrentStage;
	private ArrayList<AttachedFile> attachedFiles;
	private ITEngineer[] incharge = new ITEngineer[5];

	/**
	 *
	 * @author: Ira Goor
	 * @apiNote
	 * Constructors purpose:new Change Request (after submitted)
	 *
	 * @param requestID
	 * @param infoSysytem
	 * @param existingCondition
	 * @param suggestedChange
	 * @param reasonForChange
	 * @param remarks
	 */
	public ChangeRequest(User initiator,String requestID, String infoSysytem, String existingCondition, String suggestedChange,
			String reasonForChange, String remarks,ArrayList<AttachedFile> attachedFiles,String status) {
		this.initiator=initiator;
		this.requestID = requestID;
		this.infoSystem = infoSysytem;
		this.existingCondition = existingCondition;
		this.suggestedChange = suggestedChange;
		this.reasonForChange = reasonForChange;
		this.remarks = remarks;
		this.submitTime=ZonedDateTime.now();
		this.attachedFiles=attachedFiles;
		this.status= status;

	}

	/**
	 * @apiNote
	 * constructor purpose:
	 * save info about specific request on data table from requests viewing
	 *
	 *
	 * @param requestID
	 * @param requestName
	 * @param departarmentID
	 * @param existingCondition
	 * @param descripitionsTextArea
	 * @param dueDateLabel
	 * @param currInCharge
	 */
	public ChangeRequest(String requestID, String requestName, String departarmentID, String existingCondition,
			String descripitionsTextArea, String dueDateLabel, String currInCharge,String status) {
		this.requestName = requestName;
		this.requestID = requestID;
		this.infoSystem = departarmentID;
		this.existingCondition = existingCondition;
		this.remarks = descripitionsTextArea;
		this.dueDate=dueDateLabel;
		this.currInCharge=currInCharge;
		this.status= status;

	}

	/**
	 * @author: Ira Goor
	 * @apiNote
	 * Constructors purpose: after incharge assignment (gets an incharge ITEngineer list)
	 *
	 * @param initiator
	 * @param status
	 * @param requestID
	 * @param infoSystem
	 * @param existingCondition
	 * @param suggestedChange
	 * @param reasonForChange
	 * @param remarks
	 * @param submitTime
	 * @param attachedFiles
	 * @param incharge
	 * @param evaluator
	 */
	public ChangeRequest(User initiator, String status, String requestID, String infoSystem,
			String existingCondition, String suggestedChange, String reasonForChange, String remarks,
			ZonedDateTime submitTime,ArrayList<AttachedFile> attachedFiles, ITEngineer[] incharge
			,ITEngineer evaluator) {
		this.initiator = initiator;
		this.status = status;
		this.requestID = requestID;
		this.infoSystem = infoSystem;
		this.existingCondition = existingCondition;
		this.suggestedChange = suggestedChange;
		this.reasonForChange = reasonForChange;
		this.remarks = remarks;
		this.submitTime = submitTime;
		this.attachedFiles = attachedFiles;
		this.incharge = incharge;
		this.repeated= new ArrayList<Stage>();
		initiliazeAllStages(evaluator);
	}



	/**
	 *
	 * @author: Ira Goor
	 * @apiNote
	 * Constructors purpose: existing change Request in DB
	 *
	 * @param initiator
	 * @param status
	 * @param requestID
	 * @param infoSystem
	 * @param existingCondition
	 * @param suggestedChange
	 * @param reasonForChange
	 * @param remarks
	 * @param submitTime
	 * @param allStages
	 * @param isExtended
	 * @param repeated
	 * @param indexOfCurrentStage
	 * @param attachedFiles
	 * @param incharge
	 */

	
	public ChangeRequest(User initiator, String status, String requestID, String infoSystem,
			String existingCondition, String suggestedChange, String reasonForChange, String remarks,
			ZonedDateTime submitTime, Stage[] allStages, boolean[] isExtended, ArrayList<Stage> repeated,
			int indexOfCurrentStage, ArrayList<AttachedFile> attachedFiles, ITEngineer[] incharge) {
		this.initiator = initiator;
		this.status = status;
		this.requestID = requestID;
		this.infoSystem = infoSystem;
		this.existingCondition = existingCondition;
		this.suggestedChange = suggestedChange;
		this.reasonForChange = reasonForChange;
		this.remarks = remarks;
		this.submitTime = submitTime;
		this.allStages = allStages;
		this.isExtended = isExtended;
		this.repeated = repeated;
		this.indexOfCurrentStage = indexOfCurrentStage;
		this.attachedFiles = attachedFiles;
		this.incharge = incharge;
	}



	/**
	 * 
	 * @author Ira Goor
	 * @apiNote 
	 * method purpose: after incharge ITEnigineers have been submitted initialize stages 
	 *
	 */


	protected void initiliazeAllStages(ITEngineer evaluator)
	{
		this.indexOfCurrentStage=0;
		this.allStages[0]=new Evaluation(this, StageName.EVALUATION,evaluator , true);
		for(int i=0;i<5;i++)
			isExtended[i]=true;
	}
	
	/**
	 * 
	 * @author Ira Goor
	 * @apiNote 
	 * method purpose: close current stage and set next stage
	 *
	 *
	 */
	
	private void closeCurrentStage() {
		if(allStages[indexOfCurrentStage].closeStage())
		{
			//maybe unneeded: add allStages[this.indexOfCurrentStage] to delayed table
			
		}
		this.indexOfCurrentStage=allStages[indexOfCurrentStage].result();
		
		
	}
	
	/**
	 * 
	 * @author Ira Goor
	 * @apiNote 
	 * method purpose: get new Stage 
	 *
	 * @return
	 */
	
	protected Stage newStage(ITEngineer execurtioner,long days)
	{
		Stage stage=null;
		switch(indexOfCurrentStage)
		{
		case 0: 
			stage= new Evaluation(this,StageName.EVALUATION,execurtioner,isExtended[indexOfCurrentStage]);
			break;
		case 1:
			//placeHolder:
			//stage = new Decision();
			break;
		case 2:
			//placeHolder:
			//stage= new Execution();
			break;
		case 3:
			//placeHolder:
			//stage= new Validation();
			break;
		case 4:
			//placeHolder:
			//stage= new Closure();
			break;
			
		}
		return stage;
	}
	
	
	
	
	
	
	

	



	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the infoSysytem
	 * 
	 */
	public String getInfoSysytem() {
		return infoSystem;
	}

	/**
	 * @return the existingCondition
	 */
	public String getExistingCondition() {
		return existingCondition;
	}

	/**
	 * @return the suggestedChange
	 */
	public String getSuggestedChange() {
		return suggestedChange;
	}

	/**
	 * @return the reasonForChange
	 */
	public String getReasonForChange() {
		return reasonForChange;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @return the currentStage
	 */
	public Stage getCurrentStage() {
		return allStages[indexOfCurrentStage];
	}

	/**
	 * 
	 * @author Ira Goor
	 *  
	 *
	 * @return
	 */
	public User getInitiator() {
		return this.initiator;
	}



	/**
	 * @return the infoSystem
	 */
	public String getInfoSystem() {
		return infoSystem;
	}

	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @return the requestName
	 */
	public String getRequestName() {
		return requestName;
	}

	/**
	 * @return the currInCharge
	 */
	public String getCurrInCharge() {
		return currInCharge;
	}

	/**
	 * @return the submitTime
	 */
	public ZonedDateTime getSubmitTime() {
		return submitTime;
	}

	/**
	 * @return the allStages
	 */
	public Stage[] getAllStages() {
		return allStages;
	}

	/**
	 * @return the isExtended
	 */
	public boolean[] getIsExtended() {
		return isExtended;
	}

	/**
	 * @return the repeated
	 */
	public ArrayList<Stage> getRepeated() {
		return repeated;
	}

	/**
	 * @return the indexOfCurrentStage
	 */
	public int getIndexOfCurrentStage() {
		return indexOfCurrentStage;
	}

	/**
	 * @return the attachedFiles
	 */
	public ArrayList<AttachedFile> getAttachedFiles() {
		return attachedFiles;
	}

	/**
	 * @return the incharge
	 */
	public ITEngineer[] getIncharge() {
		return incharge;
	}

}
