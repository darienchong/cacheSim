package org.snajef.cacheSim.model;

public class CacheBlock {
	private int tag;
	private boolean isValid;
	
	public CacheBlock(int t, boolean iV) {
		tag = t;
		isValid = iV;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	@Override
	public String toString() {
		return "[Valid: " + (isValid() ? isValid() + " " : isValid()) 
			+ " | Tag = " + (isValid() ? String.format("0x%08x", getTag()) : "          ") 
			+ "]";
	}
}
