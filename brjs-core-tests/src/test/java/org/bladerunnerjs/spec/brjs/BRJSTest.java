package org.bladerunnerjs.spec.brjs;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.api.spec.engine.SpecTest;

import static org.junit.Assert.*;
import static org.bladerunnerjs.api.BRJS.Messages.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.ValidAppDirFileFilter;
import org.bladerunnerjs.model.events.CommandExecutedEvent;
import org.bladerunnerjs.plugin.brjsconformant.BRJSConformantAssetPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSTest extends SpecTest {
	private NamedDirNode brjsTemplate;
	private App app1;
	private App app2;
	private File secondaryTempFolder = null;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasBeenCreated();
		brjsTemplate = brjs.sdkTemplateGroup("default").template("brjs");
		app1 = brjs.app("app1");
		app2 = brjs.app("app2");
	}
	
	@After
	public void deleteCreatedAppsDirFromTemp() throws IOException {
		if (secondaryTempFolder != null) FileUtils.deleteQuietly(secondaryTempFolder);
	}
	
	@Test
	public void theBrjsConfIsWrittenOnPopulate() throws Exception {
		given(brjsTemplate).hasBeenCreated();
		when(brjs).populate();
		then(brjs).fileHasContents("conf/brjs.conf",
				"defaultFileCharacterEncoding: UTF-8",
				"fileObserver: watching",
				"ignoredPaths: .svn, .git",
				"jettyPort: 7070",
				"loginRealm: BladeRunnerLoginRealm",
				"orderedPlugins:",
				"   AssetPlugin:", 
				"   - ThirdpartyAssetPlugin",
				"   - BrowsableNodeSeedLocator",
				"   - BRJSConformantAssetPlugin",
				"   - '*'",
				"   ContentPlugin:", 
				"   - I18nContentPlugin",
				"   - ThirdpartyContentPlugin",
				"   - CommonJsContentPlugin",
				"   - NamespacedJsContentPlugin",
				"   - '*'");
	}
	
	@Test
	public void observersArentNotifiedOfEventsThatArentBeneathThem() throws Exception {
		given(observer).observing(app2);
		when(app1).create();
		then(observer).noNotifications();
	}
	
	@Test
	public void commandExecutedEventIsEmitted() throws Exception {
		given(observer).observing(brjs);
		when(brjs).runCommand("help");
		then(observer).notified(CommandExecutedEvent.class, brjs);
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
	public void locateAncestorNodeWorksWhenTheModelHasntBeenPopulated() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/src")).containsFile("Class.js");
		// we can't check the actual node or talk about any nodes since brjs.app('myApp') etc would cause the node to be discovered and we need to keep an empty node tree
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade/src/Class.js"), App.class);
	}
	
	@Test
	public void locateAncestorNodeWorksWhenTheModelHasntBeenPopulatedAndTheFileRepresentsTheNodeType() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/src")).containsFile("Class.js")
			.and(brjs.file("apps/app1")).containsFile("app.conf");
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade"), Blade.class);
	}
	
	@Test // this is not a duplicate of the test above even though it may look like it, this test has been seen failing when the above was passing
	public void locateAncestorNodeWorksWhenTheModelHasntBeenPopulatedAndTheFileRepresentsATestPack() throws Exception {
		given(brjs.file("apps/app1/blades/myBlade/test-unit")).containsFile("file.txt")
			.and(brjs.file("apps/app1/")).containsFileWithContents("app.conf", "");
		then(brjs).ancestorNodeCanBeFound(brjs.file("apps/app1/blades/myBlade/test-unit/file.txt"), TestPack.class);
	}
	
	@Test // this is not a duplicate of the test above even though it may look like it, this test has been seen failing when the above was passing
	public void locateAncestorNodeWorksWhenTheModelHasntBeenPopulatedAndTheFileRepresentsAnSdkNode() throws Exception {
		given(brjs.file("sdk/libs/javascript/br/test-unit")).containsFile("file.txt");
		then(brjs).ancestorNodeCanBeFound(brjs.file("sdk/libs/javascript/br/test-unit/file.txt"), TestPack.class);
	}
	
	@Test
	public void locateAncestorNodeWorksReturnsNullIfTheNodeOfTheRequiredTypeCannotBeFound() throws Exception {
		given(brjs).containsFile("apps/file.txt");
		then(brjs).ancestorNodeCannotBeFound(brjs.file("apps/file.txt"), App.class);
	}
	
	@Test
	public void appsFolderIsTheActiveAppsFolderItExists() throws Exception {
		given(testRootDirectory).containsFolder("apps")
			.and(brjs).hasBeenCreatedWithWorkingDir(testRootDirectory);
		when(brjs.app("app1")).create();
		then(brjs).hasDir("apps/app1");	
	}
	
	@Test
	public void appsFolderInTheParentOfTheWorkingDirIsUsedIfNextToSdk() throws Exception {
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		given(secondaryTempFolder).containsFolder("apps/dir1/dir2/dir3")
			.and(secondaryTempFolder).containsFolder("sdk")
			.and(brjs).hasBeenCreatedWithWorkingDir(new File(secondaryTempFolder, "apps/dir1/dir2/dir3"));
		when(brjs.app("app1")).create();
		then(secondaryTempFolder).containsDir("apps/app1");
	}
	
	@Test
	public void appsDirNextToSdkIsUsedIfWorkingDirIsBrjsDir() throws Exception {
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		given(brjs).hasBeenCreated();
		when(brjs.app("app1")).create();
		then(testRootDirectory).containsDir("apps/app1");
	}
	
	@Test
	public void workingDirIsusedIfNoOtherDirectoryCanBeFoundToUseForApps() throws Exception {
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		given(brjs).hasBeenCreatedWithWorkingDir(secondaryTempFolder);
		when(brjs.app("app1")).create();
		then(secondaryTempFolder).containsDir("app1");
	}
	
	@Test
	public void infoMessageIsLoggedWhenAppsDirectoryIsDiscovered() throws Exception {
		given(testRootDirectory).containsFolder("apps")
			.and(logging).enabled()
			.and(app1).hasBeenCreated()
			.and(app1).containsFiles(AppConf.FILE_NAME, "index.html");
		when(brjs).hasBeenCreated()
			.and(brjs).discoverUserApps();
		then(logging).infoMessageReceived(BRJS_LOCATION, brjs.dir().getAbsolutePath())
			.and(logging).infoMessageReceived(APPS_FOLDER_FOUND, testRootDirectory.getAbsolutePath() + File.separator + "apps")
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(APPS_DISCOVERED, "User", app1.getName());
	}
	
	@Test
	public void infoMessageIsLoggedContainingBRJSLocation() throws Exception {
		given(logging).enabled();
		when(brjs).hasBeenCreated()
			.and(brjs).discoverUserApps();
		then(logging).infoMessageReceived(BRJS_LOCATION, brjs.dir().getAbsolutePath())
			.and(logging).infoMessageReceived(APPS_FOLDER_FOUND, brjs.dir().getAbsolutePath() + File.separator + "apps")
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(NO_APPS_DISCOVERED, "user");
			
	}
	
	@Test
	public void debugMessageIsLoggedWhenImplicitRequirePrefixesAreUsed() throws Exception {
		App myApp = brjs.app("myApp");
		Blade blade = myApp.defaultBladeset().blade("b1");
		Aspect aspect = myApp.defaultAspect();
		given(blade).containsFile("src/pkg/Class.js")
			.and(aspect).indexPageRequires("appns/b1/pkg/Class")
			.and(logging).enabled();
		when(aspect).bundleSetGenerated();
		then(logging).debugMessageReceived(BRJSConformantAssetPlugin.IMPLICIT_PACKAGE_USED, "apps/myApp/blades/b1/src", "appns/b1", "appns/b1")
			.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void appsIsntRequiredIfCommandIsRunFromInsideAnApp() throws Exception {
		given(testRootDirectory).containsFolder("myprojects")
			.and(testRootDirectory).containsFolder("myprojects/myapp")
			.and(testRootDirectory).containsFileWithContents("myprojects/myapp/app.conf", "requirePrefix: myapp")
			.and(brjs).hasBeenCreatedWithWorkingDir( new File(testRootDirectory, "myprojects/myapp") );
		then(brjs).hasApps("myapp");
	}
	
	@Test
	public void appsCanBecreatedIfCommandIsRunFromInsideAnAppWithoutApps() throws Exception {
		given(testRootDirectory).containsFolder("myprojects")
			.and(testRootDirectory).containsFolder("myprojects/myapp")
			.and(testRootDirectory).containsFileWithContents("myprojects/myapp/app.conf", "requirePrefix: myapp")
			.and(brjs).hasBeenCreatedWithWorkingDir( new File(testRootDirectory, "myprojects/myapp") )
			.and(brjs.sdkTemplateGroup("default").template("app")).containsEmptyFile("index.html");
		when(brjs.app("anotherapp")).populate("default")
			.and((brjs.app("anotherapp"))).containsFileWithContents("app.conf", "");
		then(brjs).hasApps("anotherapp", "myapp")
			.and(testRootDirectory).containsDir("myprojects/myapp")
			.and(testRootDirectory).containsDir("myprojects/anotherapp");
	}
	
	@Test
	public void appsDirectoryIsCreatedIfItDoesNotExistAndBrjsIsRunFromSdkDir() throws Exception {
		given(brjs).doesNotContainFolder("apps")
			.and(brjs).hasBeenAuthenticallyCreatedWithWorkingDir(new File(testRootDirectory, "sdk"));
		when(brjs.app("app1")).create();
		then(testRootDirectory).containsDir("apps/app1");
	}
	
	@Test
	public void appsDirectoryIsCreatedIfItDoesNotExistAndBrjsIsRunAndCreatedFromSdkDir() throws Exception {
		File testDir = testRootDirectory;
		testRootDirectory = new File(testRootDirectory, "sdk");
		testRootDirectory.mkdirs();
		given(brjs).doesNotContainFolder("apps")
			.and(brjs).hasBeenAuthenticallyCreatedWithWorkingDir(testRootDirectory);
		when(brjs.app("app1")).create();
		then(testDir).containsDir("apps/app1");
	}
	
	@Test
	public void onlyDirsWithAppConfAreDetectedAsAppsWhenTheCommandsIsRunFromInsideAnApp() throws Exception {
		given(testRootDirectory).containsFolder("myprojects")
    		.and(testRootDirectory).containsFolder("myprojects/nonapp")
    		.and(testRootDirectory).containsFolder("myprojects/myapp")
    		.and(testRootDirectory).containsFileWithContents("myprojects/myapp/app.conf", "requirePrefix: myapp")
    		.and(brjs).hasBeenCreatedWithWorkingDir( new File(testRootDirectory, "myprojects/myapp") );
    	then(brjs).hasApps("myapp");
	}
	
	@Test
	public void warningIsLoggedIfNonAppDirsAreDiscovered() throws Exception {
		given(logging).enabled()
			.and(logging).echoEnabled();
		when(testRootDirectory).containsFolder("myprojects")
    		.and(testRootDirectory).containsFolder("myprojects/nonapp")
    		.and(testRootDirectory).containsFolder("myprojects/myapp")
    		.and(testRootDirectory).containsFileWithContents("myprojects/myapp/app.conf", "requirePrefix: myapp")
    		.and(brjs).hasBeenCreatedWithWorkingDir( new File(testRootDirectory, "myprojects/myapp") )
    		.and(brjs).discoverUserApps();
    	then(logging).warnMessageReceived(ValidAppDirFileFilter.NON_APP_DIR_FOUND_MSG, new File(testRootDirectory, "myprojects/nonapp").getAbsolutePath(), new File(testRootDirectory, "myprojects").getAbsolutePath())
    		.and(logging).otherMessagesIgnored();
	}
	
	@Test
	public void newFilesInGeneratedDirDoesntIncrementSdkVersion() throws Exception
	{
		long sdkFileVersion = brjs.getFileModificationRegistry().getFileVersion(testRootDirectory);
		when(testRootDirectory).containsFile("generated/foo");
		assertEquals(sdkFileVersion, brjs.getFileModificationRegistry().getFileVersion(testRootDirectory));
	}
	
}
