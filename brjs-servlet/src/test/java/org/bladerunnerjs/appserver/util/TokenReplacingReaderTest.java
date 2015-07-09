package org.bladerunnerjs.appserver.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.appserver.filter.TestContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TokenReplacingReaderTest
{
	@Rule
	 public final ExpectedException exception = ExpectedException.none();
	
	private Context mockJndiContext;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws Exception
	{
		mockJndiContext = TestContextFactory.getTestContext();
		when(mockJndiContext.lookup("java:comp/env/A.TOKEN")).thenReturn("token replacement");
		when(mockJndiContext.lookup("java:comp/env/AN.EMPTY.TOKEN")).thenReturn("");
		when(mockJndiContext.lookup("java:comp/env/A.NULL.TOKEN")).thenReturn(null);
		when(mockJndiContext.lookup("java:comp/env/A.NONEXISTANT.TOKEN")).thenThrow(NamingException.class);
	}

	@After
	public void teardown() throws Exception
	{
		
	}
	
	@Test
	public void testJndiIsLookupPerformedForToken() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@A.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("token replacement", replacedContent);
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
	}
	
	@Test
	public void testJndiIsLookupPerformedForTokenInsideOfALargerString() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("this is a @A.TOKEN@ :-)") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("this is a token replacement :-)", replacedContent);
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.TOKEN");
	}

	@Test
	public void tokenReplacementWorksForEmptyStringValues() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@AN.EMPTY.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("", replacedContent);
		verify(mockJndiContext, times(1)).lookup("java:comp/env/AN.EMPTY.TOKEN");
	}
	
	@Test
	public void tokenReplacementWorksForNullStringValues() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@A.NULL.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("", replacedContent);
		verify(mockJndiContext, times(1)).lookup("java:comp/env/A.NULL.TOKEN");
	}

	@Test 
	public void testExceptionIsThrownIfTokenCannotBeReplaced() throws Exception
	{
		exception.expect(IllegalArgumentException.class);
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@A.NONEXISTANT.TOKEN@") );
		IOUtils.readLines( tokenisingReader );
	}
	
	@Test
	public void closeMethodClosesTheSourceReader() throws Exception
	{
		Reader sourceReader = mock(Reader.class);
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), sourceReader );
		tokenisingReader.close();
		verify(sourceReader, times(1)).close();
	}
	
}
