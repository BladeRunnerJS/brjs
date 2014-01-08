package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class ThirdpartyBundlerPluginTest extends SpecTest {
	
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
	public void inDevSeparateJsFileRequestsAreGeneratedByDefault() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "depends:")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests("thirdparty/thirdparty-lib/bundle.js")
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
	}
	
	@Test
	public void inProdASingleBundlerRequestIsGeneratedByDefault() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "depends:")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(aspect).indexPageLoadedInProd(pageResponse, "en_GB");
		then(pageResponse).containsRequests("thirdparty/bundle.js")
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
	}
	
	@Test
	public void noRequestPathsAreGeneratedInDevIfThereAreNoLibraries() throws Exception {
		given(aspect).indexPageHasContent("<@thirdparty.bundle@/>");
		when(aspect).indexPageLoadedInDev(pageResponse, "en_GB");
		then(pageResponse).containsRequests()
			.and(pageResponse).doesNotContainText("<@thirdparty.bundle@/>");
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
	public void bundleRequestContainsAllModuleBundles() throws Exception {
		given(thirdpartyLib).containsFileWithContents("library.manifest", "depends: "+thirdpartyLib2.getName())
			.and(thirdpartyLib).containsFileWithContents("file1.js", "lib1.file1 = {}\n")
			.and(thirdpartyLib).containsFileWithContents("file2.js", "lib1.file2 = {}\n")
			.and(thirdpartyLib2).containsFileWithContents("library.manifest", "js:")
			.and(thirdpartyLib2).containsFileWithContents("file1.js", "lib2.file1 = {}\n")
			.and(aspect).indexPageHasContent("<@thirdparty.bundle@/>\n" + "require('"+thirdpartyLib.getName()+"')");
		when(app).requestReceived("/default-aspect/thirdparty/bundle.js", pageResponse);
		then(pageResponse).containsText("lib1.file1 = {}")
			.and(pageResponse).containsText("lib1.file2 = {}")
			.and(pageResponse).containsText("lib2.file1 = {}");
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
		when(app).requestReceived("/default-aspect/thirdparty/file/thirdparty-lib/some/lib/dirs/some-file.ext", pageResponse);
		then(pageResponse).textEquals("some file contents");
	}
	
}
