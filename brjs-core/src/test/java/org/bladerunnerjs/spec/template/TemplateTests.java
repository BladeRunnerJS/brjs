package org.bladerunnerjs.spec.template;

import java.util.Arrays;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class TemplateTests extends SpecTest
{
	
	App app;
	Aspect defaultAspect;
	Bladeset bladeset;
	Blade blade;
	Workbench workbench;
	JsLib userLib, thirdpartyLib;
	Blade bladeInDefaultBladeset;
	Aspect anotherAspect;
	
	@Before
	public void initTestObjects() throws Exception {		
		given(brjs).automaticallyFindsAllPlugins()
			.and(brjs).hasBeenCreated()
			.and(brjs).usesProductionTemplates();
		
		DirNode appJars = brjs.appJars();
		given(appJars).containsFile("some-lib.jar");
		
		app = brjs.app("app");
		defaultAspect = app.defaultAspect();
		anotherAspect = app.aspect("another");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
		userLib = app.jsLib("userlib");
		thirdpartyLib = app.jsLib("thirdpartyLib");
		bladeInDefaultBladeset = app.defaultBladeset().blade("b1");
	}
	
	
	//TODO: make these tests more restrictive so any new files in subfolders cause the tests to fail
	
	
	@Test
	public void brjsHasCorrectTemplate() throws Exception {
		when(brjs).populate();
		then(brjs).hasFilesAndDirs(
				Arrays.asList("conf/test-runner.conf", "conf/users.properties"),
				Arrays.asList("apps", "conf", "sdk")
		);
	}

	@Test
	public void appAndDefaultAspectHasCorrectTemplate() throws Exception {
		when(brjs).runCommand("create-app", "app", "appns");
		then(app).hasFilesAndDirs(
				Arrays.asList("app.conf", "index.html", "resources/aliases.xml", "src/App.js", "themes/common/style.css"),
				Arrays.asList("WEB-INF", "libs", "resources", "src", "unbundled-resources", "themes", "test-unit", "test-acceptance")
		);
	}
	
	@Test
	public void defaultAspectSrcHasCorrectClasses() throws Exception {
		when(brjs).runCommand("create-app", "app", "appns");
		then(app).hasFilesAndDirs(
				Arrays.asList("app.conf", "index.html", "src/App.js"),
				Arrays.asList("WEB-INF", "libs", "resources", "src", "unbundled-resources", "themes", "test-unit", "test-acceptance")
		).and(app).doesNotHaveFile("src/DefaultClass.js");
	}

	@Test
	public void createdAspectHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-aspect", "app", "another");
		then(anotherAspect).hasFilesAndDirs(
				Arrays.asList("index.html", "resources/aliases.xml", "src/App.js", "themes/common/style.css"),
				Arrays.asList("resources", "src", "unbundled-resources", "themes", "test-unit", "test-acceptance")
		);
	}
	
	@Test
	public void aspectUnitTestsHasCorrectTemplate() throws Exception {
		when(brjs).runCommand("create-app", "app", "appns");
		then(defaultAspect.testType("unit")).hasFilesAndDirs(
				Arrays.asList("jsTestDriver.conf", "resources/aliases.xml", "tests/AppTest.js", ".gitignore"),
				Arrays.asList("tests", "resources")
		);
	}
	
	@Test
	public void aspectAcceptanceTestsHasCorrectTemplate() throws Exception {
		when(brjs).runCommand("create-app", "app", "appns");
		then(defaultAspect.testType("acceptance")).hasFilesAndDirs(
				Arrays.asList("jsTestDriver.conf", "resources/aliases.xml", "tests/AppTest.js", ".gitignore"),
				Arrays.asList("tests", "resources")
		);
	}
	
	@Test
	public void bladesetHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-bladeset", "app", "bs");
		then(bladeset).hasFilesAndDirs(
				Arrays.asList("src/BsClass.js", "themes/common/style.css"),
				Arrays.asList("resources", "resources/html", "src", "test-unit", "themes")
		).and(bladeset).fileContentsContains("resources/i18n/en.properties", "appns.bs.hello.world");
	}
	
	@Test
	public void bladesetTestsHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-bladeset", "app", "bs");
		then(bladeset.testType("unit")).hasFilesAndDirs(
				Arrays.asList("jsTestDriver.conf", "resources/aliases.xml", "tests/BsClassTest.js", ".gitignore"),
				Arrays.asList("tests", "resources")
		);
	}
	
	@Test	
	public void bladeHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns")
			.and(brjs).commandHasBeenRun("create-bladeset", "app", "bs");
		when(brjs).runCommand("create-blade", "app", "bs", "b1");
		then(blade).hasFilesAndDirs(
				Arrays.asList("src/B1ViewModel.js", "themes/common/style.css"),
				Arrays.asList("resources", "resources/html", "src", "test-unit", "test-acceptance", "workbench", "themes")
		);
	}
	
	@Test
	public void bladeInDefaultBladesetHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-blade", "app", "default", "b1");
		then(app).hasFilesAndDirs(
			Arrays.asList("app.conf", "index.html"),
			Arrays.asList("WEB-INF", "src", "test-unit", "test-acceptance", "themes", "unbundled-resources", "libs", "resources", "blades")
		).and(bladeInDefaultBladeset).hasFilesAndDirs(
				Arrays.asList("src/B1ViewModel.js", "themes/common/style.css"),
				Arrays.asList("resources", "resources/html", "src", "test-unit", "test-acceptance", "workbench", "themes")
		).and(bladeInDefaultBladeset).fileContentsContains("resources/html/view.html", "appns.b1.view-template");
	}
	
	@Test
	public void bladeUnitTestsHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns")
			.and(brjs).commandHasBeenRun("create-bladeset", "app", "bs");
		when(brjs).runCommand("create-blade", "app", "bs", "b1");
		then(blade.testType("unit")).hasFilesAndDirs(
				Arrays.asList("jsTestDriver.conf", "resources/aliases.xml", "tests/B1ViewModelTest.js", ".gitignore"),
				Arrays.asList("tests", "resources")
		);
	}
	
	@Test
	public void bladeAcceptanceTestsHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns")
			.and(brjs).commandHasBeenRun("create-bladeset", "app", "bs");
		when(brjs).runCommand("create-blade", "app", "bs", "b1");
		then(blade.testType("acceptance")).hasFilesAndDirs(
				Arrays.asList("jsTestDriver.conf", "resources/aliases.xml", "tests/B1ViewModelTest.js", ".gitignore"),
				Arrays.asList("tests", "resources")
		);
	}
	
	@Test
	public void workbenchHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns")
			.and(brjs).commandHasBeenRun("create-bladeset", "app", "bs");
		when(brjs).runCommand("create-blade", "app", "bs", "b1");
		then(workbench).hasFilesAndDirs(
				Arrays.asList("index.html", "resources/aliases.xml", "resources/style/workbench.css"),
				Arrays.asList("resources", "resources/html", "resources/style/", "src")
		);
	}
	
	@Test
	public void brLibHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-library", "app", "userlib");
		then(userLib).hasFilesAndDirs(
				Arrays.asList("br-lib.conf", 
						"src/Userlib.js", 
						"test-unit/jsTestDriver.conf", 
						"test-unit/resources/aliases.xml",
						"test-unit/tests/UserlibTest.js", 
						"test-unit/.gitignore"),
				Arrays.asList("src", "test-unit"))
			.and(userLib).fileContentsContains("src/Userlib.js", "var Userlib = {}");
	}
	
	@Test //TODO: thirdparty libraries should have an improved template - the template exists, but the command doesnt use it when creating thirdparty libraries
	public void thirdpartyLibHasCorrectTemplate() throws Exception {
		given(brjs).commandHasBeenRun("create-app", "app", "appns");
		when(brjs).runCommand("create-library", "app", "thirdpartyLib", "-t", "thirdparty");
		then(thirdpartyLib).hasFilesAndDirs(
				Arrays.asList("thirdparty-lib.manifest"),
				Arrays.asList()
		);
	}
	
}
