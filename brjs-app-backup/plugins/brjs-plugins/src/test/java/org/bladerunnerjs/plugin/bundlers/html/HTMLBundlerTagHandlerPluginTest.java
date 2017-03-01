package org.bladerunnerjs.plugin.bundlers.html;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

import static org.bladerunnerjs.plugin.bundlers.html.HTMLBundlerTagHandlerPlugin.Messages.*;


public class HTMLBundlerTagHandlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private StringBuffer indexPageResponse = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void basicContentIsIdenticalToHtmlContentPluginContent() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>TESTCONTENT</template>")
			.and(aspect).indexPageHasContent("<@html.bundle@/>");
		when(aspect).requestReceivedInDev("html/bundle.html", response)
			.and(aspect).indexPageLoadedInDev(indexPageResponse, "en_GB");
		then(indexPageResponse.toString().replace("\n", "")).textEquals( 
				("<script>document.createElement(\"template\");</script>\n"+
					"<style>template{display:none;}</style>\n"+
					"<template id=\"brjs-html-templates-inline\"></template>\n"+
					response.toString()).replace("\n","") );
	}
	
	@Test
	public void templateElementIsRegisteredAboveContent() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>TESTCONTENT</template>")
			.and(aspect).indexPageHasContent("<@html.bundle@/>");
		when(aspect).indexPageLoadedInDev(indexPageResponse, "en_GB");
		then(indexPageResponse).containsText("<script>document.createElement(\"template\");</script>\n"+
				"<style>template{display:none;}</style>\n");
	}
	
	@Test
	public void i18nTokensAreReplaced() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.token}</template>")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.i18n.token=i18n replacement")
			.and(aspect).indexPageHasContent("<@html.bundle@/>");
		when(aspect).indexPageLoadedInDev(indexPageResponse, "en_GB");
		then(indexPageResponse).containsText("i18n replacement");
	}
	
	@Test //this mirrors the behaviour for tokens being replaced on the client side if they are loaded via XHR
	public void i18nTokensAreCaseInsensitive() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.TOKEN}</template>")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.i18n.token=i18n replacement")
			.and(aspect).indexPageHasContent("<@html.bundle@/>");
		when(aspect).indexPageLoadedInDev(indexPageResponse, "en_GB");
		then(indexPageResponse).containsText("i18n replacement");
	}
	
	@Test
	public void questionMarkReplacementIsUsedIfAnI18nReplacementCantBeFoundAndTheresNoFallback() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.token}</template>")
			.and(aspect).indexPageHasContent("<@html.bundle@/>")
			.and(logging).enabled();
		when(aspect).indexPageLoadedInDev(indexPageResponse, "en_GB");
		then(indexPageResponse).containsText("??? appns.i18n.token ???")
			.and(logging).warnMessageReceived(UNTRANSLATED_TOKEN_LOG_MSG, "appns.i18n.token", "en_GB");
	}
	
	@Test
	public void usesSelectedLanguageIfAvailable() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.token}</template>")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.i18n.token=i18n replacement")
			.and(aspect).containsFileWithContents("resources/de_DE.properties", "appns.i18n.token=this is written in German")
			.and(aspect).indexPageHasContent("<@html.bundle@/>")
			.and(app.appConf()).supportsLocales("en_GB","de_DE");
		when(aspect).indexPageLoadedInProd(indexPageResponse, "de_DE");
		then(indexPageResponse).containsText("this is written in German");
	}
	
	@Test
	public void fallsBackToDefaultLanguageInProd() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.token}</template>")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.i18n.token=i18n replacement")
			.and(aspect).indexPageHasContent("<@html.bundle@/>")
			.and(app.appConf()).supportsLocales("en_GB","de_DE")
			.and(logging).enabled();
		when(aspect).indexPageLoadedInProd(indexPageResponse, "de_DE");
		then(indexPageResponse).containsText("i18n replacement")
			.and(logging).warnMessageReceived(UNTRANSLATED_TOKEN_LOG_MSG, "appns.i18n.token", "de_DE");
	}
	
	@Test
	public void fallsBackToTokenNameInDev() throws Exception {
		given(aspect).containsResourceFileWithContents("html/view.html", "<template id='appns.view'>@{appns.i18n.token}</template>")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.i18n.token=i18n replacement")
			.and(aspect).indexPageHasContent("<@html.bundle@/>")
			.and(app.appConf()).supportsLocales("en_GB","de_DE")
			.and(logging).enabled();
		when(aspect).indexPageLoadedInDev(indexPageResponse, "de_DE");
		then(indexPageResponse).containsText("??? appns.i18n.token ???")
			.and(logging).warnMessageReceived(UNTRANSLATED_TOKEN_LOG_MSG, "appns.i18n.token", "de_DE");
	}
	
}
