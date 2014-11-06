package org.bladerunnerjs.spec.plugin.bundler.inline;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.bundlers.css.CssTagHandlerPlugin.Messages;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class InlineTagHandlerPluginTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private Aspect loginAspect;
	private File commonTheme;
	private File standardTheme;
	private Bladeset bladeset;
	private Blade blade;
	private Workbench workbench;
	private Aspect defaultAspect;
	private File targetDir;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlerPlugins().and(brjs).automaticallyFindsMinifierPlugins().and(brjs).hasBeenCreated();
		app = brjs.app("app1");
		appConf = app.appConf();
		aspect = app.aspect("default");
		defaultAspect = app.defaultAspect();
		commonTheme = aspect.file("themes/common");
		standardTheme = aspect.file("themes/standard");
		blade = app.bladeset("bs").blade("b1");
		loginAspect = app.aspect("login");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
		targetDir = FileUtility.createTemporaryDirectory( this.getClass() );
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
	public void fileDoesntExistWritesError() throws Exception {
		given(aspect).indexPageHasContent("<@inline file=\"doesntexist.js\" @/>");
		when(aspect).indexPageLoadedInDev(response, "en");
		then(exceptions).verifyException(IOException.class);
	}
	
}
