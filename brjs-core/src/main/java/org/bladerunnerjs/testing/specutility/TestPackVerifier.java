package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

import static org.junit.Assert.*;

public class TestPackVerifier extends NodeVerifier<NamedNode>
{
	TestPack testPack;

	public TestPackVerifier(SpecTest specTest, TestPack testPack)
	{
		super(specTest, testPack);
		this.testPack = testPack;
	}

	public VerifierChainer bundledFilesEquals(File... files) throws Exception
	{
		List<File> bundleSetFiles = new ArrayList<File>();
		List<SourceModule> sourceModules = testPack.getBundleSet().getSourceModules();
		
		for (SourceModule sourceModule : sourceModules)
		{ 
			bundleSetFiles.add( sourceModule.getUnderlyingFile() );
		}
		
		for (File expectedFile : files)
		{
			assertTrue("expected file " + expectedFile.getPath() + " wasnt found in the bundleset", bundleSetFiles.contains(expectedFile));
		}
		
		return verifierChainer;
	}

}
