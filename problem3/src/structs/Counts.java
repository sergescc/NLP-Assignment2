package structs;

import java.io.Serializable;

public class Counts implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3861187918904384189L;
	private long uniqueCounts;
	private long totalCounts;
	
	
	public long getUniqueCounts() {
		return uniqueCounts;
	}
	public void setUniqueCounts(long uniqueCounts) {
		this.uniqueCounts = uniqueCounts;
	}
	public long getTotalCounts() {
		return totalCounts;
	}
	public void setTotalCounts(long totalCounts) {
		this.totalCounts = totalCounts;
	}

}
