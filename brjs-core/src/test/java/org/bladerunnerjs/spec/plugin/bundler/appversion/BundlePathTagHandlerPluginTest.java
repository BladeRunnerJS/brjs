package org.bladerunnerjs.spec.plugin.bundler.appversion;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BundlePathTagHandlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private NamedDirNode workbenchTemplate;
	
	private StringBuffer response;
	
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
			workbench = blade.workbench();
			workbenchTemplate = brjs.template("workbench");
			
			given(workbenchTemplate).containsFileWithContents("index.html", "'<html>hello world</html>'")
				.and(workbenchTemplate).containsFolder("resources")
				.and(workbenchTemplate).containsFolder("src");
			
			response = new StringBuffer();
	}
	
	@Test
	public void bundlePathDevContentIsIncluded() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path@/>/some/path");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../v/dev/some/path" );
	}
	
	@Test
	public void bundlePathDevContentIsIncludedIfVersionAttributeSetToYes() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path version='yes' @/>/some/path");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../v/dev/some/path" );
	}
	
	@Test
	public void bundlePathDevContentIsIncludedIfVersionAttributeSetToTrue() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path version='true' @/>/some/path");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../v/dev/some/path" );
	}
	
	@Test
	public void bundlePathProdContentIsIncluded() throws Exception {
		given(brjs).hasProdVersion("1234")
			.and(aspect).indexPageHasContent("<@bundle.path@/>/some/path");
		when(aspect).indexPageLoadedInProd(response, "en_GB");
		then(response).containsText( "../v/1234/some/path" );
	}
	
	@Test
	public void unversionedBundlePathDevContentIsIncluded() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path version='no' @/>/some/path");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../some/path" )
			.and(response).doesNotContainText("dev");
	}
	
	@Test
	public void unversionedBundlePathDevContentIsIncludedWhenVersionSetToFalse() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(aspect).indexPageHasContent("<@bundle.path version='false' @/>/some/path");
		when(aspect).indexPageLoadedInDev(response, "en_GB");
		then(response).containsText( "../some/path" )
			.and(response).doesNotContainText("dev");
	}
	
	@Test
	public void unversionedBundlePathProdContentIsIncluded() throws Exception {
		given(brjs).hasProdVersion("1234")
			.and(aspect).indexPageHasContent("<@bundle.path version='no' @/>/some/path");
		when(aspect).indexPageLoadedInProd(response, "en_GB");
		then(response).containsText( "../some/path" )
			.and(response).doesNotContainText("1234");
	}
	
	@Test
	public void bundlePathContentIsIncludedForWorkbenches() throws Exception {
		given(brjs).hasDevVersion("dev")
			.and(workbench).indexPageHasContent("<@bundle.path@/>/some/path");
		when(workbench).pageLoaded(response, "en_GB");
		then(response).containsText( "../v/dev/some/path" );
	}
	
}
