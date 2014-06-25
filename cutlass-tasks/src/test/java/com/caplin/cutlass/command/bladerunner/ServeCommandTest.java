package com.caplin.cutlass.command.bladerunner;

import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.CommandOperationException;

import com.caplin.cutlass.command.CommandTaskTest;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.plugins.commands.standard.ServeCommand;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

public class ServeCommandTest extends CommandTaskTest
{
	private ApplicationServer mockAppServer;
	private BRJS brjs;

	@Before
	public void setup() throws Exception
	{
		mockAppServer = mock(ApplicationServer.class);
		when(mockAppServer.getPort()).thenReturn(1234);
		File brjsDir = new File("src/test/resources/ServeCommandTest");
		brjs = createModel(brjsDir);
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		commandTask = new ServeCommand(mockAppServer);
		commandTask.setBRJS(brjs);
	}

	@Test
	public void testStartCommandStartsTheAppServer() throws Exception
	{
		String[] args = new String[0];
		commandTask.doCommand(args);
		verify(mockAppServer).start();
	}

	@Ignore
	@Test(expected=CommandOperationException.class)
	public void testPassingInArgumentsGivesWarningAboutExtraArgs() throws Exception
	{
		String[] args = new String[] { "some", "args" };
		commandTask.doCommand(args);
		verify(mockAppServer).start();
	}

	@Test(expected=CommandOperationException.class)
	public void testExceptionIsThrownIfErrorStartingAppServer() throws Exception
	{
		String[] args = new String[0];
		doThrow(Exception.class).when(mockAppServer).start();
		commandTask.doCommand(args);
		verify(mockAppServer).start();
	}

}
