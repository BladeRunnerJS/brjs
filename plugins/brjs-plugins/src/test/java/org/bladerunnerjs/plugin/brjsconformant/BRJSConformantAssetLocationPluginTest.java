package org.bladerunnerjs.plugin.brjsconformant;

import static org.junit.Assert.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BladeWorkbench;
import org.junit.Before;
import org.junit.Test;


public class BRJSConformantAssetLocationPluginTest extends SpecTest
{

	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private BladeWorkbench workbench;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).hasBeenCreated();
		
		App app = brjs.app("app1");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade = bladeset.blade("b1");
		workbench = blade.workbench();
	}
	
	// Aspect src
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForSrcDir() throws Exception
	{
		given(aspect).hasDir("src");
		assertEquals( "appns", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(aspect, aspect.file("src")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForPackageDir() throws Exception
	{
		given(aspect).hasDir("src/appns/pkg1");
		assertEquals( "appns/pkg1", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(aspect, aspect.file("src/appns/pkg1")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForNestedPackageDirs() throws Exception
	{
		given(aspect).hasDir("src/appns/pkg1/pkg2/pkg3");
		assertEquals( "appns/pkg1/pkg2/pkg3", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(aspect, aspect.file("src/appns/pkg1/pkg2/pkg3")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyWhenRequirePrefixDirsAreOmitted() throws Exception
	{
		given(aspect).hasDir("src/pkg1");
		assertEquals( "appns/pkg1", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(aspect, aspect.file("src/pkg1")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyWhenRequirePrefixDirsAreOmittedAndNestedPackageDirsAreUsed() throws Exception
	{
		given(aspect).hasDir("src/pkg1/pkg2/pkg3");
		assertEquals( "appns/pkg1/pkg2/pkg3", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(aspect, aspect.file("src/pkg1/pkg2/pkg3")) );
	}
	
	
	// Blade src
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForBladeSrcDir() throws Exception
	{
		given(blade).hasDir("src");
		assertEquals( "appns/bs/b1", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForPackageDirWithinABlade() throws Exception
	{
		given(blade).hasDir("src/appns/bs/b1/pkg1");
		assertEquals( "appns/bs/b1/pkg1", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/appns/bs/b1/pkg1")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyForNestedPackageDirsWithinABlade() throws Exception
	{
		given(blade).hasDir("src/appns/bs/b1/pkg1/pkg2/pkg3");
		assertEquals( "appns/bs/b1/pkg1/pkg2/pkg3", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/appns/bs/b1/pkg1/pkg2/pkg3")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyWhenRequirePrefixDirsAreOmittedWithinABlade() throws Exception
	{
		given(blade).hasDir("src/pkg1");
		assertEquals( "appns/bs/b1/pkg1", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/pkg1")) );
	}
	
	@Test
	public void childRequirePrefixIsCalculatedCorrectlyWhenRequirePrefixDirsAreOmittedAndNestedPackageDirsAreUsedWithinABlade() throws Exception
	{
		given(blade).hasDir("src/pkg1/pkg2/pkg3");
		assertEquals( "appns/bs/b1/pkg1/pkg2/pkg3", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/pkg1/pkg2/pkg3")) );
	}
	
	
	
	@Test
	public void exceptionIsThrownIfRequirePrefixStartsWithAppRequirePrefixButDoesntFullyMatchContainerPrefix() throws Exception
	{
		given(blade).hasDir("src/appns/pkg1");
		try {
			BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/appns/pkg1"));
			fail("No exception thrown");
		} catch (Exception ex) {
			assertTrue(ex.getMessage().contains("The source module directory at 'apps/app1/bs-bladeset/blades/b1/src/appns/pkg1' is in an invalid location."));
		}
	}
	
	@Test
	public void exceptionIsNotThrownWhenCalculatingTheRequirePathForAValidParentDirectoryOfAValidSourceLocation() throws Exception
	{
		given(blade).hasDir("src/appns/bs/b1/");
		assertEquals( "appns", BRJSConformantAssetLocationPlugin.calculateChildRequirePrefix(blade, blade.file("src/appns")) );
	}
	
}
