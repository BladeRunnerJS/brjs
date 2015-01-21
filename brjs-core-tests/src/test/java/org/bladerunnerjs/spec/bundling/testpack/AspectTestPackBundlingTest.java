package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class AspectTestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack aspectUTs, aspectATs;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			
			aspect = app.aspect("default");
			aspectUTs = aspect.testType("unit").testTech("TEST_TECH");
			aspectATs = aspect.testType("acceptance").testTech("TEST_TECH");
	}
	
	// N A M E S P A C E D - J S
	@Test
	public void weBundleAspectFilesInUTs() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void weBundleAspectFilesInATs() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspectATs).testRefersTo("pkg/test.js", "appns.Class1");
		then(aspectATs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheBladesetSrcFolderHasAHiddenFolder() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(aspect).hasDir("src/.svn")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1");
		then(aspectUTs).bundledFilesEquals(aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void aspectTestsCanDependOnBladesetCode() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1", "appns.bs.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspect.assetLocation("src").file("appns/Class1.js"),
				bladeset.assetLocation("src").file("appns/bs/Class1.js"),
				bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
	
	@Test
	public void aspectTestsCanDependOnBladeCode() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("appns.Class1")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1", "appns.bs.b1.Class1");
		then(aspectUTs).bundledFilesEquals(
				aspect.assetLocation("src").file("appns/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void allTestResourcesAreBundled() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspectUTs).hasNamespacedJsPackageStyle()
			.and(aspectUTs).testRefersTo("pkg/test.js", "appns.Class1")
			.and(aspectUTs).containsResourceFileWithContents("en.properties", "appns.prop=val");
		when(aspectUTs).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"appns.prop\": \"val\"");
	}
}
