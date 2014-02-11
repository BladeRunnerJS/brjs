package org.bladerunnerjs.spec.plugin.bundler.thirdparty;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class BRJSThirdpartyContentPluginTest extends SpecTest {
	
	private App app;
	private Aspect aspect;
	private JsLib thirdpartyLib;
	private JsLib thirdpartyLib2;
	
	private StringBuffer pageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			thirdpartyLib = app.nonBladeRunnerLib("thirdparty-lib");
			thirdpartyLib2 = app.nonBladeRunnerLib("thirdparty-lib2");
	}	
	
	@Test
	public void singleModuleRequestContainsAllFilesForTheModule() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js, file2.js")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "file2 = {}\n")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsText("file1 = {}")
			.and(pageResponse).containsText("file2 = {}");
	}
	
	@Test
	public void singleModuleRequestOnlyContainsListedInManifest() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js, file3.js")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "file2 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file3.js", "file3 = {}\n")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsText("file1 = {}")
			.and(pageResponse).containsText("file3 = {}")
			.and(pageResponse).doesNotContainText("file2 = {}");
	}
	
	@Test
	public void allJsFilesAreIncludedIfManifestDoesntListThem() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js:")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "file2 = {}\n")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsText("file1 = {}")
			.and(pageResponse).containsText("file2 = {}");
	}
	
	@Test
	public void libBundleRequestOnlyContainsFilesForTheLib() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "depends: "+thirdpartyLib2.getName())
			.and(thirdpartyLib).containsFileWithContents("file1.js", "lib1.file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "lib1.file2 = {}\n")
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "js:")
			.and(thirdpartyLib2).containsFileWithContents("file1.js", "lib2.file1 = {}\n")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/bundle.js", pageResponse);
		then(pageResponse).containsText("lib1.file1 = {}")
			.and(pageResponse).containsText("lib1.file2 = {}")
			.and(pageResponse).doesNotContainText("lib2.file1 = {}");
	}
	
	@Test
	public void onlyWhatsInTheManifestIsLoaded_ResourcesInTheLibAreNotTreatedAsSeedFiles() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: file1.js")
			.and(thirdpartyLib).containsFileWithContents("file1.js", "lib1.file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.js", "require('appns.class1')\n")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.html", "appns.class1\n")
			.and(thirdpartyLib).containsFileWithContents("ingoredFile.xml", "appns.class1'")
			.and(aspect).hasClass("appns.class1")
			.and(aspect).indexPageHasContent("require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", pageResponse);
		then(pageResponse).containsText("lib1.file1 = {}")
			.and(pageResponse).doesNotContainText("appns.class1");
	}
	
	@Test
	public void bundleRequestContainsAllModuleBundles() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "depends: "+thirdpartyLib2.getName())
			.and(thirdpartyLib).containsFileWithContents("file1.js", "lib1.file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "lib1.file2 = {}\n")
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "js:")
			.and(thirdpartyLib2).containsFileWithContents("file1.js", "lib2.file1 = {}\n")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("lib1.file1 = {}")
			.and(pageResponse).containsText("lib1.file2 = {}")
			.and(pageResponse).containsText("lib2.file1 = {}");
	}
	
	@Test
	public void scriptsCanResideWithDirectories() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: src1.js, lib/src2.js, lib/dir/src3.js")
			.and(thirdpartyLib).containsFiles("src1.js", "lib/src2.js", "lib/dir/src3.js")
			.and(aspect).indexPageRequires(thirdpartyLib);
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsOrderedTextFragments("src1.js", "lib/src2.js", "lib/dir/src3.js");
	}
	
	@Test
	public void fileNamesThatEndWithADesiredFileNameAreNotIncluded() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "js: lib.js")
			.and(thirdpartyLib).containsFiles("lib.js", "X-lib.js", "Y-lib.js")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("lib.js")
			.and(pageResponse).doesNotContainText("X-lib.js")
			.and(pageResponse).doesNotContainText("Y-lib.js");
	}
	
	@Test
	public void assetsInALibCanBeRequestedIndividually() throws Exception {
		given(thirdpartyLib).containsFileWithContents("/some/lib/dirs/some-file.ext", "some file contents");
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/some/lib/dirs/some-file.ext", pageResponse);
		then(pageResponse).textEquals("some file contents");
	}
	
	@Test
	public void testLibraryResourceForLibraryPresentBothInAppAndSdkIsBundledFromApp() throws Exception
	{
		JsLib appLib = app.nonBladeRunnerLib("myLib");
		JsLib sdkLib = brjs.sdkNonBladeRunnerLib("myLib");
		
		given(appLib).hasBeenCreated()
			.and(appLib).containsFileWithContents("library.manifest", "js: myFile.js")
			.and(appLib).containsFileWithContents("myFile.js", "my file contents")
			.and(sdkLib).hasBeenCreated()
			.and(sdkLib).containsFileWithContents("library.manifest", "js: sdkFile.js")
			.and(sdkLib).containsFileWithContents("sdkFile.js", "sdk file contents");
		when(app).requestReceived("/default-aspect/thirdparty/myLib/myFile.js", pageResponse);
		then(pageResponse).textEquals("my file contents");
	}
	
	@Test
	public void testLibraryResourceRequestCanNotHaveAQueryString() throws Exception
	{
		JsLib appLib = app.nonBladeRunnerLib("myLib");
		
		given(appLib).hasBeenCreated()
			.and(appLib).containsFileWithContents("library.manifest", "js: myFile.js")
			.and(appLib).containsFileWithContents("myFile.js", "my file contents");
		when(app).requestReceived("/default-aspect/thirdparty/myLib/myFile.js?q=1234", pageResponse);
		then(exceptions).verifyException(MalformedRequestException.class);
	}
	
	@Test
	public void weGetAGoodMessageIfTheLibraryDoesntExist() throws Exception
	{
		given(app).hasBeenCreated();
		when(app).requestReceived("/default-aspect/thirdparty/libThatDoesntExist/myFile.js", pageResponse);
		then(exceptions).verifyException(ContentProcessingException.class, "libThatDoesntExist");
	}
	
	@Test
	public void weGetAGoodMessageIfTheFileInTheLibraryDoesntExist() throws Exception
	{
		given(app).hasBeenCreated()
			.and(thirdpartyLib).hasBeenCreated();
		when(app).requestReceived("/default-aspect/thirdparty/thirdparty-lib/myFile.js", pageResponse);
		then(exceptions).verifyException(ContentProcessingException.class, thirdpartyLib.file("myFile.js").getAbsolutePath());
	}
}
