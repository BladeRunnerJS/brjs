package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;

public class AspectBundlingOfMixedResources extends SpecTest {
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
	}
	
	// TODO - bundling tests for js, css, html and xml combined
	
}
