package org.bladerunnerjs.spec.model;

import static org.bladerunnerjs.testing.specutility.SourceModuleDescriptor.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SourceModuleTest extends SpecTest {
	private App app;
	private JsLib thirdpartyLib;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			thirdpartyLib = app.jsLib("thirdpartyLib");
	}
	
	@Ignore
	@Test
	public void thirdpartyLibrarySourceModulesAndAssetLocationsAreAsExpected() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js, file2.js")
			.and(thirdpartyLib).containsFiles("file1.js", "file2.js");
		then(thirdpartyLib).hasSourceModules(sourceModule("thirdpartyLib", "file1.js", "file2.js"))
			.and(thirdpartyLib).hasAssetLocations(".")
			.and(thirdpartyLib).assetLocationHasNoDependencies(".");
	}
}
