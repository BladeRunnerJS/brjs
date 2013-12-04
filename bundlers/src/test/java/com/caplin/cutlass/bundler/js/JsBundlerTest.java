package com.caplin.cutlass.bundler.js;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class JsBundlerTest
{
	private static final String testBase = "src/test/resources/js-bundler/js-bundler";
	private JsBundler jsBundler;
	
	private static String jsBundlHeader = StringUtils.join( Arrays.asList(
			"/***************************************************************/",
			"/**  Created with SDK Version 1.2.3 (build date 12/34/5678).  **/",
			"/***************************************************************/",
			"",
			"" ), "\n" );	
	
	@Before
	public void setup() throws Exception
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));

		jsBundler = new JsBundler();
	}
	
	@Test(expected=RequestHandlingException.class)
	public void testThrowsMalformedUrlExceptionForInvalidRequest() throws Exception
	{
		jsBundler.getBundleFiles(null, null, "abc_js.bundle");
	}
	
	@Test
	public void testBundleContainingAllNamespacedClasses() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"// package definition block\n" +
			"window.pkg1 = {\"pkg4\":{},\"pkg3\":{}};\n" +
			"window.pkg2 = {};\n" +
			"\n" +
			"this is the contents of Class1.js\n" +
			"and the end of the file\n" +
			"\n" +
			"this is the contents of Class2.js\n" +
			"and the end of the file\n" +
			"\n" +
			"this is the contents of Class3.js\n" +
			"and the end of the file\n" +
			"\n" +
			"this is the contents of Class4.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg1/Class1.js"), 
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg2/Class2.js"),
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg1/pkg3/Class3.js"),
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg1/pkg4/Class4.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingASingleUnNamespacedClass() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"this is the contents of UnNamespacedClass.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "sdk/libs/javascript/caplin/src/UnNamespacedClass.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingASingleDeepNamespacedClass() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"// package definition block\n" +
			"window.pkg1 = {\"pkg3\":{}};\n" +
			"\n" +
			"this is the contents of Class3.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg1/pkg3/Class3.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingALibraryTestClass() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"// package definition block\n" +
			"window.testpkg = {};\n" +
			"window.pkg1 = {};\n" +
			"\n" +
			"this is the contents of Class1.js\n" +
			"and the end of the file\n" +
			"\n" +
			"this is the contents of LibraryTestClass.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "sdk/libs/javascript/caplin/src/pkg1/Class1.js"),
			new File(testBase, "sdk/libs/javascript/caplin/test/pkg1/test-unit/js-test-driver/src-test/testpkg/LibraryTestClass.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingABladeClass() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"// package definition block\n" +
			"window.bladepkg = {};\n" +
			"\n" +
			"this is the contents of BladeClass.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "apps/app1/default-bladeset/blades/blade1/src/bladepkg/BladeClass.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingABladeTestClass() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"// package definition block\n" +
			"window.bladetestpkg = {};\n" +
			"\n" +
			"this is the contents of BladeTestClass.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "apps/app1/default-bladeset/blades/blade1/src-test/bladetestpkg/BladeTestClass.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	@Test
	public void testBundleContainingAThirdPartyLibrary() throws Exception
	{
		String expectedOutput = jsBundlHeader +
			"this is the contents of thirdparty-lib.js\n" +
			"and the end of the file\n" +
			"\n" +
			"caplin.onLoad();\n";
		String actualOutput = getJsBundleOutput(Arrays.asList(
			new File(testBase, "sdk/libs/javascript/thirdparty/thirdparty-lib/thirdparty-lib.js")));
		
		assertEquals(expectedOutput, actualOutput);
	}
	
	/* TODO: delete me when the static include hack has been removed */
	@Test
	public void testStaticIncludeIsIncludedBeforeCurrentClass() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-staticDepend/main-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
				SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
				APPLICATIONS_DIR + "/app-with-staticDepend/main-aspect/src/section/app/staticDepend.js", 
				APPLICATIONS_DIR + "/app-with-staticDepend/main-aspect/src/section/app/main1.js", 
				APPLICATIONS_DIR + "/app-with-staticDepend/main-aspect/src/section/app/main2.js"
		});
	}
	
	@Test
	public void testThirdpartyDependencyLookupIsGreedy() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-similar-named-thirdparty-libs/main-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-similar-named-thirdparty-libs/thirdparty-libraries/jqueryyyyyy/jQuery.js",
				APPLICATIONS_DIR + "/app-with-similar-named-thirdparty-libs/thirdparty-libraries/knockout1234a/knockout.js",
				APPLICATIONS_DIR + "/app-with-similar-named-thirdparty-libs/main-aspect/src/section/app/main1.js", 
				APPLICATIONS_DIR + "/app-with-similar-named-thirdparty-libs/main-aspect/src/section/app/main2.js"
		});
	}
	
	@Test
	public void bundlingDoesntFailIfThereAreTwoDirsInSrcDirOrInvalidPackageDirs() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-multiple-dirs-in-src-and-pkg/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-multiple-dirs-in-src-and-pkg/default-aspect/src/section/app/default1.js",
				APPLICATIONS_DIR + "/app-with-multiple-dirs-in-src-and-pkg/default-aspect/src/section/app/default2.js"
		});
	}
	
	@Test @Ignore //PCTCUT-594 - test currently fails
	public void testThirdpartyDependencyMustMatchExactly() throws Exception
	{
		/* 
		 * jqueryyyyy and knockout1234a are referenced but not available 
		 * 	- test to make sure jquery and knockout are not included instead 
		 */
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-missing-thirdparty-libs/main-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-missing-thirdparty-libs/main-aspect/src/section/app/main1.js", 
				APPLICATIONS_DIR + "/app-with-missing-thirdparty-libs/main-aspect/src/section/app/main2.js"
		});
	}
	
	@Test //PCTLIBRARY-796
	public void bundleDoesNotContainHiddenFilesOrFilesInsideHiddenFolders() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-hidden-files/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-hidden-files/default-aspect/src/section/app/default1.js",
		});
	}

	@Test
	public void referringToAnAliasCausesTheCorrespondingClassToBeIncluded() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-class-aliases/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-class-aliases/default-aspect/src/novox/MyClass.js"
		});
	}
	
	@Test
	public void referringToAnAliasCausesTheCorrespondingClassAndInterfaceToBeIncluded() throws Exception
	{
		BundlerFileTester test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-class-aliases/other-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-with-class-aliases/a-bladeset/blades/b/src/novox/a/b/MyInterface.js",
				APPLICATIONS_DIR + "/app-with-class-aliases/a-bladeset/blades/b/src/novox/a/b/MyClass.js"
		});
	}
	
	private String getJsBundleOutput(List<File> sourceFiles) throws IOException, FileNotFoundException, BundlerProcessingException
	{
		File tempWarDir = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		File warFile = new File(tempWarDir, "js.bundle");
		FileOutputStream fileOutputStream = new FileOutputStream(warFile);
		
		jsBundler.writeBundle(sourceFiles, fileOutputStream);
		String actualOutput = getContentsOfFile(new File(tempWarDir, "js.bundle"));
		
		return actualOutput;
	}
	
	private String getContentsOfFile(File theFile) throws IOException
	{
		StringBuilder ret = new StringBuilder();
		FileInputStream fstream = new FileInputStream(theFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null)
		{
			ret.append(line + "\n");
		}
		br.close();
		return ret.toString();
	}
	
}
