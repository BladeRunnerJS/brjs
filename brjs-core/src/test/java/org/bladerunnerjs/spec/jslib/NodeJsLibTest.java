package org.bladerunnerjs.spec.jslib;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class NodeJsLibTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkLib;
	private StringBuffer response = new StringBuffer();
	
	//TODO: once we have a proper NodeJS library plugin remove the thirdparty-lib.manifest creation from these tests
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkLib = brjs.sdkLib("lib");
	}
	
	@Test
	public void librariesWithPackageJsonAreWrappedInADefineBlock() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFile("package.json")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsOrderedTextFragments("define('lib', function(require, exports, module) {",
				"module.exports = function() { };\n",
				"});\n");
	}
	
	@Test
	public void librariesWithPackageJsonAndDotNoDefineFileAreNOTWrappedInADefineBlock() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFile("package.json")
			.and(sdkLib).containsFile(".no-define")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("define('lib', function(require, exports, module) {");
	}
	
	@Test
	public void librariesWithPackageJsonAreGlobalisedUsingExportsConfig() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFile("package.json")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsText("thisLib = require('lib');");
	}
	
	// ---------------------------------------- //
	//TODO: these tests wont be valid when we have proper node.js support
	@Test 
	public void librariesWithEmptyObjectExportsDontCreateInvalidJS() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFile("package.json")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: \"{}\"")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsLines(
				"// lib",
				"define('lib', function(require, exports, module) {",				
				"module.exports = function() { };")
			.and(response).doesNotContainText("{} = require('lib');");
	}
	@Test 
	public void librariesWithEmptyObjectAndWhiteSpaceExportsDontCreateInvalidJS() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
		.and(sdkLib).containsFile("package.json")
		.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: \"  {  }  \"")
		.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).doesNotContainText("= require('lib');");
	}
	// ---------------------------------------- //
}
