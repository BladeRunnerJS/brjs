package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfCSS extends SpecTest {
	private App app;
	private Aspect aspect;
	private Aspect rootDefaultAspect;
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
			rootDefaultAspect = app.defaultAspect();
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
 	
 	// Root-level Aspect
 	@Test
 	public void rootAspectCssFilesAreBundled() throws Exception {
		given(rootDefaultAspect).hasClass("appns/Class1")
			.and(rootDefaultAspect).containsFileWithContents("themes/standard/style.css", "ASPECT theme content")
			.and(rootDefaultAspect).indexPageRefersTo("appns.Class1");
 		when(rootDefaultAspect).requestReceivedInDev("css/standard/bundle.css", response);
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
