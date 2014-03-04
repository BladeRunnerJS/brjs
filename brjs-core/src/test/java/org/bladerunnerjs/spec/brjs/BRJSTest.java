package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
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
	public void theBrjsConfIsWrittenOnPopulate() throws Exception {
		given(brjsTemplate).hasBeenCreated();
		when(brjs).populate();
		then(brjs).fileHasContents("conf/bladerunner.conf", "defaultFileCharacterEncoding: UTF-8\nbrowserCharacterEncoding: UTF-8\njettyPort: 7070");
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
	
	@Test
	public void brjsConfDirIsntOverwrittenIfItExists() throws Exception {
		given(brjsTemplate).hasBeenCreated()
			.and(brjs).hasDir("conf")
			.and(brjs).containsFile("conf/some-config.xml")
			.and(brjs).hasDir("conf")
			.and(brjs).containsFileWithContents("conf/my.conf", "some config");
		when(brjs).populate();
		then(brjs).fileHasContents("conf/my.conf", "some config");
	}
	
	@Test
	public void brjsFilesInTemplateDontOverwriteExistingFiles() throws Exception {
		given(brjsTemplate).hasBeenCreated()
			.and(brjsTemplate).containsFileWithContents("conf/my.conf", "some template config")
			.and(brjs).hasDir("conf")
			.and(brjs).containsFileWithContents("conf/my.conf", "some custom config");
		when(brjs).populate();
		then(brjs).fileHasContents("conf/my.conf", "some custom config");
	}
	
}
