package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AspectCommander extends NodeCommander<Aspect> {
	private final Aspect aspect;
	public AspectCommander(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public CommanderChainer getBundledFiles() {
		fail("the model doesn't yet support bundling!");
		
		return commanderChainer;
	}

	//TODO: returns the bundle set. 
	public BundleInfoCommander getBundleInfo() throws Exception {
		
		return new BundleInfoCommander((aspect.getBundleSet()));
	}
}
