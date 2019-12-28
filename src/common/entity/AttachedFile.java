/**
 * 
 */
package common.entity;

import java.util.Date;

/**
 * @author Yuda Hatam
 * not use for know we have MyFile
 * 
 */
public class AttachedFile {
	
	private String name;
	private String type;
	private long size;
	private Date creationDate;

	/**
	 * @param type
	 * @param size
	 * @param creationDate
	 * @param name
	 */
	public AttachedFile(String type, long size, Date creationDate, String name) {
		this.type = type;
		this.size = size;
		this.creationDate = creationDate;
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "AttachedFile [name=" + name + ", type=" + type + ", size=" + size + ", creationDate=" + creationDate
				+ "]";
	}

	
	
}
