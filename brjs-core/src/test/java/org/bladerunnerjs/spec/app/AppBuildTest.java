package org.bladerunnerjs.spec.app;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.plugin.plugins.bundlers.appmeta.AppMetadataContentPlugin;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockContentPlugin;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class AppBuildTest extends SpecTest {
	private App app;
	private Aspect defaultAspect;
	private Aspect nonDefaultAspect;
	private File targetDir;
	private BladerunnerConf bladerunnerConf;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasContentPlugins(new AppMetadataContentPlugin())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			defaultAspect = app.aspect("default");
			nonDefaultAspect = app.aspect("aspect2");
			targetDir = FileUtility.createTemporaryDirectory(AppBuildTest.class.getSimpleName());
			bladerunnerConf = brjs.bladerunnerConf();
	}
	
	@Test
	public void builtAppHasLocaleForwardingPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("Locale Forwarder")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/index.html", "Locale Forwarder");
	}
	
	@Test
	public void builtAppHasAspectIndexPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("app1/en/index.html");
	}
	
	@Test
	public void indexPageHasLogicalTagsReplaced() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/en/index.html", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void builtAppHasLocalizedIndexPagePerLocale() throws Exception {
		given(app).containsFileWithContents("app.conf", "requirePrefix: app\nlocales: en, de")
			.and(defaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/en/index.html", "/i18n/en.js")
			.and(targetDir).containsFileWithContents("app1/de/index.html", "/i18n/de.js");
	}
	
	@Test
	public void jspIndexPagesAreUnprocessedAndKeepTheJspSuffix() throws Exception {
		given(defaultAspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>\n<@js.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/en/index.jsp", "<%= 1 + 2 %>")
			.and(targetDir).containsFileWithContents("app1/en/index.jsp", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void nonDefaultAspectsHaveTheSameIndexPagesButWithinANamedDirectory() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(nonDefaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("app1/en/index.html", "/js/prod/combined/bundle.js")
			.and(targetDir).containsFileWithContents("app1/aspect2/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("app1/aspect2/en/index.html", "/i18n/en.js");
	}
	
	@Test
	public void aSingleSetOfBundlesAreCreated() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("template.html", "<div id='template-id'>content</div>")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("app1/v/1234/html/bundle.html")
			.and(targetDir).containsFile("app1/v/1234/i18n/en.js");
	}
	
	@Test
	public void theWebInfDirectoryIsCopiedIfThereIsOne() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasDir("WEB-INF/lib")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsDir("app1/WEB-INF/lib");
	}
	
	@Test
	public void bundlesAvailableAsPartOfACompositeArentSerialized() throws Exception {
		given(brjs).localeForwarderHasContents("")
			.and(defaultAspect).indexPageRequires("appns/Class")
			.and(defaultAspect).hasClass("appns/Class")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("app1/v/1234/js/prod/combined/bundle.js")
			.and(targetDir).doesNotContainFile("app1/v/1234/node-js/bundle.js");
	}
	
	@Test
	public void bundlesHaveExpectedContent() throws Exception
	{
		given(brjs).localeForwarderHasContents("")
    		.and(app).hasBeenCreated()
    		.and(defaultAspect).hasClass("appns/Class")
    		.and(defaultAspect).containsFileWithContents("themes/common/style.css", "some app styling")
    		.and(defaultAspect).indexPageRequires("appns/Class")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/v/1234/js/prod/combined/bundle.js", "define('appns/Class'")
			.and(targetDir).containsFileWithContents("app1/v/1234/css/common/bundle.css", "some app styling");
	}
	
	@Test
	public void jspsAreExportedAsSourceCode() throws Exception
	{
		given(brjs).localeForwarderHasContents("")
    		.and(app).hasBeenCreated()
    		.and(defaultAspect).indexPageHasContent("")
    		.and(defaultAspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/v/1234/unbundled-resources/file.jsp", "<%= 1 + 2 %>");
	}
	
	@Test
	public void unversionedContentIsBuiltToTheRightLocation() throws Exception
	{
		given(brjs).localeForwarderHasContents("")
			.and(app).hasBeenCreated()
			.and(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/mock-content-plugin/unversioned/url", MockContentPlugin.class.getCanonicalName())
			.and(targetDir).doesNotContainFile("app1/v/1234/mock-content-plugin/unversioned/url");
	}
	
	@Test
	public void outputFilesAreEncodedProperlyAsUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsEmptyFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("en.properties", "appns.p1=\"$£€\"")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/v/1234/i18n/en.js", "window._brjsI18nProperties = [{\n"+
        				"  \"appns.p1\": \"\\\"$£€\\\"\"\n"+
        		"}];");
	}
	
	@Test
	public void outputFilesAreEncodedProperlyAsLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsEmptyFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("en.properties", "appns.p1=\"$£\"")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("app1/v/1234/i18n/en.js", "window._brjsI18nProperties = [{\n"+
        				"  \"appns.p1\": \"\\\"$£\\\"\"\n"+
        		"}];");
	}
	
}
