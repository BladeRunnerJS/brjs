package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

import static org.junit.Assert.*;


public class BRJSVerifier extends NodeVerifier<BRJS> {
	private BRJS brjs;

	public BRJSVerifier(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}

	public VerifierChainer hasApps(String... apps)
	{
		List<String> existingAppNames = new ArrayList<>();
		for (App app : brjs.apps()) {
			existingAppNames.add( app.getName() );
		}
		assertEquals( StringUtils.join(apps, ", "), StringUtils.join(existingAppNames, ", "));
		
		return verifierChainer;
	}

	public VerifierChainer ancestorNodeCanBeFound(File file, Class<? extends Node> nodeClass)
	{
		assertNotNull( brjs.locateAncestorNodeOfClass(file, nodeClass) );
		
		return verifierChainer;
	}
}
