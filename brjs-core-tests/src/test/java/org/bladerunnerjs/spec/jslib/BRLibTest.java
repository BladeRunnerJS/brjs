package org.bladerunnerjs.spec.jslib;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRLibYamlConf;
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
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			sdkLib = brjs.sdkLib("br");
			sdkLib2 = brjs.sdkLib("brlib2");
	}
	
	@Test
	public void sdkLibrariesCanHaveARequirePrefixThatsDifferentToTheirName() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: foo/bar")
			.and(sdkLib).hasClass("foo/bar/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo/bar/SdkClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("foo.bar.SdkClass");
	}
	
	@Test
	public void sdkLibrariesMustHaveARequirePrefixWithCorrectFormat() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).hasClass("foo/bar/SdkClass")
			.and(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: foo.bar")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo/bar/SdkClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(exceptions).verifyException(ConfigException.class, "foo.bar", "sdk/libs/javascript/br/br-lib.conf", BRLibYamlConf.REQUIRE_PREFIX_REGEX);
	}
	
	@Test
	public void aLibraryCanHaveTheSameRequirePrefixAsAClassInADifferentLibraryIfItHasADifferentCase() throws Exception {
		given(aspect).hasClass("appns/AspectClass")
			.and(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: foo")
			.and(sdkLib).hasClass("foo/Bar")
			.and(sdkLib2).containsFileWithContents("br-lib.conf", "requirePrefix: foo/bar")
			.and(sdkLib2).hasClass("foo/bar/SdkClass")
			.and(aspect).indexPageRefersTo("appns.AspectClass")
			.and(aspect).classRequires("appns/AspectClass", "foo/Bar");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("foo.Bar");
	}
	
	@Test
	public void sdkLibrariesCanOptionallyDisableJsNamespaceEnforcement() throws Exception {
		given(aspect).indexPageRefersTo("br.SdkClass", "anotherRootPkg.AnotherSdkClass")
			.and(sdkLib).containsFile("no-namespace-enforcement")
			.and(sdkLib).hasClass("br/SdkClass")
			.and(sdkLib).hasClass("anotherRootPkg/AnotherSdkClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsCommonJsClasses("br.SdkClass", "anotherRootPkg.AnotherSdkClass");
	}
	
	@Test
	public void sdkLibrariesCanOptionallyDisableI18nNamespaceEnforcement() throws Exception {
		given(aspect).indexPageRefersTo("br.SdkClass")
    		.and(sdkLib).containsFile("no-namespace-enforcement")
    		.and(sdkLib).hasClass("br/SdkClass")
    		.and(sdkLib).containsResourceFileWithContents("en_GB.properties", "br.property=property value\n" + "anotherRootPkg.property=another value");
		when(aspect).requestReceivedInDev("i18n/en_GB.js", response);
		then(response).containsText("\"br.property\": \"property value\"")
			.and(response).containsText("\"anotherRootPkg.property\": \"another value\"");
	}
	
	@Test
	public void librariesDontHaveToRepeatTheAssetContainerRequirePrefix() throws Exception {
		given(sdkLib).hasClass("pkg/Class")
			.and(aspect).indexPageRequires("br/pkg/Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/pkg/Class");
	}
	
	@Test
	public void sdkLibrariesCanDefineAnArbitraryRequirePrefix() throws Exception {
		given(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: lib/pkg")
			.and(sdkLib).hasClass("lib/pkg/Class")
			.and(aspect).indexPageRequires("lib/pkg/Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("lib/pkg/Class");
	}
	
	@Test
	public void librariesDontHaveToRepeatAnExplicitlyDefinedAssetContainerRequirePrefix() throws Exception {
		given(sdkLib).containsFileWithContents("br-lib.conf", "requirePrefix: lib/pkg")
			.and(sdkLib).hasClass("Class")
			.and(aspect).indexPageRequires("lib/pkg/Class");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsDefinedClasses("lib/pkg/Class");
	}
}
