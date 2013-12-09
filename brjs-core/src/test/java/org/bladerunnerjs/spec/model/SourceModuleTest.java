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
			brjsLib = app.jsLib("brjsLib");
			brjsThirdpartyLib = app.jsLib("thirdpartyLib");
			nodeJsLib = app.jsLib("nodeJsLib");
	}
	
	// TODO: prefix all require paths with /appns
	@Test
	public void aspectSourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(aspect).hasClasses("Class1", "Class2", "pkg.Class3")
			.and(aspect).containsFiles("resources/config1.xml", "resources/dir/config2.xml");
		then(aspect).hasSourceModules(sourceModule("/Class1", "Class1"), sourceModule("/Class2", "Class2"), sourceModule("/pkg/Class3", "pkg.Class3"))
			.and(aspect).hasAssetLocations("resources/", "src/", "src/pkg/")
			.and(aspect).sourceModuleHasAssetLocation("/Class1", "src/")
			.and(aspect).sourceModuleHasAssetLocation("/pkg/Class3", "src/pkg/")
			.and(aspect).assetLocationHasNoDependencies("resources/")
			.and(aspect).assetLocationHasDependencies("src/", "resources/")
			.and(aspect).assetLocationHasDependencies("src/pkg/", "src/");
	}
	
	// TODO: prefix all require paths with /appns
	@Test
	public void aspectSourceModulesCanHandleDeepDirectoryStructures() throws Exception {
		given(aspect).hasClasses("Class1", "pkg1.Class2", "pkg1.pkg2.Class3")
			.and(aspect).containsFiles("resources/config1.xml", "resources/dir/config2.xml");
		then(aspect).hasSourceModules(sourceModule("/Class1", "Class1"), sourceModule("/pkg1/Class2", "pkg1.Class2"), sourceModule("/pkg1/pkg2/Class3", "pkg1.pkg2.Class3"))
			.and(aspect).hasAssetLocations("resources/", "src/", "src/pkg1/", "src/pkg1/pkg2/")
			.and(aspect).sourceModuleHasAssetLocation("/Class1", "src/")
			.and(aspect).sourceModuleHasAssetLocation("/pkg1/Class2", "src/pkg1/")
			.and(aspect).sourceModuleHasAssetLocation("/pkg1/pkg2/Class3", "src/pkg1/pkg2/")
			.and(aspect).assetLocationHasNoDependencies("resources/")
			.and(aspect).assetLocationHasDependencies("src/", "resources/")
			.and(aspect).assetLocationHasDependencies("src/pkg1/", "src/")
			.and(aspect).assetLocationHasDependencies("src/pkg1/pkg2/", "src/pkg1/");
	}
	
	// TODO: prefix all require paths with /brjsLib
	@Test
	public void brjsLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(brjsLib).hasClasses("Class1", "Class2", "pkg.Class3")
			.and(brjsLib).containsFiles("resources/config1.xml", "resources/dir/config2.xml");
		then(brjsLib).hasSourceModules(sourceModule("/Class1", "Class1"), sourceModule("/Class2", "Class2"), sourceModule("/pkg/Class3", "pkg.Class3"))
			.and(brjsLib).hasAssetLocations("", "resources/", "src/", "src/pkg/")
			.and(brjsLib).assetLocationHasNoDependencies("resources/")
			.and(brjsLib).assetLocationHasDependencies("src/", "resources/")
			.and(brjsLib).assetLocationHasDependencies("src/pkg/", "src/");
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
