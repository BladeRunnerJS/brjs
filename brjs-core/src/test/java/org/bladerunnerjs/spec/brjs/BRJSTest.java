package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BRJSTest extends SpecTest {
	private NamedDirNode brjsTemplate;
	private App app1;
	private App app2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			brjsTemplate = brjs.templateGroup("default").template("brjs");
			app1 = brjs.app("app1");
			app2 = brjs.app("app2");
	}
	
	@Test
	public void theBrjsConfIsWrittenOnPopulate() throws Exception {
		given(brjsTemplate).hasBeenCreated();
		when(brjs).populate();
		then(brjs).fileHasContents("conf/brjs.conf", "defaultFileCharacterEncoding: UTF-8\nignoredPaths: .svn, .git\njettyPort: 7070\nloginRealm: BladeRunnerLoginRealm");
	}
	
	@Test
	public void observersArentNotifiedOfEventsThatArentBeneathThem() throws Exception {
		given(observer).observing(app2);
		when(app1).populate("default");
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
			.and(brjsTemplate).containsFileWithContents("conf/some-config.xml", "some config")
			.and(brjs).containsFileWithContents("conf/my.conf", "my custom config");
		when(brjs).populate();
		then(brjs).fileHasContents("conf/my.conf", "my custom config");
	}
	
	@Test
	public void brjsConfPopulatesFilesThatDontExist() throws Exception {
		given(brjsTemplate).hasBeenCreated()
			.and(brjsTemplate).hasDir("conf")
			.and(brjsTemplate).containsFileWithContents("conf/some.conf", "some config")
			.and(brjsTemplate).containsFileWithContents("conf/more.conf", "more config")
			.and(brjs).hasDir("conf")
			.and(brjs).containsFileWithContents("conf/some.conf", "custom config");
		when(brjs).populate();
		then(brjs).fileHasContents("conf/some.conf", "custom config")
			.and(brjs).fileHasContents("conf/more.conf", "more config");
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
	
	@Test
	public void locateAncestoreNodeWorksWhenTheModelHasntBeenPrimed() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/src")).containsFile("Class.js");
		// we can't check the actual node or talk about any nodes since brjs.app('myApp') etc would cause the node to be discovered and we need to keep an empty node tree
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade/src/Class.js"), App.class);
	}
	
	@Test
	public void locateAncestoreNodeWorksWhenTheModelHasntBeenPrimedAndTheFileRepresentsTheNodeType() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/src")).containsFile("Class.js");
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade"), Blade.class);
	}
	
	@Test // this is not a duplicate of the test above even though it may look like it, this test has been seen failing when the above was passing
	public void locateAncestoreNodeWorksWhenTheModelHasntBeenPrimedAndTheFileRepresentsATestPack() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/test-unit")).containsFile("file.txt");
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade/test-unit/file.txt"), TestPack.class);
	}
	
	@Test // this is not a duplicate of the test above even though it may look like it, this test has been seen failing when the above was passing
	public void locateAncestoreNodeWorksWhenTheModelHasntBeenPrimedAndTheFileRepresentsAnSdkNode() throws Exception {
		given(brjs.file("libs/javascript/br/test-unit")).containsFile("file.txt");
		then(brjs).ancestorNodeCanBeFound(brjs.file("libs/javascript/br/test-unit/file.txt"), TestPack.class);
	}
	
	@Test
	public void templateFromConfIsUsedIfTemplateDefinedBothInConfAndSdk() throws Exception {
		given(brjs.file("sdk/templates/foo")).containsFile("file.txt")
		   .and(brjs.file("conf/templates/default/foo")).containsFile("something.txt");
		then(brjs.templateGroup("default").template("foo")).hasFile("something.txt");
	}
	
	@Test
	public void templateFromConfIsUsedIfTemplateOnlyDefinedInConf() throws Exception {
		given(brjs.file("conf/templates/default/foo")).containsFile("something.txt");
		then(brjs.templateGroup("default").template("foo")).hasFile("something.txt");
	}
	
//	@Test
//	public void templateFromSdkIsUsedIfTemplateOnlyDefinedInSdk() throws Exception {
//		given(brjs.file("sdk/templates/foo")).containsFile("file.txt");
//		then(brjs.templateGroup("default").template("foo")).hasFile("file.txt");
//	}
}
