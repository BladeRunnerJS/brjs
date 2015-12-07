package org.bladerunnerjs.spec.service;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.spec.aliasing.AliasDefinitionsFileBuilder;
import org.bladerunnerjs.spec.aliasing.AliasesFileBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.*;


public class ServiceDataTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private SdkJsLib servicesLib;
	private AliasesFileBuilder aspectAliasesFile;
	private StringBuffer response = new StringBuffer();
	private AliasDefinitionsFileBuilder aspectAliasDefinitionsFile;

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs)
			.automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()			
			.and(brjs).hasBeenCreated();
		app = brjs.app("app");
		aspect = app.defaultAspect();
		
		aspectAliasesFile = new AliasesFileBuilder(this, aliasesFile(aspect));
		aspectAliasDefinitionsFile = new AliasDefinitionsFileBuilder(this, aliasDefinitionsFile(aspect, "resources"));
		
		servicesLib = brjs.sdkLib("ServicesLib");
		given(servicesLib).containsFileWithContents("br-lib.conf", "requirePrefix: br")
			.and(servicesLib).hasClasses("br/AliasRegistry", "br/ServiceRegistry")
			.and(servicesLib).hasClass("br/UnknownClass");
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServicesUsed() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).hasClass("App")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServiceAreDefinedViaAliasesAreUsed() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataIsEmptyIfNoServiceAreDefinedViaAliasesWithAnImplementation() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceInterface")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", null, "appns/ServiceInterface");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = { }"
		);
	}
	
	@Test
	public void serviceDataListsASingleUsedService_whenServiceDefinedViaAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\""
		);
	}
	
	@Test
	public void serviceDataListsASingleUsedService_whenServiceDefinedViaAliaseDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).hasClass("ServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\""
		);
	}
	
	@Test
	public void serviceDataListsAllServices_whenServiceDefinedViaAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classFileHasContent("App", "require('service!some-service'); require('service!another-service');")
			.and(aspect).hasClass("ServiceClass")
			.and(aspect).hasClass("AnotherServiceClass")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass")
    		.and(aspectAliasesFile).hasAlias("another-service", "appns/AnotherServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!another-service\": {",
			"		\"requirePath\": \"appns/AnotherServiceClass\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAllServices_whenServiceDefinedViaAliaseDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classFileHasContent("App", "require('service!some-service'); require('service!another-service');")
			.and(aspect).hasClass("ServiceClass")
			.and(aspect).hasClass("AnotherServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass")
    		.and(aspectAliasDefinitionsFile).hasAlias("another-service", "appns/AnotherServiceClass");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!another-service\": {",
			"		\"requirePath\": \"appns/AnotherServiceClass\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAServicesServiceDependencies_whenServiceDefinedViaAliases() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).classFileHasContent("ServiceClass", "require('service!dependent-service-1'); require('service!dependent-service-2'); require('service!dependent-service-3');")
			.and(aspect).hasClass("DependentServiceClass1")
			.and(aspect).hasClass("DependentServiceClass2")
			.and(aspect).hasClass("DependentServiceClass3")
			.and(aspectAliasesFile).hasAlias("some-service", "appns/ServiceClass")
			.and(aspectAliasesFile).hasAlias("dependent-service-1", "appns/DependentServiceClass1")
			.and(aspectAliasesFile).hasAlias("dependent-service-2", "appns/DependentServiceClass2")
			.and(aspectAliasesFile).hasAlias("dependent-service-3", "appns/DependentServiceClass3");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": [",
			"			\"dependent-service-1\",",
			"			\"dependent-service-2\",",
			"			\"dependent-service-3\"",
			"		]",
			"	},",
			"	\"service!dependent-service-1\": {",
			"		\"requirePath\": \"appns/DependentServiceClass1\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-2\": {",
			"		\"requirePath\": \"appns/DependentServiceClass2\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-3\": {",
			"		\"requirePath\": \"appns/DependentServiceClass3\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}
	
	@Test
	public void serviceDataListsAServicesServiceDependencies_whenServiceDefinedViaAliaseDefinitions() throws Exception {
		given(aspect).indexPageRequires("appns/App", "service!$data")
			.and(aspect).classRequires("App", "service!some-service")
			.and(aspect).classFileHasContent("ServiceClass", "require('service!dependent-service-1'); require('service!dependent-service-2'); require('service!dependent-service-3');")
			.and(aspect).hasClass("DependentServiceClass1")
			.and(aspect).hasClass("DependentServiceClass2")
			.and(aspect).hasClass("DependentServiceClass3")
			.and(aspectAliasDefinitionsFile).hasAlias("some-service", "appns/ServiceClass")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-1", "appns/DependentServiceClass1")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-2", "appns/DependentServiceClass2")
			.and(aspectAliasDefinitionsFile).hasAlias("dependent-service-3", "appns/DependentServiceClass3");
		when(aspect).requestReceivedInDev("js/dev/combined/bundle.js", response);
		then(response).containsLines(
			"module.exports = {",
			"	\"service!some-service\": {",
			"		\"requirePath\": \"appns/ServiceClass\",",
			"		\"dependencies\": [",
			"			\"dependent-service-1\",",
			"			\"dependent-service-2\",",
			"			\"dependent-service-3\"",
			"		]",
			"	},",
			"	\"service!dependent-service-1\": {",
			"		\"requirePath\": \"appns/DependentServiceClass1\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-2\": {",
			"		\"requirePath\": \"appns/DependentServiceClass2\",",
			"		\"dependencies\": []",
			"	},",
			"	\"service!dependent-service-3\": {",
			"		\"requirePath\": \"appns/DependentServiceClass3\",",
			"		\"dependencies\": []",
			"	}",
			"}"
		);
	}


	
	
}
