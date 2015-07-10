package org.bladerunnerjs.appserver.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
		when(mockJndiContext.lookup("java:comp/env/LONG.TOKEN.REPLACEMENT")).thenReturn( StringUtils.leftPad("", 5000, "0") );
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
	}
	
	@Test
	public void tokenReplacementWorksForNullStringValues() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@A.NULL.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("", replacedContent);
	}

	@Test 
	public void testExceptionIsThrownIfTokenCannotBeReplaced() throws Exception
	{
		exception.expect(IllegalArgumentException.class);
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@A.NONEXISTANT.TOKEN@") );
		IOUtils.readLines( tokenisingReader );
	}
	
	@Test
	public void longTokenReplacementsCanBeUsed() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader("@LONG.TOKEN.REPLACEMENT@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals(StringUtils.leftPad("", 5000, "0"), replacedContent);
	}
	
	@Test
	public void tokenStringsCanSpanBufferLimits() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader(
				StringUtils.leftPad("", 4094, "0")+" @A.TOKEN@ "+StringUtils.leftPad("", 4094, "0"))
		);
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals( 
				StringUtils.leftPad("", 4094, "0")+" token replacement "+StringUtils.leftPad("", 4094, "0")
		, replacedContent);
	}
	
	@Test
	public void tokensAreReplacedInsideOfLargeContent() throws Exception
	{
		for (int padLength : Arrays.asList(4096, 5000, 10000)) {
    		Reader tokenisingReader = new TokenReplacingReader( new JndiTokenFinder(mockJndiContext), new StringReader(
    				StringUtils.leftPad("", padLength, "0")+" @A.TOKEN@ "+StringUtils.leftPad("", padLength, "0"))
    		);
    		String replacedContent = IOUtils.toString( tokenisingReader );
    		assertEquals( 
    				StringUtils.leftPad("", padLength, "0")+" token replacement "+StringUtils.leftPad("", padLength, "0")
    		, replacedContent);
		}
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
