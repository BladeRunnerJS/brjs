package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchVerifier extends NodeVerifier<Workbench> {
	
	public WorkbenchVerifier(SpecTest modelTest, Workbench workbench) 
	{
		super(modelTest, workbench);
	}
}
