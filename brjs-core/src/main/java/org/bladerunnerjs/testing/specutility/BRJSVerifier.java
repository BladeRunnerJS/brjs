package org.bladerunnerjs.testing.specutility;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Assert;


public class BRJSVerifier extends NodeVerifier<BRJS> {
	private BRJS brjs;

	public BRJSVerifier(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}

	public void hasApps(String... apps)
	{
		List<String> existingAppNames = new ArrayList<>();
		for (App app : brjs.apps()) {
			existingAppNames.add( app.getName() );
		}
		Assert.assertEquals( StringUtils.join(apps, ", "), StringUtils.join(brjs.apps(), ", "));
	}
}
