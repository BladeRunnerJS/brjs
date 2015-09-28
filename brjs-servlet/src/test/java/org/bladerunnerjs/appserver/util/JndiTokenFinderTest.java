package org.bladerunnerjs.appserver.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.*;

public class JndiTokenFinderTest
{

	private JndiTokenFinder tokenFinder;
	private Context mockContext;

	@Before
	public void setup()
	{
		mockContext = mock(Context.class);
		tokenFinder = new JndiTokenFinder(mockContext);
	}

	@After
	public void tearDown()
	{
		verifyNoMoreInteractions(mockContext);
	}

	@Test
	public void testNullIsReturnedForEmptyToken() throws Exception
	{
		assertNull(tokenFinder.findTokenValue(""));
	}

	@Test
	public void testNullIsReturnedForNullToken() throws Exception
	{
		assertNull(tokenFinder.findTokenValue(null));
	}

	@Test
	public void testTokenIsReturnedFromContextLookup() throws Exception
	{
		when(mockContext.lookup("java:comp/env/TEST.TOKEN")).thenReturn("token value");

		assertEquals("token value", tokenFinder.findTokenValue("TEST.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/TEST.TOKEN");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExceptionIsThrownIfLookupThrowsException() throws Exception
	{
        when(mockContext.lookup("java:comp/env/NON.EXISTENT.TOKEN")).thenThrow(NamingException.class);
        try {
            tokenFinder.findTokenValue("NON.EXISTENT.TOKEN");
            fail("Expect an exception to be thrown");
        } catch (TokenReplacementException ex) {
            assertEquals("An error occurred when the token finder 'JndiTokenFinder' attempted to locate a replacement for the the token 'NON.EXISTENT.TOKEN'.", ex.getMessage());
            assertEquals(NamingException.class, ex.getCause().getClass());
        }
        finally {
		    verify(mockContext, times(1)).lookup("java:comp/env/NON.EXISTENT.TOKEN");
        }
	}

	@Test
	public void testEmptyStringIsReturnedIfLookupReturnsNull() throws Exception
	{
		when(mockContext.lookup("java:comp/env/NON.EXISTANT.TOKEN")).thenReturn(null);

		assertEquals("", tokenFinder.findTokenValue("NON.EXISTANT.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/NON.EXISTANT.TOKEN");
	}

	@Test
	public void testEmptyStringIsReturnedIfLookupReturnsAnEmptyString() throws Exception
	{
		when(mockContext.lookup("java:comp/env/EMPTY.TOKEN")).thenReturn("");

		assertEquals("", tokenFinder.findTokenValue("EMPTY.TOKEN"));
		verify(mockContext, times(1)).lookup("java:comp/env/EMPTY.TOKEN");
	}

}
