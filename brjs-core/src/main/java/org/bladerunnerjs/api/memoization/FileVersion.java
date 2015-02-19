package org.bladerunnerjs.api.memoization;


public class FileVersion
{
	private long value = Long.valueOf(0);
	
	long getValue() {
		return value;
	}
	
	void incrememntValue() {
		value++;
	}
	
}
