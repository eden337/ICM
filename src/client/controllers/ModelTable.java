package client.controllers;

public class ModelTable {
	private String id;
	private String name, position, email;
	private String wantedChange, existChange;
	private String treatmentPhase, status, Reason;
	private String currResponsible, systemID;
	private String comments,date, dueDate, fileName;
	/**
	 * @param id
	 * @param name
	 * @param position
	 * @param email
	 * @param wantedChange
	 * @param existChange
	 * @param treatmentPhase
	 * @param status
	 * @param reason
	 * @param currResponsible
	 * @param systemID
	 * @param comments
	 * @param date
	 * @param dueDate
	 * @param fileName
	 */
	public ModelTable(Object id, Object name, Object position, Object email, Object wantedChange, Object existChange,
			Object treatmentPhase, Object status, Object reason, Object currResponsible,Object  systemID,
			Object comments, Object date, Object dueDate, Object fileName) {
		super();
		this.id = ((Integer)id).toString();
		this.name = name.toString();
		this.position = position.toString();
		this.email = email.toString();
		this.wantedChange = wantedChange.toString();
		this.existChange = existChange.toString();
		this.treatmentPhase = treatmentPhase.toString();
		this.status = status.toString();
		Reason = reason.toString();
		this.currResponsible = currResponsible.toString();
		this.systemID = systemID.toString();
		this.comments = comments.toString();
		this.date = date.toString();
		this.dueDate = dueDate.toString();
		this.fileName = fileName.toString();
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the wantedChange
	 */
	public String getWantedChange() {
		return wantedChange;
	}
	/**
	 * @param wantedChange the wantedChange to set
	 */
	public void setWantedChange(String wantedChange) {
		this.wantedChange = wantedChange;
	}
	/**
	 * @return the existChange
	 */
	public String getExistChange() {
		return existChange;
	}
	/**
	 * @param existChange the existChange to set
	 */
	public void setExistChange(String existChange) {
		this.existChange = existChange;
	}
	/**
	 * @return the treatmentPhase
	 */
	public String getTreatmentPhase() {
		return treatmentPhase;
	}
	/**
	 * @param treatmentPhase the treatmentPhase to set
	 */
	public void setTreatmentPhase(String treatmentPhase) {
		this.treatmentPhase = treatmentPhase;
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
	 * @return the reason
	 */
	public String getReason() {
		return Reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		Reason = reason;
	}
	/**
	 * @return the currResponsible
	 */
	public String getCurrResponsible() {
		return currResponsible;
	}
	/**
	 * @param currResponsible the currResponsible to set
	 */
	public void setCurrResponsible(String currResponsible) {
		this.currResponsible = currResponsible;
	}
	/**
	 * @return the systemID
	 */
	public String getSystemID() {
		return systemID;
	}
	/**
	 * @param systemID the systemID to set
	 */
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}
	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	


}
