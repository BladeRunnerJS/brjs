package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.junit.Before;
import org.junit.Test;

public class ThirdpartyContentPluginTest extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;
	private JsLib thirdpartyLib2;
	private BladerunnerConf bladerunnerConf;
	private StringBuffer pageResponse = new StringBuffer();
	private StringBuffer cssResponse = new StringBuffer();
	private StringBuffer jsResponse = new StringBuffer();
	private SdkJsLib sdkLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.jsLib("thirdparty-lib");
			thirdpartyLib2 = app.jsLib("thirdparty-lib2");
			bladerunnerConf = brjs.bladerunnerConf();
			sdkLib = brjs.sdkLib("lib");
	}	
	
	@Test
	public void singleModuleRequestContainsAllFilesForTheModule() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file1.js, file2.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("file1.js", "file2.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("file1.js", "file2.js");
	}
	
	@Test
	public void singleModuleRequestOnlyContainsListedInManifest() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file1.js, file3.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("file1.js", "file2.js", "file3.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("file1.js", "file3.js")
			.and(pageResponse).doesNotContainText("file2.js");
	}
	
	@Test
	public void allJsFilesAreIncludedIfManifestDoesntListThem() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(thirdpartyLib).containsFiles("file1.js", "file2.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("file1.js", "file2.js");
	}
	
	@Test
	public void libBundleRequestOnlyContainsFilesForTheLib() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "depends: "+thirdpartyLib2.getName()+"\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("lib1-file1.js", "lib1-file2.js")
			.and(thirdpartyLib2).containsFileWithContents("thirdparty-lib.manifest", "exports: lib")
			.and(thirdpartyLib2).containsFiles("lib2-file1.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("lib1-file1.js", "lib1-file2.js")
			.and(pageResponse).doesNotContainText("lib2-file1.js");
	}
	
	@Test
	public void onlyWhatsInTheManifestIsLoaded_ResourcesInTheLibAreNotTreatedAsSeedFiles() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file1.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFile("file1.js")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.js", "require('appns.class1')\n")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.html", "appns.class1\n")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.xml", "appns.class1'")
			.and(aspect).hasClass("appns/class1")
			.and(aspect).indexPageHasContent("require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsText("file1.js")
			.and(pageResponse).doesNotContainText("appns.class1");
	}
	
	@Test
	public void bundleRequestContainsAllModuleBundles() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "depends: "+thirdpartyLib2.getName()+"\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("lib1-file1.js", "lib1-file2.js")
			.and(thirdpartyLib2).containsFileWithContents("thirdparty-lib.manifest", "exports: lib\n"+"exports: lib")
			.and(thirdpartyLib2).containsFiles("lib2-file1.js")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("lib2-file1", "lib1-file1", "lib1-file2");
	}
	
	@Test
	public void scriptsCanResideWithDirectories() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: src1.js, lib/src2.js, lib/dir/src3.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("src1.js", "lib/src2.js", "lib/dir/src3.js")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("src1.js", "lib/src2.js", "lib/dir/src3.js");
	}
	
	@Test
	public void fileNamesThatEndWithADesiredFileNameAreNotIncluded() throws Exception {
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: lib.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFiles("lib.js", "X-lib.js", "Y-lib.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("lib.js")
			.and(pageResponse).doesNotContainText("X-lib.js")
			.and(pageResponse).doesNotContainText("Y-lib.js");
	}
	
	@Test
	public void testLibraryResourceRequestCanNotHaveAQueryString() throws Exception
	{
		JsLib appLib = app.jsLib("myLib");
		
		given(appLib).hasBeenCreated()
			.and(appLib).containsFileWithContents("thirdparty-lib.manifest", "js: myFile.js")
			.and(appLib).containsFile("myFile.js");
		when(aspect).requestReceivedInDev("thirdparty/myLib/myFile.js?q=1234", pageResponse);
		then(exceptions).verifyException(MalformedRequestException.class);
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("file.js", "$£€");
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("$£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("file.js", "$£");
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("$£");
	}
	
	@Test
	public void weCanUseUnicodeFilesWithABomMarkerEvenWhenThisIsNotTheDefaultEncoding() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("UTF-16")
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: file.js\n"+"exports: lib")
			.and(thirdpartyLib).containsFileWithContents("file.js", "$£€");
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("$£€");
	}
	
	@Test
	public void exceptionIsThrownIfAJSFileInTheManifestDoesntExist() throws Exception
	{
		given(app).hasBeenCreated()
			.and(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "js: doesnt-exist.js\n"+"exports: lib");
		when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(exceptions).verifyException(ConfigException.class, "doesnt-exist.js", "brjs-apps/app1/libs/thirdparty-lib/thirdparty-lib.manifest");
	}
	
	@Test
	public void exceptionIsThrownIfADependentLibraryDoesntExist() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageRequires(thirdpartyLib)
			.and(thirdpartyLib).hasBeenCreated()
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "depends: invalid-lib\n"+"exports: lib");
		when(aspect).requestReceivedInDev("thirdparty/bundle.js", pageResponse);
		then(exceptions).verifyException(ConfigException.class, "thirdparty-lib", "invalid-lib");
	}
	
	@Test
	public void allSettingsAreOptional() throws Exception
	{
		given(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "")
    		.and(thirdpartyLib).containsFiles("file1.js")
    		.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
    	when(aspect).requestReceivedInDev("thirdparty/thirdparty-lib/bundle.js", pageResponse);
    	then(pageResponse).containsText("file1.js")
    		.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void librariesWithUmdConfigAreWrappedInADefineBlock() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib\n"+"commonjsDefinition: true")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("define('lib', function(require, exports, module) {",
				"module.exports = function() { };\n");
	}
	
	@Test 
	public void librariesWithEmptyObjectExportsDontCreateInvalidJS() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "some lib content")
			.and(sdkLib).containsFile("package.json")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: \"{}\"")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsLines(
				"// lib",
				"some lib content",
				"",
				"define('lib', function(require, exports, module) {",		
				"module.exports = {};")
			.and(pageResponse).doesNotContainText("{} = require('lib');");
	}
	
	@Test
	public void librariesWithUmdConfigAreNotGlobalisedByDefault() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib")
			.and(aspect).indexPageRequires("lib");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).doesNotContainText("thisLib = require('lib');");
	}
	
	@Test
	public void librariesWithUmdConfigAreNotGlobalisedIfANamespacedJsClassIsNotUsed() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib\n"+"commonjsDefinition: true")
			.and(aspect).hasClass("App")
			.and(aspect).classRequiresThirdpartyLib("App", sdkLib)
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).doesNotContainText("thisLib = require('lib');");
	}
	
	@Test
	public void librariesWithUmdConfigAreGlobalisedIfANamespacedJsClassIsUsed() throws Exception {
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib\n"+"commonjsDefinition: true")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("App")
			.and(aspect).classDependsOnThirdpartyLib("App", sdkLib)
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsText("lib = require('lib');");
	}
	
	@Test
	public void aJsSafeVersionOfTheLibraryNameIsUsedToGlobalisedUmdWrappedLibraries() throws Exception {
		sdkLib = brjs.sdkLib("my-lib");
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib\n"+"commonjsDefinition: true")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("App")
			.and(aspect).classDependsOnThirdpartyLib("App", sdkLib)
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsText("my_lib = require('my-lib');");
	}
	
	@Test
	public void windowIsUsedToGlobaliseLibraries() throws Exception {
		sdkLib = brjs.sdkLib("my-lib");
		given(sdkLib).containsFileWithContents("lib.js", "module.exports = function() { };")
			.and(sdkLib).containsFileWithContents("thirdparty-lib.manifest", "exports: thisLib\n"+"commonjsDefinition: true")
			.and(aspect).hasNamespacedJsPackageStyle()
			.and(aspect).hasClass("App")
			.and(aspect).classDependsOnThirdpartyLib("App", sdkLib)
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", pageResponse);
		then(pageResponse).containsText("window.my_lib");
	}
	
	@Test
	public void unusedCssAssetsFromADependentLibraryAreNotBundled() throws Exception {
		given(thirdpartyLib2).containsFileWithContents("css/file.css", "unused css file")
			.and(thirdpartyLib2).containsFileWithContents("file.js", "thirdparty-lib2 js file")
			.and(thirdpartyLib2).containsFileWithContents("thirdparty-lib.manifest", "")
			.and(thirdpartyLib).containsFileWithContents("thirdparty-lib.manifest", "depends: thirdparty-lib2")
			.and(aspect).hasClass("App")
			.and(aspect).classRequiresThirdpartyLib("App", thirdpartyLib)
			.and(aspect).indexPageRequires("appns/App");
		when(aspect).requestReceivedInDev("css/common/bundle.css", cssResponse)
			.and(aspect).requestReceivedInDev("js/dev/combined/bundle.js", jsResponse);
		then(cssResponse).doesNotContainText("unused css file")
			.and(jsResponse).containsText("thirdparty-lib2 js file");
	}
	
}
