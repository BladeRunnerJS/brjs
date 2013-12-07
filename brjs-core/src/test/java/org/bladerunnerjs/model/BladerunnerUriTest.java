package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;


public class BladerunnerUriTest
{
	private App app;
	
	@Before
	public void setUp() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("BladerunnerUri-test");
		new File(tempDir, "sdk").mkdir();
		BRJS brjs = BRJSTestFactory.createBRJS(tempDir);
		app = brjs.app("the-app");
		
		FileUtils.write(app.bladeset("another").file("js/empty.txt"), "");
		FileUtils.write(app.bladeset("another").blade("blade1").workbench().file("empty.txt"), "");
	}
	
	@Test
	public void eachComponentOfTheUrlShouldBeAvailableSeparatelyAndCanBeRecombined() throws Exception
	{
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/the-app", "/;param=value", "query=value");
		
		assertEquals("/the-app", uri.contextPath);
		assertEquals("/", uri.scopePath);
		assertEquals("", uri.logicalPath);
		assertEquals(";param=value", uri.pathParameter);
		assertEquals("query=value", uri.queryString);
		
		assertEquals("/the-app/;param=value?query=value", uri.getUri());
		assertEquals("/", uri.getInternalPath());
	}
	
	@Test
	public void requestUrisWithoutPathParametersAndQueryStringsAreSupported() throws Exception
	{
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/the-app", "/", null);
		
		assertEquals("/the-app", uri.contextPath);
		assertEquals("/", uri.scopePath);
		assertEquals("", uri.logicalPath);
		assertEquals("", uri.pathParameter);
		assertNull(uri.queryString);
		
		assertEquals("/the-app/", uri.getUri());
		assertEquals("/", uri.getInternalPath());
	}
	
	@Test
	public void theScopeAndLogicalPathsShouldBothBeAvailable() throws Exception
	{
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/the-app/", "/another-bladeset/js/empty.txt", null);
		
		assertEquals("/another-bladeset/", uri.scopePath);
		assertEquals("js/empty.txt", uri.logicalPath);
	}
	
	@Test
	public void theScopeAndLogicalPathsShouldBeCorrectForWorkbenchUrls() throws Exception
	{
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/the-app/", "/another-bladeset/blades/blade1/workbench/empty.txt", null);
		
		assertEquals("/another-bladeset/blades/blade1/workbench/", uri.scopePath);
		assertEquals("empty.txt", uri.logicalPath);
	}
}
