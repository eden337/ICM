package common.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;


public class Report implements Serializable{

	private String type;
	private Date created;
	private Date from;
	
	
	private Date to;
	private String data;
	private ArrayList<String> period;
	

	/**
	 *@author: Ira Goor
	 *
	 *Constructors purpose: non period Report
	 *
	 * @param type
	 * @param created
	 */
	public Report(String type, Date created) {
		this.type = type;
		this.created = created;
		this.period=new ArrayList<String>();
		this.period.add("Activity");
	}

	/**
	 *@author: Ira Goor
	 *
	 *Constructors purpose: for a period report
	 *
	 * @param type
	 * @param from
	 * @param to
	 */
	public Report(String type, Date created,Date from, Date to) {
		this.type = type;
		this.created=created;
		this.from = from;
		this.to = to;
		this.period=new ArrayList<String>();
		period.add("Activity");

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	
	public ArrayList<String> getPeriod() {
		return period;
	}

	public void setPeriod(ArrayList<String> period) {
		this.period = period;
	}

	public boolean isPeriodReport()
	{
		return period.contains(type);
	}
	public String flipDateformat(String date)
	{
		StringBuilder s=new StringBuilder();
		s.append(date.substring(8));
		
		s.append('-');
		s.append(date.substring(5, 7));
		s.append('-');
		s.append(date.substring(0, 4));
		return s.toString();
	}

	
	@Override
	public String toString() {
		if(isPeriodReport())
			return type + " created " + flipDateformat(created.toString()) + " from " + flipDateformat(from.toString()) + " to " + flipDateformat(to.toString());
		else
			return  type + " created " + flipDateformat(created.toString()) ;
	}
	
	
}
