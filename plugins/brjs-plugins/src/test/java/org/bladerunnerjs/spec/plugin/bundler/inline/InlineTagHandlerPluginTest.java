package org.bladerunnerjs.spec.plugin.bundler.inline;

import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class InlineTagHandlerPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();

	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins().and(brjs).automaticallyFindsMinifierPlugins().and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.aspect("default");
	}
	
	@Test
	public void fileIsInlinedCorrectly() throws Exception {
		given(aspect).indexPageHasContent("<@inline file=\"inline.js\"@/>")
			.and(aspect).containsFileWithContents("inline.js", "My JavaScript to Inline");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsText("My JavaScript to Inline");
	}
	
	@Test
	public void fileCanBeInlinedMoreThanOnce() throws Exception {
		given(aspect).indexPageHasContent("<@inline file=\"inline.js\" @/><@inline file=\"inline.js\" @/>")
			.and(aspect).containsFileWithContents("inline.js", "My JavaScript to Inline");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(response).containsText("My JavaScript to InlineMy JavaScript to Inline");
	}
	
	@Test
	public void fileCanBeInlinedFromASubFolder() throws Exception {
		given(aspect).indexPageHasContent("<@inline file=\"subfolder/inline.js\" @/>")
			.and(aspect).containsFileWithContents("subfolder/inline.js", "My JavaScript to Inline");
		when(aspect).indexPageLoadedInDev(response, "en");
		
		then(response).containsText("My JavaScript to Inline");
	}
	
	@Test
	public void errorIsThrownIfFileDoesntExist() throws Exception {
		given(aspect).indexPageHasContent("<@inline file=\"doesntexist.js\" @/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyException(IOException.class);
	}
	
}
