package org.bladerunnerjs.spec.model;

import static org.bladerunnerjs.testing.specutility.SourceModuleDescriptor.*;

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
		given(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.pkg.Class3")
			.and(aspect).containsFiles("resources/config1.xml", "resources/dir/config2.xml");
		then(aspect).hasSourceModules(sourceModule("appns/Class1", "appns.Class1"), sourceModule("appns/Class2", "appns.Class2"), sourceModule("appns/pkg/Class3", "appns.pkg.Class3"))
			.and(aspect).hasAssetLocations("resources/", "src/", "src/appns/", "src/appns/pkg/")
			.and(aspect).sourceModuleHasAssetLocation("appns/Class1", "src/appns/")
			.and(aspect).sourceModuleHasAssetLocation("appns/pkg/Class3", "src/appns/pkg/")
			.and(aspect).assetLocationHasNoDependencies("resources/")
			.and(aspect).assetLocationHasDependencies("src/", "resources/")
			.and(aspect).assetLocationHasDependencies("src/appns/", "src/")
			.and(aspect).assetLocationHasDependencies("src/appns/pkg/", "src/appns/");
	}
	
	@Test
	public void brjsLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(brjsLib).hasClasses("brjslib.Class1", "brjslib.Class2", "brjslib.pkg.Class3")
			.and(brjsLib).containsFiles("resources/config1.xml", "resources/dir/config2.xml");
		then(brjsLib).hasSourceModules(sourceModule("brjslib/Class1", "brjslib.Class1"), sourceModule("brjslib/Class2", "brjslib.Class2"), sourceModule("brjslib/pkg/Class3", "brjslib.pkg.Class3"))
			.and(brjsLib).hasAssetLocations("resources/", "src/", "src/brjslib/", "src/brjslib/pkg/")
			.and(brjsLib).assetLocationHasNoDependencies("resources/")
			.and(brjsLib).assetLocationHasDependencies("src/", "resources/")
			.and(brjsLib).assetLocationHasDependencies("src/brjslib/", "src/")
			.and(brjsLib).assetLocationHasDependencies("src/brjslib/pkg/", "src/brjslib/");
	}
	
	@Ignore
	@Test
	public void brjsThirdpartyLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(brjsThirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js, file2.js")
			.and(brjsThirdpartyLib).containsFiles("file1.js", "file2.js");
		then(brjsThirdpartyLib).hasSourceModules(sourceModule("brjsThirdpartyLib", "file1.js", "file2.js"))
			.and(brjsThirdpartyLib).hasAssetLocations(".")
			.and(brjsThirdpartyLib).assetLocationHasNoDependencies(".");
	}
	
	@Ignore
	@Test
	public void nodeJsLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(nodeJsLib).containsPackageJsonWithMainSourceModule("lib/file1.js")
			.and(nodeJsLib).containsFiles("lib/file1.js", "lib/file2.js");
		then(nodeJsLib).hasSourceModules(sourceModule("nodeJsLib", "lib/file1.js"), sourceModule("nodeJsLib/lib/file1", "lib/file1.js"), sourceModule("nodeJsLib/lib/file2", "lib/file2.js"))
			.and(nodeJsLib).hasAssetLocations(".")
			.and(nodeJsLib).assetLocationHasNoDependencies(".");
	}
}
