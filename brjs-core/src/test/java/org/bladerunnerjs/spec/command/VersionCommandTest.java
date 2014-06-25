package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class VersionCommandTest extends SpecTest {
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("version", "x");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: x"));
	}
	
	@Test
	public void versionNumberAndBuildDateIsIncludedInTheMessage() throws Exception {
		given(brjs).containsFileWithContents("sdk/version.txt", "{'Version': 'the-version', 'BuildDate': 'the-build-date'}");
		when(brjs).runCommand("version");
		then(logging).containsConsoleText("BladeRunnerJS version: the-version, built: the-build-date");
	}
	
	@Test
	public void asciiArtIsIncludedInTheMessage() throws Exception {
		when(brjs).runCommand("version");
		then(logging).containsConsoleText("-- Divide & conquer complex web apps --");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("version");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
