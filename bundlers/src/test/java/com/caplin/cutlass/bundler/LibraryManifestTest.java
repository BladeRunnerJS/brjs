package com.caplin.cutlass.bundler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class LibraryManifestTest
{
	private static final String lib1Dir = "src/test/resources/librarymanifest/lib1";
	private static final String lib2Dir = "src/test/resources/librarymanifest/lib2";
	private static final String lib3Dir = "src/test/resources/librarymanifest/lib3";
	private static final String lib4Dir = "src/test/resources/librarymanifest/lib4";
	
	@Test
	public void testCreatingLibraryManifestIfManifestFileDoesNotExist() throws Exception
	{
		LibraryManifest manifest = new LibraryManifest(new File(lib1Dir));
		assertEquals(0, manifest.getLibraryDependencies().size());
		assertEquals(0, manifest.getJavascriptFiles().size());
		assertEquals(0, manifest.getCssFiles().size());
	}
	
	@Test
	public void testCreatingLibraryManifestForManifestWithSingleDependency() throws Exception
	{
		LibraryManifest manifest = new LibraryManifest(new File(lib2Dir));
		assertEquals(1, manifest.getLibraryDependencies().size());
		assertTrue(manifest.getLibraryDependencies().contains("lib1"));
		assertEquals(0, manifest.getJavascriptFiles().size());
		assertEquals(0, manifest.getCssFiles().size());
	}
	
	@Test
	public void testCreatingLibraryManifestForManifestWithNoDependenciesAndSpecifiedJsAndCssFiles() throws Exception
	{
		LibraryManifest manifest = new LibraryManifest(new File(lib3Dir));
		assertEquals(0, manifest.getLibraryDependencies().size());
		
		assertEquals(2, manifest.getJavascriptFiles().size());
		assertTrue(manifest.getJavascriptFiles().contains("lib3-1.js"));
		assertTrue(manifest.getJavascriptFiles().contains("lib3-2.js"));
		
		assertEquals(2, manifest.getCssFiles().size());
		assertTrue(manifest.getCssFiles().contains("lib3-1.css"));
		assertTrue(manifest.getCssFiles().contains("lib3-2.css"));
	}
	
	@Test
	public void testCreatingLibraryManifestForManifestWithSpecifiedDependenciesAndJsAndCssFiles() throws Exception
	{
		LibraryManifest manifest = new LibraryManifest(new File(lib4Dir));
		assertEquals(2, manifest.getLibraryDependencies().size());
		assertTrue(manifest.getLibraryDependencies().contains("lib2"));
		assertTrue(manifest.getLibraryDependencies().contains("lib3"));
		
		assertEquals(2, manifest.getJavascriptFiles().size());
		assertTrue(manifest.getJavascriptFiles().contains("lib4-1.js"));
		assertTrue(manifest.getJavascriptFiles().contains("lib4-2.js"));
		
		assertEquals(2, manifest.getCssFiles().size());
		assertTrue(manifest.getCssFiles().contains("lib4-1.css"));
		assertTrue(manifest.getCssFiles().contains("lib4-2.css"));
	}
}
