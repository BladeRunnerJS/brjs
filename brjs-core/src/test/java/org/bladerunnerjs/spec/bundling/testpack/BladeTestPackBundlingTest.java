package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladeTestPackBundlingTest extends SpecTest
{
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladeUTs, bladeATs;
	
	private JsLib bootsrapThirdparty, browserModules, appThirdparty,sdkJsLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeUTs = blade.testType("unit").testTech("TEST_TECH");
			bladeATs = blade.testType("acceptance").testTech("TEST_TECH");
			
			bootsrapThirdparty = brjs.sdkNonBladeRunnerLib("br-bootstrap");
			browserModules = brjs.sdkNonBladeRunnerLib("browser-modules");
			appThirdparty = app.nonBladeRunnerLib("appThirdparty");
			sdkJsLib = brjs.sdkLib("br");
	}
	
	// N A M E S P A C E D - J S
	@Test
	public void weBundleBladeFilesInUTs() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeUTs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeFilesInATs() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeSrcTestContentsInUTs() throws Exception {		
		given(blade).hasNamespacedJsPackageStyle()
			.and(bladeUTs).containsFile("src-test/pkg/Util.js")
			.and(blade).hasClasses("appns.bs.b1.Class1")
			.and(bladeUTs).classExtends("pkg.Util", "appns.bs.b1.Class1")
			.and(bladeUTs).testRefersTo("pkg/test.js", "pkg.Util");
		then(bladeUTs).bundledFilesEquals(
			blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
			bladeUTs.testSource().file("pkg/Util.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheBladeSrcFolderHasAHiddenFolder() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).hasDir("src/.svn")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
			blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
			blade.assetLocation("src").file("appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weCanBundleBladesetAndBladeFilesInATs() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(bladeset).hasClasses("appns.bs.Class1", "appns.bs.Class2")
			.and(bladeset).classDependsOn("appns.bs.Class1", "appns.bs.Class2")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2", "appns.bs.Class1")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"),
				bladeset.assetLocation("src").file("appns/bs/Class1.js"),
				bladeset.assetLocation("src").file("appns/bs/Class2.js"));
	}
	
	@Test
	public void weCanBundleAspectSrcCodeInATs() throws Exception {
		given(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).classFileHasContent("appns.Class1", "aspect content")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.Class1", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"),
				aspect.assetLocation("src").file("appns/Class1.js"));
	}
	
	@Test
	public void weCanBundleAppThirdpartyLibrariesInATs() throws Exception {
		given(appThirdparty).hasNamespacedJsPackageStyle()
			.and(appThirdparty).containsFileWithContents("library.manifest", "js: src1.js, src2.js\n"+"exports: browserModules")
			.and(appThirdparty).containsFiles("src1.js", "src2.js", "src3.js")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "'" + appThirdparty.getName() + "'", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"),
				appThirdparty.dir());
	}

	@Test
	public void weCanBundleSdkJsLibIncludingSdkThirdpartyBootstrapInATs() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle()
			.and(sdkJsLib).classFileHasContent("br.namespaced.Class1", "sdk class1 contents")
			.and(browserModules).containsFileWithContents("library.manifest", "js: file.js\n"+"exports: browserModules")
			.and(browserModules).containsFileWithContents("file.js", "browser-modules-content")
			.and(bootsrapThirdparty).containsFileWithContents("library.manifest", "depends: browser-modules\n"+"exports: bootsrapThirdparty")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "br.namespaced.Class1", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).bundledFilesEquals(
				blade.assetLocation("src").file("appns/bs/b1/Class1.js"),
				blade.assetLocation("src").file("appns/bs/b1/Class2.js"),
				sdkJsLib.assetLocation("src").file("br/namespaced/Class1.js"),
				bootsrapThirdparty.dir(),
				browserModules.dir());
	}
	
}
