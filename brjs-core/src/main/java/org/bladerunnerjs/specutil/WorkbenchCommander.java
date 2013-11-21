package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class WorkbenchCommander extends NodeCommander<Workbench> {
	@SuppressWarnings("unused")
	private final Workbench workbench;
	
	public WorkbenchCommander(SpecTest modelTest, Workbench workbench) {
		super(modelTest, workbench);
		this.workbench = workbench;
	}
}
