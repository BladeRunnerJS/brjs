package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Test;


public class BladerunnerCommandTest extends SpecTest
{

	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated();
		when(brjs).runCommand("help", "start");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
}
