package org.bladerunnerjs.model;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IOTest {
	private IO io = new IO( FalseFileFilter.INSTANCE );
	private File tempDir;
	private File subDir1;
	private File subDir2;
	private File tempHelloWorldFile;
	private File subDir1HelloWorldFile;
	
	@Before
	public void setUp() throws Exception {
		tempDir = FileUtils.createTemporaryDirectory( this.getClass() );
		subDir1 = new File(tempDir, "subdir1");
		subDir2 = new File(tempDir, "subdir2");
		org.apache.commons.io.FileUtils.forceMkdir(subDir1);
		
		tempHelloWorldFile = new File(tempDir, "file.txt");
		org.apache.commons.io.FileUtils.write(tempHelloWorldFile, "Hello World!");
		
		subDir1HelloWorldFile = new File(subDir1, "file.txt");
		org.apache.commons.io.FileUtils.write(subDir1HelloWorldFile, "Hello Other World!");
	}
	
	@After
	public void tearDown() {
		io.uninstallFileAccessChecker();
	}
	
	@Test
	public void weCanReadAFileIfWeveInstalledTheAccessCheckerButHaveNotYetLimitedAccess() throws Exception {
		io.installFileAccessChecker();
		assertEquals("Hello World!", org.apache.commons.io.FileUtils.readFileToString(tempHelloWorldFile));
	}
	
	@Test
	public void weCanReadAFileIfWeveLimitedAccessButHaveNotYetInstalledTheAccessChecker() throws Exception {
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1})) {
			assertEquals("Hello World!", org.apache.commons.io.FileUtils.readFileToString(tempHelloWorldFile));
		}
	}
	
	@Test(expected = BRJSMemoizationFileAccessException.class)
	public void weCantReadAFileIfWeveBothLimitedAccessAndInstalledTheAccessChecker() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1})) {
			org.apache.commons.io.FileUtils.readFileToString(tempHelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinTheLimitedArea() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			org.apache.commons.io.FileUtils.readFileToString(tempHelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainDeepWithinTheLimitedArea() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			org.apache.commons.io.FileUtils.readFileToString(subDir1HelloWorldFile);
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinOneOfTheLimitedAreasWithinASingleScope() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			org.apache.commons.io.FileUtils.readFileToString(subDir1HelloWorldFile);
		}
	}
	
	@Test(expected = BRJSMemoizationFileAccessException.class)
	public void anExceptionIsThrownIfWePassOneScopeButNotOneOfTheOthers() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			
			try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir2})) {
				org.apache.commons.io.FileUtils.readFileToString(subDir1HelloWorldFile);
			}
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinBothScopes() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
			
			try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir1})) {
				org.apache.commons.io.FileUtils.readFileToString(subDir1HelloWorldFile);
			}
		}
	}
	
	@Test
	public void noExceptionIsThrownIfWeRemainWithinMultipleScopes() throws Exception {
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope rootScope = io.limitAccessToWithin("id", new File[] {tempDir})) {
			
			try(FileAccessLimitScope innerScope = io.limitAccessToWithin("id", new File[] {subDir1, subDir2})) {
				
				try(FileAccessLimitScope outerScope = io.limitAccessToWithin("id", new File[] {subDir1})) {
					org.apache.commons.io.FileUtils.readFileToString(subDir1HelloWorldFile);
				}
			}
		}
	}
	
	@Test
	public void weCanUseTheGlobalFileFIlterToIgnoreGlobalFiles() throws Exception {
		io = new IO( new NameFileFilter(tempHelloWorldFile.getName()) );
		
		io.installFileAccessChecker();
		
		try(FileAccessLimitScope scope = io.limitAccessToWithin("id", new File[] {subDir1})) {
			scope.getClass(); // reference scope to prevent compiler warnings
			org.apache.commons.io.FileUtils.readFileToString(tempHelloWorldFile);
		}
	}
	
}
