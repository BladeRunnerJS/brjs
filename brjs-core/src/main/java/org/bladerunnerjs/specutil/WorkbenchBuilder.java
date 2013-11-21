package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class WorkbenchBuilder extends AssetContainerBuilder<Workbench> {
	public Workbench workbench;
	
	public WorkbenchBuilder(SpecTest modelTest, Workbench workbench) {
		super(modelTest,  workbench);
		this.workbench = workbench;
	}
}
