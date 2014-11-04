package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchVerifier extends NodeVerifier<BladeWorkbench> {
	
	public WorkbenchVerifier(SpecTest modelTest, BladeWorkbench workbench) 
	{
		super(modelTest, workbench);
	}
}
