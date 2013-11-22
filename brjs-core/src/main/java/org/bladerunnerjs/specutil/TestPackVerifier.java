package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.File;

import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.VerifierChainer;


public class TestPackVerifier extends NodeVerifier<NamedNode>
{
	TestPack testPack;

	public TestPackVerifier(SpecTest specTest, TestPack testPack)
	{
		super(specTest, testPack);
		this.testPack = testPack;
	}

	public VerifierChainer bundledFilesEquals(File... files) throws Exception {
		fail("the model doesn't yet support bundling for testPacks!");
		
		return verifierChainer;
	}

}
