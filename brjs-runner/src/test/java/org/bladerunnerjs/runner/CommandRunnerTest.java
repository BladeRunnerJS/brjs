package org.bladerunnerjs.runner;

import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.runner.CommandRunner;
import org.bladerunnerjs.runner.CommandRunner.InvalidDirectoryException;
import org.bladerunnerjs.runner.CommandRunner.NoSdkArgumentException;
import org.bladerunnerjs.utility.UserCommandRunner;

import com.caplin.cutlass.util.FileUtility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.StaticLoggerBinder;

public class CommandRunnerTest {
	private CommandRunner commandRunner;
	private ByteArrayOutputStream systemOutputStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	private File tempDir;
	
	private PrintStream oldSysOut;
	
	@Before
	public void setUp() throws IOException, InvalidSdkDirectoryException {
		StaticLoggerBinder.getSingleton().getLoggerFactory().setOutputStreams(new PrintStream(outputStream), new PrintStream(errorStream));
		commandRunner = new CommandRunner();
		
		tempDir = FileUtility.createTemporaryDirectory(getClass().getSimpleName());
		ThreadSafeStaticBRJSAccessor.destroy();
		oldSysOut = System.out;
		System.setOut( new PrintStream(systemOutputStream) );
	}
	
	@After
	public void tearDown() {
		System.setOut( oldSysOut );		
	}
	
	
	@Test(expected=NoSdkArgumentException.class)
	public void anExceptionIsThrownIfNoSdkDirectoryIsProvided() throws Exception {
		commandRunner.run(new String[] {});
	}
	
	@Test(expected=InvalidDirectoryException.class)
	public void anExceptionIsThrownIfTheSdkArgumentIsNotADirectory() throws Exception {
		commandRunner.run(new String[] {dir("no-such-directory")});
	}
	
	@Test(expected=CommandOperationException.class)
	public void anExceptionIsThrownIfTheSdkArgumentIsNotAValidSdkDirectory() throws Exception {
		dirFile("not-a-valid-sdk-directory").mkdirs();
		commandRunner.run(new String[] {dir("not-a-valid-sdk-directory")});
	}
	
	@Test
	public void theCommandIsExecutedWhenAValidDirectoryIsProvided() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory")});
	}
	
	@Test
	public void builtInCommandsShowWarnLevelLogLinesByDefault() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void consoleLoggingIsAlwaysVisible() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		
		String output = systemOutputStream.toString("UTF-8");
		assertContains("console-level", output);
	}
	
	@Test
	public void verboseLogLinesCanBeEnabled() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void debugLogLinesCanBeEnabled() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--debug"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertContains("debug-level", output);
	}
	
	@Test
	public void externalCommandsDontShowAnyLogsEvenWhenDebugLoggingIsUsed() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--debug"});
		
		String output = outputStream.toString("UTF-8");
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabled() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--pkg", "org.other, org.external", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabledViaWildcard() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--pkg", "ALL", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void errorsAndWarningsForAllPackagesAreDisplayedEvenIfNotLoggingThatPackage() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("error-level", output);
		assertContains("warn-level", output);
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void theClassResponsibleForEachLogLineCanBeDisplayed() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--show-pkg"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("org.bladerunnerjs.runner.LogTestCommand: warn-level", output);
	}
	
	@Test
	public void nonLogArgumentsAreReceivedCorrectly() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "arg-test", "arg1", "arg2", "--info"});
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "arg-test", "argX", "--info", "--show-pkg"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("arg1, arg2", output);
		assertContains("argX", output);
	}
	
	@Test
	public void warningIsPrintedIfTheServletJarIsOutdated() throws Exception
	{
		dirFile("valid-sdk-directory/sdk").mkdirs();
		dirFile("valid-sdk-directory/sdk/libs/java").mkdirs();
		FileUtils.write( dirFile("valid-sdk-directory/sdk/libs/java/application/brjs-servlet-1.2.3.jar"), "some jar contents" );
		dirFile("valid-sdk-directory/apps/myApp/WEB-INF/lib").mkdirs();
		FileUtils.write( dirFile("valid-sdk-directory/apps/myApp/WEB-INF/lib/brjs-servlet-1.2.2.jar"), "old jar contents" );
		
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		String output = outputStream.toString("UTF-8");
		String warnMessage = String.format(UserCommandRunner.Messages.OUTDATED_JAR_MESSAGE, "myApp", "brjs-", "sdk/libs/java/application");
		assertContains(warnMessage, output);
	}
	
	private File dirFile(String dirName) {
		return new File(tempDir, dirName);
	}
	
	private String dir(String dirName) {
		return dirFile(dirName).getPath();
	}
}
