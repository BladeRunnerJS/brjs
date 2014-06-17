package org.bladerunnerjs.spec.jslib;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantRootAssetLocation;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class JsLibTest extends SpecTest {
	private App app;
	private JsLib lib;
	private JsLib badLib;
	private NamedDirNode libTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			lib = app.jsLib("lib1");
			badLib = app.jsLib("%$&@");
			libTemplate = brjs.template(BRJSConformantRootAssetLocation.class.getSimpleName().toLowerCase());
	}
	
	@Test
	public void parentAppGivesTheCorrectNode() throws Exception {
		when(lib).create();
		then(lib.app()).isSameAs(app);
	}
	
	@Test
	public void populateFailsIfTheDirectoryNameIsInvalid() throws Exception {
		given(logging).enabled();
		when(badLib).populate("libx");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, badLib.getClass().getSimpleName(), badLib.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, badLib.getName(), badLib.dir().getPath());
	}
	
	@Test
	public void populateFailsIfTheLibraryNamespaceIsInvalid() throws Exception {
		when(lib).populate("LIBX");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "LIBX", lib.dir().getPath());
	}
	
	@Test
	public void populateFailsIfTheLibraryNamespaceIsReserved() throws Exception {
		when(lib).populate("caplin");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, "caplin", lib.dir().getPath());
	}
	
	@Test
	public void libraryIsBaselinedDuringPopulation() throws Exception {
		given(libTemplate).containsFolder("@libns")
			.and(libTemplate).containsFileWithContents("some-@libns-file.txt", "'@libns'");
		when(lib).populate("libx");
		then(lib).hasDir("libx")
			.and(lib).doesNotHaveDir("@libns")
			.and(lib).fileHasContents("some-libx-file.txt", "'libx'");
	}
	
}
