package org.bladerunnerjs.appserver.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringReader;

import javax.naming.Context;
import javax.naming.NamingException;

import org.bladerunnerjs.appserver.filter.TestContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StreamTokeniserTest
{
	@Rule
	 public final ExpectedException exception = ExpectedException.none();
	
	private Context mockJndiContext;
	private StreamTokeniser tokeniser;

	@Before
	public void setup() throws Exception
	{
		mockJndiContext = TestContextFactory.getTestContext();
		tokeniser = new StreamTokeniser( new JndiTokenFinder(mockJndiContext) );
	}

	@After
	public void teardown() throws Exception
	{
		
	}
	
	@Test
	public void testJndiIsLookupPerformedForToken() throws Exception
	{
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");
		StringBuffer replacedContent = tokeniser.replaceTokens( new StringReader("@A.TOKEN@") );
		assertEquals("token replacement", replacedContent.toString());
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
	}

	@Test
	public void tokenReplacementWorksForEmptyStringValues() throws Exception
	{
		when(mockJndiContext.lookup("java:comp/env/AN.EMPTY.TOKEN")).thenReturn(null);;
		StringBuffer replacedContent = tokeniser.replaceTokens( new StringReader("@AN.EMPTY.TOKEN@") );
		assertEquals("", replacedContent.toString());
		verify(mockJndiContext, times(1)).lookup("java:comp/env/AN.EMPTY.TOKEN");
	}

	@SuppressWarnings("unchecked")
	@Test 
	public void testExceptionIsThrownIfTokenCannotBeReplaced() throws Exception
	{
		when(mockJndiContext.lookup("java:comp/env/A.NONEXISTANT.TOKEN")).thenThrow(NamingException.class);
		exception.expect(IllegalArgumentException.class);
		tokeniser.replaceTokens( new StringReader("@A.NONEXISTANT.TOKEN@"));
	}
	
}
