package org.bladerunnerjs.spec.app;

import java.io.File;
import java.util.Arrays;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockContentPlugin;
import org.bladerunnerjs.testing.utility.ScriptedContentPlugin;
import org.bladerunnerjs.testing.utility.ScriptedRequestGeneratingTagHandlerPlugin;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BuildAppTest extends SpecTest {
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
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			defaultAspect = app.aspect("default");
			nonDefaultAspect = app.aspect("aspect2");
			targetDir = FileUtils.createTemporaryDirectory( this.getClass() );
			bladerunnerConf = brjs.bladerunnerConf();
	}
	
	@After
	public void deleteTempDir() {
		org.apache.commons.io.FileUtils.deleteQuietly(targetDir);
	}
	
	@Test
	public void builtAppHasLocaleForwardingPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("Locale Forwarder")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.html", "Locale Forwarder");
	}
	
	@Test
	public void builtAppHasAspectIndexPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("en/index.html");
	}
	
	@Test
	public void indexPageHasLogicalTagsReplaced() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/en/index.html", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void builtAppHasLocalizedIndexPagePerLocale() throws Exception {
		given(app).containsFileWithContents("app.conf", "requirePrefix: app\nlocales: en, de")
			.and(defaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/en/index.html", "/i18n/en.js")
			.and(targetDir).containsFileWithContents("/de/index.html", "/i18n/de.js");
	}
	
	@Test
	public void jspIndexPagesAreUnprocessedAndKeepTheJspSuffix() throws Exception {
		given(defaultAspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>\n<@js.bundle@/>")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/en/index.jsp", "<%= 1 + 2 %>")
			.and(targetDir).containsFileWithContents("/en/index.jsp", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void nonDefaultAspectsHaveTheSameIndexPagesButWithinANamedDirectory() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(nonDefaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeForwarderHasContents("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("/en/index.html", "/js/prod/combined/bundle.js")
			.and(targetDir).containsFileWithContents("/aspect2/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("/aspect2/en/index.html", "/i18n/en.js");
	}
	
	@Test
	public void aSingleSetOfBundlesAreCreated() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("template.html", "<div id='template-id'>content</div>")
			.and(brjs).localeForwarderHasContents("")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("v/1234/html/bundle.html")
			.and(targetDir).containsFile("v/1234/i18n/en.js");
	}
	
	@Test
	public void theWebInfDirectoryIsCopiedIfThereIsOne() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeForwarderHasContents("")
			.and(app).hasDir("WEB-INF/lib")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsDir("WEB-INF/lib");
	}
	
	@Test
	public void bundlesAvailableAsPartOfACompositeArentSerialized() throws Exception {
		given(brjs).localeForwarderHasContents("")
			.and(defaultAspect).indexPageHasContent("<@js.bundle @/>\n"+"require('appns/Class');")
			.and(defaultAspect).hasClass("appns/Class")
			.and(brjs).hasProdVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("v/1234/js/prod/combined/bundle.js")
			.and(targetDir).doesNotContainFile("v/1234/common-js/bundle.js");
	}
	
	@Test
	public void bundlesHaveExpectedContent() throws Exception
	{
		given(brjs).localeForwarderHasContents("")
    		.and(app).hasBeenCreated()
    		.and(defaultAspect).hasClass("appns/Class")
    		.and(defaultAspect).containsFileWithContents("themes/common/style.css", "some app styling")
    		.and(defaultAspect).indexPageHasContent("<@css.bundle @/>\n"+"<@js.bundle @/>\n"+"require('appns/Class');")
    		.and(brjs).hasProdVersion("1234")
    		.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/v/1234/js/prod/combined/bundle.js", "define('appns/Class'")
			.and(targetDir).containsFileWithContents("/v/1234/css/common/bundle.css", "some app styling");
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
		then(targetDir).containsFileWithContents("/v/1234/unbundled-resources/file.jsp", "<%= 1 + 2 %>");
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
		then(targetDir).containsFileWithContents("/mock-content-plugin/unversioned/url", MockContentPlugin.class.getCanonicalName())
			.and(targetDir).doesNotContainFile("v/1234/mock-content-plugin/unversioned/url");
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
		then(targetDir).containsFileWithContents("/v/1234/i18n/en.js", "window._brjsI18nProperties = [{\n"+
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
		then(targetDir).containsFileWithContents("/v/1234/i18n/en.js", "window._brjsI18nProperties = [{\n"+
        				"  \"appns.p1\": \"\\\"$£\\\"\"\n"+
        		"}];");
	}
	
	@Test
	public void onlyUrlsIdentifiedByACorrespondingTagHandlerAreIncludedInTheBuiltApp() throws Exception {
		
		given(brjs).hasNotYetBeenCreated()
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/used/url", "/unused/url") )
			.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("ScriptedContentPlugin"), Arrays.asList("/used/url")) )
			.and(brjs).hasBeenCreated()
			.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag @/>")
			.and(brjs).localeForwarderHasContents("")
			.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("used/url")
			.and(targetDir).doesNotContainFile("unused/url");
	}
	
	@Test
	public void allUrlsAreIncludedInTheBuiltAppIfACorrespondingTagHandlerPluginIsNotFound() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/used/url", "/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList(), Arrays.asList("/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag @/>")
    		.and(brjs).localeForwarderHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("used/url")
    		.and(targetDir).containsFile("unused/url");
	}
	
	@Test
	public void urlsOnlyHaveToBeIdentifiedByASingleTagHandlerIfMultipleCanSupportTheSameContentPlugin() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/used/url", "/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag1", Arrays.asList("ScriptedContentPlugin"), Arrays.asList()), 
    										new ScriptedRequestGeneratingTagHandlerPlugin("tag2", Arrays.asList("ScriptedContentPlugin"), Arrays.asList("/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag1 @/> <@tag2 @/>")
    		.and(brjs).localeForwarderHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("used/url")
    		.and(targetDir).doesNotContainFile("unused/url");
	}
	
	@Test
	public void contentPluginsIdentifiedByATagHandlerAreNotIncludedInTheBuiltAppIfTheTagIsntUsed() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/used/url", "/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("ScriptedContentPlugin"), Arrays.asList("/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("")
    		.and(brjs).localeForwarderHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).doesNotContainFile("used/url")
    		.and(targetDir).doesNotContainFile("unused/url");
	}
	
	@Test
	public void contentUrlsUsedInOtherAspectsAreStillContainedInTheBuiltApp() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/used/url", "/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("ScriptedContentPlugin"), Arrays.asList("/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").aspect("empty") ).indexPageHasContent("")
    		.and( brjs.app("app1").aspect("nonempty") ).indexPageHasContent("<@tag @/>")
    		.and(brjs).localeForwarderHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).doesNotContainFile("empty/used/url")
    		.and(targetDir).doesNotContainFile("empty/unused/url")
    		.and(targetDir).containsFile("nonempty/used/url")
    		.and(targetDir).doesNotContainFile("nonempty/unused/url");
	}
	
	@Test
	public void bundlesFromContentPluginsThatOutputAllBundlesAreOutputRegardlessOfWhetherTheTagIsUsed() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(true, "/used/url", "/unused/url") )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("")
    		.and(brjs).localeForwarderHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("used/url")
    		.and(targetDir).containsFile("unused/url");
	}
	
}
