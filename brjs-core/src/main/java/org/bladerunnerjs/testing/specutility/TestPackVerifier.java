package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestAssetLocation;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

import com.google.common.base.Joiner;

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

	public VerifierChainer bundledFilesEquals(File... expectedFiles) throws Exception
	{
		List<File> bundleSetFiles = new ArrayList<File>();
		List<SourceModule> sourceModules = testPack.getBundleSet().getSourceModules();
		
		for (SourceModule sourceModule : sourceModules)
		{
			if(!(sourceModule.assetLocation() instanceof TestAssetLocation)) {
				bundleSetFiles.add( new File(sourceModule.dir(), sourceModule.getAssetName()) );
			}
		}
		
		for (File expectedFile : expectedFiles)
		{
			if(!bundleSetFiles.contains(expectedFile) || expectedFiles.length != bundleSetFiles.size())
			{
				List<File> sortedExpectedFiles = Arrays.asList(expectedFiles);
				List<File> sortedActualFiles = Arrays.asList(bundleSetFiles.toArray(new File[bundleSetFiles.size()]));
				
				Collections.sort(sortedExpectedFiles);
				Collections.sort(sortedActualFiles);
				
				assertEquals("expected file " + expectedFile.getPath() + " wasnt found in the bundleset", 
						getFileList(sortedExpectedFiles), 
						getFileList(sortedActualFiles));
			}
		}
		
		return verifierChainer;
	}
	
	
	private String getFileList(List<File> files)
	{
		List<String> fileNames = new ArrayList<>();
		
		for(File file : files)
		{
			fileNames.add(file.getPath());
		}
		
		return Joiner.on("\n").join(fileNames);
	}

}
