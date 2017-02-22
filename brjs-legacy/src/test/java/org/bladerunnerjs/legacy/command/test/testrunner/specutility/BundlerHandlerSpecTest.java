package org.bladerunnerjs.legacy.command.test.testrunner.specutility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.legacy.command.test.testrunner.BundlerHandler;
import org.bladerunnerjs.legacy.command.test.testrunner.JsTestDriverBundleCreator;

import static org.junit.Assert.*;


public class BundlerHandlerSpecTest extends SpecTest
{

	protected JSTDTestPackCommander whenJstdTests(TestPack testPack)
	{
		return new JSTDTestPackCommander(testPack);
	}

	protected JSTDTestPackVerifier thenJstdTests(TestPack testPack)
	{
		return new JSTDTestPackVerifier(testPack);
	}

	public class JSTDTestPackCommander
	{

		private TestPack testPack;

		public JSTDTestPackCommander(TestPack testPack)
		{
			this.testPack = testPack;
		}

		public void runWithPaths(String... requestPaths) throws Exception
		{
			for (String requestPath : requestPaths) {
    			File bundleFile = testPack.file(requestPath);
    			String bundlePath = StringUtils.substringAfterLast(bundleFile.getAbsolutePath(), JsTestDriverBundleCreator.BUNDLES_DIR_NAME + File.separator);
    			bundlePath = StringUtils.replace(bundlePath, "\\", "/");
    			new BundlerHandler(testPack, "combined").createBundleFile(brjs, bundleFile, bundlePath, brjs.getAppVersionGenerator().getVersion());
			}
		}
	}

	public class JSTDTestPackVerifier
	{

		private TestPack testPack;

		public JSTDTestPackVerifier(TestPack testPack)
		{
			this.testPack = testPack;
		}

		public void testBundleContainsText(String bundleFilePath, String expectedContents) throws IOException
		{
			File bundleFile = testPack.file(bundleFilePath);
			String bundleFileContents = org.apache.commons.io.FileUtils.readFileToString(bundleFile, BladerunnerConf.OUTPUT_ENCODING);
			if (!bundleFileContents.contains(expectedContents))
			{
				assertEquals("bundle file didnt contain expected text", expectedContents, bundleFileContents);
			}
		}

		public void testBundleDoesNotContainText(String bundleFilePath, String doesNotContainContents) throws IOException
		{
			File bundleFile = testPack.file(bundleFilePath);
			String bundleFileContents = org.apache.commons.io.FileUtils.readFileToString(bundleFile, BladerunnerConf.OUTPUT_ENCODING);
			if (bundleFileContents.contains(doesNotContainContents))
			{
				assertEquals("bundle file didnt contain expected text", doesNotContainContents, bundleFileContents);
			}
		}

	}

}
