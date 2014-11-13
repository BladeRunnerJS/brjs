package org.bladerunnerjs.spec.brjs.appserver;

import java.util.Hashtable;

import static org.mockito.Mockito.mock;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;


public class TestContextFactory implements InitialContextFactory
{
	private static Context testContext;
	
    public TestContextFactory()
    {
    }
    
    public static Context getTestContext()
    {
    	testContext = mock(Context.class);
    	return testContext;
    }
    
    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException
    {
    	return testContext;
    }
}
