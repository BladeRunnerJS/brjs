package org.bladerunnerjs.jstestdriver.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.jstestdriver.BundlerHandler;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

import com.google.jstestdriver.FileInfo;

import static org.junit.Assert.*;


public class BRJSBundleInjectorSpecTest extends SpecTest
{

	protected JSTDTestPackCommander whenJstdTests(TestPack testPack)
	{
		return new JSTDTestPackCommander(testPack);
	}
	
	protected JSTDTestPackVerifier thenJstdTests(TestPack testPack)
	{
		return new JSTDTestPackVerifier(testPack);
	}
	
	
	
	
	public class JSTDTestPackCommander {

		private TestPack testPack;
		
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		
		public JSTDTestPackCommander(TestPack testPack)
		{
			this.testPack = testPack;
		}

		public void runWithPaths(String... requestPaths) throws Exception
		{
			for (String filePath : requestPaths)
			{
				String absoluteInputFilePath = testPack.file(filePath).getAbsolutePath();
				inputFiles.add( new FileInfo(absoluteInputFilePath, -1, -1, false, false, null, absoluteInputFilePath) );
			}
			new BundlerHandler().processDependencies(inputFiles);
		}
	}
	
	public class JSTDTestPackVerifier {
		
		private TestPack testPack;
		
		public JSTDTestPackVerifier(TestPack testPack)
		{
			this.testPack = testPack;
		}

		public void testBundleContainsText(String bundleFilePath, String expectedContents) throws IOException
		{
			File bundleFile = testPack.file(bundleFilePath);
			String bundleFileContents = FileUtils.readFileToString(bundleFile);
			if (!bundleFileContents.contains(expectedContents))
			{
				assertEquals( "bundle file didnt contain expected text", expectedContents, bundleFileContents );				
			}
		}

		public void testBundleDoesNotContainText(String bundleFilePath, String doesNotContainContents) throws IOException
		{
			File bundleFile = testPack.file(bundleFilePath);
			String bundleFileContents = FileUtils.readFileToString(bundleFile);
			if (bundleFileContents.contains(doesNotContainContents))
			{
				assertEquals( "bundle file didnt contain expected text", doesNotContainContents, bundleFileContents );				
			}
		}
		
	}
	
	
}
