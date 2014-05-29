package org.bladerunnerjs.spec.jslib;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRLibConf;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BRLibTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkLib;
	private StringBuffer response = new StringBuffer();
	private JsLib sdkLib2;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkLib = brjs.sdkLib("br");
			sdkLib2 = brjs.sdkLib("brlib2");
	}
	
	@Test
	public void sdkLibrariesCanHaveARequirePrefixThatsDifferentToTheirName() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).containsFileWithContents("br.manifest", "requirePrefix: foo/bar")
			.and(sdkLib).hasClass("foo/bar/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo.bar.SdkClass");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsNodeJsClasses("foo.bar.SdkClass");
	}
	
	@Test
	public void sdkLibrariesMustHaveARequirePrefixWithCorrectFormat() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).hasClass("foo/bar/SdkClass")
			.and(sdkLib).containsFileWithContents("br.manifest", "requirePrefix: foo.bar")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo.bar.SdkClass");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(ConfigException.class, "foo.bar", "sdk/libs/javascript/br-libs/br/br.manifest", BRLibConf.REQUIRE_PREFIX_REGEX);
	}
	
	@Test
	public void aLibraryCanHaveTheSameRequirePrefixAsAClassInADifferentLibraryIfItHasADifferentCase() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).containsFileWithContents("br.manifest", "requirePrefix: foo")
			.and(sdkLib).hasClass("foo/Bar")
			.and(sdkLib2).containsFileWithContents("br.manifest", "requirePrefix: foo/bar")
			.and(sdkLib2).hasClass("foo/bar/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo.Bar");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsNodeJsClasses("foo.Bar");
	}
	
	@Test
	public void sdkLibrariesCanOptionallyDisableJsNamespaceEnforcement() throws Exception {
		given(aspect).indexPageRefersTo("br.SdkClass", "anotherRootPkg.AnotherSdkClass")
			.and(sdkLib).containsFile("no-namespace-enforcement")
			.and(sdkLib).hasClass("br/SdkClass")
			.and(sdkLib).hasClass("anotherRootPkg/AnotherSdkClass");
		when(aspect).requestReceived("js/dev/combined/bundle.js", response);
		then(response).containsNodeJsClasses("br.SdkClass", "anotherRootPkg.AnotherSdkClass");
	}
	
	@Test
	public void sdkLibrariesCanOptionallyDisableI18nNamespaceEnforcement() throws Exception {
		given(aspect).indexPageRefersTo("br.SdkClass")
    		.and(sdkLib).containsFile("no-namespace-enforcement")
    		.and(sdkLib).hasClass("br/SdkClass")
    		.and(sdkLib).containsResourceFileWithContents("en_GB.properties", "br.property=property value\n" + "anotherRootPkg.property=another value");
		when(aspect).requestReceived("i18n/en_GB.js", response);
		then(response).containsText("\"br.property\": \"property value\"")
			.and(response).containsText("\"anotherRootPkg.property\": \"another value\"");
	}
	
}
