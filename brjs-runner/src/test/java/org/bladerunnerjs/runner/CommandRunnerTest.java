package org.bladerunnerjs.runner;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.runner.CommandRunner;
import org.bladerunnerjs.runner.CommandRunner.InvalidDirectoryException;
import org.bladerunnerjs.runner.CommandRunner.NoSdkArgumentException;

import com.caplin.cutlass.util.FileUtility;

import org.junit.Before;
import org.junit.Test;

public class CommandRunnerTest {
	private CommandRunner commandRunner;
	private File tempDir;
	
	@Before
	public void setUp() throws IOException {
		commandRunner = new CommandRunner();
		tempDir = FileUtility.createTemporaryDirectory(getClass().getSimpleName());
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
	
	// TODO: add tests to verify the --quiet, --verbose & --debug flags are being handled correctly
	
	private File dirFile(String dirName) {
		return new File(tempDir, dirName);
	}
	
	private String dir(String dirName) {
		return dirFile(dirName).getPath();
	}
}
