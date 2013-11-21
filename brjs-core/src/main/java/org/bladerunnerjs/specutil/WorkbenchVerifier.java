package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class WorkbenchVerifier extends NodeVerifier<Workbench> {
	
	public WorkbenchVerifier(SpecTest modelTest, Workbench workbench) 
	{
		super(modelTest, workbench);
	}
}
