package org.bladerunnerjs.runner;

import static org.bladerunnerjs.api.spec.utility.BRJSAssertions.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.events.BundleSetCreatedEvent;
import org.bladerunnerjs.model.events.NewInstallEvent;
import org.bladerunnerjs.runner.CommandRunner;
import org.bladerunnerjs.runner.CommandRunner.InvalidDirectoryException;
import org.bladerunnerjs.runner.CommandRunner.NoSdkArgumentException;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.UserCommandRunner;
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
	private InputStream oldSysIn;
	
	@Before
	public void setUp() throws IOException, InvalidSdkDirectoryException {
		StaticLoggerBinder.getSingleton().getLoggerFactory().setOutputStreams(new PrintStream(outputStream), new PrintStream(errorStream));
		commandRunner = new CommandRunner();
		
		tempDir = FileUtils.createTemporaryDirectory( getClass() );
		ThreadSafeStaticBRJSAccessor.destroy();
		oldSysOut = System.out;
		oldSysIn = System.in;
		System.setIn(new ByteArrayInputStream("".getBytes()));
		System.setOut( new PrintStream(systemOutputStream) );
	}
	
	@After
	public void tearDown() {
		System.setOut( oldSysOut );		
		System.setIn( oldSysIn );
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
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory")});
	}
	
	@Test
	public void builtInCommandsShowWarnLevelLogLinesByDefault() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void consoleLoggingIsAlwaysVisible() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		
		String output = systemOutputStream.toString("UTF-8");
		assertContains("console-level", output);
	}
	
	@Test
	public void verboseLogLinesCanBeEnabled() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void debugLogLinesCanBeEnabled() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--debug"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertContains("debug-level", output);
	}
	
	@Test
	public void externalCommandsDontShowAnyLogsEvenWhenDebugLoggingIsUsed() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--debug"});
		
		String output = outputStream.toString("UTF-8");
		assertDoesNotContain("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabled() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--pkg", "org.other, org.external", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void externalCommandsCanHaveTheirLoggingEnabledViaWildcard() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "external-log-test", "--pkg", "ALL", "--info"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("warn-level", output);
		assertContains("info-level", output);
		assertDoesNotContain("debug-level", output);
	}
	
	@Test
	public void errorsAndWarningsForAllPackagesAreDisplayedEvenIfNotLoggingThatPackage() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
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
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test", "--show-pkg"});
		
		String output = outputStream.toString("UTF-8");
		assertContains("org.bladerunnerjs.runner.LogTestCommand: warn-level", output);
	}
	
	@Test
	public void nonLogArgumentsAreReceivedCorrectly() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
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
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		dirFile("valid-sdk-directory/sdk/libs/java").mkdirs();
		org.apache.commons.io.FileUtils.write( dirFile("valid-sdk-directory/sdk/libs/java/application/brjs-servlet-1.2.3.jar"), "some jar contents" );
		dirFile("valid-sdk-directory/apps/myApp/WEB-INF/lib").mkdirs();
		org.apache.commons.io.FileUtils.write( dirFile("valid-sdk-directory/apps/myApp/WEB-INF/lib/brjs-servlet-1.2.2.jar"), "old jar contents" );
		
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "log-test"});
		String output = outputStream.toString("UTF-8");
		String warnMessage = String.format(UserCommandRunner.Messages.OUTDATED_JAR_MESSAGE, "myApp", "brjs-", "sdk/libs/java/application");
		assertContains(warnMessage, output);
	}
	
	//if I do an incorrect command I get the properties outputted in the right order
	@Test
	public void theCommandIsExecutedWithIncorrectParametersExpectCorrectPropertiesOrder() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "multiple-args-command-test"});
		
		String output = systemOutputStream.toString("UTF-8"); //expected val
		
		String[] valuesInQuotes = StringUtils.substringsBetween(output , "\'", "\'");
		
		assertContains("arg1", valuesInQuotes[0]);
		assertContains("arg2", valuesInQuotes[1]);
		assertContains("arg3", valuesInQuotes[2]);
		assertContains("arg4", valuesInQuotes[3]);
		assertContains("arg5", valuesInQuotes[4]);
	}
	
	@Test
	public void newInstallEventIsEmittedIfYesIsAnsweredToStatsCollection() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		BRJS brjs = ThreadSafeStaticBRJSAccessor.initializeModel( new File(dir("valid-sdk-directory")) );
		EventObserver mockEventObserver = mock(EventObserver.class);
		brjs.addObserver(NewInstallEvent.class, mockEventObserver);
	
		System.setIn(new ByteArrayInputStream("y\r\n".getBytes()));
		commandRunner.run(new String[] {dir("valid-sdk-directory")});
		String brjsConfLine1 = org.apache.commons.io.FileUtils.readLines(dirFile("valid-sdk-directory/conf/brjs.conf")).get(0);
		assertEquals("allowAnonymousStats: true", brjsConfLine1);
		verify(mockEventObserver).onEventEmitted(any(NewInstallEvent.class), eq(brjs));
	}
	
	@Test
	public void newInstallEventIsNotEmittedIfNoIsAnsweredToStatsCollection() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		BRJS brjs = ThreadSafeStaticBRJSAccessor.initializeModel( new File(dir("valid-sdk-directory")) );
		EventObserver mockEventObserver = mock(EventObserver.class);
		brjs.addObserver(NewInstallEvent.class, mockEventObserver);
	
		System.setIn(new ByteArrayInputStream("n\r\n".getBytes()));
		commandRunner.run(new String[] {dir("valid-sdk-directory")});
		String brjsConfLine1 = org.apache.commons.io.FileUtils.readLines(dirFile("valid-sdk-directory/conf/brjs.conf")).get(0);
		assertEquals("allowAnonymousStats: false", brjsConfLine1);
		verifyZeroInteractions(mockEventObserver);
	}
	
	@Test
	public void newInstallEventIsNotEmittedIfThereIsNoStdin_egBrjsIsExecutedFromScripts() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		BRJS brjs = ThreadSafeStaticBRJSAccessor.initializeModel( new File(dir("valid-sdk-directory")) );
		EventObserver mockEventObserver = mock(EventObserver.class);
		brjs.addObserver(BundleSetCreatedEvent.class, mockEventObserver);
	
		commandRunner.run(new String[] {dir("valid-sdk-directory")});
		String brjsConfLine1 = org.apache.commons.io.FileUtils.readLines(dirFile("valid-sdk-directory/conf/brjs.conf")).get(0);
		assertEquals("allowAnonymousStats: false", brjsConfLine1);
		verifyZeroInteractions(mockEventObserver);
	}
		
	@Test
	public void newInstallEventIsEmittedIfStatsFlagIsUsed() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		BRJS brjs = ThreadSafeStaticBRJSAccessor.initializeModel( new File(dir("valid-sdk-directory")) );
		EventObserver mockEventObserver = mock(EventObserver.class);
		brjs.addObserver(NewInstallEvent.class, mockEventObserver);
	
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "--stats"});
		String brjsConfLine1 = org.apache.commons.io.FileUtils.readLines(dirFile("valid-sdk-directory/conf/brjs.conf")).get(0);
		assertEquals("allowAnonymousStats: true", brjsConfLine1);
		verify(mockEventObserver).onEventEmitted(any(NewInstallEvent.class), eq(brjs));
	}
	
	@Test
	public void newInstallEventIsNotEmittedIfNoStatsFlagIsUsed() throws Exception {
		dirFile("valid-sdk-directory/conf/templates/default/brjs").mkdirs();
		dirFile("valid-sdk-directory/sdk").mkdirs();
		BRJS brjs = ThreadSafeStaticBRJSAccessor.initializeModel( new File(dir("valid-sdk-directory")) );
		EventObserver mockEventObserver = mock(EventObserver.class);
		brjs.addObserver(NewInstallEvent.class, mockEventObserver);
	
		commandRunner.run(new String[] {dir("valid-sdk-directory"), "--no-stats"});
		String brjsConfLine1 = org.apache.commons.io.FileUtils.readLines(dirFile("valid-sdk-directory/conf/brjs.conf")).get(0);
		assertEquals("allowAnonymousStats: false", brjsConfLine1);
		verifyZeroInteractions(mockEventObserver);
	}
	
	
	
	// -------------------------
	
	private File dirFile(String dirName) {
		return new File(tempDir, dirName);
	}
	
	private String dir(String dirName) {
		return dirFile(dirName).getPath();
	}

}
