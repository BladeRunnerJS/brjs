package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.junit.Before;
import org.junit.Test;


public class BRJSUnitTest
{

	BRJS brjs;
	
	@Before
	public void setup() throws IOException
	{
		brjs = BRJSTestFactory.createBRJS( FileUtility.createTemporaryDirectory( this.getClass().getCanonicalName() ) );
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
