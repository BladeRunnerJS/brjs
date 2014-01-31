package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingCachingTest extends SpecTest 
{
	private App app;
	private Aspect aspect;

	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void weDoNotCacheIndexPageReferencesToAspectSource() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).hasClass("appns.Class2")
			.and(aspect).indexPageRefersTo("appns.Class1")
			.and(app).hasReceivedRequst("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(aspect).indexPageRefersTo("appns.Class2")
			.and(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsClasses("appns.Class2")
			.and(response).doesNotContainText("appns.Class1");
	}
	
}
