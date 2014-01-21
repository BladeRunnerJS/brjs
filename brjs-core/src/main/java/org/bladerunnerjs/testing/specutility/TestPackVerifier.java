package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

import static org.junit.Assert.*;

public class TestPackVerifier extends AssetContainerVerifier
{
	TestPack testPack;
	private VerifierChainer verifierChainer;

	public TestPackVerifier(SpecTest specTest, TestPack testPack)
	{
		super(testPack);
		this.testPack = testPack;
		this.verifierChainer = new VerifierChainer(specTest);
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
