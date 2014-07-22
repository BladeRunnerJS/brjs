package org.bladerunnerjs.spec.plugin.bundler.i18n;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class I18nContentPluginTest extends SpecTest
{

	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private BladerunnerConf bladerunnerConf;
	private SdkJsLib sdkLib;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
			bladerunnerConf = brjs.bladerunnerConf();
			sdkLib = brjs.sdkLib("br");
	}
	
	@Test
	public void theRequestsGeneratedIsTiedToTheLocalesTheAppSupports() throws Exception {
		then(aspect).prodAndDevRequestsForContentPluginsAre("i18n", "i18n/en.js");
	}
	
	@Test
	public void fullLocaleRequestsWillAlsoBeGeneratedIfTheAppConfIsConfiguredForThis() throws Exception {
		given(appConf).supportsLocales("en", "en_GB");
		then(aspect).prodAndDevRequestsForContentPluginsAre("i18n", "i18n/en.js", "i18n/en_GB.js");
	}
	
	@Test
	public void requestForI18nWithoutAnyAssetsReturnsEmptyResponse() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals("window._brjsI18nProperties = [{}];");
	}
	
	@Test
	public void i18nFilesForTheGivenLocaleInAspectResourcesAreBundled() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void i18nFilesForOtherLocalesInAspectResourcesAreIgnored() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=property value")
			.and(aspect).containsResourceFileWithContents("de_DE.properties", "appns.property=a different value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void requestsForALocaleCanContainTheLanguageOnly() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.property=property value");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void requestsForALanguageDoesntIncludeLocationSpecificProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.property=property value")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=another value");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void locationSpecificPropertiesAreAddedToLanguageValues() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.some.property=property value")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.another.property=another value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.another.property\": \"another value\",\n"+
						"  \"appns.some.property\": \"property value\"\n"+
				"}];");
	}

	@Test
	public void locationSpecificPropertiesOverrideLanguageProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.property=property value")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "appns.property=another value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"another value\"\n"+
				"}];");
	}
	
	@Test
	public void locationSpecificRequestWillUseLanguageOnlyProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void propertiesCanBeInASubfolderOfResources() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("i18n/en.properties", "appns.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	@Test
	public void propertiesCanBeInASrcDirectoryWithTheAssociatedClass() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRefersTo("appns.Class")
			.and(aspect).hasClass("appns/Class")
			.and(aspect).containsFileWithContents("src/appns/en.properties", "appns.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.property\": \"property value\"\n"+
				"}];");
	}
	
	
	@Test
	public void bladePropertiesAreOverriddenByAspectProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class")
			.and(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("en.properties", "appns.bs.b1.property=blade value")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.bs.b1.property=aspect value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.bs.b1.property\": \"aspect value\"\n"+
				"}];");
	}
	
	@Test
	public void bladePropertiesAreOverriddenByWorkbenchProperties() throws Exception 
	{
		given(app).hasBeenCreated()
		.and(workbench).indexPageRequires("appns.bs.b1.Class")
		.and(blade).hasClass("appns/bs/b1/Class")
		.and(blade).containsResourceFileWithContents("en.properties", "appns.bs.b1.property=blade value")
		.and(workbench).containsResourceFileWithContents("en.properties", "appns.bs.b1.property=workbench value");
		when(workbench).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.bs.b1.property\": \"workbench value\"\n"+
				"}];");
	}
	
	@Test
	public void i18nPropertyKeysMustMatchTheAspectNamespace() throws Exception 
	{
		given(blade).hasClass("appns/bs/b1/Class")
			.and(blade).containsResourceFileWithContents("en_GB.properties", "some.property=property value")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(exceptions).verifyException(NamespaceException.class, "some.property", "appns.bs.b1.*");
	}
	
	@Test
	public void i18nPropertyKeysDefinedWithTheAspectDoNotNeedToBeNamespace() throws Exception 
	{
		given(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en_GB.properties", "some.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void propertiesAreOrderedAlphabetically() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.p3=v3\nappns.p1=v1\nappns.p2=v2\n");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.p1\": \"v1\",\n"+
						"  \"appns.p2\": \"v2\",\n"+
						"  \"appns.p3\": \"v3\"\n"+
				"}];");
	}
	
	@Test
	public void newLinesWithinPropertiesArePreserved() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.p1=v\\n1");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.p1\": \"v\\n1\"\n"+
				"}];");
	}
	
	@Test
	public void quotesWithinPropertiessAreProperlyEscaped() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.p1=\"quoted\"");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.p1\": \"\\\"quoted\\\"\"\n"+
				"}];");
	}
	
	@Test
	public void weCanUseUTF8() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("UTF-8")
			.and().activeEncodingIs("UTF-8")
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsResourceFileWithContents("en.properties", "appns.p1=\"$£€\"");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"appns.p1\": \"\\\"$£€\\\"\"\n"+
				"}];");
	}
	
	@Test
	public void weCanUseLatin1() throws Exception {
		given(bladerunnerConf).defaultFileCharacterEncodingIs("ISO-8859-1")
    		.and().activeEncodingIs("ISO-8859-1")
    		.and(aspect).hasBeenCreated()
    		.and(aspect).containsEmptyFile("index.html")
    		.and(aspect).containsResourceFileWithContents("en.properties", "appns.p1=\"$£\"");
    	when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
    	then(response).textEquals(	
    			"window._brjsI18nProperties = [{\n"+
    					"  \"appns.p1\": \"\\\"$£\\\"\"\n"+
    			"}];");
	}
	
	@Test
	public void theCorrectRequirePrefixIsUsedForNamespaceEnforcement() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: foo/bar")
			.and(sdkLib).hasClass("foo/bar/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo.bar.SdkClass")
			.and(sdkLib).containsResourceFileWithContents("en_GB.properties", "foo.bar.property=property value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"  \"foo.bar.property\": \"property value\"\n"+
				"}];");
	}
}
