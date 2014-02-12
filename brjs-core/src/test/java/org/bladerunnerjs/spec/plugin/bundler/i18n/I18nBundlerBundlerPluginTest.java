package org.bladerunnerjs.spec.plugin.bundler.i18n;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class I18nBundlerBundlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			workbench = blade.workbench();
	}
	
	
	
	@Test
	public void requestForI18nWithoutAnyAssetsReturnsEmptyResponse() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals("window._brjsI18nProperties = [{\n}];");
	}
	
	@Test
	public void i18nFilesForTheGivenLocaleInAspectResourcesAreBundled() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void i18nFilesForOtherLocalesInAspectResourcesAreIgnored() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.property=property value")
			.and(aspect).containsFileWithContents("resources/de_DE.properties", "appns.property=a different value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void requestsForALocaleCanContainTheLanguageOnly() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void requestsForALanguageDoesntIncludeLocationSpecificProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void locationSpecificPropertiesAreAddedToLanguageValues() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.some.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.another.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.some.property\":\"property value\",\n"+
						"\"appns.another.property\":\"another value\"\n"+
				"}];");
	}

	@Test
	public void locationSpecificPropertiesOverrideLanguageProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"another value\"\n"+
				"}];");
	}
	
	@Test
	public void locationSpecificRequestWillUseLanguageOnlyProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void propertiesCanBeInASubfolderOfResources() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/i18n/en.properties", "appns.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	@Test
	public void propertiesCanBeInASrcDirectoryWithTheAssociatedClass() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).indexPageRequires("appns.Class")
			.and(aspect).hasClass("appns.Class")
			.and(aspect).containsFileWithContents("src/appns/en.properties", "appns.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.property\":\"property value\"\n"+
				"}];");
	}
	
	
	@Test
	public void bladePropertiesAreOverriddenByAspectProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).indexPageRequires("appns.bs.b1.Class")
			.and(blade).hasClass("appns.bs.b1.Class")
			.and(blade).containsFileWithContents("resources/en.properties", "appns.bs.b1.property=blade value")
			.and(aspect).containsFileWithContents("resources/en.properties", "appns.bs.b1.property=aspect value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.bs.b1.property\":\"aspect value\"\n"+
				"}];");
	}
	
	@Test
	public void bladePropertiesAreOverriddenByWorkbenchProperties() throws Exception 
	{
		given(app).hasBeenCreated()
		.and(workbench).indexPageRequires("appns.bs.b1.Class")
		.and(blade).hasClass("appns.bs.b1.Class")
		.and(blade).containsFileWithContents("resources/en.properties", "appns.bs.b1.property=blade value")
		.and(workbench).containsFileWithContents("resources/en.properties", "appns.bs.b1.property=workbench value");
		when(app).requestReceived("/bs-bladeset/blades/b1/workbench/i18n/en_GB.js", response);
		then(response).textEquals(	
				"window._brjsI18nProperties = [{\n"+
						"\"appns.bs.b1.property\":\"workbench value\"\n"+
				"}];");
	}
	
	@Test
	public void i18nPropertyKeysMustMatchTheAspectNamespace() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "some.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.js", response);
		then(exceptions).verifyException(NamespaceException.class, "some.property", "default-aspect/resources/en_GB.properties", "appns");
	}
	
}
