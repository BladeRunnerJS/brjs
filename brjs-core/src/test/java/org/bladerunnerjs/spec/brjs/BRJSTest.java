package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BRJSTest extends SpecTest {
	private NamedDirNode brjsTemplate;
	private App app1;
	private App app2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			brjsTemplate = brjs.template("brjs");
			app1 = brjs.app("app1");
			app2 = brjs.app("app2");
	}
	
	@Ignore //TODO: fix test
	@Test
	public void theAppConfIsWrittenOnPopulate() throws Exception {
		given(brjsTemplate).hasBeenCreated();
		when(brjs).populate();
		then(brjs).fileHasContents("conf/bladerunner.conf", "defaultInputEncoding: UTF-8\ndefaultOutputEncoding: UTF-8\njettyPort: 7070");
	}
	
	@Test
	public void observersArentNotifiedOfEventsThatArentBeneathThem() throws Exception {
		given(observer).observing(app2);
		when(app1).populate();
		then(observer).noNotifications();
	}
	
	@Test
	public void exceptionIsThrownIfRunCommandIsInvokedWithoutACommandName() throws Exception {
		when(brjs).runCommand("");
		then(exceptions).verifyException(NoSuchCommandException.class);
	}
	
	@Test
	public void exceptionIsThrownIfRunCommandIsInvokedWithANonExistentCommandName() throws Exception {
		when(brjs).runCommand("no-such-command");
		then(exceptions).verifyException(NoSuchCommandException.class, "no-such-command");
	}
	
}
