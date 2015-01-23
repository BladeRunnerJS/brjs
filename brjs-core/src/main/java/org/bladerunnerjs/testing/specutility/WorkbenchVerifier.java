package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchVerifier extends BundlableNodeVerifier<Workbench<?>> {
	
	public WorkbenchVerifier(SpecTest modelTest, Workbench<?> workbench) 
	{
		super(modelTest, workbench);
	}
}
