package org.bladerunnerjs.spec.app;

import java.io.File;
import java.util.Arrays;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.spec.brjs.appserver.MockTagHandler;
import org.bladerunnerjs.testing.utility.MockContentPlugin;
import org.bladerunnerjs.testing.utility.ScriptedContentPlugin;
import org.bladerunnerjs.testing.utility.ScriptedRequestGeneratingTagHandlerPlugin;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BuildAppTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect defaultAspect;
	private Aspect nonDefaultAspect;
	private File targetDir;
	private BladerunnerConf bladerunnerConf;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasContentPlugins(new MockContentPlugin())
			.and(brjs).hasTagHandlerPlugins(new MockTagHandler("tagToken", "dev replacement", "prod replacement"))
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
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
	public void builtSingleLocaleAppHasAspectIndexPage() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("index.html", "index.html");
	}
	
	@Test
	public void builtMultiLocaleAppHasLocaleForwardingPage() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).containsFile("index.html")
			.and(brjs).localeSwitcherHasContents("Locale Forwarder")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.html", "Locale Forwarder");
	}
	
	@Test
	public void builtMultiLocaleAppHasAspectIndexPage() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).containsFile("index.html")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("en.html", "index.html");
	}
	
	@Test
	public void indexPageHasLogicalTagsReplaced() throws Exception {
		given(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.html", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void builtAppHasLocalizedIndexPagePerLocale() throws Exception {
		given(app).containsFileWithContents("app.conf", "requirePrefix: app\nlocales: en, de")
			.and(defaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/en.html", "/i18n/en.js")
			.and(targetDir).containsFileWithContents("/de.html", "/i18n/de.js");
	}
	
	@Test
	public void indexJspIndexPagesAreUnprocessedAndKeepTheJspSuffix() throws Exception {
		given(defaultAspect).containsFileWithContents("index.jsp", "<%= 1 + 2 %>\n<@js.bundle@/>")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.jsp", "<%= 1 + 2 %>")
			.and(targetDir).containsFileWithContents("/index.jsp", "/js/prod/combined/bundle.js");
	}
	
	@Test
	public void tokensInIndexJspAreReplaced() throws Exception
	{
		given(defaultAspect).containsFileWithContents("index.jsp", "<@tagToken @/>")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.jsp", "prod replacement");
	}
	
	@Test
	public void nonDefaultAspectsHaveTheSameIndexPagesButWithinANamedDirectory() throws Exception {
		given(appConf).supportsLocales("en", "de")
			.and(defaultAspect).containsFileWithContents("index.html", "<@js.bundle@/>")
			.and(nonDefaultAspect).containsFileWithContents("index.html", "<@i18n.bundle@/>")
			.and(brjs).localeSwitcherHasContents("locale-forwarder.js")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("/en.html", "/js/prod/combined/bundle.js")
			.and(targetDir).containsFileWithContents("/aspect2/index.html", "locale-forwarder.js")
			.and(targetDir).containsFileWithContents("/aspect2/en.html", "/i18n/en.js");
	}
	
	@Test
	public void aSingleSetOfBundlesAreCreated() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("template.html", "<div id='template-id'>content</div>")
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).hasVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("v/1234/html/bundle.html")
			.and(targetDir).containsFile("v/1234/i18n/en.js");
	}
	
	@Test
	public void theWebInfDirectoryIsCopiedIfThereIsOne() throws Exception {
		given(defaultAspect).containsFile("index.html")
			.and(brjs).localeSwitcherHasContents("")
			.and(app).hasDir("WEB-INF/lib")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsDir("WEB-INF/lib");
	}
	
	@Test
	public void bundlesAvailableAsPartOfACompositeArentSerialized() throws Exception {
		given(brjs).localeSwitcherHasContents("")
			.and(defaultAspect).indexPageHasContent("<@js.bundle @/>\n"+"require('appns/Class');")
			.and(defaultAspect).hasClass("appns/Class")
			.and(brjs).hasVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("v/1234/js/prod/combined/bundle.js")
			.and(targetDir).doesNotContainFile("v/1234/common-js/bundle.js");
	}
	
	@Test
	public void bundlesHaveExpectedContent() throws Exception
	{
		given(brjs).localeSwitcherHasContents("")
    		.and(app).hasBeenCreated()
    		.and(defaultAspect).hasClass("appns/Class")
    		.and(defaultAspect).containsFileWithContents("themes/common/style.css", "some app styling")
    		.and(defaultAspect).indexPageHasContent("<@css.bundle @/>\n"+"<@js.bundle @/>\n"+"require('appns/Class');")
    		.and(brjs).hasVersion("1234")
    		.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/v/1234/js/prod/combined/bundle.js", "define('appns/Class'")
			.and(targetDir).containsFileWithContents("/v/1234/css/common/bundle.css", "some app styling");
	}
	
	@Test
	public void jspsAreExportedAsSourceCode() throws Exception
	{
		given(brjs).localeSwitcherHasContents("")
    		.and(app).hasBeenCreated()
    		.and(defaultAspect).indexPageHasContent("")
    		.and(defaultAspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
    		.and(brjs).hasVersion("1234")
    		.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/v/1234/unbundled-resources/file.jsp", "<%= 1 + 2 %>");
	}
	
	@Test
	public void unversionedContentIsBuiltToTheRightLocation() throws Exception
	{
		given(brjs).localeSwitcherHasContents("")
			.and(app).hasBeenCreated()
			.and(defaultAspect).indexPageHasContent("")
			.and(defaultAspect).containsFileWithContents("unbundled-resources/file.jsp", "<%= 1 + 2 %>")
			.and(brjs).hasVersion("1234")
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
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).hasVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/v/1234/i18n/en.js", "if (!window.$_brjsI18nProperties) { window.$_brjsI18nProperties = {} };\n"
				+ "window.$_brjsI18nProperties['en'] = {\n"
				+ "  \"appns.p1\": \"\\\"$£€\\\"\"\n"
				+ "};\n"
				+ "window.$_brjsI18nUseLocale = 'en';");
	}
	
	@Test
	public void outputFilesAreEncodedProperlyAsLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsEmptyFile("index.html")
			.and(defaultAspect).containsResourceFileWithContents("en.properties", "appns.p1=\"$£\"")
			.and(brjs).localeSwitcherHasContents("")
			.and(brjs).hasVersion("1234")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("/v/1234/i18n/en.js", "if (!window.$_brjsI18nProperties) { window.$_brjsI18nProperties = {} };\n"
				+ "window.$_brjsI18nProperties['en'] = {\n"
				+ "  \"appns.p1\": \"\\\"$£\\\"\"\n"
				+ "};\n" 
				+ "window.$_brjsI18nUseLocale = 'en';");
	}
	
	@Test
	public void onlyUrlsIdentifiedByACorrespondingTagHandlerAreIncludedInTheBuiltApp() throws Exception {
		
		given(brjs).hasNotYetBeenCreated()
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/scripted/used/url", "/scripted/unused/url") )
			.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("scripted"), Arrays.asList("/scripted/used/url")) )
			.and(brjs).hasBeenCreated()
			.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag @/>")
			.and(brjs).localeSwitcherHasContents("")
			.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
		then(targetDir).containsFile("scripted/used/url")
			.and(targetDir).doesNotContainFile("scripted/unused/url");
	}
	
	@Test
	public void allUrlsAreIncludedInTheBuiltAppIfACorrespondingTagHandlerPluginIsNotFound() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/scripted/used/url", "/scripted/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList(), Arrays.asList("/scripted/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag @/>")
    		.and(brjs).localeSwitcherHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("scripted/used/url")
    		.and(targetDir).containsFile("scripted/unused/url");
	}
	
	@Test
	public void urlsOnlyHaveToBeIdentifiedByASingleTagHandlerIfMultipleCanSupportTheSameContentPlugin() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/scripted/used/url", "/scripted/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag1", Arrays.asList("scripted"), Arrays.asList()), 
    										new ScriptedRequestGeneratingTagHandlerPlugin("tag2", Arrays.asList("scripted"), Arrays.asList("/scripted/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("<@tag1 @/> <@tag2 @/>")
    		.and(brjs).localeSwitcherHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("scripted/used/url")
    		.and(targetDir).doesNotContainFile("scripted/unused/url");
	}
	
	@Test
	public void contentPluginsIdentifiedByATagHandlerAreNotIncludedInTheBuiltAppIfTheTagIsntUsed() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/scripted/used/url", "/scripted/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("scripted"), Arrays.asList("/scripted/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("")
    		.and(brjs).localeSwitcherHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).doesNotContainFile("scripted/used/url")
    		.and(targetDir).doesNotContainFile("scripted/unused/url");
	}
	
	@Test
	public void contentUrlsUsedInOtherAspectsAreStillContainedInTheBuiltApp() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(false, "/scripted/used/url", "/scripted/unused/url") )
    		.and(brjs).hasTagHandlerPlugins( new ScriptedRequestGeneratingTagHandlerPlugin("tag", Arrays.asList("scripted"), Arrays.asList("/scripted/used/url")) )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").aspect("empty") ).indexPageHasContent("")
    		.and( brjs.app("app1").aspect("nonempty") ).indexPageHasContent("<@tag @/>")
    		.and(brjs).localeSwitcherHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).doesNotContainFile("empty/scripted/used/url")
    		.and(targetDir).doesNotContainFile("empty/scripted/unused/url")
    		.and(targetDir).containsFile("nonempty/scripted/used/url")
    		.and(targetDir).doesNotContainFile("nonempty/scripted/unused/url");
	}
	
	@Test
	public void bundlesFromContentPluginsThatOutputAllBundlesAreOutputRegardlessOfWhetherTheTagIsUsed() throws Exception {
		given(brjs).hasNotYetBeenCreated()
    		.and(brjs).automaticallyFindsBundlerPlugins()
    		.and(brjs).hasContentPlugins( new ScriptedContentPlugin(true, "/scripted/used/url", "/scripted/unused/url") )
    		.and(brjs).hasBeenCreated()
    		.and( brjs.app("app1").defaultAspect() ).indexPageHasContent("")
    		.and(brjs).localeSwitcherHasContents("")
    		.and( brjs.app("app1") ).hasBeenBuilt(targetDir);
    	then(targetDir).containsFile("scripted/used/url")
    		.and(targetDir).containsFile("scripted/unused/url");
	}
	
	@Test
	public void userTokensCanBeReplaced() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
			.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@")
			.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
			.and(brjs).hasVersion("dev")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("v/dev/js/prod/combined/bundle.js", "token replacement");
	}
	
	@Test
	public void brjsTokensCanBeReplaced() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsFileWithContents("src/App.js", "@BRJS.BUNDLE.PATH@/some/path")
			.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
			.and(brjs).hasVersion("123")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("v/123/js/prod/combined/bundle.js", "v/123/some/path");
	}
	
	@Test
	public void brjsTokensHaveTheCorrectValues() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en\n"
				+ "requirePrefix: appns")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).containsFileWithContents("src/App.js", 
    				"name = @BRJS.APP.NAME@\n"+
    				"version = @BRJS.APP.VERSION@\n"+
    				"bundlepath = @BRJS.BUNDLE.PATH@\n"
			)
			.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
			.and(brjs).hasVersion("123")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("v/123/js/prod/combined/bundle.js", "name = app1\n")
			.and(targetDir).containsFileWithContents("v/123/js/prod/combined/bundle.js", "version = 123\n")
			.and(targetDir).containsFileWithContents("v/123/js/prod/combined/bundle.js", "bundlepath = v/123\n");
	}
	
	@Test
	public void brjsAppLocaleTokensCanBeReplacedForIndexPages() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en,de\n"
				+ "requirePrefix: appns")
			.and(defaultAspect).hasBeenCreated()
			.and(defaultAspect).indexPageHasContent("@BRJS.APP.LOCALE@")
			.and(brjs).hasVersion("123")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("en.html", "en")
			.and(targetDir).containsFileWithContents("de.html", "de");
	}
	
	@Test
	public void defaultAppLocaleTokensIsUsedForBundles() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
				+ "locales: en,de\n"
				+ "requirePrefix: appns")
			.and(defaultAspect).containsFileWithContents("src/App.js", "@BRJS.APP.LOCALE@")
			.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
			.and(brjs).hasVersion("123")
			.and(app).hasBeenBuilt(targetDir);
		then(targetDir).containsFileWithContents("v/123/js/prod/combined/bundle.js", "en");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(app).hasBeenCreated()
    		.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
    			+ "locales: en\n"
    			+ "requirePrefix: appns")
    		.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
    		.and(defaultAspect).hasBeenCreated()
    		.and(defaultAspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@ // $£€")
    		.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
    		.and(brjs).hasVersion("dev")
    		.and(app).hasBeenBuilt(targetDir);
    	then(targetDir).containsFileWithContents("v/dev/js/prod/combined/bundle.js", "token replacement // $£€");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
			.and().activeEncodingIs("ISO-8859-1")
    		.and(app).hasBeenCreated()
    		.and(app).containsFileWithContents("app.conf", "localeCookieName: BRJS.LOCALE\n"
    			+ "locales: en\n"
    			+ "requirePrefix: appns")
    		.and(app).hasDefaultEnvironmentProperties("SOME.TOKEN", "token replacement")
    		.and(defaultAspect).hasBeenCreated()
    		.and(defaultAspect).containsFileWithContents("src/App.js", "@SOME.TOKEN@ // $£")
    		.and(defaultAspect).indexPageHasContent("<@js.bundle@/>\n"+"require('appns/App');")
    		.and(brjs).hasVersion("dev")
    		.and(app).hasBeenBuilt(targetDir);
    	then(targetDir).containsFileWithContents("v/dev/js/prod/combined/bundle.js", "token replacement // $£");
	}
	
}
