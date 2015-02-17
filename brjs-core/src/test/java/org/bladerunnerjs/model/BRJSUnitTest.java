package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSUnitTest 
{
	private BRJS brjs;
	private File testSdkDirectory;
	
	@Before
	public void setup() throws Exception
	{
		testSdkDirectory = FileUtils.createTemporaryDirectory( this.getClass() );
		new File(testSdkDirectory, "sdk").mkdir();
		
		brjs = BRJSTestModelFactory.createModel(testSdkDirectory);
	}
	
	@After
	public void teardown()
	{
		brjs.close();
		org.apache.commons.io.FileUtils.deleteQuietly(testSdkDirectory);
	}
	
	@Test
	public void getApplicationServerReturnsSameInstance() throws Exception
	{
		assertTrue( "app server should return the same instance if called multiple times", brjs.applicationServer(1234) == brjs.applicationServer(1234) );
	}
	
	@Test
	public void getApplicationServerUsesBladerunnerConfForDefaultPort() throws Exception
	{
		assertTrue( "app server should return the same instance if called multiple times", brjs.applicationServer() == brjs.applicationServer( brjs.bladerunnerConf().getJettyPort() ) );
	}
	
}
