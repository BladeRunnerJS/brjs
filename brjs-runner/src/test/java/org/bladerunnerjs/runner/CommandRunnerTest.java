package org.bladerunnerjs.runner;

import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.runner.CommandRunner;
import org.bladerunnerjs.runner.CommandRunner.InvalidDirectoryException;
import org.bladerunnerjs.runner.CommandRunner.NoSdkArgumentException;

import com.caplin.cutlass.util.FileUtility;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.StaticLoggerBinder;

public class CommandRunnerTest {
	private CommandRunner commandRunner;
	private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
	private File tempDir;
	
	@Before
	public void setUp() throws IOException, InvalidSdkDirectoryException {
		StaticLoggerBinder.getSingleton().getLoggerFactory().setOutputStreams(new PrintStream(outputStream), new PrintStream(errorStream));
		commandRunner = new CommandRunner();
		tempDir = FileUtility.createTemporaryDirectory(getClass().getSimpleName());
		ThreadSafeStaticBRJSAccessor.destroy();
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
	public void verboseLogLinesCanBeEnabled() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--verbose"});
		
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
		assertDoesNotContain("warn-level", output);
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabled() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--log", "org.other, org.external", "--verbose"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabledViaWildcard() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--log", "*", "--verbose"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void theClassResponsibleForEachLogLineCanBeDisplayed() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--log-info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("org.bladerunnerjs.runner.LogTestCommand: warn-level", output);
	}
	
	@Test
	public void nonLogArgumentsAreReceivedCorrectly() throws Exception {
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "arg-test", "arg1", "arg2", "--verbose"});
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "arg-test", "argX", "--verbose", "--log-info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("arg1, arg2", output);
		assertContains("argX", output);
	}
	
	private File dirFile(String dirName) {
		return new File(tempDir, dirName);
	}
	
	private String dir(String dirName) {
		return dirFile(dirName).getPath();
	}
}
