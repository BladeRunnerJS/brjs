package org.bladerunnerjs.utility.filemodification;

import java.util.Date;

public class PessimisticFileModificationInfo implements FileModificationInfo {
	@Override
	public long getLastModified() {
		return (new Date()).getTime();
	}
}
