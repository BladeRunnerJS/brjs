package org.bladerunnerjs.appserver.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TokenReplacingReaderTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	private TokenFinder mockTokenFinder;
    private NoTokenReplacementHandler mockTokenReplacementHandler = mock(NoTokenReplacementHandler.class);
	
	@Before
	public void setup() throws Exception
	{
		mockTokenFinder = mock(TokenFinder.class);
		when(mockTokenFinder.findTokenValue("A.TOKEN")).thenReturn("token replacement");
		when(mockTokenFinder.findTokenValue("AN.EMPTY.TOKEN")).thenReturn("");
		when(mockTokenFinder.findTokenValue("A.NULL.TOKEN")).thenReturn(null);
		when(mockTokenFinder.findTokenValue("EXCEPTION.THROWING.TOKEN")).thenThrow(TokenReplacementException.class);
		when(mockTokenFinder.findTokenValue("LONG.TOKEN.REPLACEMENT")).thenReturn(StringUtils.leftPad("", 5000, "0") );
	}

	@After
	public void teardown() throws Exception
	{
		
	}
	
	@Test
	public void testfindTokenValueIsPerformedForToken() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("token replacement", replacedContent);
		verify(mockTokenFinder, times(1)).findTokenValue("A.TOKEN");
	}
	
	@Test
	public void testTokensCanBeReplacedBackToBack() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.TOKEN@@A.TOKEN@@A.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("token replacementtoken replacementtoken replacement", replacedContent);
	}
	
	@Test
	public void testTokensMustBeUppcaseAndDots() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.token@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@A.token@", replacedContent);
		verify(mockTokenFinder, times(0)).findTokenValue(any(String.class));
	}
	
	@Test
	public void testTokensMustBeContainedWithin2AtSymbols() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.token") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@A.token", replacedContent);
		verify(mockTokenFinder, times(0)).findTokenValue(any(String.class));
	}
	
	@Test
	public void testTokensCannotContainInvalidChars() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A_TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@A_TOKEN@", replacedContent);
		verify(mockTokenFinder, times(0)).findTokenValue(any(String.class));
	}
	
	@Test
	public void testTwoAtSymbolsArentAValidToken() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@@", replacedContent);
		verify(mockTokenFinder, times(0)).findTokenValue(any(String.class));
	}
	
	@Test
	public void tokenStringThatIsntClosedIsOutputAsTheTokenString() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.TOKEN") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@A.TOKEN", replacedContent);
	}
	
	@Test
	public void tokenAfterASingleAtSymbolIsReplaced() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@Foo@A.TOKEN@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("@Footoken replacement", replacedContent);
	}
	
	@Test
	public void testJndiIsfindTokenValuePerformedForTokenInsideOfALargerString() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("this is a @A.TOKEN@ :-)") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals("this is a token replacement :-)", replacedContent);
		verify(mockTokenFinder, times(1)).findTokenValue("A.TOKEN");
	}

	@Test
	public void tokenReplacementWorksForEmptyStringValues() throws Exception {
		Reader tokenisingReader = new TokenReplacingReader(mockTokenFinder, new StringReader("@AN.EMPTY.TOKEN@"));
		String replacedContent = IOUtils.toString(tokenisingReader);
		assertEquals("", replacedContent);
	}

    @Test
    public void testEmptyStringIsUsedIfTokenReaderReturnsNull() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.NULL.TOKEN@") );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("", replacedContent);
    }


    @Test
	public void testExceptionIsThrownIfTokenFinderThrowsAnInvalidTokenException() throws Exception
	{
		exception.expect(IllegalArgumentException.class);
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@EXCEPTION.THROWING.TOKEN@") );
		IOUtils.readLines(tokenisingReader);
	}
	
	@Test
	public void longTokenReplacementsCanBeUsed() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@LONG.TOKEN.REPLACEMENT@") );
		String replacedContent = IOUtils.toString( tokenisingReader );
		assertEquals(StringUtils.leftPad("", 5000, "0"), replacedContent);
	}
	
	@Test
	public void tokenStringsCanSpanBufferLimits() throws Exception
	{
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader(
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
    		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader(
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
		Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, sourceReader );
		tokenisingReader.close();
		verify(sourceReader, times(1)).close();
	}

    @Test
    public void tokenReplacingReaderCanBeConfiguredToIgnoreFailedReplacementsAndIncludeOriginalToken() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@EXCEPTION.THROWING.TOKEN@"), mockTokenReplacementHandler );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("@EXCEPTION.THROWING.TOKEN@", replacedContent);
    }

    @Test
    public void ignoredFailedReplacementsDoNotCauseOtherTokensTobeReplacedBefore() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.TOKEN@@EXCEPTION.THROWING.TOKEN@"), mockTokenReplacementHandler );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("token replacement@EXCEPTION.THROWING.TOKEN@", replacedContent);
    }

    @Test
    public void ignoredFailedReplacementsDoNotCauseOtherTokensTobeReplacedAfterwards() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@EXCEPTION.THROWING.TOKEN@@A.TOKEN@"), mockTokenReplacementHandler );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("@EXCEPTION.THROWING.TOKEN@token replacement", replacedContent);
    }

    @Test
    public void ignoredFailedReplacementsDoNotCauseOtherTokensTobeReplacedBeforeAndAfterwards() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@A.TOKEN@@EXCEPTION.THROWING.TOKEN@@A.TOKEN@"), mockTokenReplacementHandler );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("token replacement@EXCEPTION.THROWING.TOKEN@token replacement", replacedContent);
    }

    @Test
    public void tokenReplacementHandlerIsNotifiedAndCanCauseTheFailedReplacementToBeIgnored() throws Exception
    {
        Reader tokenisingReader = new TokenReplacingReader( mockTokenFinder, new StringReader("@EXCEPTION.THROWING.TOKEN@"), mockTokenReplacementHandler );
        String replacedContent = IOUtils.toString(tokenisingReader);
        assertEquals("@EXCEPTION.THROWING.TOKEN@", replacedContent);
        verify(mockTokenReplacementHandler, times(0)).handleNoTokenFound( eq("EXCEPTION.THROWING.TOKEN"), any(TokenReplacementException.class) );
    }

}
