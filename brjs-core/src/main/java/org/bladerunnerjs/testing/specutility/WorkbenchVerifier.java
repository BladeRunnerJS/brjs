package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchVerifier extends BundlableNodeVerifier<BladeWorkbench> {
	
	public WorkbenchVerifier(SpecTest modelTest, BladeWorkbench workbench) 
	{
		super(modelTest, workbench);
	}
}
