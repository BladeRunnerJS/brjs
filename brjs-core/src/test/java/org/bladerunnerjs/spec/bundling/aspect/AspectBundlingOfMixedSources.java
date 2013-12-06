package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;

public class AspectBundlingOfMixedSources extends SpecTest {
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
	}
	
	// TODO - bundling tests for bundling JS together from multiple levels 
	
}
