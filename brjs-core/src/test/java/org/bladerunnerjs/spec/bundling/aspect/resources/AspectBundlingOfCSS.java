package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfCSS extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}
		
	// Aspect
 	@Test
 	public void aspectCssFilesAreBundled() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
			.and(aspect).indexPageRefersTo("appns.Class1");
 		when(aspect).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("ASPECT theme content");
 	}
	
	// Bladeset
 	@Test
 	public void bladesetCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(bladeset).hasClass("appns/bs/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.Class1")
			.and(bladeset).containsFileWithContents("themes/standard/style.css", "BLADESET theme content");
 		when(aspect).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("BLADESET theme content");
 	}
	
	// Blade
	@Test
 	public void bladeCssFilesAreBundledWhenReferencedInTheAspect() throws Exception {
		given(blade).hasClass("appns/bs/b1/Class1")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1")
			.and(blade).containsFileWithContents("themes/standard/style.css", "BLADE theme content");
 		when(aspect).requestReceivedInDev("css/standard/bundle.css", response);
 		then(response).containsText("BLADE theme content");
 	}
}
