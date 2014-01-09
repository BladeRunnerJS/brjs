package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;


public class BRJSUnitTest
{
	private BRJS brjs;
	
	@Before
	public void setup() throws IOException
	{
		brjs = BRJSTestFactory.createBRJS( FileUtility.createTemporaryDirectory( this.getClass().getCanonicalName() ) );
	}
	
	@After
	public void teardown()
	{
		brjs.close();
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
