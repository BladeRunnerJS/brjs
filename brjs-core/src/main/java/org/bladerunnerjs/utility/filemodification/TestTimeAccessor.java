package org.bladerunnerjs.utility.filemodification;

public class TestTimeAccessor implements TimeAccessor {
	long lastModified = 0;
	
	@Override
	public long getTime() {
		return lastModified++;
	}
}
