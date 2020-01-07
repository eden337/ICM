package common.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

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
public class Stage implements Serializable {

	private int requestID;
	private String stageName;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;
	private ZonedDateTime deadline;
	private String incharge;
	private boolean extend;

	@Override
	public String toString() {
		return "Stage{" +
				"requestID=" + requestID +
				", stageName='" + stageName + '\'' +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", deadline=" + deadline +
				", incharge='" + incharge + '\'' +
				", extend=" + extend +
				", init=" + init +
				", init_confirmed=" + init_confirmed +
				'}';
	}

	private int init;

	public int getRequestID() {
		return requestID;
	}

	public String getStageName() {
		return stageName;
	}

	public ZonedDateTime getStartTime() {
		return startTime;
	}

	public ZonedDateTime getEndTime() {
		return endTime;
	}

	public ZonedDateTime getDeadline() {
		return deadline;
	}

	public String getIncharge() {
		return incharge;
	}

	public boolean isExtend() {
		return extend;
	}

	public int getInit() {
		return init;
	}

	public int getInit_confirmed() {
		return init_confirmed;
	}

	public Stage(int requestID, String stageName, ZonedDateTime startTime, ZonedDateTime endTime, ZonedDateTime deadline, String incharge, boolean extend, int init, int init_confirmed) {
		this.requestID = requestID;
		this.stageName = stageName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.deadline = deadline;
		this.incharge = incharge;
		this.extend = extend;
		this.init = init;
		this.init_confirmed = init_confirmed;
	}

	private int init_confirmed;

//	/**
//	 *
//	 * @author Ira Goor
//	 * @apiNote
//	 * method purpose:set final time info per Stage
//	 * a.stage delayed
//	 * b.stage not delayed
//	 * @return true (a),false (b)
//	 */
//
//	protected boolean  closeStage()
//	{
//		this.endTime=ZonedDateTime.now();
//		return isDelayed();
//	}
//
//
//
//
//	/**
//	 *
//	 * @author Ira Goor
//	 * @apiNote
//	 * method purpose: does stage can be extended? check first if
//	 *         stage wasn't extended already (can only extend once per Stage) then
//	 *         check if there's 3 or less days until deadline if so return true else
//	 *         return false
//	 *
//	 *
//	 * @return if Stage can be Extended
//	 */
//	private boolean canExtend() {
//		if (canBeExtend)
//			return Duration.between(ZonedDateTime.now(), deadline).toDays() > 3 ? false : true;
//		return false;
//	}
//
//	/**
//	 *
//	 * @author Ira Goor
//	 * @apiNote
//	 * method purpose: check if stage is delayed and update amount of days delayed
//	 *
//	 *
//	 * @return
//	 */
//
//	private boolean isDelayed() {
//		setDayDelayed();
//		return this.daysDelayed == 0 ? false : true;
//
//	}
//	/**
//	 *
//	 * @author Ira Goor
//	 * @apiNote
//	 * method purpose: update amount of days delayed
//	 *
//	 */
//	private void setDayDelayed() {
//		long temp = Duration.between(deadline, ZonedDateTime.now()).toDays();
//		this.daysDelayed = temp > 0 ? temp : 0;
//	}




}
