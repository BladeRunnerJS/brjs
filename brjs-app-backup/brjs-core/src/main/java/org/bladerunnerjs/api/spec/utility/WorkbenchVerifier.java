package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.spec.engine.SpecTest;

public class WorkbenchVerifier extends BundlableNodeVerifier<Workbench<?>> {
	
	public WorkbenchVerifier(SpecTest modelTest, Workbench<?> workbench) 
	{
		super(modelTest, workbench);
	}
}
