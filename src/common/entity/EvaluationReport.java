/**
 * 
 */
package common.entity;

/**
 * @author yudah
 *	This class represents the reports generated from evaluation stage 
 */
public class EvaluationReport {
	private String reportID;
	private String systemID;
	private String requiredChange;
	private String expectedResult;
	private String expectedRisks;
	private String estimatedTime;
	
	/**
	 * @param systemID
	 * @param requiredChange
	 * @param expectedResult
	 * @param expectedRisks
	 * @param estimatedTime
	 */
	public EvaluationReport(String systemID, String requiredChange, String expectedResult, String expectedRisks,
			String estimatedTime) {
		this.systemID = systemID;
		this.requiredChange = requiredChange;
		this.expectedResult = expectedResult;
		this.expectedRisks = expectedRisks;
		this.estimatedTime = estimatedTime;
	}

	/**
	 * @return the reportID
	 */
	public String getReportID() {
		return reportID;
	}

	/**
	 * @param reportID the reportID to set
	 */
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	/**
	 * @return the systemID
	 */
	public String getSystemID() {
		return systemID;
	}

	/**
	 * @return the requiredChange
	 */
	public String getRequiredChange() {
		return requiredChange;
	}

	/**
	 * @return the expectedResult
	 */
	public String getExpectedResult() {
		return expectedResult;
	}

	/**
	 * @return the expectedRisks
	 */
	public String getExpectedRisks() {
		return expectedRisks;
	}

	/**
	 * @return the estimatedTime
	 */
	public String getEstimatedTime() {
		return estimatedTime;
	}
	

}
