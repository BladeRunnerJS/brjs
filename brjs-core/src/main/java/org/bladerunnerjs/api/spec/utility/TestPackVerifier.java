package org.bladerunnerjs.api.spec.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;

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

	public VerifierChainer srcOnlyBundledFilesEquals(MemoizedFile... expectedFiles) throws Exception
	{
		List<MemoizedFile> bundleSetFiles = new ArrayList<>();
		List<SourceModule> sourceModules = testPack.getBundleSet().getAssets(SourceModule.class);
		
		for (SourceModule sourceModule : sourceModules)
		{
			if (sourceModule instanceof TestAsset) {
				continue;
			}
			bundleSetFiles.add( sourceModule.file() );				
		}
		
		for (MemoizedFile expectedFile : expectedFiles)
		{
			if(!bundleSetFiles.contains(expectedFile) || expectedFiles.length != bundleSetFiles.size())
			{
				List<MemoizedFile> sortedExpectedFiles = Arrays.asList(expectedFiles);
				List<MemoizedFile> sortedActualFiles = Arrays.asList(bundleSetFiles.toArray(new MemoizedFile[bundleSetFiles.size()]));
				
				Collections.sort(sortedExpectedFiles);
				Collections.sort(sortedActualFiles);
				
				assertEquals("expected file " + expectedFile.getPath() + " wasnt found in the bundleset", 
						getFileList(sortedExpectedFiles), 
						getFileList(sortedActualFiles));
			}
		}
		
		return verifierChainer;
	}
	
	
	private String getFileList(List<MemoizedFile> files)
	{
		List<String> fileNames = new ArrayList<>();
		
		for(File file : files)
		{
			fileNames.add(file.getPath());
		}
		
		return Joiner.on("\n").join(fileNames);
	}

}
