package org.bladerunnerjs.model;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BRJSIOTest {
	private final BRJSIO io = new BRJSIO();
	private File tempDir;
	private File subDir1;
	private File subDir2;
	private File tempHelloWorldFile;
	private File subDir1HelloWorldFile;
	
	@Before
	public void setUp() throws Exception {
		tempDir = FileUtility.createTemporaryDirectory("BRJSIOTest");
		subDir1 = new File(tempDir, "subdir1");
		subDir2 = new File(tempDir, "subdir2");
		FileUtils.forceMkdir(subDir1);
		
		tempHelloWorldFile = new File(tempDir, "file.txt");
		FileUtils.write(tempHelloWorldFile, "Hello World!");
		
		subDir1HelloWorldFile = new File(subDir1, "file.txt");
		FileUtils.write(subDir1HelloWorldFile, "Hello Other World!");
	}
	
	@After
	public void tearDown() {
		io.uninstallFileAccessChecker();
	}
	
	@Test
	public void weCanReadAFileIfWeveInstalledTheAccessCheckerButHaveNotYetLimitedAccess() throws Exception {
		io.installFileAccessChecker();
		assertEquals("Hello World!", FileUtils.readFileToString(tempHelloWorldFile));
	}
	
	@Test
	public void weCanReadAFileIfWeveLimitedAccessButHaveNotYetInstalledTheAccessChecker() throws Exception {
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1})) {
			scope.preventCompilerWarning();
			assertEquals("Hello World!", FileUtils.readFileToString(tempHelloWorldFile));
		}
	}
	
	@Test(expected = BRJSMemoizationFileAccessException.class)
	public void weCantReadAFileIfWeveBothLimitedAccessAndInstalledTheAccessChecker() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1})) {
			scope.preventCompilerWarning();
			FileUtils.readFileToString(tempHelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinTheLimitedArea() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			scope.preventCompilerWarning();
			FileUtils.readFileToString(tempHelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainDeepWithinTheLimitedArea() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			scope.preventCompilerWarning();
			FileUtils.readFileToString(subDir1HelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinOneOfTheLimitedAreasWithinASingleScope() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			scope.preventCompilerWarning();
			FileUtils.readFileToString(subDir1HelloWorldFile);
		}
	}
	
	@Test(expected = BRJSMemoizationFileAccessException.class)
	public void anExceptionIsThrownIfWePassOneScopeButNotOneOfTheOthers() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			innerScope.preventCompilerWarning();
			
			try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir2})) {
				outerScope.preventCompilerWarning();
				FileUtils.readFileToString(subDir1HelloWorldFile);
			}
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinBothScopes() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			innerScope.preventCompilerWarning();
			
			try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir1})) {
				outerScope.preventCompilerWarning();
				FileUtils.readFileToString(subDir1HelloWorldFile);
			}
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinMultipleScopes() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope rootScope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			rootScope.preventCompilerWarning();
			
			try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
				innerScope.preventCompilerWarning();
				
				try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir1})) {
					outerScope.preventCompilerWarning();
					FileUtils.readFileToString(subDir1HelloWorldFile);
				}
			}
		}
	}
}
