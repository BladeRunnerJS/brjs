package org.bladerunnerjs.spec.bundling.testpack;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladeTestPackBundlingTest extends SpecTest
{
	private App app;
	private Bladeset bladeset;
	private Blade blade;
	private TestPack bladeUTs, bladeATs;
	private StringBuffer response = new StringBuffer();
	
	private JsLib bootsrapThirdparty, browserModules, appThirdparty,sdkJsLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			bladeUTs = blade.testType("unit").testTech("TEST_TECH");
			bladeATs = blade.testType("acceptance").testTech("TEST_TECH");
			
			bootsrapThirdparty = brjs.sdkLib("br-bootstrap");
			browserModules = brjs.sdkLib("browser-modules");
			appThirdparty = app.jsLib("appThirdparty");
			sdkJsLib = brjs.sdkLib("br");
	}
	
	// N A M E S P A C E D - J S
	@Test
	public void weBundleBladeFilesInUTs() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeUTs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/Class1.js"),
				blade.file("src/appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeFilesInATs() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/Class1.js"),
				blade.file("src/appns/bs/b1/Class2.js"));
	}
	
	@Test
	public void weBundleBladeSrcTestContentsInUTs() throws Exception {		
		given(blade).hasNamespacedJsPackageStyle()
			.and(bladeUTs).containsFile("src-test/appns/bs/b1/Util.js")
			.and(blade).hasClasses("appns.bs.b1.Class1")
			.and(bladeUTs).classExtends("appns.bs.b1.Util", "appns.bs.b1.Class1")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Util");
		then(bladeUTs).srcOnlyBundledFilesEquals(
			blade.file("src/appns/bs/b1/Class1.js"),
			bladeUTs.file("src-test/appns/bs/b1/Util.js"));
	}
	
	@Test
	public void noExceptionsAreThrownIfTheBladeSrcFolderHasAHiddenFolder() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).hasDir("src/.svn")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).srcOnlyBundledFilesEquals(
			blade.file("src/appns/bs/b1/Class1.js"),
			blade.file("src/appns/bs/b1/Class2.js"));
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
		then(bladeATs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/Class1.js"),
				blade.file("src/appns/bs/b1/Class2.js"),
				bladeset.file("src/appns/bs/Class1.js"),
				bladeset.file("src/appns/bs/Class2.js"));
	}
	
	@Test
	public void weCanBundleAppThirdpartyLibrariesInATs() throws Exception {
		given(appThirdparty).hasNamespacedJsPackageStyle()
			.and(appThirdparty).containsFileWithContents("thirdparty-lib.manifest", "js: src1.js, src2.js\n"+"exports: browserModules")
			.and(appThirdparty).containsFiles("src1.js", "src2.js", "src3.js")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "'" + appThirdparty.getName() + "'", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/Class1.js"),
				blade.file("src/appns/bs/b1/Class2.js"),
				appThirdparty.dir());
	}

	@Test
	public void weCanBundleSdkJsLibIncludingSdkThirdpartyBootstrapInATs() throws Exception {
		given(sdkJsLib).hasNamespacedJsPackageStyle()
			.and(sdkJsLib).classFileHasContent("br.namespaced.Class1", "sdk class1 contents")
			.and(browserModules).containsFileWithContents("thirdparty-lib.manifest", "js: file.js\n"+"exports: browserModules")
			.and(browserModules).containsFileWithContents("file.js", "browser-modules-content")
			.and(bootsrapThirdparty).containsFileWithContents("thirdparty-lib.manifest", "depends: browser-modules\n"+"exports: bootsrapThirdparty")
			.and(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.Class1", "appns.bs.b1.Class2")
			.and(blade).classDependsOn("appns.bs.b1.Class1", "br.namespaced.Class1", "appns.bs.b1.Class2")
			.and(bladeATs).testRefersTo("pkg/test.js", "appns.bs.b1.Class1");
		then(bladeATs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/Class1.js"),
				blade.file("src/appns/bs/b1/Class2.js"),
				sdkJsLib.file("src/br/namespaced/Class1.js"),
				bootsrapThirdparty.dir(),
				browserModules.dir());
	}
	
	@Test
	public void bladesCanNotDependOnTestClasses() throws Exception {
		given(blade).hasNamespacedJsPackageStyle()
			.and(blade).hasClasses("appns.bs.b1.BladeClass")
			.and(bladeUTs).containsFile("src-test/pkg/Util.js")
			.and(blade).classDependsOn("appns.bs.b1.BladeClass", "pkg.Util")
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.BladeClass");
		then(bladeUTs).srcOnlyBundledFilesEquals(
				blade.file("src/appns/bs/b1/BladeClass.js"));
	}
	
	@Test
	public void encapsulatedStyleSourceModulesAreGlobalizedIfTheyAreUsedWithinANamespacedSourceClass() throws Exception {	
		given(blade).hasCommonJsPackageStyle()
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(bladeUTs).hasNamespacedJsPackageStyle()			
			.and(bladeUTs).testRefersTo("pkg/test.js", "appns.bs.b1.Class");
		when(bladeUTs).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(bladeUTs).srcOnlyBundledFilesEquals( blade.file("src/appns/bs/b1/Class.js") )
			.and(response).containsText( "appns.bs.b1.Class = require('appns/bs/b1/Class');" );
	}
}
