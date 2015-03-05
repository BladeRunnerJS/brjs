package org.bladerunnerjs.appserver.filter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileExtensionFilterTest extends ServletFilterTest
{
	private Server appServer;
	
	@Before
	public void setup() throws Exception
	{
		appServer = createAppServer(new DefaultServlet(), new FileExtensionFilter());
		appServer.start();
		
		File htmlFile = new File(contextDir, "page.html");
		FileUtils.write(htmlFile, "<h1>Hello World!</h1>", "UTF-8");
		
		File textFile = new File(contextDir, "text.txt");
		FileUtils.write(textFile, "Hello World!", "UTF-8");
	}

	@After
	public void teardown() throws Exception
	{
		appServer.stop();
	}
	
	@Test
	public void htmlFilesCanBeRequestedWithAFileSuffix() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/page.html");
		
		assertEquals("200", response.get("responseCode"));
		assertEquals("<h1>Hello World!</h1>", response.get("responseText"));
		assertEquals("text/html", response.get("responseContentType"));
	}
	
	@Test
	public void htmlFilesCanBeRequestedWithoutAFileSuffix() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/page");
		
		assertEquals("200", response.get("responseCode"));
		assertEquals("<h1>Hello World!</h1>", response.get("responseText"));
		assertEquals("text/html", response.get("responseContentType"));
	}
	
	@Test
	public void textFilesCanBeRequestedWithAFileSuffix() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/text.txt");
		
		assertEquals("200", response.get("responseCode"));
		assertEquals("Hello World!", response.get("responseText"));
		assertEquals("text/plain", response.get("responseContentType"));
	}
	
	@Test
	public void textFilesCanNotBeRequestedWithoutAFileSuffixAtPresent() throws Exception
	{
		Map<String, String> response = makeRequest("http://localhost:"+serverPort+"/text");
		
		assertEquals("404", response.get("responseCode"));
	}
}
