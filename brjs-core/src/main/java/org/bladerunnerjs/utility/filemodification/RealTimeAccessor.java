package org.bladerunnerjs.utility.filemodification;

import java.util.Date;

public class RealTimeAccessor implements TimeAccessor {
	@Override
	public long getTime() {
		return new Date().getTime();
	}
}
