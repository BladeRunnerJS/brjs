package com.caplin.cutlass.filter.versionfilter;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersionRedirectorTest {

	@Test
	public void versionAtStartOfPathIsReplaced() throws Exception {
		assertEquals( "xml.bundle", new VersionRedirector().getRedirectedUrl("v_12345/xml.bundle") );
	}
	
	@Test
	public void versionAtSecondDirectoryInPathIsReplaced() throws Exception {
		assertEquals( "app1/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/v_12345/xml.bundle") );
	}
	
	@Test
	public void versionAfterSecondDirectoryInPathIsNotReplaced() throws Exception {
		assertEquals( "app1/a/v_12345/path/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/a/v_12345/path/xml.bundle") );
		assertEquals( "app1/a/b/v_12345/path/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/a/b/v_12345/path/xml.bundle") );
	}
	
	@Test
	public void testContextCanHaveASimilarFormatToAVersionString() throws Exception {
		assertEquals( "v987/xml.bundle", new VersionRedirector().getRedirectedUrl("v987/v_12345/xml.bundle") );
		assertEquals( "v987/xml.bundle", new VersionRedirector().getRedirectedUrl("v987/xml.bundle") );
		assertEquals( "v987/xml.bundle", new VersionRedirector().getRedirectedUrl("v987/v_987/xml.bundle") );
	}
	
	@Test
	public void testOnlyFirstVersionInPathIsReplaced() throws Exception {
		assertEquals( "app1/a/path/v_12345/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/v_12345/a/path/v_12345/xml.bundle") );
		assertEquals( "app1/v_12345/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/v_12345/v_12345/xml.bundle") );
	}
	
	@Test
	public void testUrlWithoutAVersionIsUnchanged() throws Exception {
		assertEquals( "app1/a/path/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/a/path/xml.bundle") );
		assertEquals( "app1/v12345abc/path/xml.bundle", new VersionRedirector().getRedirectedUrl("app1/v12345abc/path/xml.bundle") );
	}
	
	@Test
	public void testTryingToRedirectRootUrl() throws Exception {
		assertEquals( "", new VersionRedirector().getRedirectedUrl("") );
	}
	
}
