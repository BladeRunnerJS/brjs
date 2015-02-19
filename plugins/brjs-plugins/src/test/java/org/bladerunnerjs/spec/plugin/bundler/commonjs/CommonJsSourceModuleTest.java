package org.bladerunnerjs.spec.plugin.bundler.commonjs;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class CommonJsSourceModuleTest extends SpecTest {
	private App app;
	private Aspect aspect;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	@Test
	public void differentDependencyTypesAreAllCorrectlyDiscovered() throws Exception {
		given(aspect).indexPageRequires("appns/Class1")
			.and(aspect).hasClasses("appns/Class2", "appns/Class3", "appns/Class4")
			.and(aspect).classFileHasContent("appns/Class1",
				"var Class2 = require('appns/Class2');\n" +
				"function Class1() {\n" +
				"  this.obj = new (require('appns/Class4'))();" +
				"};\n" +
				"Class1.extends(Class2);\n" +
				"\n" +
				"module.exports = Class1;\n" +
				"\n" +
				"var Class3 = require('appns/Class3');\n");
		then(aspect).classHasPreExportDependencies("appns/Class1", "appns/Class2")
			.and(aspect).classHasPostExportDependencies("appns/Class1", "appns/Class3")
			.and(aspect).classHasUseTimeDependencies("appns/Class1", "appns/Class4");
	}
}
