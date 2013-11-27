package org.bladerunnerjs.specutil;

import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;


public class AppVerifier extends NodeVerifier<App> {
	
	App app;
	
	public AppVerifier(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
	}

	public void hasLibs(JsLib... libs)
	{
		List<JsLib> appLibs = app.jsLibs();
		Assert.assertThat(appLibs, IsIterableContainingInOrder.contains(libs));
	}
}
