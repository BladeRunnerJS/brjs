package com.caplin.cutlass.bundler;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;

public class UsedBladesFinderTest
{

	private static final String TEST_BASE = "src/test/resources/generic-bundler/bundler-structure-tests/";
	
	private static final String TEST_APPS_BASE = TEST_BASE + APPLICATIONS_DIR;
	private static final String APP1_BASE = TEST_APPS_BASE + "/test-app1";
	private static final String APP2_BASE = TEST_APPS_BASE + "/test-app2";
	
	private static final String XML_SEED_BASE = TEST_APPS_BASE + "/xml-seed-app";
	private static final String INDEX_HTML_SEED_BASE = TEST_APPS_BASE + "/index-html-seed-app";
	private static final String HTML_SEED_BASE = TEST_APPS_BASE + "/html-seed-app";
	
	private static final String UNUSED_BLADES_APP_BASE = TEST_APPS_BASE+"/app-with-unused-blades";
	private static final String UNUSED_BLADESETS_APP_BASE = TEST_APPS_BASE+"/app-with-unused-bladesets";
	
	
	private UsedBladesFinder finder;
	
	@Before
	public void setup() 
	{
		finder = new UsedBladesFinder();
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(TEST_BASE)));
	}
	
	@Test
	public void testASingleBladeUsedBladeIsReturned() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(APP1_BASE, "main-aspect"));
		assertEquals(1, files.size());
		assertTrue( fileContainsText(new File(APP1_BASE,"main-aspect/src/section/app/main2.js"), "section.a.blade1") ); 
		assertEquals( new File(APP1_BASE,"a-bladeset/blades/blade1"), files.get(0) );
	}
	
	@Test
	public void testIncludeBladeViaAspectResourceXmlSeedFile() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(XML_SEED_BASE, "default-aspect"));
		assertEquals(1, files.size());
		assertEquals( new File(XML_SEED_BASE,"a-bladeset/blades/blade1"), files.get(0) );
	}
	
	@Test
	public void testIncludeBladeViaAspectIndexHtmlScriptTag() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(INDEX_HTML_SEED_BASE, "default-aspect"));
		assertEquals(2, files.size());
		assertEquals( new File(INDEX_HTML_SEED_BASE,"a-bladeset/blades/blade1"), files.get(0) );
		assertEquals( new File(INDEX_HTML_SEED_BASE,"a-bladeset/blades/blade2"), files.get(1) );
	}
	
	@Test
	public void testYouCanNotIncludeBladeViaAspectResourceHtmlSeedFile() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(HTML_SEED_BASE, "default-aspect"));
		assertEquals(0, files.size());
	}
	
	@Test
	public void testOnlyUsedBladesAreReturned() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(UNUSED_BLADES_APP_BASE,"main-aspect"));
		assertEquals(2, files.size());
		assertTrue( fileContainsText(new File(UNUSED_BLADES_APP_BASE,"main-aspect/src/section/app/main2.js"), "section.fi.fi-blade1") ); ;
		assertTrue( fileContainsText(new File(UNUSED_BLADES_APP_BASE,"main-aspect/src/section/app/main2.js"), "section.fx.fx-blade2") ); ;
		assertEquals( new File(UNUSED_BLADES_APP_BASE,"fi-bladeset/blades/fi-blade1"), files.get(0) );
		assertEquals( new File(UNUSED_BLADES_APP_BASE,"fx-bladeset/blades/fx-blade2"), files.get(1) );
	}
	
	@Test
	public void testOnlyUsedBladesAreReturned_withUnusedBladeset() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(UNUSED_BLADESETS_APP_BASE,"main-aspect"));
		assertEquals(2, files.size());
		assertTrue( fileContainsText(new File(UNUSED_BLADESETS_APP_BASE,"main-aspect/src/section/app/main2.js"), "section.fi.fi-blade1") ); ;
		assertTrue( fileContainsText(new File(UNUSED_BLADESETS_APP_BASE,"main-aspect/src/section/app/main2.js"), "section.fi.fi-blade2") ); ;
		assertEquals( new File(UNUSED_BLADESETS_APP_BASE,"fi-bladeset/blades/fi-blade1"), files.get(0) );
		assertEquals( new File(UNUSED_BLADESETS_APP_BASE,"fi-bladeset/blades/fi-blade2"), files.get(1) );
	}

	@Test
	public void testEmptyListIsReturnedIfNoBladesFound() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(APP2_BASE,"empty-aspect"));
		assertEquals(0, files.size());
	}
	
	@Test
	public void testEmptyListIsReturnedForNonExistantAspect() throws Exception
	{
		List<File> files = finder.findUsedBlades(new File(APP2_BASE,"nonExistant-aspect"));
		assertEquals(0, files.size());
	}
	
	
	private boolean fileContainsText(File file, String text) throws IOException
	{
		String fileContents = FileUtils.readFileToString(file);
		return fileContents.contains(text);
	}
	
}
