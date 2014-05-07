package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SourceModuleTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib brjsLib, brjsThirdpartyLib, nodeJsLib;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			brjsLib = app.jsLib("brjslib");
			brjsThirdpartyLib = app.jsLib("thirdparty-lib");
			nodeJsLib = app.jsLib("nodejs-lib");
	}
	
	@Test
	public void aspectSourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(aspect).hasClasses("appns/Class1", "appns/Class2", "appns/pkg/Class3")
			.and(aspect).containsFiles("resources/config1.xml", "resources/dir/config2.xml", "themes/theme1/style.css");
		then(aspect).hasSourceModules("appns/Class1", "appns/Class2", "appns/pkg/Class3")
			.and(aspect).hasAssetLocations("", "resources", "src", "src-test", "src/appns", "src/appns/pkg")
			.and(aspect).sourceModuleHasAssetLocation("appns/Class1", "src/appns")
			.and(aspect).sourceModuleHasAssetLocation("appns/pkg/Class3", "src/appns/pkg")
			.and(aspect).assetLocationHasDependencies("resources", "themes/theme1")
			.and(aspect).assetLocationHasDependencies("src", "resources")
			.and(aspect).assetLocationHasDependencies("src/appns", "src")
			.and(aspect).assetLocationHasDependencies("src/appns/pkg", "src/appns");
	}
	
	@Test
	public void brjsLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(brjsLib).hasClasses("brjslib/Class1", "brjslib/Class2", "brjslib/pkg/Class3")
			.and(brjsLib).containsFiles("resources/config1.xml", "resources/dir/config2.xml", "themes/theme1/style.css");
		then(brjsLib).hasSourceModules("brjslib/Class1", "brjslib/Class2", "brjslib/pkg/Class3")
			.and(brjsLib).hasAssetLocations("", "resources", "src", "src-test", "src/brjslib", "src/brjslib/pkg")
			.and(brjsLib).assetLocationHasDependencies("resources", "themes/theme1")
			.and(brjsLib).assetLocationHasDependencies("src", "resources")
			.and(brjsLib).assetLocationHasDependencies("src/brjslib", "src")
			.and(brjsLib).assetLocationHasDependencies("src/brjslib/pkg", "src/brjslib");
	}
	
	@Test
	public void brjsThirdpartyLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(brjsThirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js, file2.js\n"+"exports: lib")
			.and(brjsThirdpartyLib).containsFiles("file1.js", "file2.js");
		then(brjsThirdpartyLib).hasSourceModules("thirdparty-lib")
			.and(brjsThirdpartyLib).hasAssetLocations("")
			.and(brjsThirdpartyLib).assetLocationHasNoDependencies("");
	}
	
	@Ignore
	@Test
	public void nodeJsLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(nodeJsLib).containsPackageJsonWithMainSourceModule("lib/file1.js")
			.and(nodeJsLib).containsFiles("lib/file1.js", "lib/file2.js");
		then(nodeJsLib).hasSourceModules("nodeJsLib", "nodeJsLib/lib/file1", "nodeJsLib/lib/file2")
			.and(nodeJsLib).hasAssetLocations(".")
			.and(nodeJsLib).assetLocationHasNoDependencies(".");
	}
}
