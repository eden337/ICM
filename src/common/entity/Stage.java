package common.entity;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * 
 * @author Ira Goor
 *
 *  
 *         class purpose: General structure of a stage in request treatment
 *         process classes extends from it: Evaluation, Decision,
 *         Execution,Validation and Closure. Auxiliary classes, enums, and
 *         interfaces: StageName
 *
 */
public abstract class Stage {

	/**
	 * 
	 * 
	 * 
	 */
	private ChangeRequest request;
	private StageName stageName;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private ZonedDateTime deadline;
	private long daysDelayed;
	private ITEngineer executionEngineer;
	private boolean canBeExtend;

	private boolean delayed;
	/**
	 * @author Ira Goor
	 * 
	 * @apiNote
	 * Constructor purpose: initialize new stage (not evaluation) 
	 * used in: creating a new stage, moving forward (nextStage()) or backwards (lastStage())
	 * 
	 * @param request
	 * @param stageName
	 * @param executionEngineer
	 * @param days
	 * @param canBeExtend
	 */
	public Stage(ChangeRequest request, StageName stageName, ITEngineer executionEngineer, long days,
			boolean canBeExtend) {
		this.request = request;
		this.stageName = stageName;
		this.canBeExtend = canBeExtend;
		this.executionEngineer = executionEngineer;
		daysDelayed = 0;
		delayed=false;
		startTime = ZonedDateTime.now();
		deadline = startTime.plusDays(days);
	}
	
	
	/**
	 * @author Ira Goor
	 * 
	 * @apiNote
	 * Constructor purpose: initialize new  evaluation stage
	 * used in: creating a new evaluation stage
	 * 
	 * @param request
	 * @param stageName
	 * @param executionEngineer
	 * @param canBeExtend
	 */

	public Stage(ChangeRequest request, StageName stageName, ITEngineer executionEngineer,boolean canBeExtend) {
		this.request = request;
		this.stageName = stageName;
		this.executionEngineer = executionEngineer;
		this.canBeExtend=canBeExtend;
	}
	
	/**
	 * 
	 *@author: Ira Goor
	 *
	 * @apiNote
	 * Constructors purpose: Generate existing Stage
	 *
	 * @param request
	 * @param stageName
	 * @param startTime
	 * @param deadline
	 * @param executionEngineer
	 * @param canBeExtend
	 * @param delay
	 */




	public Stage(ChangeRequest request, StageName stageName, ZonedDateTime startTime,
			ZonedDateTime deadline, ITEngineer executionEngineer, boolean canBeExtend,
			boolean delay) {
		this.request = request;
		this.stageName = stageName;
		this.startTime = startTime;
		this.deadline = deadline;
		this.executionEngineer = executionEngineer;
		this.canBeExtend = canBeExtend;
		this.delayed =checkDelay(delay);
		
			
	}
	
	/**
	 * 
	 * @author Ira Goor
	 * @apiNote 
	 * method purpose:set next step in request treatment
	 *
	 * @return
	 */
	public abstract int result();
	/**
	 * 
	 * @author Ira Goor 
	 * @apiNote
	 * method purpose: check if stage was already delayed
	 * description:
	 * checks if stage was already delayed last time it was called upon
	 * 1.it's wasn't late
	 * 1.1 check if it's late now and update days delayed
	 * 1.1.1 (now it's late) send messages to concern parties
	 * 1.2 (now it's not late) do nothing (will return false)
	 * 2. it's was late  
	 * 2.1 update days delayed
	 *  
	 *  
	 *
	 * @param delay
	 * @return
	 */


	private boolean checkDelay(boolean delay) {
		if(!delay)
		{
			if(delay=isDelayed())
			{
				/*
				 * placeholder: here we need to send a notification to director, inCharge,and supervisor
				 * 
				 */
			}
		}
		else
			setDayDelayed();
		return delay;
	}
	
	/**
	 * 
	 * @author Ira Goor 
	 * @apiNote
	 * method purpose:set final time info per Stage 
	 * a.stage delayed
	 * b.stage not delayed
	 * @return true (a),false (b)
	 */
	
	protected boolean  closeStage()
	{
		this.endTime=ZonedDateTime.now();
		return isDelayed();
	}


	/**
	 * 
	 * @author Ira Goor
	 *
	 *
	 * @return Stage with calculated time delayed, time
	 */
	/*public Stage nextStage() {

		if(isDelayed())
			
		return this;

	}*/

	/**
	 * 
	 * @author Ira Goor
	 * @apiNote
	 * method purpose: does stage can be extended? check first if
	 *         stage wasn't extended already (can only extend once per Stage) then
	 *         check if there's 3 or less days until deadline if so return true else
	 *         return false
	 * 
	 *
	 * @return if Stage can be Extended
	 */
	private boolean canExtend() {
		if (canBeExtend)
			return Duration.between(ZonedDateTime.now(), deadline).toDays() > 3 ? false : true;
		return false;
	}

	/**
	 * 
	 * @author Ira Goor
	 * @apiNote
	 * method purpose: check if stage is delayed and update amount of days delayed
	 * 
	 *
	 * @return
	 */

	private boolean isDelayed() {
		setDayDelayed();
		return this.daysDelayed == 0 ? false : true;

	}
	/**
	 * 
	 * @author Ira Goor
	 * @apiNote 
	 * method purpose: update amount of days delayed
	 *
	 */
	private void setDayDelayed() {
		long temp = Duration.between(deadline, ZonedDateTime.now()).toDays();
		this.daysDelayed = temp > 0 ? temp : 0;
	}
	
	
	
	
	

}
